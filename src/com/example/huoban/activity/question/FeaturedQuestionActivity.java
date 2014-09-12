package com.example.huoban.activity.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.push.Push;
import com.baidu.push.PushArticle;
import com.baidu.push.PushListResult;
import com.baidu.push.PushModel;
import com.example.huoban.R;
import com.example.huoban.adapter.FeatureQuestionAdapter;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.widget.pull.PinnedHeaderListView;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshPinnedHeaderListView;

public class FeaturedQuestionActivity extends BaseActivity implements OnClickListener, DBOperaterInterFace, OnRefreshListener {

	private PullToRefreshPinnedHeaderListView mPinnedHeaderListView;
	private PinnedHeaderListView mListView;
	private FeatureQuestionAdapter mAdapter = null;
	public ArrayList<PushArticle> articles;
	private View mHeaderView;
	private int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fedture_question);
		setupViews();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showProgress(null, R.string.waiting, false);
		operateDb(2, null);
	}
	
	@Override
	protected void setupViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.feature_question);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);
		mPinnedHeaderListView = (PullToRefreshPinnedHeaderListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPinnedHeaderListView.getRefreshableView();
		mPinnedHeaderListView.setLoadMoreEnable(false);
		mPinnedHeaderListView.setOnRefreshListener(this);
		articles = new ArrayList<PushArticle>();
		mAdapter = new FeatureQuestionAdapter(this, articles);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mHeaderView = getLayoutInflater().inflate(R.layout.feature_question_time_head, mListView, false);
		mHeaderView.setBackgroundDrawable(null);
		mListView.setPinnedHeaderView(mHeaderView);
		showProgress(null, R.string.waiting, false);
		operateDb(2, null);

	}

	private void getPushList(boolean showProgress) {
		Task task = new Task();
		task.target = this;
		task.noShowToast = true;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = PushListResult.class;
		task.taskQuery = URLConstant.URL_GET_PUSH_LIST;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("page", String.valueOf(page));
		map.put("page_size", "8");
		StringBuffer sb = new StringBuffer();
		sb.append("page=");
		sb.append(page);
		sb.append("&page_size=8");
		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign +MD5Util.MD5KEY));
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void setPush(Push push) {
		String[] times = TimeFormatUtils.formatPushDate(push.create_time);
		if (push.articles != null) {
			for (int i = 0; i < push.articles.size(); i++) {
				PushArticle article = push.articles.get(i);
				article.create_time = push.create_time;
				article.message_id = push.message_id;
				article.index = String.valueOf(i);
				article.MD = times[0];
				article.XQ = times[1];
			}
		}

	}

	/**
	 * 处理新消息
	 */
	private void processData() {
		if(application==null){
			return;
		}
		PushModel pushModel = application.getPushModel();
		if (pushModel != null && pushModel.business_param != null && pushModel.business_param.message != null) {
			Push push = pushModel.business_param.message;
			if (push.articles != null) {

				setPush(push);
				articles.addAll(0, push.articles);
			}
		}

		application.setPushModel(null);
		if (articles.size() > 0) {
			TextView tvTimeA = (TextView) mHeaderView.findViewById(R.id.tv_timea);
			TextView tvTimeB = (TextView) mHeaderView.findViewById(R.id.tv_timeb);
			tvTimeA.setText(articles.get(0).MD);
			tvTimeB.setText(articles.get(0).XQ);
			mHeaderView.setBackgroundResource(R.drawable.bg_push_date_head);
		} else {
			mHeaderView.setBackgroundDrawable(null);
		}

		mAdapter.notifyDataSetChanged();
		getPushList(false);
	}

	private void operateDb(int id, Object object) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPinnedHeaderListView.onRefreshComplete();
		Task task = (Task) param[0];
		PushListResult pushListResult = (PushListResult) task.result;
		if (pushListResult != null && pushListResult.data != null) {
			ArrayList<Push> list = pushListResult.data;
			if (list.size() > 0) {
				if(page==1){
					articles.clear();
				}
				for (Push push : list) {
					setPush(push);
					if (push.articles != null) {
						articles.addAll(push.articles);
					}
				}
				/**
				 * 排序
				 */
				Collections.sort(articles, new Comparator<PushArticle>() {
					@Override
					public int compare(PushArticle lhs, PushArticle rhs) {
						return rhs.MD.compareTo(lhs.MD);
					}
				});
				if(page==1){
					operateDb(3, articles);
				}
				
			}
			if(page < pushListResult.total_pages){
				page++;
				mPinnedHeaderListView.setLoadMoreEnable(true);
			}else{
				mPinnedHeaderListView.setLoadMoreEnable(false);
			}
		}
		if (articles.size() > 0) {
			TextView tvTimeA = (TextView) mHeaderView.findViewById(R.id.tv_timea);
			TextView tvTimeB = (TextView) mHeaderView.findViewById(R.id.tv_timeb);
			tvTimeA.setText(articles.get(0).MD);
			tvTimeB.setText(articles.get(0).XQ);
			mHeaderView.setBackgroundResource(R.drawable.bg_push_date_head);
		} else {
			mHeaderView.setBackgroundDrawable(null);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPinnedHeaderListView != null) {
			mPinnedHeaderListView.onRefreshComplete();
			
			Task task = (Task) param[0];
			if(task.result!=null){
				BaseResult baseResult = (BaseResult) task.result;
				if(baseResult!=null&&"there is no message".equals(baseResult.msg)){
					mPinnedHeaderListView.setLoadMoreEnable(false);
				}
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		Object object = null;
		switch (dbParamData.taskId) {
		case 2:
			object = DBOperateUtils.readPush(this);
			ArrayList<PushArticle> list = (ArrayList<PushArticle>) object;
			Collections.sort(list, new Comparator<PushArticle>() {
				@Override
				public int compare(PushArticle lhs, PushArticle rhs) {
					return rhs.MD.compareTo(lhs.MD);
				}
			});
			if(list.size()>10){
				ArrayList<PushArticle> last = new ArrayList<PushArticle>();
				for (int i = 0; i < 10; i++) {
					last.add(list.get(i));
				}
				list.clear();
				list.addAll(last);
				object = list;
			}
			break;

		case 3:
			ArrayList<PushArticle> articles = (ArrayList<PushArticle>) dbParamData.object;
			DBOperateUtils.savePushList(this, articles);
			break;
		default:
			break;
		}
		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		switch (dbParamData.taskId) {
		case 2:
			ArrayList<PushArticle> list = (ArrayList<PushArticle>) dbParamData.object;
			if (list != null) {
				articles.clear();
				articles.addAll(list);
			}
			if(FeaturedQuestionActivity.this==null||isFinishing()){
				return;
			}
			processData();
			break;

		default:
			break;
		}

	}

	@Override
	public void onRefresh() {
		page = 1;
		getPushList(false);

	}

	@Override
	public void onLoadMore() {
		getPushList(false);

	}

}
