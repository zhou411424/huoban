package com.example.huoban.activity.diary;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.fragment.diary.CommentFragment;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class CommentActivity extends BaseActivity {
	private ImageButton ibBack;
	private Button btnSend;
	private EditText etSend;
	private CommentFragment fragment;
	private boolean isReplyComment = false;
	public static final String COMMENT_ACTIVITY_FLAG = "CommentActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		setupViews();
	}

	@Override
	protected void setupViews() {
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);

		etSend = (EditText) findViewById(R.id.et_diary_comment_bottom);

		btnSend = (Button) findViewById(R.id.btn_diary_comment_bottom);
		btnSend.setOnClickListener(onClickListener);

		fragment = new CommentFragment();
		Bundle bundle = new Bundle();
		bundle.putString(CommentFragment.COMMENT_BUNDLE_KEY,
				COMMENT_ACTIVITY_FLAG);
		bundle.putInt(CommentFragment.COMMENT_DIARY_ID, getIntent()
				.getIntExtra("diary_id", 0));// 日记Id
		fragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frgmt_comment_container, fragment).commit();
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtn_left:
				finish();
				break;

			case R.id.btn_diary_comment_bottom:
				send();
				break;
			}
		}
	};

	/**
	 * 回复
	 * 
	 * @param text
	 *            设置EditText的hint
	 * */
	public void reply(String text) {
		etSend.setHint(text+" ");
//		etSend.setText(text);
		// EditText获得焦点
		etSend.requestFocus();
		//光标移动到最后
//		etSend.setSelection(text.length());
		isReplyComment=true;
		// 弹出软键盘
		InputMethodManager imm = (InputMethodManager) etSend.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}
	
	private void send(){
		
		if(!Utils.stringIsNotEmpty(etSend.getText().toString())){
			ToastUtil.showToast(this, "有点内容再发吧！");
			return;
		}
		String hint = etSend.getHint().toString();
		String content = etSend.getText().toString();
//		if(Utils.stringIsNotEmpty(hint)){
			content = hint+content;
//		}
		if(!isReplyComment){
			fragment.addComment(content);
		}else{
			fragment.addReplyComment(content);
		}
		isReplyComment=false;
		etSend.setHint("");
		etSend.setText("");
	}
}
