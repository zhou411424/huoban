package com.example.huoban.activity.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class AccountActivity extends BaseActivity implements OnRefreshListener, OnClickListener,DBOperaterInterFace {

	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private HuoBanApplication app;
	private List<Bill> billList = null;
	private MyAccountAdapter accountAdapter = null;
	private TextView tvAllAccount = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_partner_account);
		setupViews();
	}

	@Override
	protected void setupViews() {
		app = HuoBanApplication.getInstance();
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.account);

		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);

		ibtn = (ImageButton) findViewById(R.id.ibtn_right);
		ibtn.setOnClickListener(this);
		ibtn.setImageResource(R.drawable.red_searcho);
		ibtn.setVisibility(View.VISIBLE);

		RelativeLayout rlAdd = (RelativeLayout) findViewById(R.id.rl_remember_a);
		rlAdd.setOnClickListener(this);

		tvAllAccount = (TextView) findViewById(R.id.budget_amount);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);

		billList = new ArrayList<Bill>();
		accountAdapter = new MyAccountAdapter(this, billList);
		mListView.setAdapter(accountAdapter);
		mListView.setHeaderDividersEnabled(false);
		showProgress(null, R.string.waiting, false);
		operateDb(9, null);

	}
	
	private void operateDb(int id,Object object){
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		DataBaseManager.operateDataBase(this, dbParamData);
	}
	
	private void getAccount(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_ACCOUNT_LIST;
		task.resultDataClass = BillResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String imei = Utils.getDeviceId(this);
		String updateDate = res.getString(R.string.zero);
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
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
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
			setAllAccount();
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
			if(infoResult!=null&&infoResult.data!=null&&infoResult.data.contacter_list!=null){
				/**
				 * 设置头像
				 */
				for (Bill bill : list) {
					for (Contact contact : infoResult.data.contacter_list) {
						if(bill.user_id!=null&&bill.user_id.equals(contact.user_id)){
							bill.avatar = contact.avatar;
							break;
						}
					}
				}
				
			}
			
			
		}
	}

	private void setAllAccount() {
		long allAccount = 0;
		for (Bill bill : billList) {
			if (Utils.stringIsNotEmpty(bill.bill_amount)) {
				try {
					long current = Long.parseLong(bill.bill_amount);
					allAccount = allAccount + current;
				} catch (Exception e) {
				}
			}
		}
		
		String budget = StringConstant.ZERO;
		if(app.getInfoResult()!=null){
			UserInfoResult infoResult = app.getInfoResult();
			if(infoResult.data!=null&&infoResult.data.user_info!=null){
				budget = infoResult.data.user_info.budget;
			}
		}
		
		tvAllAccount.setText(StringConstant.YANG + allAccount+"/"+budget);
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	@Override
	public void onRefresh() {
		getAccount(false);

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			intent = new Intent(this, AccountSearchActivity.class);
			intent.putExtra("billList", (Serializable) billList);
			startActivityForResult(intent, 10089);
			break;
		case R.id.rl_remember_a:

			intent = new Intent(this, AddAcountActivity.class);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);

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
		} else if (arg0 == 10089 && arg1 == RESULT_OK) {
			List<Bill> list = (List<Bill>) arg2.getSerializableExtra("billList");
			if (list != null) {
				billList = list;
				accountAdapter.refresh(billList);
				setAllAccount();
			}
		}
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		
		Object object = null;
		
		switch (dbParamData.taskId) {
		case 9:
			/**
			 * 读取
			 */
			
			object = DBOperateUtils.readBillFromDb(this);
			break;
		case 10:
			/**
			 * 存储
			 */
			List<Bill> bills = (List<Bill>) dbParamData.object;
			DBOperateUtils.saveBillListToDb(this, bills);
			break;

		default:
			break;
		}
		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		switch (dbParamData.taskId) {
		case 9:
			List<Bill> bills = (List<Bill>) dbParamData.object;
			if(bills!=null){
				billList.addAll(bills);
				processData();
				accountAdapter.refresh(billList);
			}
			getAccount(false);
			break;

		default:
			break;
		}
		
	}
}
