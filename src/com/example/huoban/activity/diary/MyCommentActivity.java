package com.example.huoban.activity.diary;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.MyComment;
import com.example.huoban.model.MyCommentResult;
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

public class MyCommentActivity extends BaseActivity implements OnRefreshListener{
	private TextView tvTitle;
	private ImageButton ibBack;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private CommentAdapter adapter;
	private ArrayList<MyComment> mComments;
	private ImageLoader mImageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_diary_comment);
		setupViews();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getMyComment(true);
	}

	private void getMyComment(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_MY_COMMENT;
		task.resultDataClass = MyCommentResult.class;

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
		tvTitle.setText(R.string.my_comment);

		// 返回按钮
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_my_diary_comment_list);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setHeaderDividersEnabled(false);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MyCommentActivity.this,
						CommentActivity.class);
				intent.putExtra("diary_id", mComments.get(arg2).diary_id);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		Task task = (Task) param[0];
		MyCommentResult result = (MyCommentResult) task.result;
		int status = result.status;
		if (status == 0) {
			ToastUtil.showToast(this, "您还没有任何评论！" , Gravity.CENTER);
		}
		mComments = result.data;

		if (adapter == null) {
			adapter = new CommentAdapter();
			mListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	private class CommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			try {
				return mComments.size();
			} catch (NullPointerException e) {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return mComments.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_my_diary_comment_items, arg2, false);
			}
			TextView tvTitle = ViewHolder.get(view,
					R.id.tv_my_diary_comment_title);
			TextView tvCount = ViewHolder.get(view,
					R.id.tv_my_diary_comment_count);
			TextView tvContent = ViewHolder.get(view,
					R.id.tv_my_diary_comment_content);
			TextView tvDate = ViewHolder.get(view,
					R.id.tv_my_diary_comment_date);
			RoundImageView ivPicture = ViewHolder.get(view,
					R.id.iv_my_diary_comment_pic);

			MyComment myComment=mComments.get(arg0);
			tvTitle.setText(myComment.diary_title);
			tvCount.setText(String.valueOf(myComment.comments.size()));
			tvContent.setText(myComment.comments
							.get(myComment.comments.size()-1).reply_content);
			tvDate.setText(myComment.comments
					.get(myComment.comments.size()-1).reply_time);

			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.bg_cover)
					.showImageOnFail(R.drawable.bg_cover).cacheInMemory(true)
					.cacheOnDisc(true).build();
			mImageLoader.displayImage(myComment.cover_url, ivPicture, options);

			return view;
		}
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

	@Override
	public void onRefresh() {
		getMyComment(false);		
	}

	@Override
	public void onLoadMore() {
		
	}

}
