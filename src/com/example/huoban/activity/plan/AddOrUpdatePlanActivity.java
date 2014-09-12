package com.example.huoban.activity.plan;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.Plan;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.time.YMDTimeUtil;
import com.example.huoban.widget.time.YMDTimeUtil.OnDateTimeSetListener;

public class AddOrUpdatePlanActivity extends BaseActivity implements
		OnClickListener {

	private EditText etContent;
	private EditText etRemark;
	private TextView tvTime;
	private Plan plan;
	private AlertDialog alertDialog;
	private YMDTimeUtil ymdTimeUtil;

	private HuoBanApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_plan);
		setupViews();
	}

	@Override
	protected void setupViews() {
		app = HuoBanApplication.getInstance();
		plan = (Plan) getIntent().getSerializableExtra("plan");

		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);
		TextView tvRight = (TextView) findViewById(R.id.tv_right);
		tvRight.setOnClickListener(this);
		tvRight.setText(R.string.save);

		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvTime.setOnClickListener(this);
		etContent = (EditText) findViewById(R.id.et_content);
		etRemark = (EditText) findViewById(R.id.et_remark);

		if (plan != null) {
			/**
			 * 修改
			 */
			tvTitle.setText(R.string.modify_plan);
			etContent.setText(plan.plan_content);
			etRemark.setText(plan.remark);
			if (Utils.stringIsNotEmpty(plan.plan_done_date)
					&& !StringConstant.ZERO.equals(plan.plan_done_date)) {
				tvTime.setText(TimeFormatUtils
						.formatLongToDate(plan.plan_done_date));
				tvTime.setTag(plan.plan_done_date);
			}
		} else {
			tvTitle.setText(R.string.add_plan);
		}
		
		// 助手添加计划
		String content = getIntent().getStringExtra("content");
		if (content != null) {
			etContent.setText(content);
		}

	}

	private void addOrUpdatePlan() {
		String content = etContent.getText().toString();
		String remark = etRemark.getText().toString();
		String date = StringConstant.ZERO;
		if (tvTime.getTag() != null) {
			date = tvTime.getTag().toString();
		}
		if (!Utils.stringIsNotEmpty(content)) {
			ToastUtil.showToast(this, R.string.add_new_account_msg,
					Gravity.CENTER);
			return;
		}
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		String plan_id = null;
		if (plan != null) {
			/**
			 * 修改计划
			 */
			task.taskQuery = URLConstant.URL_MODIFY_PLAN;
			plan_id = plan.plan_id;
		} else {
			/**
			 * 添加计划
			 */
			task.taskQuery = URLConstant.URL_ADD_NEW_PLAN;
		}

		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		if (StringConstant.ZERO.equals(date)) {
			sb.append("auto_complete=2");
			map.put("auto_complete", "2");
		} else {
			sb.append("auto_complete=1");
			sb.append("&done_date=");
			sb.append(date);
			map.put("auto_complete", "1");
			map.put("done_date", date);
		}
		String imei = Utils.getDeviceId(this);
		sb.append("&imei=");
		sb.append(imei);
		map.put("imei", imei);
		sb.append("&plan_content=");
		sb.append(content);
		map.put("plan_content", content);

		if (plan_id != null) {
			sb.append("&plan_id=");
			sb.append(plan_id);
			map.put("plan_id", plan_id);
		}

		if (Utils.stringIsNotEmpty(remark)) {
			sb.append("&remark=");
			sb.append(remark);
			map.put("remark", remark);
		}
		String user_id = app.getUserId(this);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("sign", sign);
		map.put("user_id", user_id);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (plan == null) {

			ToastUtil
					.showToast(this, R.string.add_plan_success, Gravity.CENTER);
		} else {
			ToastUtil.showToast(this, R.string.modify_plan_success,
					Gravity.CENTER);
		}
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		hideSoftInput();
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			addOrUpdatePlan();
			break;
		case R.id.tv_time:
			timeClick();
			break;

		default:
			break;
		}

	}

	private void timeClick() {
		String time = tvTime.getText().toString();
		if (Utils.stringIsNotEmpty(time)) {
			String[] items = res.getStringArray(R.array.date_item);
			alertDialog = new AlertDialog.Builder(this).setItems(items,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:

								break;
							case 1:
								timeChoise();
								break;
							case 2:
								tvTime.setText(StringConstant.EMPTY_DEFAULT);
								tvTime.setTag(0);
								break;

							default:
								break;
							}
							alertDialog.dismiss();
						}
					}).show();
		} else {
			timeChoise();
		}
	}

	private void timeChoise() {
		if (ymdTimeUtil == null) {
			ymdTimeUtil = new YMDTimeUtil(this, tvTime.getText().toString(),
					new OnDateTimeSetListener() {

						@Override
						public void onDateTimeSet(int year, int monthOfYear,
								int dayOfMonth) {
							tvTime.setText(monthOfYear + "-" + dayOfMonth);
							tvTime.setTag(TimeFormatUtils
									.formatYMD2Seconds(year + "-" + monthOfYear
											+ "-" + dayOfMonth));
						}
					});
		}
		ymdTimeUtil.show();
	}
}
