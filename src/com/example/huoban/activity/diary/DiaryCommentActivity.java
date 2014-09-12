package com.example.huoban.activity.diary;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.fragment.diary.CommentFragment;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.DiaryAndCommentData;
import com.example.huoban.model.DiaryAndCommentResult;
import com.example.huoban.model.DiaryContent;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DiaryCommentActivity extends BaseActivity {
	private ImageButton ibBack;
	private ScrollView scrollView;
	private Button btnSend;
	private EditText etSend;
	private TextView tvContent;
	private boolean isReplyComment = false;
	private String diaryId;
	private String diaryDate;
	private DiaryAndCommentData diaryAndCommentData;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private CommentFragment fragment;
	public static final String DIARY_COMMENT_ACTIVITY_FLAG = "DiaryCommentActivity";
	public static final String DIARY_CONTENT = "diary_content";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_comment);
		setupViews();

		diaryId = getIntent().getStringExtra(MyRemindActivity.REMIND_DIARY_ID);
		diaryDate = getIntent().getStringExtra(MyRemindActivity.REMIND_DIARY_DATE);
		if (diaryId != null && diaryDate != null) {
			getDiaryAndComment(diaryId, diaryDate);// 从MyRemindActivity跳转来时调用该方法
		} else {
			initFragment();
		}
	}

	/**
	 * 获取日记以及相应的评论
	 * */
	private void getDiaryAndComment(String id, String date) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_COMMENT_DIARY;
		task.resultDataClass = DiaryAndCommentResult.class;

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("date", date);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void setupViews() {
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);

		scrollView = (ScrollView) findViewById(R.id.sv_diary_comment);
		scrollView.smoothScrollTo(0, 0);

		etSend = (EditText) findViewById(R.id.et_diary_comment_bottom);
		etSend.setEnabled(true);

		btnSend = (Button) findViewById(R.id.btn_diary_comment_bottom);
		btnSend.setOnClickListener(onClickListener);

		tvContent = (TextView) findViewById(R.id.tv_diary_comment_content);

	}

	private void initFragment() {
		fragment = new CommentFragment();
		View rlImage = findViewById(R.id.rl_image);
		ImageView img = (ImageView) findViewById(R.id.iv_diary_image);
		// TextView tv=(TextView) findViewById(R.id.tv_image_description);

		DiaryContent diaryContent = (DiaryContent) getIntent().getSerializableExtra("diaryContent");
		if (diaryContent != null) {
			if (diaryContent.type == 1) {
				tvContent.setText(diaryContent.reply_content);
			} else if (diaryContent.type == 2) {
				rlImage.setVisibility(View.VISIBLE);
				imageLoader.displayImage(diaryContent.reply_content, img);
				// tv.setText(diaryContent.description);
			}
		}

		if (diaryAndCommentData != null) {
			if (diaryAndCommentData.diary.type == 1) {
				tvContent.setText(diaryAndCommentData.diary.reply_content);
			} else if (diaryAndCommentData.diary.type == 2) {
				rlImage.setVisibility(View.VISIBLE);
				imageLoader.displayImage(diaryAndCommentData.diary.reply_content, img);
			}
		}

		Bundle args = new Bundle();
		args.putString(CommentFragment.COMMENT_BUNDLE_KEY, DIARY_COMMENT_ACTIVITY_FLAG);
		args.putInt(CommentFragment.COMMENT_DIARY_ID, 0);// 日记Id
		args.putSerializable("diaryContent", diaryContent);
		args.putInt("position", getIntent().getIntExtra("position", 0));
		if (diaryAndCommentData != null) {
			args.putSerializable("diary_and_comment", diaryAndCommentData);
		}
		fragment.setArguments(args);

		getSupportFragmentManager().beginTransaction().replace(R.id.frgmt_diary_comment_container, fragment).commit();
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		DiaryAndCommentResult result = (DiaryAndCommentResult) task.result;
		if (result.data != null) {
			diaryAndCommentData = result.data;
			initFragment();
		}
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
		etSend.setHint(text + " ");
		// etSend.setText(text);
		// EditText获得焦点
		etSend.requestFocus();
		// 光标移动到最后
		// etSend.setSelection(text.length());
		isReplyComment = true;
		// 弹出软键盘
		InputMethodManager imm = (InputMethodManager) etSend.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	private void send() {

		if (!Utils.stringIsNotEmpty(etSend.getText().toString())) {
			ToastUtil.showToast(this, "有点内容再发吧！");
			return;
		}

		String hint = etSend.getHint().toString();
		String content = etSend.getText().toString();
		// if(Utils.stringIsNotEmpty(hint)){
		content = hint + content;
		// }
		if (!isReplyComment) {
			fragment.addDiaryComment(content);
		} else {
			fragment.addReplyComment(content);
		}
		isReplyComment = false;
		etSend.setHint("");
		etSend.setText("");
	}

	public void setScrollViewToBottom() {
		// scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
}
