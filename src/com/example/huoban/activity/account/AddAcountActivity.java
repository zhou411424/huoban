package com.example.huoban.activity.account;

import java.util.Date;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.Bill;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.time.YMDTimeUtil;
import com.example.huoban.widget.time.YMDTimeUtil.OnDateTimeSetListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 添加记账和修改记账页面
 * 
 * @author albel.lei
 * 
 */
public class AddAcountActivity extends BaseActivity implements OnClickListener {

	private EditText etMoney, etContent, etRemark;
	private TextView tvDate;
	private Button btnDel;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private HuoBanApplication app = HuoBanApplication.getInstance();
	private YMDTimeUtil ymdTimeUtil = null;
	private boolean isModifyAccount;
	private Bill bill;
	private DisplayImageOptions optionsHead = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.circle_default_avatar).showImageOnFail(R.drawable.circle_default_avatar).showImageOnLoading(R.drawable.circle_default_avatar).cacheInMemory(true).cacheOnDisc(true).build();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);
		setupViews();
	}

	@Override
	protected void setupViews() {
		Intent intent = getIntent();
		bill = (Bill) intent.getSerializableExtra("bill");
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setVisibility(View.VISIBLE);
		ibtn.setOnClickListener(this);
		TextView tvRight = (TextView) findViewById(R.id.tv_right);
		tvRight.setText(R.string.save);
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setOnClickListener(this);
		etMoney = (EditText) findViewById(R.id.edMoney);
		etContent = (EditText) findViewById(R.id.edProject);
		etRemark = (EditText) findViewById(R.id.edRemark);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvDate.setOnClickListener(this);

		btnDel = (Button) findViewById(R.id.btnDel);
		btnDel.setOnClickListener(this);
		ImageView iv = (ImageView) findViewById(R.id.ivUser);
		String bill_date = null;
		if (bill != null) {
			if (Utils.stringIsNotEmpty(bill.bill_amount)) {

				etMoney.setText(bill.bill_amount);
				etMoney.setSelection(bill.bill_amount.length());
			}
			etContent.setText(bill.bill_content);
			etRemark.setText(bill.bill_remark);
			bill_date = bill.create_date;
			if (bill.isCreateFromPlan) {
				/**
				 * 表示从计划跳转创建
				 */
				tvTitle.setText(R.string.add_account);
			} else {
				/**
				 * 表示修改或者查看账目
				 */
				if (getWindow() != null)
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				isModifyAccount = true;
				tvTitle.setText(R.string.modify_account);
				btnDel.setVisibility(View.VISIBLE);
				etMoney.clearFocus();
				
				
			}
		} else {
			/**
			 * 表示添加账目
			 */
			tvTitle.setText(R.string.add_account);
		}
		if (Utils.stringIsNotEmpty(bill_date)) {
			tvDate.setText(TimeFormatUtils.formatLongToDateYMD(bill_date));
		} else {
			Date date = new Date();
			tvDate.setText(TimeFormatUtils.Y_M_D.format(date));
		}

		if (bill != null && bill.user_id != null && !bill.user_id.equals(application.getUserId(this))) {
			imageLoader.displayImage(bill.avatar, iv,optionsHead);
		} else {
			UserInfoResult infoResult = app.getInfoResult();
			if (infoResult.data != null && infoResult.data.user_info != null && infoResult.data.user_info.avatar != null) {
				imageLoader.displayImage(infoResult.data.user_info.avatar, iv,optionsHead);
			}
		}
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.taskID == 108) {
			ToastUtil.showToast(this, R.string.del_success, Gravity.CENTER);
		} else if (task.result instanceof BaseResult) {
			if (isModifyAccount) {
				ToastUtil.showToast(this, R.string.modify_account_success, Gravity.CENTER);
			} else {
				ToastUtil.showToast(this, R.string.add_account_success, Gravity.CENTER);
			}
		}
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	/**
	 * 删除账户
	 */
	private void delAccount() {
		Task task = new Task();
		task.taskID = 108;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_DEL_ACCOUNT;
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		String bill_id = bill.bill_id;
		String imei = Utils.getDeviceId(this);
		String user_id = app.getUserId(this);
		sb.append("bill_id=");
		sb.append(bill_id);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("bill_id", bill_id);
		map.put("imei", imei);
		map.put("sign", sign);
		map.put("user_id", user_id);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
		// StringBuffer sb = new StringBuffer();
		// sb = dataManager.jsonPut(sb, param, "bill_id",
		// billId + "");
		// sb = dataManager.jsonPut(sb, param, "imei",
		// dataManager.getPhoneDeviceId());
		// sb = dataManager.jsonPut(sb, param, "user_id", userid + "");
		// String sign = sb.toString();
		// sign = sign.substring(0, sign.length() - 1);
		// param.put("sign", MD5Util.MD5(sign + MD5KEY));
	}

	/**
	 * 添加或修改账户
	 */
	private void addOrUpdateAccount() {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		if (isModifyAccount) {
			task.taskQuery = URLConstant.URL_UPDATE_ACCOUNT;
		} else {

			task.taskQuery = URLConstant.URL_ADD_ACCOUNT;
		}
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String bill_amount = etMoney.getText().toString();
		String bill_content = etContent.getText().toString();
		String bill_remark = etRemark.getText().toString();
		String date = tvDate.getText().toString();

		String hint = null;

		if (!Utils.stringIsNotEmpty(bill_amount)) {
			hint = res.getString(R.string.account_bill_can_not_null);
		} else if (!Utils.stringIsNotEmpty(bill_content)) {
			hint = res.getString(R.string.account_content_can_not_null);
		}
		if (hint != null) {
			ToastUtil.showToast(this, hint, Gravity.CENTER);
			return;
		}
		String imei = Utils.getDeviceId(this);
		String user_id = app.getUserId(this);
		StringBuffer sb = new StringBuffer();
		sb.append("bill_amount=");
		sb.append(bill_amount);
		sb.append("&bill_content=");
		sb.append(bill_content);
		sb.append("&bill_date=");
		sb.append(date);
		if (isModifyAccount) {
			sb.append("&bill_id=");
			sb.append(bill.bill_id);
		}

		if (Utils.stringIsNotEmpty(bill_remark)) {
			sb.append("&bill_remark=");
			sb.append(bill_remark);
		}
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("bill_amount", bill_amount);
		map.put("bill_content", bill_content);
		map.put("bill_date", date);
		if (isModifyAccount) {
			map.put("bill_id", bill.bill_id);
		}
		if (Utils.stringIsNotEmpty(bill_remark)) {
			map.put("bill_remark", bill_remark);
		}
		map.put("imei", imei);
		map.put("sign", sign);
		map.put("user_id", user_id);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			hideSoftInput();
			finish();
			break;
		case R.id.tv_right:
			hideSoftInput();
			addOrUpdateAccount();
			break;
		case R.id.btnDel:

			DialogUtils.twoButtonShow(this, 0, R.string.is_del_this_account, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					delAccount();

				}
			}, null);

			break;
		case R.id.tvDate:
			/**
			 * 时间选择
			 */
			if (ymdTimeUtil == null) {
				ymdTimeUtil = new YMDTimeUtil(this, tvDate.getText().toString(), new OnDateTimeSetListener() {

					@Override
					public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth) {
						StringBuffer sb = new StringBuffer();
						sb.append(year);
						sb.append("-");
						sb.append(monthOfYear);
						sb.append("-");
						sb.append(dayOfMonth);
						tvDate.setText(sb.toString());

					}
				});
			}
			ymdTimeUtil.show();
			break;

		default:
			break;
		}

	}

}
