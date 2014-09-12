package com.example.huoban.activity.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.adapter.MyAccountAdapter;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.Bill;
import com.example.huoban.model.BillResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class AccountSearchActivity extends BaseActivity implements OnClickListener, TextWatcher, DBOperaterInterFace {

	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private HuoBanApplication app;
	private List<Bill> billList = null;
	private EditText et = null;
	private MyAccountAdapter accountAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_search);
		setupViews();
	}

	@Override
	protected void setupViews() {

		billList = (List<Bill>) getIntent().getSerializableExtra("billList");
		if (billList == null) {
			billList = new ArrayList<Bill>();
		}

		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		TextView tvRight = (TextView) findViewById(R.id.tv_right);
		tvRight.setOnClickListener(this);
		et = (EditText) findViewById(R.id.et_search);
		et.addTextChangedListener(this);
		et.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					/**
					 * 键盘确定键为搜索
					 */
					accountAdapter.getFilter().filter(et.getText().toString().trim());
				}
				return false;
			}
		});
		app = HuoBanApplication.getInstance();
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setRefreshEnable(false);
		accountAdapter = new MyAccountAdapter(this, billList);
		accountAdapter.setOriginalValues(billList);
		mListView.setAdapter(accountAdapter);
		mListView.setHeaderDividersEnabled(false);
	}

	private void operateDb(int id, Object object) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		BillResult billResult = (BillResult) task.result;
		if (billResult.data != null && billResult.data.bill_res != null) {
			billList.clear();
			billList.addAll(billResult.data.bill_res);
			processData();
			accountAdapter.refresh(billList);
			accountAdapter.setOriginalValues(billList);
			operateDb(10, billList);
		}

	}

	/**
	 * 本地筛除被删除的Bill
	 */
	private void processData() {
		if (billList.size() > 0) {
			List<Bill> list = new ArrayList<Bill>();
			for (Bill bill : billList) {
				if (StringConstant.Three.equals(bill.status)) {
					list.add(bill);
				}
			}
			billList.removeAll(list);
			UserInfoResult infoResult = app.getInfoResult();
			if (infoResult != null && infoResult.data != null && infoResult.data.contacter_list != null) {
				/**
				 * 设置头像
				 */
				for (Bill bill : list) {
					for (Contact contact : infoResult.data.contacter_list) {
						if (bill.user_id != null && bill.user_id.equals(contact.user_id)) {
							bill.avatar = contact.avatar;
							break;
						}
					}
				}

			}
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	private void setBackResultData() {
		Intent intent = new Intent();
		intent.putExtra("billList", (Serializable) billList);
		setResult(RESULT_OK, intent);
	}

	@Override
	public void onBackPressed() {
		setBackResultData();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_right:
			setBackResultData();
			finish();
			break;
		case R.id.ibtn_left:
			accountAdapter.getFilter().filter(et.getText().toString().trim());
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == RESULT_FIRST_USER && arg1 == RESULT_OK) {
			getAccount(true);
		}
	}

	private void getAccount(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_ACCOUNT_LIST;
		task.resultDataClass = BillResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String imei = Utils.getDeviceId(this);
		String updateDate = app.getTempFromSharedPreferences("bill_time", this);
		if (!Utils.stringIsNotEmpty(updateDate))
			;
		{
			updateDate = res.getString(R.string.zero);
		}
		String user_id = app.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&sync=");
		sb.append(updateDate);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("imei", imei);
		map.put("sign", sign);
		map.put("sync", updateDate);
		map.put("user_id", user_id);
		// sb = dataManager.jsonPut(sb, param, "imei",
		// dataManager.getPhoneDeviceId());
		// // sb = dataManager.jsonPut(sb, param, "page", page + "");
		// // if (!search.equals("")) {
		// // sb = dataManager.jsonPut(sb, param, "search", search + "");
		// // }
		// String updateDate = "0";
		// if (!dataManager.readTempData("bill_time").equals("0")) {
		// updateDate = dataManager.readTempData("bill_time");
		// }
		//
		// sb = dataManager.jsonPut(sb, param, "sync", updateDate);
		// sb = dataManager.jsonPut(sb, param, "user_id", userid + "");
		// String sign = sb.toString();
		// sign = sign.substring(0, sign.length() - 1);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		accountAdapter.getFilter().filter(s);

	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		/**
		 * 存储
		 */
		List<Bill> bills = (List<Bill>) dbParamData.object;
		DBOperateUtils.saveBillListToDb(this, bills);
		return null;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		// TODO Auto-generated method stub

	}
}
