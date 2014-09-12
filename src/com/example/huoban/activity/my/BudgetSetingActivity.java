package com.example.huoban.activity.my;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class BudgetSetingActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "BudgetSetingActivity";
	private TextView tv_title;
	private ImageButton ib_back;
	private TextView tv_save;

	private EditText budget;

	private String number;

	private HashMap<String, String> map = new HashMap<String, String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_treasure_budget_seting);
		initTitleBar();
		initView();
	}

	private void initView() {
		budget = (EditText) findViewById(R.id.budget_seting_budget);
		if (application.getInfoResult().data.user_info.budget != null) {
			budget.setText(application.getInfoResult().data.user_info.budget);
			budget.setSelection(application.getInfoResult().data.user_info.budget.length());
		}
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("预算设置");
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

	private void budgetSetting() {

		Task task = new Task();

		task.target = this;

		task.taskQuery = URLConstant.URL_SET_BUDGET;

		task.taskParam = getParam();

		task.resultDataClass = BaseResult.class;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		showProgress("设置中...", 0, false);
		doTask(task);
	}

	public HashMap<String, String> getParam() {
		StringBuffer sb = new StringBuffer();
		String budget = number;
		sb.append("budget=" + budget + "&");
		map.put("budget", budget);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		dismissProgress();
		application.getInfoResult().data.user_info.budget = number;
		ToastUtil.showToast(this, "设置成功");
		finish();
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			number = budget.getText().toString();
			if (Utils.checkNumber(number)) {
				budgetSetting();
			} else {
				ToastUtil.showToast(this, "输入不正确，请输入数字！");
			}
			break;
		}
	}
}
