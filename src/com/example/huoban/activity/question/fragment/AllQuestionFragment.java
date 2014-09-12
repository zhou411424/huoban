package com.example.huoban.activity.question.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.push.PushArticle;
import com.baidu.push.PushListResult;
import com.example.huoban.R;
import com.example.huoban.activity.question.FeaturedQuestionActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.Question;
import com.example.huoban.model.QuestionResult;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AllQuestionFragment extends QuestionsFragment implements OnClickListener {

	private static final String TAG = "MyQuestionFragment";

	private ListAdapter mAdapter;

	private View headView;

	// private Button questionBtn;

	private TextView titleTv;

	private ImageView imageIv;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.question_all_head_image).showImageOnFail(R.drawable.question_all_head_image).cacheInMemory(true).cacheOnDisc(true).build();

	public AllQuestionFragment(Context context) {
		super(context);
	}

	protected void initHeadView(ListView mListView) {
		headView = View.inflate(context, R.layout.layout_question_all_head, null);
		// questionBtn = (Button) headView.findViewById(R.id.question_all_head_button);
		titleTv = (TextView) headView.findViewById(R.id.question_all_head_title);
		imageIv = (ImageView) headView.findViewById(R.id.question_all_head_image);
		mListView.addHeaderView(headView);
		imageIv.setOnClickListener(this);
		getPushList(false);
	}

	protected BaseQuestionAdapter getAdapter(Context mContext) {
		if (mAdapter == null) {
			mAdapter = new ListAdapter(mContext);
		}
		return mAdapter;
	}

	protected int getQuestionType(int replyStatus) {
		if (replyStatus == 0) {
			// headView.setVisibility(View.VISIBLE);
			mListView.addHeaderView(headView);
			return 1;
		} else {
			mListView.removeHeaderView(headView);
			// headView.setVisibility(View.GONE);
			return 0;
		}
	}

	protected String getPostName() {
		return "";
	}

	protected ArrayList<Question> getQuestions() {
		return DBOperateUtils.getQuestions(context, DBConstant.COL_POSTER_NAME + "<>?", new String[] { application.getInfoResult().data.user_info.user_name }, null, null);
	}

	protected void saveQuestions(ArrayList<Question> questions) {
		DBOperateUtils.upDateQuestion(context, questions, false, application.getInfoResult().data.user_info.user_name);
	}

	private class ListAdapter extends BaseQuestionAdapter {

		public ListAdapter(Context mContext) {
			super(mContext);
		}

		protected void setItemView(Question q, CheckBox isAnsered, TextView time_ago, ImageView iv) {
			isAnsered.setVisibility(View.GONE);
			time_ago.setText(TimeFormatUtils.getFriendlyDate(q.create_time));
			time_ago.setVisibility(View.VISIBLE);
		}
	}

	private void getPushList(boolean showProgress) {
		task = new Task();
		task.fragment = this;
		task.noShowToast = true;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = PushListResult.class;
		task.taskQuery = URLConstant.URL_GET_PUSH_LIST;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("page", "1");
		map.put("page_size", "1");
		StringBuffer sb = new StringBuffer();
		sb.append("page=1");
		sb.append("&page_size=1");
		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign +MD5Util.MD5KEY));
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	protected void setItemView(Question q, CheckBox isAnsered, TextView time_ago, ImageView iv) {
		isAnsered.setVisibility(View.GONE);
		time_ago.setText(TimeFormatUtils.getFriendlyDate(q.create_time));
		time_ago.setVisibility(View.VISIBLE);
	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		if (task.resultDataClass == QuestionResult.class) {
			super.refresh(param);
		} else {
			dismissProgress();
			PushListResult pushListResult = (PushListResult) task.result;
			if (pushListResult != null && pushListResult.data != null && pushListResult.data.size() > 0 && pushListResult.data.get(0).articles != null && pushListResult.data.get(0).articles.size() > 0) {
				PushArticle pushArticle = pushListResult.data.get(0).articles.get(0);
				imageLoader.displayImage(pushArticle.first_img, imageIv, defaultOptions);
				titleTv.setText(pushArticle.title);
				pushArticle.message_id = pushListResult.data.get(0).message_id;
				imageIv.setTag(pushArticle);
			}
		}
	}

	public void onLoadMore() {
		super.onLoadMore();
		getPushList(false);
	}

	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.question_all_head_button:
			intent.setClass(context, FeaturedQuestionActivity.class);
			context.startActivity(intent);
			break;
		case R.id.question_all_head_image:
			// intent.setClass(context, PushDetailActicity.class);
			// PushArticle pushArticle = (PushArticle) v.getTag();
			// intent.putExtra("message_id", pushArticle.message_id);
			// intent.putExtra("title", pushArticle.title);
			// intent.putExtra("index", "0");
			intent.setClass(context, FeaturedQuestionActivity.class);
			context.startActivity(intent);
			break;
		}
	}

	@Override
	protected String setFragmentName() {
		return TAG;
	}
}
