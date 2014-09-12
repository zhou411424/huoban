package com.example.huoban.fragment.diary;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.CommentActivity;
import com.example.huoban.activity.diary.DiaryCommentActivity;
import com.example.huoban.activity.diary.DiaryDetailActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.ArticleCommentResult;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.Comment;
import com.example.huoban.model.CommentData;
import com.example.huoban.model.CommentResult;
import com.example.huoban.model.DiaryAndCommentData;
import com.example.huoban.model.DiaryContent;
import com.example.huoban.utils.DynamicSetListViewUtil;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.other.CircleImageView;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentFragment extends BaseFragment implements OnRefreshListener {
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private ArrayList<Comment> mComments;
	private String activityFlag;// 用于标记activity
	private CommentAdapter adapter;
	private FragmentActivity mActivity;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	// private DiaryContent diaryContent;
	private String reply_pid;
	private String reply_time;
	private int id;
	private String commentName;
	private boolean isAddComment = false;
	public static final String COMMENT_BUNDLE_KEY = "Comment";
	public static final String COMMENT_DIARY_ID = "comment_diary_id";
	private final static int GET_COMMNET = 0; // 文章评论
	private final static int GET_ARTICLE_COMMNET = 1; // 某一日记评论
	private final static int ADD_COMMNET = 2; // 添加文章评论
	private final static int ADD_ARTICLE_COMMNET = 3; // 添加某一日记评论
	private final static int ADD_COMMNET_REPLY = 4; // 添加回复的评论

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diary_comment, container, false);
		setupViews(view);

		if (mActivity instanceof CommentActivity) {
			id = getArguments().getInt(COMMENT_DIARY_ID);
			getComment(true);
		} else if (mActivity instanceof DiaryCommentActivity) {
			DiaryContent diaryContent = (DiaryContent) getArguments().getSerializable("diaryContent");
			DiaryAndCommentData diaryAndCommentData = (DiaryAndCommentData) getArguments().getSerializable("diary_and_comment");
			if (diaryContent != null) {
				reply_pid = diaryContent.reply_pid;
				reply_time = diaryContent.reply_time;
				getArticleComment(true);
			}
			if (diaryAndCommentData != null) {
				reply_pid = diaryAndCommentData.diary.reply_pid;
				reply_time = diaryAndCommentData.diary.reply_time;
				mComments = diaryAndCommentData.comment;
				updateCommentList();
			}
		}
		return view;
	}

	@Override
	protected void setupViews(View view) {
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_diary_comment);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activityFlag = getArguments().getString(COMMENT_BUNDLE_KEY);
		if (activityFlag.equals(DiaryCommentActivity.DIARY_COMMENT_ACTIVITY_FLAG)) {
			mActivity = (DiaryCommentActivity) activity;
		} else {
			mActivity = (CommentActivity) activity;
		}

	}

	/**
	 * 获取文章评论
	 * */
	private void getComment(boolean showProgress) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskID = GET_COMMNET;
		task.taskQuery = URLConstant.URL_GET_COMMENT_LIST;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(id));
		task.taskParam = params;
		task.resultDataClass = CommentResult.class;
		if (showProgress)
			showProgress(null, 0, false);
		doTask(task);
	}

	/**
	 * 获取某一日记评论
	 * */
	private void getArticleComment(boolean showProgress) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskID = GET_ARTICLE_COMMNET;
		task.taskQuery = URLConstant.URL_GET_DIARY_COMMENT;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", reply_pid);
		params.put("date", reply_time.substring(0, 10));
		task.taskParam = params;
		task.resultDataClass = ArticleCommentResult.class;
		if (showProgress)
			showProgress(null, 0, false);
		doTask(task);
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		Task task = (Task) param[0];
		Intent intent = new Intent();
		intent.putExtra("position", getArguments().getInt("position"));
		intent.setAction(DiaryDetailActivity.ACTION_DIARY_COMMENT);
		switch (task.taskID) {
		case GET_COMMNET:
			CommentResult result = (CommentResult) task.result;
			CommentData data = result.data;
			if (data.comment_list == null) {
				return;
			}
			mComments = data.comment_list;
			updateCommentList();
			break;
		case GET_ARTICLE_COMMNET:
			ArticleCommentResult aResult = (ArticleCommentResult) task.result;
			if (aResult.data == null) {
				return;
			}
			mComments = aResult.data;
			updateCommentList();
			break;
		case ADD_COMMNET:
			isAddComment = true;
			BaseResult baseResult = (BaseResult) task.result;
			if (baseResult.status == 1) {
				getComment(false);
			}
			break;
		case ADD_ARTICLE_COMMNET:
			isAddComment = true;
			BaseResult baseResultArticle = (BaseResult) task.result;
			if (baseResultArticle.status == 1) {
				getArticleComment(false);
				getActivity().sendBroadcast(intent);
			}
			break;
		case ADD_COMMNET_REPLY:
			isAddComment = true;
			if (mActivity instanceof DiaryCommentActivity) {
				BaseResult replyCommentResult = (BaseResult) task.result;
				if (replyCommentResult.status == 1) {
					getArticleComment(false);
					getActivity().sendBroadcast(intent);
				}
			} else {
				getComment(false);
			}
			break;
		}
	}

	/**
	 * 更新评论列表
	 * */
	private void updateCommentList() {
		if (adapter == null) {
			adapter = new CommentAdapter();
			mListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

		if (activityFlag.equals(DiaryCommentActivity.DIARY_COMMENT_ACTIVITY_FLAG)) {
			mPullToRefreshListView.setRefreshEnable(false);
			mPullToRefreshListView.setLoadMoreEnable(false);
			DynamicSetListViewUtil.setListViewHeightBasedOnChildren(mListView);
		}

		// 添加回复自动跳转到底部
		if (isAddComment) {
			mListView.setSelection(mListView.getAdapter().getCount() - 1);
			if (mActivity instanceof DiaryCommentActivity)
				((DiaryCommentActivity) mActivity).setScrollViewToBottom();
		}

	}

	private class CommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			try {
				return mComments.size();
			} catch (NullPointerException e) {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.view_comment_items, arg2, false);
			}
			TextView tvName = ViewHolder.get(view, R.id.tv_comment_user_name);
			TextView tvDate = ViewHolder.get(view, R.id.tv_comment_date);
			TextView tvContent = ViewHolder.get(view, R.id.tv_comment_content);
			CircleImageView ivPic = ViewHolder.get(view, R.id.iv_comment_user_pic);
			Button btnReply = ViewHolder.get(view, R.id.btn_comment_reply);

			final Comment comment = mComments.get(arg0);

			tvName.setText(comment.replyer_name);
			tvDate.setText(comment.reply_time);
 
			tvContent.setText(comment.reply_content);

			DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build();
 
			if(comment.reply_content.startsWith("回复")){
				try{
					SpannableStringBuilder content=new SpannableStringBuilder(comment.reply_content);
					content.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.diary_main_color))
							, comment.reply_content.indexOf("复")+1
							, comment.reply_content.indexOf("：")
							, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					tvContent.setText(content);
				}catch(IndexOutOfBoundsException e){
					tvContent.setText(comment.reply_content);
				}
			}else{
				tvContent.setText(comment.reply_content);
			}
//			DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageForEmptyUri(R.drawable.ren)
//					.showImageOnFail(R.drawable.ren).cacheInMemory(true)
//					.cacheOnDisc(true).build();
 
			mImageLoader.displayImage(comment.replyer_avatar, ivPic, options);

			btnReply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mActivity instanceof DiaryCommentActivity) {
						((DiaryCommentActivity) mActivity).reply("回复" + comment.replyer_name + "：");
					} else {
						((CommentActivity) mActivity).reply("回复" + comment.replyer_name + "：");
					}
					commentName = comment.replyer_name;
				}
			});
			return view;
		}

	}

	/**
	 * 添加文章评论
	 * */
	public void addComment(String content) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_ADD_COMMENT;
		task.resultDataClass = BaseResult.class;
		task.taskID = ADD_COMMNET;
		String imei = Utils.getDeviceId(getActivity());
		String user_id = application.getUserId(getActivity());

		StringBuffer sb = new StringBuffer();
		sb.append("content=");
		sb.append(content);
		sb.append("&id=");
		sb.append(String.valueOf(id));
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&type=");
		sb.append(String.valueOf(1));
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("content", content);
		map.put("id", String.valueOf(id));
		map.put("imei", imei);
		map.put("type", String.valueOf(1));
		map.put("user_id", user_id);
		map.put("sign", sign);

		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 添加日记评论
	 * */
	public void addDiaryComment(String content) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_ADD_COMMENT;
		task.resultDataClass = BaseResult.class;
		task.taskID = ADD_ARTICLE_COMMNET;
		String imei = Utils.getDeviceId(getActivity());
		String user_id = application.getUserId(getActivity());

		StringBuffer sb = new StringBuffer();
		sb.append("content=");
		sb.append(content);
		sb.append("&date=");
		sb.append(reply_time.substring(0, 10));
		sb.append("&id=");
		sb.append(reply_pid);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&type=");
		sb.append(String.valueOf(2));
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign + "#######");
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("content", content);
		map.put("date", reply_time.substring(0, 10));
		map.put("id", reply_pid);
		map.put("imei", imei);
		map.put("type", String.valueOf(2));
		map.put("user_id", user_id);
		map.put("sign", sign);

		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 添加评论的回复
	 * */
	public void addReplyComment(String content) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_ADD_COMMENT;
		if (mActivity instanceof DiaryCommentActivity) {
			task.resultDataClass = BaseResult.class;
		} else {
			task.resultDataClass = CommentResult.class;
		}
		task.taskID = ADD_COMMNET_REPLY;
		String imei = Utils.getDeviceId(getActivity());
		String user_id = application.getUserId(getActivity());

		StringBuffer sb = new StringBuffer();
		sb.append("comment_name=");
		sb.append(commentName);
		sb.append("&content=");
		sb.append(content);

		if (mActivity instanceof DiaryCommentActivity) {
			sb.append("&date=");
			sb.append(reply_time.substring(0, 10));
			sb.append("&id=");
			sb.append(reply_pid);
			sb.append("&imei=");
			sb.append(imei);
			sb.append("&type=");
			sb.append(String.valueOf(3));
		} else {
			sb.append("&id=");
			sb.append(String.valueOf(id));
			sb.append("&imei=");
			sb.append(imei);
			sb.append("&type=");
			sb.append(String.valueOf(1));
		}
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		LogUtil.logE(sign);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("comment_name", commentName);
		map.put("content", content);
		if (mActivity instanceof DiaryCommentActivity) {
			map.put("date", reply_time.substring(0, 10));
			map.put("id", reply_pid);
			map.put("imei", imei);
			map.put("type", String.valueOf(3));
		} else {
			map.put("id", String.valueOf(id));
			map.put("imei", imei);
			map.put("type", String.valueOf(1));
		}
		map.put("user_id", user_id);
		map.put("sign", sign);

		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void onRefresh() {
		if (mActivity instanceof CommentActivity) {
			getComment(true);
		} else if (mActivity instanceof DiaryCommentActivity) {
			getArticleComment(true);
		}
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	protected String setFragmentName() {
		return "CommentFragment";
	}

}
