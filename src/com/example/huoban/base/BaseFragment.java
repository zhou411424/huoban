package com.example.huoban.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.http.HttpTask;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseQOResult;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.other.MyDialog;

public abstract class BaseFragment extends Fragment {
	protected MyDialog progress;
	protected Resources res;
	protected HttpTask httpTask;
	protected HuoBanApplication application = HuoBanApplication.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		res = getResources();

	}

	public void doTask(Task task) {
		if (httpTask == null) {
			if(getActivity()==null){
				return;
			}
			httpTask = new HttpTask(getActivity());
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
		} else if (result != null && result.status <= 0) {
			hintString = result.msg;
		} else if (baseQOResult != null && !"000".equals(baseQOResult.response_code)) {
			hintString = baseQOResult.response_desc;

		}
		if (hintString == null) {
			refresh(param);
		} else {
			try {
				if (getActivity() != null) {
					if (!task.noShowToast)
						ToastUtil.showToast(getActivity(), hintString, Gravity.CENTER);
					dismissProgress();
					getDataFailed(task);
				}

			} catch (Exception e) {
				Log.e("MYTAG", "i am crashing");
			}
		}
	}

	private void reLogin() {
		dismissProgress();
		try {
			DialogUtils.oneButtonShow(getActivity(), 0, R.string.account_change, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					HuoBanApplication.exitOrRelogin(getActivity(), true);
				}
			});
		} catch (Exception e) {

		}
	}

	protected abstract void getDataFailed(Object... param);

	/**
	 * 控件初始化
	 */
	protected abstract void setupViews(View view);

	/**
	 * 
	 * 回调该函数时,表明连接与获取数据已经成功
	 * 
	 * @param param
	 */
	protected abstract void refresh(Object... param);

	/**
	 * 显示prigressbar
	 */
	public void showProgress(String msg, int id, boolean canBeCanled) {
		if (null == progress) {
			progress = MyDialog.createDialog(getActivity());
		}
		progress.setCancelable(canBeCanled);
		if (msg != null) {
			progress.setMessage(msg);
		} else if (id != 0) {
			progress.setMessage(res.getString(id));
		} else {
			progress.setMessage(res.getString(R.string.data_loading));
		}
		try {
			progress.show();
		} catch (Exception e) {
		}

	}

	/**
	 * dismissprogressbar
	 */
	public void dismissProgress() {
		if (null != progress && progress.isShowing()) {
			try {
				progress.dismiss();
			} catch (Exception e) {
			}

			progress = null;
		}
	}

	public void onActivityResult(int arg0, int arg1, Intent arg2) {
	}

	public void initDataForChoised() {

	}

	public int getWindowWidth() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	@Override
	public void onResume() {
		super.onResume();
//		StatService.onResume(this);
		StatService.onPageStart(getActivity(), setFragmentName());
	}

	@Override
	public void onPause() {
		super.onPause();
//		StatService.onPause(this);
		StatService.onPageEnd(getActivity(), setFragmentName());
	}
	
	
	
	protected abstract String setFragmentName();
}
