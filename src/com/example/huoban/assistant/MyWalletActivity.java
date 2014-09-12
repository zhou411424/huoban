package com.example.huoban.assistant;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.adapter.WalletAdapter;
import com.example.huoban.assistant.model.ActivateMemberResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.Task;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.ToastUtil;
/**
 * 齐家钱包首页页面
 * @author cwchun.chen
 *
 */
public class MyWalletActivity extends BaseActivity {
	private static final int AUTO_ACTIVATE_MEMBER = 100;
	private ListView listView;
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		setupViews();
	}

	@Override
	protected void setupViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.wallet_title);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MyWalletActivity.this.finish();
			}});
		WalletAdapter walletAdapter = new WalletAdapter(this); 
		listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(walletAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//打开页面前先判断账号是否激活
				doCheckActivate();
				MyWalletActivity.this.position = position;
			}
		});
	}

	protected void switchTask(int position) {
		switch(position){
		case 0:	//订单明细
			ActivityUtils.gotoOtherActivity(this, OrderDetailsActivity.class);
			break;
		case 1:	//交易记录
			ActivityUtils.gotoOtherActivity(this, TransactionActivity.class);
			break;
		}
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case AUTO_ACTIVATE_MEMBER:
			ActivateMemberResult result = (ActivateMemberResult) task.result;
			if("0".equals(result.status)){
				ToastUtil.showToast(this, R.string.activate);
			}else{
				if("success".equals(result.msg)){
					String memberId = result.data.member_id;
					String balance = result.data.balance;
					HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_MEMBER_ID, memberId, this);
					HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_BALANCE, balance, this);
					switchTask(position);
				}else{
					ToastUtil.showToast(this, result.msg);
				}
			}
			break;
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 检查账户是否激活
	 */
	private void doCheckActivate(){
		Task task = TasksHelper.getAutoActivateMemberTask(this, AUTO_ACTIVATE_MEMBER);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
}
