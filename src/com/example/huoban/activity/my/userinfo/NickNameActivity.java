package com.example.huoban.activity.my.userinfo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.userinfo.NickNameResult;
import com.example.huoban.model.userinfo.SyncQOResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.RSAUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class NickNameActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "NickNameActivity";

	private Intent mIntent = new Intent();

	private TextView tv_title;

	private ImageButton ib_back;

	private TextView tv_save;

	private EditText mEditText;

	private String newNickname;

	private HashMap<String, String> map = new HashMap<String, String>();

	private Task task = new Task();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info_nick_name);
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("修改昵称");
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

		mEditText = (EditText) findViewById(R.id.user_info_nick_name);
		mEditText.setHint(R.string.user_info_name);
		mEditText.setText(application.getInfoResult().data.user_info.nick);
		mEditText.setSelection(application.getInfoResult().data.user_info.nick.length());
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

	private void syncNickName(String nickname) {
		Task task = new Task();

		task.target = this;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		try {
			task.taskParam = getParam(nickname);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		task.resultDataClass = SyncQOResult.class;

		task.taskQuery = URLConstant.URL_SYNC_USER_NICK;

		showProgress("正在同步", 0, false);
		doTask(task);
	}

	protected Object getParam(String nickname) throws UnsupportedEncodingException {

		String auth_info = Utils.objectToJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("encrypt_method", "RSA");

		Msg msgPlaintText = new Msg();
		msgPlaintText.app_id = "101";
		msgPlaintText.user_id = application.getInfoResult().data.user_info.jia_user_id;
		System.out.println("jia_user_id = " + msgPlaintText.user_id);
		msgPlaintText.nick_name = nickname;

		byte[] msg_encrypted_byte = RSAUtil.encryptByPublicKey(Utils.objectToJson(msgPlaintText).getBytes("UTF-8"), application.getSalt(this).public_key);
		String msg_encrypted = Base64.encodeToString(msg_encrypted_byte, Base64.DEFAULT);

		map.put("msg_encrypted", msg_encrypted);
		map.put("timestamp", Utils.getTimeStamp());
		return map;
	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.result instanceof NickNameResult) {
			// 更新完成后同步到Q-open
			System.out.println("nickname = " + newNickname);

			syncNickName(newNickname);
		} else if (task.result instanceof SyncQOResult) {
			if ("200".equals(((SyncQOResult) task.result).msg_encrypted.statusCode)) {
				application.getInfoResult().data.user_info.nick = newNickname;
				mIntent.putExtra("resultStr", newNickname);
				ToastUtil.showToast(this, "同步成功");
				setResult(1, mIntent);
				finish();
			} else {
				ToastUtil.showToast(this, "同步失败");
			}
		}
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	private void upLoadData() {
		task.target = this;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskQuery = URLConstant.URL_CHANGE_NICK_NAME;

		task.resultDataClass = NickNameResult.class;

		task.taskParam = getParam();

		doTask(task);
	}

	private Object getParam() {
		map.clear();
		StringBuffer sb = new StringBuffer();

		String data = newNickname;
		sb.append("data=" + data + "&");
		map.put("data", data);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String type = "" + 2;
		sb.append("type=" + type + "&");
		map.put("type", type);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			newNickname = mEditText.getText().toString();
			if (!newNickname.equals(application.getInfoResult().data.user_info.nick)) {
				upLoadData();
			} else {
				Toast.makeText(this, "未修改昵称", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	static class Msg {
		public String app_id;
		public String user_id;
		public String nick_name;
	}
}
