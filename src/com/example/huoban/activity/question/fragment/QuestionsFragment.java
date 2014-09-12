package com.example.huoban.activity.question.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.QuestionDetailActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.Question;
import com.example.huoban.model.QuestionResult;
import com.example.huoban.model.QuestionResultInfo;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public abstract class QuestionsFragment extends BaseFragment implements OnRefreshListener, DBOperaterInterFace {

	private static final String TAG = "QuestionFragment";

	private BaseQuestionAdapter mAdapter;

	protected Task task = new Task();

	protected int totalPage = 1;

	protected ListView mListView;

	protected PullToRefreshListView mPullToRefreshListView;

	protected Context context;

	protected HashMap<String, String> map = new HashMap<String, String>();

	protected MsgPlaintText msgPlaintText = new MsgPlaintText();

	protected boolean isLoadMore = false;

	protected int currentPage = 1;

	protected int currentReplyStatus = 0;

	protected int questionType = 0;

	public QuestionsFragment(Context context) {
		this.context = context;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.logI(TAG, "onCreateView");
		return inflater.inflate(R.layout.question_fragment, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.logI(TAG, "onActivityCreated");
		mPullToRefreshListView = (PullToRefreshListView) getView().findViewById(R.id.question_fragement_list);
		mPullToRefreshListView.setOnRefreshListener(this);

		mListView = mPullToRefreshListView.getRefreshableView();

		mAdapter = getAdapter(context);
		initHeadView(mListView);

		mListView.setAdapter(mAdapter);

		mListView.setVerticalScrollBarEnabled(false);
		DbParamData data = new DbParamData();
		data.taskId = 2;
		DataBaseManager.operateDataBase(this, data);
	}

	public int getReplyStatus() {
		return currentReplyStatus;
	}

	protected abstract void initHeadView(ListView mListView);

	public void setReplyStatus(int replyStatus) {
		showProgress("正在加载", 0, false);
		questionType = getQuestionType(replyStatus);
		currentReplyStatus = replyStatus;
		currentPage = 1;
		mAdapter.getQuestions().clear();
		loadData(currentReplyStatus, questionType, currentPage, false);
	}

	protected int getQuestionType(int replyStatus) {
		return 0;
	}

	/**
	 * 获取数据
	 * 
	 * @param replystatus
	 *            0所有问题 1 未回答问题 2 以回答问题
	 * @param questiontype
	 *            1精选问题
	 * @param page
	 *            页码
	 * @param showProgress
	 *            是否显示进度
	 * @return
	 */
	public void loadData(int replystatus, int questiontype, int page, boolean showProgress) {
		task = new Task();
		task.fragment = this;
		task.resultDataClass = QuestionResult.class;
		task.taskQuery = URLConstant.URL_GET_QUESTION_LIST;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getParam(replystatus, questiontype, page);
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 参数设置
	 * 
	 * @param replystatus
	 *            1 未回答问题 2 已回答问题 0所有问题
	 * @param questiontype
	 *            1精选问题
	 * @param page
	 *            页码
	 * @return
	 */
	protected Object getParam(int replystatus, int questiontype, int page) {

		String auth_info = Utils.objectToJson(application.getSalt(context).auth_info);
		map.put("auth_info", auth_info);
		map.put("sign_method", "MD5");

		msgPlaintText.app_id = "101";
		msgPlaintText.poster_name = getPostName();
		msgPlaintText.question_type = "" + questiontype;
		msgPlaintText.reply_status = "" + replystatus;
		msgPlaintText.size = "10";
		msgPlaintText.page = "" + page;
		msgPlaintText.time_stamp = Utils.getTimeStamp();
		String msgPlaintext = Utils.objectToJson(msgPlaintText);

		map.put("msg_plaintext", msgPlaintext);
		map.put("sign_info", MD5Util.getMD5String(auth_info + msgPlaintext + application.getSalt(context).salt_key));
		map.put("timestamp", Utils.getTimeStamp());
		Log.e("TURNTO", "auth_info : " + auth_info + "\n" + "msg_plaintext : " + msgPlaintext + "\n" + "sign_info : " + MD5Util.getMD5String(auth_info + msgPlaintext + application.getSalt(context).salt_key) + "\n" + "timestamp :" + Utils.getTimeStamp());
		return map;
	}

	public void onRefresh() {
		currentPage = 1;
		loadData(currentReplyStatus, questionType, currentPage, false);
	}

	public void onLoadMore() {
		if (currentPage == totalPage)
			return;
		isLoadMore = true;
		loadData(currentReplyStatus, questionType, ++currentPage, false);
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	protected void setupViews(View view) {

	}

	protected void refresh(Object... param) {
		dismissProgress();
		final Task task = (Task) param[0];
		QuestionResultInfo mQuestionResultInfo = ((QuestionResult) task.result).msg_plaintext.result;
		if (!isLoadMore) {
			mAdapter.getQuestions().clear();
			if (currentReplyStatus == 0) {
				DbParamData data = new DbParamData();
				data.taskId = 1;
				if (mQuestionResultInfo.topic_list != null)
					data.questions = mQuestionResultInfo.topic_list;
				DataBaseManager.operateDataBase(this, data);
			}
		}
		totalPage = Integer.parseInt(mQuestionResultInfo.total_page);
		setIsRead(mQuestionResultInfo.topic_list);
		mAdapter.getQuestions().addAll(mQuestionResultInfo.topic_list);

		mAdapter.notifyDataSetChanged();
		mPullToRefreshListView.setLoadMoreEnable(currentPage < totalPage);
		mPullToRefreshListView.onRefreshComplete();
		if (isLoadMore) {
			isLoadMore = false;
		}
	}

	/**
	 * 设置已读还是未读
	 * 
	 * @param topic_list
	 */
	private void setIsRead(ArrayList<Question> topic_list) {
		ArrayList<String> hasReadQSIdS = application.getReadQSIdS();
		if (hasReadQSIdS == null) {
			return;
		}
		for (Question q : topic_list) {
			if (hasReadQSIdS.contains(q.topic_id)) {
				q.isRead = true;
			}
		}

	}

	public Object getDataFromDB(DbParamData data) {
		switch (data.taskId) {
		case 1:
			saveQuestions(data.questions);
			return null;
		case 2:
			return getQuestions();
		}
		return null;
	}

	protected abstract ArrayList<Question> getQuestions();

	protected abstract void saveQuestions(ArrayList<Question> questions);

	protected abstract BaseQuestionAdapter getAdapter(Context mContext);

	protected abstract String getPostName();

	@SuppressWarnings("unchecked")
	public void returnDataFromDb(DbParamData data) {
		if (data.object == null)
			return;
		ArrayList<Question> questions = (ArrayList<Question>) data.object;
		setIsRead(questions);
		mAdapter.getQuestions().addAll(questions);
		mAdapter.notifyDataSetChanged();
		loadData(currentReplyStatus, questionType, currentPage, false);
	}

	protected class MsgPlaintText {

		public String app_id;
		public String poster_name;
		public String reply_status;
		public String question_type;
		public String page;
		public String size;
		public String time_stamp;

	}

	protected abstract class BaseQuestionAdapter extends BaseAdapter {

		private final Context mContext;

		private ArrayList<Question> questions = new ArrayList<Question>();

		public Drawable drawable = null;

		public BaseQuestionAdapter(Context mContext) {
			this.mContext = mContext;
			drawable = mContext.getResources().getDrawable(R.drawable.question_contain_image);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		}

		public ArrayList<Question> getQuestions() {
			return questions;
		}

		public int getCount() {
			return questions.size();
		}

		public Question getItem(int arg0) {
			return questions.get(arg0);
		}

		public long getItemId(int arg0) {

			return arg0;
		}

		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = View.inflate(mContext, R.layout.question_item, null);
			}
			final TextView title = (TextView) arg1.findViewById(R.id.question_item_title);
			final TextView scannum = (TextView) arg1.findViewById(R.id.question_item_scanner_num);
			final CheckBox isAnsered = (CheckBox) arg1.findViewById(R.id.question_item_answered);
			final TextView time_ago = (TextView) arg1.findViewById(R.id.question_item_time);
			final ImageView iv = (ImageView) arg1.findViewById(R.id.iv);
			final Question q = getItem(arg0);

			ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			SpannableString mSpannableString = null;
			if (q.pic_urls != null && q.pic_urls.size() > 0) {
				mSpannableString = new SpannableString(q.title + "    ");
				mSpannableString.setSpan(mImageSpan, mSpannableString.length() - 1, mSpannableString.length(), Spannable.SPAN_COMPOSING);
			} else {
				mSpannableString = new SpannableString(q.title);
			}

			title.setText(mSpannableString);

			scannum.setText(q.view_num);

			setItemView(q, isAnsered, time_ago, iv);

			arg1.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					q.view_num = (Integer.parseInt(q.view_num) + 1) + "";

					scannum.setText(q.view_num);

					Intent intent = new Intent(mContext, QuestionDetailActivity.class);

					intent.putExtra("question_id", q.topic_id);

					mContext.startActivity(intent);

					updataHasReadQUID(q, v);

				}
			});
			return arg1;
		}

		protected abstract void setItemView(Question q, CheckBox isAnsered, TextView time_ago, ImageView iv);
	}

	private void updataHasReadQUID(Question q, View v) {

		if (application != null && application.getReadQSIdS() != null) {
			if (!application.getReadQSIdS().contains(q.topic_id)) {
				application.getReadQSIdS().add(q.topic_id);
			}
		}
		ImageView iv = (ImageView) v.findViewById(R.id.iv);
		iv.setVisibility(View.INVISIBLE);
		q.isRead = true;
	}

}
