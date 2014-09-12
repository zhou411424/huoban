package com.example.huoban.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.account.AccountActivity;
import com.example.huoban.activity.plan.PlanActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.LogUtil;

public class TreasureBoxActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "TreasureBoxActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private RelativeLayout plan, bill, budget;
	private Intent mIntent = new Intent();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_treasure_box);
		initView();
	}

	private void initTitlebar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("百宝箱");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
	}

	private void initView() {
		initTitlebar();

		plan = (RelativeLayout) findViewById(R.id.treasure_box_plan);
		plan.setOnClickListener(this);
		((TextView) plan.findViewById(R.id.treasure_box_item_title)).setText("计划");

		bill = (RelativeLayout) findViewById(R.id.treasure_box_bill);
		bill.setOnClickListener(this);
		((TextView) bill.findViewById(R.id.treasure_box_item_title)).setText("记账");

		budget = (RelativeLayout) findViewById(R.id.treasure_budget_setting);
		budget.setOnClickListener(this);
		((TextView) budget.findViewById(R.id.treasure_box_item_title)).setText("预算设置");
		((TextView) budget.findViewById(R.id.treasure_box_item_desc)).setVisibility(View.GONE);
		if (application.getPartnerNewsResult() == null)
			return;
		if (application.getPartnerNewsResult().data == null)
			return;
		if (application.getPartnerNewsResult().data.plan != null && application.getPartnerNewsResult().data.plan.content != null) {
			((TextView) plan.findViewById(R.id.treasure_box_item_desc)).setText(application.getPartnerNewsResult().data.plan.content);
		} else {
			((TextView) plan.findViewById(R.id.treasure_box_item_desc)).setVisibility(View.GONE);
		}
		if (application.getPartnerNewsResult().data.bill != null && application.getPartnerNewsResult().data.bill.content != null) {
			((TextView) bill.findViewById(R.id.treasure_box_item_desc)).setText(application.getPartnerNewsResult().data.bill.content);
		} else {
			((TextView) bill.findViewById(R.id.treasure_box_item_desc)).setVisibility(View.GONE);
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.treasure_box_plan:
			mIntent.setClass(this, PlanActivity.class);
			startActivity(mIntent);
			break;
		case R.id.treasure_box_bill:
			mIntent.setClass(this, AccountActivity.class);
			startActivity(mIntent);
			break;
		case R.id.treasure_budget_setting:
			mIntent.setClass(this, BudgetSetingActivity.class);
			startActivity(mIntent);
			break;
		default:
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
