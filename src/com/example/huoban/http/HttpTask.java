package com.example.huoban.http;

import android.content.Context;
import android.os.AsyncTask;

import com.example.huoban.constant.URLConstant;
import com.example.huoban.model.BaseQOResult;
import com.example.huoban.model.CompanyDetailResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.Utils;
import com.google.gson.JsonSyntaxException;

public class HttpTask {
	private static final String HTTP_STR = "http://";
	private Context context;

	public HttpTask(Context context) {
		this.context = context;
	}

	private void addTask(Task task, boolean isShowToast) {
		if (Utils.isNetworkAvailable(context)) {
			new DownloadWebpage().execute(task);
		} else {
			task.networkIsNotOk = true;
			if (task.target != null && !task.target.isFinishing()) {
				task.target.processingResults(task);
			} else if (task.fragment != null && task.fragment.getActivity() != null) {
				task.fragment.processingResults(task);
			}
		}
	}

	public void addTask(Task task) {
		addTask(task, true);
	}

	class DownloadWebpage extends AsyncTask<Task, Void, String> {
		private Task task;

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(Task... params) {
			task = params[0];
			if (task.taskQuery != null && task.taskQuery.startsWith(HTTP_STR)) {
				task.taskUrl = task.taskQuery;
			} else {
				task.taskUrl = URLConstant.HOST_NAME + task.taskQuery;
			}

			String result = Caller.httpExecute(task);
			LogUtil.logE(result);

			if (task.resultDataClass != null && result != null) {
				try {
					task.result = ResultsResponse.getResult(result, task.resultDataClass);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					LogUtil.logE("解析失败");
					if (task.resultDataClass == CompanyDetailResult.class) {
						task.result=new BaseQOResult();
						task.failed=true;
					}
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (task.target != null && !task.target.isFinishing()) {
				task.target.processingResults(task);
			} else if (task.fragment != null) {
				task.fragment.processingResults(task);
			}
		}
	}
}
