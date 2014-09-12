package com.example.huoban.activity.my.userinfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.userinfo.SexResult;
import com.example.huoban.model.userinfo.SyncQOResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.RSAUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class SexSetActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "SexSetActivity";

	private final static String[] SEX = { "保密", "男", "女" };

	private Intent mIntent = new Intent();

	private TextView tv_title;

	private ImageButton ib_back;

	private TextView tv_save;

	private HashMap<String, String> map = new HashMap<String, String>();

	private ArrayList<RelativeLayout> mRelativeLayouts = new ArrayList<RelativeLayout>();

	private int saveSex = -1;

	private Task task = new Task();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info_sex_set);
		initView();
	}

	private void saveData() {

		task.target = this;

		task.resultDataClass = SexResult.class;

		task.taskQuery = URLConstant.URL_EDIT_USER_INFO;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskParam = getParam(map);

		doTask(task);
	}

	private HashMap<String, String> getParam(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();
		String data = saveSex + "";
		sb.append("data=" + data + "&");
		map.put("data", data);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String type = 3 + "";
		sb.append("type=" + type + "&");
		map.put("type", type);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("修改性别");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

		tv_save = (TextView) findViewById(R.id.tv_right);
		tv_save.setText("保存");
		tv_save.setVisibility(View.VISIBLE);
		tv_save.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mRelativeLayouts.add((RelativeLayout) findViewById(R.id.sex_set_secret));
		mRelativeLayouts.add((RelativeLayout) findViewById(R.id.sex_set_man));
		mRelativeLayouts.add((RelativeLayout) findViewById(R.id.sex_set_woman));
		int i = 0;
		for (RelativeLayout iRelativeLayout : mRelativeLayouts) {
			((TextView) iRelativeLayout.findViewById(R.id.sex_set_item_title)).setText(SEX[i++]);
			iRelativeLayout.setOnClickListener(this);
		}
		updateUI(Integer.parseInt(application.getInfoResult().data.user_info.sex));
	}

	/**
	 * 
	 * @param n
	 *            0保密 1男 2女
	 */
	private void updateUI(int n) {
		for (int i = 0; i < 3; i++) {
			RelativeLayout iRelativeLayout = mRelativeLayouts.get(i);
			if (i == n) {
				iRelativeLayout.findViewById(R.id.sex_set_item_status).setVisibility(View.VISIBLE);
				saveSex = i;
			} else {
				iRelativeLayout.findViewById(R.id.sex_set_item_status).setVisibility(View.GONE);
			}
		}
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

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		dismissProgress();
		if (task.result instanceof SexResult) {
			syncUserSex(saveSex + "");
		} else if (task.result instanceof SyncQOResult) {
			if ("200".equals(((SyncQOResult) task.result).msg_encrypted.statusCode)) {
				application.getInfoResult().data.user_info.sex = saveSex + "";
				mIntent.putExtra("resultStr", SEX[saveSex]);
				setResult(1, mIntent);
				ToastUtil.showToast(this, "同步成功");
				finish();
			} else {
				ToastUtil.showToast(this, "同步失败");
			}
		}
	}

	private void syncUserSex(String gender) {
		Task task = new Task();

		task.target = this;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		try {
			task.taskParam = getParam(gender);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		task.resultDataClass = SyncQOResult.class;

		task.taskQuery = URLConstant.URL_SYNC_USER_SEX;

		showProgress("正在同步", 0, false);
		doTask(task);
	}

	protected Object getParam(String gender) throws UnsupportedEncodingException {

		String auth_info = Utils.objectToJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("encrypt_method", "RSA");

		Msg msgPlaintText = new Msg();
		msgPlaintText.app_id = "101";
		msgPlaintText.user_id = application.getInfoResult().data.user_info.jia_user_id;
		msgPlaintText.gender = gender;

		byte[] msg_encrypted_byte = RSAUtil.encryptByPublicKey(Utils.objectToJson(msgPlaintText).getBytes("UTF-8"), application.getSalt(this).public_key);
		String msg_encrypted = Base64.encodeToString(msg_encrypted_byte, Base64.DEFAULT);

		map.put("msg_encrypted", msg_encrypted);
		map.put("timestamp", Utils.getTimeStamp());
		return map;
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.sex_set_secret:
			updateUI(0);
			break;
		case R.id.sex_set_man:
			updateUI(1);
			break;
		case R.id.sex_set_woman:
			updateUI(2);
			break;
		case R.id.tv_right:
			/**
			 * 
			 */
			saveData();
			break;
		}
	}

	static class Msg {
		public String app_id;
		public String user_id;
		public String gender;
	}
}
