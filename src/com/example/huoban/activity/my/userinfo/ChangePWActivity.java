package com.example.huoban.activity.my.userinfo;

import java.util.HashMap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.userinfo.PassWordResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.ClearEditText;

public class ChangePWActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ChangePWActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private TextView tv_save;

	private ClearEditText mNewPw;

	private ClearEditText mReEnterPw;

	private String newPassword;

	private Task mTask = new Task();

	private HashMap<String, String> map = new HashMap<String, String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info_changepw);
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("修改密码");
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
		mNewPw = (ClearEditText) findViewById(R.id.user_info_new_pw);
		mReEnterPw = (ClearEditText) findViewById(R.id.user_info_re_enter_pw);
		mReEnterPw.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void afterTextChanged(Editable arg0) {
				if (arg0.toString().equals(mNewPw.getText().toString()) && arg0.toString().length() > 0) {
					mReEnterPw.setRadGou(true);
				} else {
					mReEnterPw.setRadGou(false);
				}
			}
		});
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
		Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	protected void getDataFailed(Object... param) {
		Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			savePassWord();
			break;
		}
	}

	private void savePassWord() {
		String mNewString = mNewPw.getText().toString();
		String mReEnterString = mReEnterPw.getText().toString();
		if (!mNewString.equals(mReEnterString)) {
			Toast.makeText(this, "两次输入必须相同", Toast.LENGTH_SHORT).show();
			return;
		} else {
			newPassword = mNewString;
		}
		upLoadData();
	}

	private void upLoadData() {

		mTask.target = this;

		mTask.taskQuery = URLConstant.URL_CHANGE_PASS_WORD;

		mTask.taskHttpTpye = HTTPConfig.HTTP_GET;

		mTask.resultDataClass = PassWordResult.class;

		mTask.taskParam = getParam(map);

		doTask(mTask);
	}

	private HashMap<String, String> getParam(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();
		String api_type = "up_psw";
		sb.append("api_type=" + api_type + "&");
		map.put("api_type", api_type);

		String id = application.getUserId(this);
		sb.append("id=" + id + "&");
		map.put("id", id);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String mobile = application.getMobile(this);
		sb.append("mobile=" + mobile + "&");
		map.put("mobile", mobile);

		String newPassword = this.newPassword;
		sb.append("newPassword=" + newPassword);
		map.put("newPassword", newPassword);

		String sign = MD5Util.getMD5String(sb.toString() + MD5Util.MD5KEY);
		map.put("sign", sign);

		return map;
	}
}
