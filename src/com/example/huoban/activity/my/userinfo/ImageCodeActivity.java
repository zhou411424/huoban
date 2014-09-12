package com.example.huoban.activity.my.userinfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.CreateQRUtil;
import com.example.huoban.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageCodeActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ImageCodeActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private ImageView ivImageCode;

	private ImageView ivUserIcon;

	private TextView tvUserName;

	private ImageLoader mImageLoader = ImageLoader.getInstance();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info_image_code);
		initTitleBar();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("我的二维码");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

		ivImageCode = (ImageView) findViewById(R.id.image_code_image_code);
		ivUserIcon = (ImageView) findViewById(R.id.image_code_user_icon);
		tvUserName = (TextView) findViewById(R.id.image_code_user_name);

		// 显示用户二维码
		String userInfo = application.getMobile(this);
		CreateQRUtil.createQRImage(userInfo, ivImageCode);

		// 显示用户像
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.my_user_icon_default).showImageOnFail(R.drawable.my_user_icon_default).cacheInMemory(true).cacheOnDisc(true).build();
		mImageLoader.displayImage(application.getInfoResult().data.user_info.avatar, ivUserIcon, options);

		// 显示用户名
		tvUserName.setText(application.getUserName(this));

	}

	public void onResume() {
		super.onResume();
		LogUtil.logI(TAG, "onResume");
	}

	public void onPause() {
		super.onPause();
		LogUtil.logI(TAG, "onPause");
	}

	public void onStop() {
		super.onStop();
		LogUtil.logI(TAG, "onStop");
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		}
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}
}
