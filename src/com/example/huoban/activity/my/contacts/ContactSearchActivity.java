package com.example.huoban.activity.my.contacts;

import java.io.Serializable;
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
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.model.contacts.ContactSearchReslut;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.ClearEditText;

public class ContactSearchActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ContactSearchActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private Task task = new Task();

	private String mSearch;

	private TextView dosearch, contacts, shaomiao;

	private ClearEditText searchEditText;

	private HashMap<String, String> map = new HashMap<String, String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_contacts_search);
		initTitleBar();
		initView();
	}

	private void initView() {
		searchEditText = (ClearEditText) findViewById(R.id.contacts_add_name);
		shaomiao = (TextView) findViewById(R.id.contacts_add_shaomiao);
		shaomiao.setOnClickListener(this);
		contacts = (TextView) findViewById(R.id.contacts_add_contacts);
		contacts.setOnClickListener(this);
		dosearch = (TextView) findViewById(R.id.contacts_add_do_search);
		dosearch.setOnClickListener(this);
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

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("添加好友");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		Intent mIntent = new Intent();
		ContactSearchReslut result = (ContactSearchReslut) task.result;
		dismissProgress();
		if (result.data != null && result.data.size() > 0) {
			mIntent.setClass(this, SearchResultActivity.class);

			ArrayList<Contact> mContacts = ((ContactSearchReslut) task.result).data;

			mIntent.putExtra("query", mSearch);
			LogUtil.logI(TAG, "query = " + mSearch);
			mIntent.putExtra("result", (Serializable) mContacts);

			startActivity(mIntent);
		} else {
			DialogUtils.oneButtonShow(this, "搜索结果", "该用户不存在\n请检查输入是否正确", null);
		}

	}

	protected void getDataFailed(Object... param) {

	}

	private void doSearch() {

		task.target = this;

		task.taskQuery = URLConstant.URL_SEARCH_CONTACT;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.resultDataClass = ContactSearchReslut.class;

		task.taskParam = getSearchParam();

		doTask(task);

	}

	public Object getSearchParam() {
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String search = mSearch;
		sb.append("search=" + search + "&");
		map.put("search", search);

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
		case R.id.contacts_add_do_search:
			mSearch = searchEditText.getText().toString();
			showProgress("正在查找", 0, false);
			doSearch();
			break;
		case R.id.contacts_add_contacts:
			break;
		case R.id.contacts_add_shaomiao:
			break;
		}
	}

}
