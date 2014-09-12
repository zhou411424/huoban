package com.example.huoban.login;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.HomeActivity;
import com.example.huoban.activity.question.FeaturedQuestionActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.NumberConstant;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.CheckLoginResult;
import com.example.huoban.model.ForgetPassResult;
import com.example.huoban.model.LoginResult;
import com.example.huoban.model.RegistCodeResult;
import com.example.huoban.model.RegistResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.UserSatusData;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.ResizeLayout;

public class LoginAndRegisterActivity extends BaseActivity implements OnClickListener {

	private TextView tvInputHint, tvHttpHint, tvNext, tvContentHint, tvForgetPassOrRedo;
	private EditText et;
	private ImageView ivBack, ivDel;

	private String mobile, userName, password, forword, userMsg;
	private HuoBanApplication app = null;
	private boolean isCheckedForRegister;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case View.GONE:
				findViewById(R.id.rl_head_image).setVisibility(View.GONE);
				break;
			case View.VISIBLE:
				findViewById(R.id.rl_head_image).setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logE("LoginAndRegisterActivity+onCreate");
		setContentView(R.layout.activity_login_and_register);
		setupViews();
	}

	public void onResume() {
		LogUtil.logE("LoginAndRegisterActivity+onResume");
		super.onResume();
	}

	protected void onDestroy() {
		LogUtil.logE("LoginAndRegisterActivity+onDestroy");
		super.onDestroy();
	}

	protected void onRestart() {
		LogUtil.logE("LoginAndRegisterActivity+onRestart");
		super.onRestart();
	}

	protected void setupViews() {

		app = HuoBanApplication.getInstance();

		tvInputHint = (TextView) findViewById(R.id.tv_hinta);
		tvHttpHint = (TextView) findViewById(R.id.tv_http_hint);
		tvNext = (TextView) findViewById(R.id.tv_next);
		tvContentHint = (TextView) findViewById(R.id.tv_input_msg_show);
		ivBack = (ImageView) findViewById(R.id.login_back);
		ivDel = (ImageView) findViewById(R.id.iv_del);
		tvForgetPassOrRedo = (TextView) findViewById(R.id.tv_forget_or_redo);
		ivBack.setOnClickListener(this);
		tvNext.setOnClickListener(this);
		ivDel.setOnClickListener(this);
		tvForgetPassOrRedo.setOnClickListener(this);

		et = (EditText) findViewById(R.id.et);
		et.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void afterTextChanged(Editable s) {

				if (s.length() > 0) {
					if (ivDel.getVisibility() != View.VISIBLE) {
						ivDel.setVisibility(View.VISIBLE);
					}
				} else {
					ivDel.setVisibility(View.INVISIBLE);
				}
			}
		});

		et.setHint(R.string.login_input_username_or_mobile);
		initListener();
	}

	private void initListener() {
		ResizeLayout layout = (ResizeLayout) this.findViewById(R.id.login_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void resetLayout(boolean changed, int l, int t, int r, int b) {
				int height = getWindowHeight();
				if (b > height - 200) {
					mHandler.sendEmptyMessage(View.VISIBLE);

				} else {
					mHandler.sendEmptyMessage(View.GONE);
				}

			}
		});

		// layout.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// hideSoftInput();
		// break;
		// default:
		// break;
		// }
		// return false;
		// }
		// });
	}

	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		tvHttpHint.setText(StringConstant.EMPTY_DEFAULT);
		if (task.result instanceof CheckLoginResult) {
			showSoft(true);
			CheckLoginResult checkLoginResult = (CheckLoginResult) task.result;
			ivBack.setVisibility(View.VISIBLE);
			if ("success".equals(checkLoginResult.msg)) {
				if (Utils.stringIsNotEmpty(checkLoginResult.mobile))
					app.saveTempToSharedPreferences("mobile", checkLoginResult.mobile, this);

				/**
				 * 账号验证成功！已存在
				 */
				if (isCheckedForRegister) {
					/**
					 * 注册流程 验证用户名或者手机号是否存在 success表示用户已存在 提示用户已存在
					 */
					ToastUtil.showToast(this, R.string.login_usrename_mobile_error, Gravity.CENTER);
					return;
				} else {
					/**
					 * 正常登录 提示输入密码或用户名登录
					 */
					tvForgetPassOrRedo.setTag("login_nomal");
					tvForgetPassOrRedo.setVisibility(View.VISIBLE);
					tvForgetPassOrRedo.setText(R.string.login_forget_pass);
					tvForgetPassOrRedo.setTextColor(res.getColor(R.color.orange));
					et.setText(StringConstant.EMPTY_DEFAULT);
					et.setHint(R.string.login_input_pass);
					et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					tvInputHint.setText(R.string.login_your_use_this_account);
					tvNext.setText(R.string.login_login);
					tvContentHint.setText(forword);
					tvContentHint.setVisibility(View.VISIBLE);
				}

			} else {
				if (!isCheckedForRegister) {
					showSoft(true);
					/**
					 * 账号验证失败！不存在,提示注册
					 */
					et.setText(StringConstant.EMPTY_DEFAULT);
					et.setInputType(InputType.TYPE_CLASS_TEXT);
					tvInputHint.setText(R.string.login_your_creat_account);
					tvNext.setText(R.string.login_get_check_code);
					tvContentHint.setText(forword);
					tvContentHint.setVisibility(View.VISIBLE);

					/**
					 * 判断用手机创建还是用户名创建提示不同的提示语
					 */
					if (Utils.checkIsMobileNumber(forword)) {
						/**
						 * 手机号
						 */
						et.setHint(R.string.login_input_userName);
					} else {
						et.setHint(R.string.login_input_mobile);
					}
				} else {
					showSoft(false);
					/**
					 * 获取登陆码
					 */
					tvForgetPassOrRedo.setTag("getcode");
					tvForgetPassOrRedo.setVisibility(View.VISIBLE);
					cutDownTime();
					getRegistCode();
				}
			}
		} else if (task.result instanceof RegistCodeResult) {
			showSoft(false);
			tvHttpHint.setText(StringConstant.EMPTY_DEFAULT);
			RegistCodeResult registCodeResult = (RegistCodeResult) task.result;
			if ("success".equals(registCodeResult.msg)) {
				/**
				 * 提示登陆吗正在发过来,进入登录状态
				 */
				DialogUtils.oneButtonShow(this, 0, R.string.login_code_on_sending, null);
				setHttpTextViewMsg(StringConstant.EMPTY_DEFAULT);
				et.setText(StringConstant.EMPTY_DEFAULT);
				et.setHint(R.string.login_input_your_code);
				et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				tvNext.setText(R.string.login_register);
			} else {

				ToastUtil.showToast(this, registCodeResult.msg, Gravity.CENTER);
			}
		} else if (task.result instanceof RegistResult) {
			showSoft(false);
			RegistResult registResult = (RegistResult) task.result;
			if ("ok".equals(registResult.msg)) {
				/**
				 * 注册成功获取用户信息
				 */
				UserSatusData result = registResult.result;
				app.saveTempToSharedPreferences("isLogin", "true", this);
				app.saveTempToSharedPreferences("jia_user_id", result.id, this);
				app.saveTempToSharedPreferences("username", result.login_name, this);
				app.saveTempToSharedPreferences("mobile", result.mobile, this);
				getUserInfo();
			} else {

				ToastUtil.showToast(this, registResult.msg, Gravity.CENTER);
			}
		} else if (task.result instanceof LoginResult) {
			showSoft(false);
			LoginResult loginResult = (LoginResult) task.result;
			if ("ok".equals(loginResult.msg)) {
				/**
				 * 登录成功获取用户
				 */
				UserSatusData result = loginResult.result;
				app.saveTempToSharedPreferences("isLogin", "true", this);
				app.saveTempToSharedPreferences("jia_user_id", result.id, this);
				app.saveTempToSharedPreferences("username", result.login_name, this);
				app.saveTempToSharedPreferences("mobile", result.mobile, this);
				getUserInfo();
			} else {

				DialogUtils.oneButtonShow(this, 0, R.string.error_password, null);
			}
		} else if (task.result instanceof UserInfoResult) {
			showSoft(false);
			UserInfoResult infoResult = (UserInfoResult) task.result;
			if ("success".equals(infoResult.msg)) {
				app.setInfoResult(infoResult);
				String uid = null;
				;
				if (infoResult.data != null && infoResult.data.user_info != null) {
					LogUtil.logE("username+:" + infoResult.data.user_info.user_name);
					app.saveTempToSharedPreferences("username", infoResult.data.user_info.user_name, this);
					app.saveTempToSharedPreferences("mobile", infoResult.data.user_info.mobile, this);
					uid = infoResult.data.user_info.user_id;
					app.saveTempToSharedPreferences("userid", uid, this);
				}

				if (application.getPushModel() != null) {
					/**
					 * 推送
					 */
					toPush();
				} else {

					toHome(uid);
				}

			} else {
				ToastUtil.showToast(this, infoResult.msg, Gravity.CENTER);
			}
		}

	}

	private void showSoft(boolean isShow) {
		if (getWindow() != null) {
			if (isShow) {

				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			} else {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		}
	}

	private void toPush() {
		Intent intent = new Intent(this, FeaturedQuestionActivity.class);
		startActivity(intent);
		finish();
	}

	private void toHome(String uid) {

		String oldUid = app.getTempFromSharedPreferences("oldUid", this);
		if (oldUid != null && !oldUid.equals(uid)) {
			/**
			 * 换了账号登陆 清除本地数据
			 */
			File file = getDatabasePath(DBConstant.DB_NAME);
			if (file.exists()) {
				file.delete();
			}
		}
		app.saveTempToSharedPreferences("oldUid", uid, this);

		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	protected void getDataFailed(Object... param) {
		if (tvHttpHint != null)
			tvHttpHint.setText(StringConstant.EMPTY_DEFAULT);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_next:
			String strCurrentHint = tvNext.getText().toString();
			String etInput = et.getText().toString();
			if (res.getString(R.string.login_next).equals(strCurrentHint)) {
				/**
				 * 验证用户账号是否存在
				 */
				if (Utils.stringIsNotEmpty(etInput)) {
					forword = etInput;
					userMsg = etInput;
					checkUserIsRegistered(etInput, false);
				} else {
					ToastUtil.showToast(this, R.string.login_input_username_or_mobile, Gravity.CENTER);
				}

			} else if (res.getString(R.string.login_login).equals(strCurrentHint)) {
				/**
				 * 账号通过验证,登录app
				 */
				if (!Utils.stringIsNotEmpty(etInput)) {
					ToastUtil.showToast(this, R.string.login_input_content, Gravity.CENTER);
					return;
				}
				doLogin(etInput);

			} else if (res.getString(R.string.login_get_check_code).equals(strCurrentHint)) {
				/**
				 * 注册流程
				 */
				if (!Utils.stringIsNotEmpty(etInput)) {
					ToastUtil.showToast(this, R.string.login_input_content, Gravity.CENTER);
					return;
				}

				if (et.getHint().toString().equals(res.getString(R.string.login_input_userName))) {
					/**
					 * 当前应该输入手机号
					 */

					userName = etInput;
					mobile = forword;
					if (userName.matches("[0-9]+")) {
						/**
						 * 提示用户名不能全为数字
						 */
						ToastUtil.showToast(this, R.string.username_error_all_num, Gravity.CENTER);
					}

				} else {
					userName = forword;
					if (Utils.checkIsMobileNumber(etInput)) {
						mobile = etInput;
					} else {
						/**
						 * 提示当前应该正确输入手机号
						 */
						ToastUtil.showToast(this, R.string.login_input_mobile_correctly, Gravity.CENTER);
						return;
					}
				}
				/**
				 * 再次验证用户名或手机号有没有被绑定
				 */

				app.saveTempToSharedPreferences("username", userName, this);
				app.saveTempToSharedPreferences("mobile", mobile, this);
				checkUserIsRegistered(etInput, true);
			} else if (res.getString(R.string.login_register).equals(strCurrentHint)) {
				/**
				 * 注册
				 */
				if (!Utils.stringIsNotEmpty(etInput)) {
					ToastUtil.showToast(this, R.string.login_input_content, Gravity.CENTER);
					return;
				}
				if (!etInput.equals(app.getTempFromSharedPreferences("regist_code", this))) {
					ToastUtil.showToast(this, R.string.login_input_your_code_correct, Gravity.CENTER);
					return;
				}
				doRegister(etInput);
			}
			break;

		case R.id.iv_del:
			et.setText(StringConstant.EMPTY_DEFAULT);
			break;
		case R.id.login_back:
			initLoginStatus();
			break;
		case R.id.tv_forget_or_redo:
			if ("getcode".equals(v.getTag().toString())) {
				/**
				 * 重新发验证码
				 */
				getRegistCode();
			} else {
				/**
				 * 忘记密码
				 */
				doForgetPass();
			}
			cutDownTime();
			break;
		default:
			break;
		}

	}

	public void onBackPressed() {
		if (ivBack.getVisibility() == View.VISIBLE) {
			initLoginStatus();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 检查用户是否已经注册
	 */

	private void checkUserIsRegistered(String userNameOrMobile, boolean isCheckedForRegister) {
		this.isCheckedForRegister = isCheckedForRegister;

		Task task = new Task();
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_CHECK_USER_ACCOUNT;
		task.resultDataClass = CheckLoginResult.class;
		String imei = Utils.getDeviceId(this);
		String login_name = userNameOrMobile;
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&login_name=");
		sb.append(login_name);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("login_name", login_name);
		map.put("sign", sign);
		task.taskParam = map;
		setHttpTextViewMsg(res.getString(R.string.login_on_check_your_account));
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	/**
	 * 登录
	 */
	private void doLogin(String input) {
		Task task = new Task();
		task.target = this;
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_LOGIN;
		task.resultDataClass = LoginResult.class;
		String api_type = "login";
		String imei = Utils.getDeviceId(this);
		String login_name = app.getTempFromSharedPreferences("mobile", this);
		if (login_name == null) {
			login_name = app.getTempFromSharedPreferences("username", this);
		}
		String password = input;
		String ip = Utils.readIp(this);

		StringBuffer sb = new StringBuffer();
		sb.append("api_type=");
		sb.append(api_type);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&last_login_ip=");
		sb.append(ip);
		sb.append("&login_name=");
		sb.append(login_name);
		sb.append("&password=");
		sb.append(password);

		String sign = sb.toString();
		LogUtil.logE(sign + "___login");
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("api_type", api_type);
		map.put("imei", imei);
		map.put("last_login_ip", ip);
		map.put("login_name", login_name);
		map.put("password", password);
		map.put("sign", sign);
		task.taskParam = map;
		setHttpTextViewMsg(res.getString(R.string.login_on_loging));
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	/**
	 * 注册
	 */

	private void doRegister(String input) {
		Task task = new Task();
		task.target = this;
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_REGISTER;
		task.resultDataClass = RegistResult.class;

		String api_type = "register";
		String imei = Utils.getDeviceId(this);
		String login_name = app.getTempFromSharedPreferences("username", this);
		String mobile = app.getTempFromSharedPreferences("mobile", this);
		String mobile_status = "1";
		String password = input;
		String register_ip = Utils.readIp(this);

		StringBuffer sb = new StringBuffer();
		sb.append("api_type=");
		sb.append(api_type);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&login_name=");
		sb.append(login_name);
		sb.append("&mobile=");
		sb.append(mobile);
		sb.append("&mobile_status=");
		sb.append(mobile_status);
		sb.append("&password=");
		sb.append(password);
		sb.append("&register_ip=");
		sb.append(register_ip);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("api_type", api_type);
		map.put("imei", imei);
		map.put("login_name", login_name);
		map.put("mobile", mobile);
		map.put("mobile_status", mobile_status);
		map.put("password", password);
		map.put("register_ip", register_ip);
		map.put("sign", sign);
		task.taskParam = map;
		setHttpTextViewMsg(res.getString(R.string.login_on_register));
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 注册发送验证码
	 */

	private void getRegistCode() {
		Task task = new Task();
		task.target = this;
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_REGISTER_CODE;
		task.resultDataClass = RegistCodeResult.class;

		String code = getRadomCode();
		LogUtil.logE(code + "registCode");
		app.saveTempToSharedPreferences("regist_code", code, this);
		String imei = Utils.getDeviceId(this);
		String mobileStr = mobile;

		StringBuffer sb = new StringBuffer();
		sb.append("content=");
		sb.append(code);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&mobile=");
		sb.append(mobileStr);
		String sign = sb.toString();
		LogUtil.logE(sign + "getcode");
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("content", code);
		map.put("imei", imei);
		map.put("mobile", mobileStr);
		map.put("sign", sign);
		task.taskParam = map;

		setHttpTextViewMsg(res.getString(R.string.login_code_on_request));
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	private void doForgetPass() {
		Task task = new Task();
		task.target = this;
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_LOGIN;
		task.resultDataClass = ForgetPassResult.class;
		StringBuffer sb = new StringBuffer();

		String api_type = "ver_code";
		String client_ip = Utils.readIp(this);
		String imei = Utils.getDeviceId(this);
		String mobile = app.getTempFromSharedPreferences("mobile", this);
		String type = "3";
		String update_pwd = "1";
		sb.append("api_type=");
		sb.append(api_type);
		sb.append("&client_ip=");
		sb.append(client_ip);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&mobile=");
		sb.append(mobile);
		sb.append("&type=");
		sb.append(type);
		sb.append("&update_pwd=");
		sb.append(update_pwd);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("api_type", api_type);
		map.put("client_ip", client_ip);
		map.put("imei", imei);
		map.put("mobile", mobile);
		map.put("sign", sign);
		map.put("type", type);
		map.put("update_pwd", update_pwd);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 随机验证码
	 * 
	 * @return
	 */

	private String getRadomCode() {

		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(String.valueOf(random.nextInt(9)));
		}
		return sb.toString();
	}

	/**
	 * 设置执行Http任务时，textView提示语
	 */
	private void setHttpTextViewMsg(String msg) {
		tvHttpHint.setText(msg);

	}

	private void initLoginStatus() {
		ivBack.setVisibility(View.INVISIBLE);
		et.setHint(R.string.login_input_username_or_mobile);
		if (userMsg != null) {
			et.setText(userMsg);
		} else {
			et.setText(StringConstant.EMPTY_DEFAULT);
		}
		tvContentHint.setText(StringConstant.EMPTY_DEFAULT);
		tvInputHint.setText(R.string.login_do_login_or_regist);
		tvContentHint.setVisibility(View.GONE);
		tvHttpHint.setText(StringConstant.EMPTY_DEFAULT);
		tvNext.setText(R.string.login_next);
		tvForgetPassOrRedo.setVisibility(View.INVISIBLE);
		et.setInputType(InputType.TYPE_CLASS_TEXT);
	}

	/**
	 * 获取用户信息
	 */

	private void getUserInfo() {
		Task task = new Task();
		task.target = this;
		task.taskID = NumberConstant.CHECK_USER_ACCOUNT_ID;
		task.taskQuery = URLConstant.URL_GET_USER_INFO;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		String userid = app.getTempFromSharedPreferences("jia_user_id", this);
		String username = app.getTempFromSharedPreferences("username", this);
		String mobile = app.getTempFromSharedPreferences("mobile", this);
		String imei = Utils.getDeviceId(this);
		String ip = Utils.readIp(this);
		StringBuffer sb = new StringBuffer();
		sb.append("from=3");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&ip=");
		sb.append(ip);
		sb.append("&jia_user_id=");
		sb.append(userid);
		sb.append("&mobile=");
		sb.append(mobile);
		sb.append("&user_name=");
		sb.append(username);
		String sign = sb.toString();
		LogUtil.logE(sign + "***getUserInfoA");
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("from", "3");
		map.put("imei", imei);
		map.put("ip", ip);
		map.put("jia_user_id", userid);
		map.put("mobile", mobile);
		map.put("sign", sign);
		map.put("user_name", username);
		task.taskParam = map;
		task.resultDataClass = UserInfoResult.class;
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (tvForgetPassOrRedo != null) {
				int what = msg.what;
				if (what < 10) {
					tvForgetPassOrRedo.setText((10 - what) + res.getString(R.string.time_later_do));

				} else {
					if ("getcode".equals(tvForgetPassOrRedo.getTag().toString())) {
						tvForgetPassOrRedo.setText(R.string.login_resend);
					} else {

						tvForgetPassOrRedo.setText(R.string.login_forget_pass);
					}
					tvForgetPassOrRedo.setTextColor(res.getColor(R.color.orange));
					tvForgetPassOrRedo.setEnabled(true);
				}

			}
		}
	};

	private void cutDownTime() {
		tvForgetPassOrRedo.setText(10 + res.getString(R.string.time_later_do));
		tvForgetPassOrRedo.setTextColor(res.getColor(R.color.color_grey));
		tvForgetPassOrRedo.setEnabled(false);
		new Thread() {
			public void run() {
				int i = 1;
				while (i < 11) {
					try {

						Thread.sleep(1000);
						Message message = handler.obtainMessage();
						message.what = i;
						handler.sendMessage(message);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}
			}
		}.start();
	}

}
