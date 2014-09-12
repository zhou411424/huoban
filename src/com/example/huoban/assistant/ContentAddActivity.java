package com.example.huoban.assistant;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.AddDiscussResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.Task;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
/**
 * 添加新评论主题
 * @author cwchun.chen
 *
 */
public class ContentAddActivity extends BaseActivity implements OnClickListener{
	public static final String TAG = "ContentAddActivity";
	private static final int ADD_DISCUSS = 0;
	private EditText contentContent, contentMemo;
	private TextView contentAddMemo;
	private boolean isSelect;
	public static final int RESULT_CODE_BACK = 20;
	public static final int RESULT_CODE_SUCCESS = 21;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_add);
		setupViews();
	}

	private void setFocus(EditText textView) {
		textView.setFocusable(true);
		textView.setFocusableInTouchMode(true);
		textView.requestFocus();
	}
	@Override
	protected void setupViews() {
		//返回按钮
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(this);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.content_add);
		
		TextView tvSave = (TextView) findViewById(R.id.tv_right);
		tvSave.setText(R.string.save);
		tvSave.setOnClickListener(this);
		
		contentContent = (EditText) this.findViewById(R.id.content_content);
		contentMemo = (EditText) this.findViewById(R.id.content_memo);
		contentAddMemo = (TextView) this.findViewById(R.id.content_add_memo);

		contentMemo.setVisibility(View.GONE);
		contentContent.setFocusable(true);  

	    //该方法可解决控件获取焦点后键盘不默认弹出问题		
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
					im.showSoftInput(contentContent, 0);
				}
			},
			600);
		
		contentAddMemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSelect) {
					contentAddMemo.setText(R.string.content_add_memo2);
					contentMemo.setVisibility(0);
					isSelect = true;
					setFocus(contentMemo);
				} else {   
					contentAddMemo.setText(R.string.content_add_memo1);
					contentMemo.setVisibility(8);
					contentMemo.setText("");
					isSelect = false;
					setFocus(contentContent);
				}
			}
		});

		contentContent
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, final boolean hasFocus) {
						(new Handler()).postDelayed(new Runnable() {
							public void run() {
								Log.i("log", "onFocusChange=" + hasFocus);
								InputMethodManager imm = (InputMethodManager) ContentAddActivity.this
										.getSystemService(Context.INPUT_METHOD_SERVICE);
								if (hasFocus) {
									imm.showSoftInput(contentContent, 0);
								}
							}
						}, 100);
					}
				});

		contentMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, final boolean hasFocus) {
				(new Handler()).postDelayed(new Runnable() {
					public void run() {
						Log.i("log", "onFocusChange=" + hasFocus);
						InputMethodManager imm = (InputMethodManager) ContentAddActivity.this
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						if (hasFocus) {
							imm.showSoftInput(contentMemo, 0);
						}
					} 
				}, 100);
			}
		});
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case ADD_DISCUSS:
			AddDiscussResult addDiscussResult = (AddDiscussResult) task.result;
			if("success".equals(addDiscussResult.msg)){
				ToastUtil.showToast(this, R.string.content_success);
				Intent intent = new Intent();
				setResult(RESULT_CODE_SUCCESS, intent);
				// 关闭掉这个Activity
				this.finish();
			}else{
				ToastUtil.showToast(this, addDiscussResult.msg);
			}
			break;
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ibtn_left:	//返回
			Intent intent = new Intent();
			setResult(RESULT_CODE_BACK, intent);
			// 关闭掉这个Activity 
			ContentAddActivity.this.finish();
			break;
		case R.id.tv_right:	//保存
			save();
			break;
		}
	}

	private void save() {
		/**
		 * 隐藏键盘
		 */
		Utils.hideInputKeyboard(ContentAddActivity.this);
		if (contentContent.getText().toString().trim().equals("")) {
			ToastUtil.showToast(this, R.string.content_input);
			return;
		}
		String content = contentContent.getText().toString();
		String badContent = contentMemo.getText().toString();
		int type = 1;
		content = content.replaceAll("\n","");
		int cateId = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_CATE_ID, -1, this);
		Task task = TasksHelper.getAddDiscussTask(this, ADD_DISCUSS, Integer.valueOf(cateId), content, badContent, type, 0, null);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

}
