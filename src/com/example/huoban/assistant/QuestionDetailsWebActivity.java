package com.example.huoban.assistant;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.dao.SupervisorDao;
import com.example.huoban.assistant.model.TipInfoResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.data.SupervisorBean;
import com.example.huoban.data.WebViewBean;
import com.example.huoban.http.Task;
/**
 * 服务详情 Web 界面
 * @author cwchun.chen
 *
 */
public class QuestionDetailsWebActivity extends BaseActivity {
	private static final int GET_TIP_INFO = 0;
	private TextView tv_title;
	private WebView webView;
	private int tipId;
	private WebViewBean viewBean;
	public static final int SEND_MESSAGE = 9;
	private SupervisorDao supervisorDao;
	protected Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEND_MESSAGE:
				getMessageData(msg);
				break;
			}
		}
	};
	protected void getMessageData(Message msg) {
		viewBean = (WebViewBean) msg.obj;
		tv_title.setText(viewBean.getTitle());
		addWebView(viewBean.getContent());

	}
	@Override
	protected void setupViews() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("");
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				QuestionDetailsWebActivity.this.finish();
			}});
		
		webView = (WebView) findViewById(R.id.webView);
		getData();
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case GET_TIP_INFO:
			TipInfoResult tipInfoResult = (TipInfoResult) task.result;
			if("success".equals(tipInfoResult.msg)){
				supervisorDao = new SupervisorDao(this);
				String strReturn = tipInfoResult.data.get("return").toString();
				try {
					JSONObject supervisorObj = new JSONObject(strReturn);
					SupervisorBean bean = new SupervisorBean();
					bean.setApi_type(supervisorObj.getInt("api_type"));
						WebViewBean viewBean = new WebViewBean();
						viewBean.setAip_id(supervisorObj.getInt("tip_id"));
						viewBean.setAip_type(supervisorObj.getInt("api_type"));
						viewBean.setTitle(supervisorObj.getString("title"));
						String countent = supervisorObj.getString("content");
						viewBean.setContent(countent);
						//TODO 插入数据库
						supervisorDao.inserWebView(viewBean);
						Message msg = mHandler.obtainMessage();
						msg.what = SEND_MESSAGE;
						//TODO 从数据库获取
						msg.obj = supervisorDao.queryWebView(tipId);
						mHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
			break;
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_details_web);
		tipId = getIntent().getExtras().getInt("tip_id");
		setupViews();

	}
	int type = 2;
	/**
	 * 获取齐家服务、常见问题详细内容
	 */
	private void getData() {
		Task task = TasksHelper.getSupervisorTask(this, GET_TIP_INFO, tipId, type);
		showProgress(null, R.string.waiting, false);
		doTask(task);
		
	}

	private void addWebView(String content) {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.loadDataWithBaseURL("about:blank", content, "text/html", "utf-8", null);
	}

}