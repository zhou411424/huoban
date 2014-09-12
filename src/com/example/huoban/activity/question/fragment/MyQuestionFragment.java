package com.example.huoban.activity.question.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.QuestionsActivity;
import com.example.huoban.database.DBConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DbParamData;
import com.example.huoban.model.Question;
import com.example.huoban.utils.LogUtil;

public class MyQuestionFragment extends QuestionsFragment {

	private static final String TAG = "MyQuestionFragment";

	private ListAdapter mAdapter;

	private ImageView emptyView;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(QuestionsActivity.QUESTIONS_LIST_REFRESH_FOR_NEW_PUBLISH)) {
				currentPage = 1;
				loadData(currentReplyStatus, questionType, currentPage, false);
			}
		}
	};

	public MyQuestionFragment(Context context) {
		super(context);
	}

	protected void initHeadView(ListView mListView) {
		emptyView = (ImageView) getView().findViewById(R.id.question_fragement_empty);
		context.registerReceiver(receiver, new IntentFilter(QuestionsActivity.QUESTIONS_LIST_REFRESH_FOR_NEW_PUBLISH));

	}

	protected BaseQuestionAdapter getAdapter(Context mContext) {
		if (mAdapter == null) {
			mAdapter = new ListAdapter(mContext);
		}
		return mAdapter;
	}

	private void addEmptyView() {
		LogUtil.logI("addEmptyView   " + "  count = " + mAdapter.getCount() + "   currentReplyStatus = " + currentReplyStatus + "");
		if (mAdapter.getCount() == 0 && currentReplyStatus == 0) {
			LogUtil.logI("addEmptyView------------------>1");
			emptyView.setVisibility(View.VISIBLE);
			mPullToRefreshListView.setRefreshEnable(false);
		} else {
			LogUtil.logI("addEmptyView------------------>2");
			emptyView.setVisibility(View.GONE);
			mPullToRefreshListView.setRefreshEnable(true);
		}
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
		context.unregisterReceiver(receiver);
	}

	protected String getPostName() {
		return application.getInfoResult().data.user_info.user_name;
	}

	protected void refresh(Object... param) {
		super.refresh(param);
		addEmptyView();
	}

	protected ArrayList<Question> getQuestions() {
		return DBOperateUtils.getQuestions(context, DBConstant.COL_POSTER_NAME + "=?", new String[] { application.getInfoResult().data.user_info.user_name }, null, null);
	}

	protected void saveQuestions(ArrayList<Question> questions) {
		DBOperateUtils.upDateQuestion(context, questions, true, application.getInfoResult().data.user_info.user_name);
	}

	public void returnDataFromDb(DbParamData data) {
		super.returnDataFromDb(data);
		addEmptyView();
	}

	private class ListAdapter extends BaseQuestionAdapter {

		public ListAdapter(Context mContext) {
			super(mContext);
		}

		protected void setItemView(Question q, CheckBox isAnsered, TextView time_ago, ImageView iv) {

			isAnsered.setVisibility(View.VISIBLE);
			time_ago.setVisibility(View.GONE);
			isAnsered.setChecked("2".equals(q.reply_status));
			if ("2".equals(q.reply_status) && !q.isRead) {
				iv.setVisibility(View.VISIBLE);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	protected String setFragmentName() {
		return TAG;
	}

}
