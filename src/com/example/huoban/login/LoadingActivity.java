package com.example.huoban.login;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.push.PushModel;
import com.example.huoban.R;
import com.example.huoban.activity.HomeActivity;
import com.example.huoban.activity.question.FeaturedQuestionActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.SaltResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;

public class LoadingActivity extends BaseActivity {

	private HuoBanApplication app;
	private PushModel pushModel;
	private ImageView ivProgress;
	private TextView tvHint;
	private AnimationDrawable mAnimationDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		setupViews();
	}

	@Override
	protected void setupViews() {
		app = HuoBanApplication.getInstance();
		ivProgress = (ImageView) findViewById(R.id.tv2);
		tvHint = (TextView) findViewById(R.id.tv_hint);
		mAnimationDrawable = (AnimationDrawable) ivProgress.getBackground();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getSalt();
		initPushModel();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus&&mAnimationDrawable!=null){
			mAnimationDrawable.start();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mAnimationDrawable!=null){
			mAnimationDrawable.stop();
			mAnimationDrawable = null;
		}
	}
	private void initPushModel() {
		Intent intent = getIntent();
		if (intent != null) {
			pushModel = (PushModel) intent.getSerializableExtra("pushModel");
			app.setPushModel(pushModel);
		}
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		if (task.result instanceof SaltResult) {
			SaltResult saltResult = (SaltResult) task.result;
			app.saveSalt(saltResult, this);
			boolean isLogined = app.getIsLogined(this);
			if (isLogined) {
				/**
				 * 用户已经登录过,更新用户信息
				 */
				getUserInfo();

			} else {
				/**
				 * 进入登录流程
				 */
				Intent intent = new Intent(this, LoginAndRegisterActivity.class);
				startActivity(intent);
				finish();
			}
		} else if (task.result instanceof UserInfoResult) {
			UserInfoResult infoResult = (UserInfoResult) task.result;
			if ("success".equals(infoResult.msg)) {
				app.setInfoResult(infoResult);
				if (infoResult.data != null && infoResult.data.user_info != null) {

					app.saveTempToSharedPreferences("username", infoResult.data.user_info.user_name, this);
					app.saveTempToSharedPreferences("mobile", infoResult.data.user_info.mobile, this);
					app.saveTempToSharedPreferences("userid", infoResult.data.user_info.user_id, this);
				}

				Intent intent = null;
				if (pushModel != null) {
					/**
					 * 推送
					 */
					intent = new Intent(this, FeaturedQuestionActivity.class);
				} else {
					intent = new Intent(this, HomeActivity.class);

				}
				startActivity(intent);
				finish();
			}

		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		ivProgress.setVisibility(View.GONE);
		tvHint.setText(R.string.data_fail);
	}

	/**
	 * 获取盐值
	 */
	private void getSalt() {
		Task task = new Task();
		task.taskQuery = URLConstant.URL_GET_SALT;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		HashMap<String, String> map = new HashMap<String, String>();
		AuthInfo authInfo = new AuthInfo();
		authInfo.appkey = StringConstant.APP_KEY;
		authInfo.sessionid = "";
		authInfo.deviceid = Utils.getDeviceId(this);
		map.put("auth_info", Utils.objectToJson(authInfo));

		LogUtil.logE(authInfo.deviceid + "::::authInfo.deviceid");
		LogUtil.logE(Utils.objectToJson(authInfo));
		task.taskParam = map;
		task.resultDataClass = SaltResult.class;
		// showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 获取用户信息
	 */

	// private void getUserInfo() {
	// Task task = new Task();
	// task.target = this;
	// task.taskQuery = URLConstant.URL_GET_USER_INFO;
	// task.taskHttpTpye = HTTPConfig.HTTP_GET;
	// // String userid = app.getTempFromSharedPreferences("jia_user_id", this);
	// String username = app.getTempFromSharedPreferences("username", this);
	// // String mobile = app.getTempFromSharedPreferences("mobile", this);
	// // String imei = Utils.getDeviceId(this);
	// // String ip = Utils.readIp(this);
	// StringBuffer sb = new StringBuffer();
	// // sb.append("from=3");
	// // sb.append("&imei=");
	// // sb.append(imei);
	// // sb.append("&ip=");
	// // sb.append(ip);
	// // sb.append("&jia_user_id=");
	// // sb.append(userid);
	// // sb.append("&mobile=");
	// // sb.append(mobile);
	// sb.append("user_name=");
	// sb.append(username);
	// String sign = sb.toString();
	// sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
	// HashMap<String, String> map = new HashMap<String, String>();
	// // map.put("from", "3");
	// // map.put("imei", imei);
	// // map.put("ip", ip);
	// // map.put("jia_user_id", userid);
	// // map.put("mobile", mobile);
	// map.put("sign", sign);
	// map.put("user_name", username);
	// task.taskParam = map;
	// task.resultDataClass = UserInfoResult.class;
	// doTask(task);
	// }
	//
	private void getUserInfo() {
		Task task = new Task();
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_USER_INFO;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		String userid = app.getTempFromSharedPreferences("jia_user_id", this);
		String username = app.getTempFromSharedPreferences("username", this);
		String mobile = app.getTempFromSharedPreferences("mobile", this);
		String imei = Utils.getDeviceId(this);
		String ip = Utils.readIp(this);
		StringBuffer sb = new StringBuffer();
		sb.append("from=3");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&ip=");
		sb.append(ip);
		sb.append("&jia_user_id=");
		sb.append(userid);
		sb.append("&mobile=");
		sb.append(mobile);
		sb.append("&user_name=");
		sb.append(username);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("from", "3");
		map.put("imei", imei);
		map.put("ip", ip);
		map.put("jia_user_id", userid);
		map.put("mobile", mobile);
		map.put("sign", sign);
		map.put("user_name", username);
		task.taskParam = map;
		task.resultDataClass = UserInfoResult.class;
		doTask(task);
	}

}
