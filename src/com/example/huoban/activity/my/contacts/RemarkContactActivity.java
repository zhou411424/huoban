package com.example.huoban.activity.my.contacts;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class RemarkContactActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "RemarkContactActivity";

	private Intent mIntent = new Intent();

	private TextView tv_title;

	private ImageButton ib_back;

	private TextView tv_save;

	private EditText mEditText;

	private String newNickname;

	private HashMap<String, String> map = new HashMap<String, String>();

	private Task task = new Task();

	private Contact mContact;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		mIntent = getIntent();
		mContact = (Contact) mIntent.getSerializableExtra("contact");
		if (mContact == null) {
			return;
		}
		setContentView(R.layout.layout_user_info_nick_name);
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("设置备注");
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
		mEditText.setHint("请输入备注");
		mEditText.setText(mContact.remark_name);
		if (mContact.remark_name != null) {
			mEditText.setSelection(mContact.remark_name.length());
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
		mContact.remark_name = newNickname;
		Intent intent = new Intent();
		intent.putExtra("contact", mContact);
		setResult(RESULT_OK, mIntent);
		application.refreshNewRemakName(mContact, null);
		ToastUtil.showToast(this, "修改成功");
		finish();
	}

	protected void getDataFailed(Object... param) {

	}

	private void remarkContact() {
		task.target = this;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskQuery = URLConstant.URL_REMARK_CONTACT;

		task.resultDataClass = BaseResult.class;

		task.taskParam = getParam();

		doTask(task);
	}

	private HashMap<String, String> getParam() {
		map.clear();
		StringBuffer sb = new StringBuffer();

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		sb.append("nick_name=" + newNickname + "&");
		map.put("nick_name", newNickname);

		String relationid = mContact.relation_id;
		sb.append("relation_id=" + relationid + "&");
		map.put("relation_id", relationid);

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
			if (newNickname != null && !newNickname.equals(mContact.remark_name)) {
				remarkContact();
			} else {
				Toast.makeText(this, "未修改备注", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
