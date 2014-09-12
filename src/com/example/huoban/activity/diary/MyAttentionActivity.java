package com.example.huoban.activity.diary;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.MyAttention;
import com.example.huoban.model.MyAttentionResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.other.RoundImageView;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyAttentionActivity extends BaseActivity implements OnRefreshListener{
	private TextView tvTitle;
	private ImageButton ibBack;
	private PullToRefreshListView mPullToRefreshListView;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private ListView mListView;
	private ArrayList<MyAttention> mAttentions;
	private DiaryAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_diary_attention);
		setupViews();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		geMyAttention(true);
	}

	private void geMyAttention(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_MY_FOCUS;
		task.resultDataClass = MyAttentionResult.class;

		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void setupViews() {
		// 设置标题
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.my_attention);

		// 返回按钮
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_my_diary_attention);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		Task task = (Task) param[0];
		MyAttentionResult result = (MyAttentionResult) task.result;
		int status=result.status;
		if(status==0){
			ToastUtil.showToast(this, "你还未关注任何文章！", Gravity.CENTER);
		}
		mAttentions = result.data;
		
		if(adapter==null){
			adapter=new DiaryAdapter();
			mListView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtn_left:
				finish();
				break;
			}
		}
	};

	private class DiaryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			try {
				return mAttentions.size();
			} catch (NullPointerException e) {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return mAttentions.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View view, ViewGroup arg2) {
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_my_diary_attention_items, arg2, false);
			}
			RoundImageView ivImg = ViewHolder.get(view,
					R.id.iv_my_diary_attention_img);
			TextView tvTitle = ViewHolder.get(view,
					R.id.tv_my_diary_attention_title);

			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.bg_cover)
					.showImageOnFail(R.drawable.bg_cover).cacheInMemory(true)
					.cacheOnDisc(true).build();
			mImageLoader.displayImage(mAttentions.get(arg0).cover_url, ivImg,
					options);

			tvTitle.setText(mAttentions.get(arg0).diary_title);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MyAttentionActivity.this, DiaryDetailActivity.class);
					intent.putExtra("diary_id", mAttentions.get(arg0).topic_id);
					MyAttentionActivity.this.startActivity(intent);
				}
			});
			return view;
		}

	}

	@Override
	public void onRefresh() {
		geMyAttention(false);
	}

	@Override
	public void onLoadMore() {
		
	}

}
