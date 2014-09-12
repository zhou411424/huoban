package com.example.huoban.activity.diary;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.MyRemindResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.MyDiaryItem;

public class MyDiaryActivity extends BaseActivity {
	private TextView tvTitle;
	private ImageButton ibBack;
	private ArrayList<MyDiaryItem> mItemViews = new ArrayList<MyDiaryItem>();
	private int[] mItemTitle = new int[] { R.string.my_remind,
			R.string.my_attention, R.string.my_comment };
	private int[] mItemIcon = new int[] { R.drawable.ico_remind,
			R.drawable.ico_attention, R.drawable.ico_comment };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_diary);
		setupViews();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getMyRemind(true);
	}
	
	/**
	 * 获取我的提醒
	 * */
	private void getMyRemind(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_MY_REMIND;
		task.resultDataClass = MyRemindResult.class;

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
		LogUtil.logE(sign);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, true);
		doTask(task);
	}

	@Override
	protected void setupViews() {
		// 设置标题
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.my_diary);

		// 返回按钮
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);

		MyDiaryItem myItemView = (MyDiaryItem) findViewById(R.id.my_item_diary_remind);
		mItemViews.add(myItemView);

		myItemView = (MyDiaryItem) findViewById(R.id.my_item_diary_attention);
		mItemViews.add(myItemView);

		myItemView = (MyDiaryItem) findViewById(R.id.my_item_diary_comment);
		mItemViews.add(myItemView);

		for (int i = 0; i < mItemViews.size(); i++) {
			mItemViews.get(i).getTitleTextView().setText(mItemTitle[i]);
			mItemViews.get(i).getIconImageView()
					.setBackgroundResource(mItemIcon[i]);
			mItemViews.get(i).setOnClickListener(onClickListener);
		}
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task=(Task) param[0];
		MyRemindResult result=(MyRemindResult) task.result;
		if(result.status ==1){
			mItemViews.get(0).getUpdateImageView().setVisibility(View.VISIBLE);//有提醒显示红点
		}else{
			mItemViews.get(0).getUpdateImageView().setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void getDataFailed(Object... param) {

	}


	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.ibtn_left:
				finish();
				break;
			case R.id.my_item_diary_remind://我的提醒
				intent = new Intent(MyDiaryActivity.this,
						MyRemindActivity.class);
				startActivity(intent);
				break;
			case R.id.my_item_diary_attention://我的关注
				intent = new Intent(MyDiaryActivity.this,
						MyAttentionActivity.class);
				startActivity(intent);
				break;
			case R.id.my_item_diary_comment://我的评论
				intent = new Intent(MyDiaryActivity.this,
						MyCommentActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

}
