package com.example.huoban.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baidu.mobstat.StatService;
import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.NumberConstant;
import com.example.huoban.http.HttpTask;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseQOResult;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.other.MyDialog;

public abstract class BaseActivity extends FragmentActivity implements OnBackStackChangedListener {
	protected MyDialog progress;
	protected Resources res;
	protected InputMethodManager imm;
	protected HttpTask httpTask;
	protected HuoBanApplication application = HuoBanApplication.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		res = getResources();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		FragmentManager manager = getSupportFragmentManager();
		manager.addOnBackStackChangedListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null) {
				if (getCurrentFocus().getWindowToken() != null) {
					hideSoftInput();
				}
			}
		}
		return super.onTouchEvent(event);
	}

	public void hideSoftInput() {
		if (getCurrentFocus() != null)
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void showSoftInput(View view) {
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	@Override
	public void onBackStackChanged() {
		hideSoftInput();
	}

	public void doTask(Task task) {
		if (httpTask == null) {
			httpTask = new HttpTask(this);
		}
		task.networkIsNotOk = false;
		task.bTimeout = false;
		httpTask.addTask(task);
	}

	/**
	 * 
	 * @param param
	 * @Summary 对返回结果的处理
	 */
	public void processingResults(Object... param) {
		Task task = (Task) param[0];
		BaseQOResult baseQOResult = null;
		BaseResult result = null;
		if (task.result != null && task.result instanceof BaseQOResult) {
			baseQOResult = (BaseQOResult) task.result;

		} else {
			result = (BaseResult) task.result;
		}

		String hintString = null;
		if (task != null && task.networkIsNotOk) {
			hintString = res.getString(R.string.no_net);
		} else if (task != null && task.bTimeout) {
			hintString = res.getString(R.string.time_out);
		} else if (null == result && null == baseQOResult) {
			hintString = res.getString(R.string.data_fail);
		} else if (result != null && result.status == -10) {
			/**
			 * 在另一台手机上登录，退出应用重新登录
			 * 
			 */
			reLogin();
			return;
		} else if (result != null && result.status <= 0 && task.taskID != NumberConstant.CHECK_USER_ACCOUNT_ID) {
			hintString = result.msg;
		} else if (baseQOResult != null && !"000".equals(baseQOResult.response_code)) {
			hintString = baseQOResult.response_desc;

		}
		if (hintString == null) {
			refresh(param);
		} else {
			try {
				if (!task.noShowToast)
					ToastUtil.showToast(this, hintString, Gravity.CENTER);
				dismissProgress();
				getDataFailed(task);
			} catch (Exception e) {
				Log.e("MYTAG", "i am crashing");
			}
		}

	}

	private void reLogin() {
		dismissProgress();
		try {
			DialogUtils.oneButtonShow(this, 0, R.string.account_change, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HuoBanApplication.exitOrRelogin(BaseActivity.this, true);
				}
			});
		} catch (Exception e) {

		}
	}

	/**
	 * 控件初始化
	 */
	protected abstract void setupViews();

	/**
	 * 
	 * 回调该函数时,表明连接与获取数据已经成功
	 * 
	 * @param param
	 */
	protected abstract void refresh(Object... param);

	/**
	 * 
	 * 回调该函数时,表明连接与获取数据失败相应处理界面变化
	 * 
	 * @param param
	 */
	protected abstract void getDataFailed(Object... param);

	public void showProgress(String msg, int id, boolean canBeCanled) {
		if (null == progress) {
			progress = MyDialog.createDialog(this);
		}
		progress.setCancelable(canBeCanled);
		if (msg != null) {
			progress.setMessage(msg);
		} else if (id != 0) {
			progress.setMessage(res.getString(id));
		} else {
			progress.setMessage(res.getString(R.string.data_loading));
		}

		progress.show();
	}

	/**
	 * 取消progressbar
	 */
	public void dismissProgress() {
		if (null != progress && progress.isShowing()) {
			progress.dismiss();
			progress = null;
		}
	}

	public int getWindowWidth() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	public int getWindowHeight() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		application = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
}
