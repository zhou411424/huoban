package com.example.huoban.activity.my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.circle.PublishDynamicActivity;
import com.example.huoban.adapter.MyAlumAdapter;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.CircleResult;
import com.example.huoban.model.Topic;
import com.example.huoban.model.UserInfo;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AlumActivity extends BaseActivity implements OnClickListener, OnRefreshListener, DBOperaterInterFace {

	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private int pageIndex = 1;
	private String see_id;
	private List<Topic> topicList = null;
	private MyAlumAdapter myAlumAdapter = null;
	private ImageButton ibtnAdd;
	/**
	 * 当为ture返回上一级需要通知刷新 发布了新的动态
	 */
	private boolean hasNewPublish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alum);
		setupViews();
	}

	@Override
	protected void setupViews() {

		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);

		ibtnAdd = (ImageButton) findViewById(R.id.ibtn_right);
		ibtnAdd.setOnClickListener(this);
		ibtnAdd.setVisibility(View.VISIBLE);
		ibtnAdd.setImageResource(R.drawable.red_addo);

		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText("我的相册");

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setHeaderDividersEnabled(false);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);

		see_id = getIntent().getStringExtra("see_id");
		String user_name = getIntent().getStringExtra("user_name");
		String user_avatar = getIntent().getStringExtra("user_avatar");

		initHeadView(getLayoutInflater(), user_name, user_avatar);

		topicList = new ArrayList<Topic>();
		myAlumAdapter = new MyAlumAdapter(this, topicList, getWindowWidth());
		mListView.setAdapter(myAlumAdapter);
		showProgress(null, R.string.waiting, false);
		operateDb(11, null);
	}

	private void initHeadView(LayoutInflater inflater, String user_name, String user_avatar) {
		View headView = inflater.inflate(R.layout.circle_head_view_alum, null);
		mListView.addHeaderView(headView);

		ImageView ivCovel = (ImageView) headView.findViewById(R.id.iv_cover);
		ImageView ivHead = (ImageView) headView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) headView.findViewById(R.id.tv_name);
		DisplayImageOptions optionsPage = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).cacheInMemory(true).cacheOnDisc(true).build();

		if (application.getUserId(this).equals(see_id)) {
			/**
			 * 自己的相册
			 */
			UserInfoResult infoResult = application.getInfoResult();
			if (infoResult != null && infoResult.data != null && infoResult.data.user_info != null) {
				UserInfo user_info = infoResult.data.user_info;
				tvName.setText(user_info.user_name);
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(user_info.avatar, ivHead);
				imageLoader.displayImage(user_info.cover_url, ivCovel,optionsPage);
			}
		} else {
			ibtnAdd.setVisibility(View.INVISIBLE);
			tvName.setText(user_name);
			if (Utils.stringIsNotEmpty(user_avatar)) {
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(user_avatar, ivHead,optionsPage);
			}
		}

	}

	private void getData(boolean showProgress) {
		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_ALUM_FOR_CIRCLE;
		task.resultDataClass = CircleResult.class;

		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);
		sb.append("imei=");
		sb.append(imei);
		sb.append("&page=");
		sb.append(String.valueOf(pageIndex));
		sb.append("&see_id=");
		sb.append(see_id);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("page", String.valueOf(pageIndex));
		map.put("user_id", user_id);
		map.put("see_id", see_id);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void operateDb(int id, Object param) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = param;
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.result instanceof CircleResult) {
			/**
			 * 获取装修界列表
			 */
			mPullToRefreshListView.onRefreshComplete();
			CircleResult circleResult = (CircleResult) task.result;
			if (pageIndex == 1) {
				topicList.clear();
			}
			if (circleResult.data != null) {

				if (circleResult.data.topic_list != null) {

					topicList.addAll(circleResult.data.topic_list);
				}

				if (pageIndex == 1) {
					operateDb(10, circleResult.data.topic_list);
				}

				if (topicList.size() == circleResult.data.topic_num) {
					mPullToRefreshListView.setLoadMoreEnable(false);
				} else {
					pageIndex++;
					mPullToRefreshListView.setLoadMoreEnable(true);
				}
			}
			myAlumAdapter.refresh(topicList);

		}

	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			Intent intent = new Intent(this, PublishDynamicActivity.class);
			startActivityForResult(intent, 100);

			break;

		default:
			break;
		}

	}

	@Override
	public void finish() {
		setResult();
		super.finish();
	}

	private void setResult() {
		if (hasNewPublish) {
			setResult(RESULT_OK);
		}
	}

	@Override
	public void onRefresh() {
		pageIndex = 1;
		getData(false);

	}

	@Override
	public void onLoadMore() {
		getData(false);

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 100 && arg1 == RESULT_OK) {
			hasNewPublish = true;
			pageIndex = 1;
			getData(false);
		}
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		Object object = null;
		switch (dbParamData.taskId) {
		case 10:
			/**
			 * 存储
			 */
			ArrayList<Topic> list = (ArrayList<Topic>) dbParamData.object;
			DBOperateUtils.saveAlumListToDb(this, list, see_id);

			break;
		case 11:
			/**
			 * 读取
			 */
			object = DBOperateUtils.readAlumList(this, see_id);

			break;

		default:
			break;
		}
		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		switch (dbParamData.taskId) {
		case 11:
			ArrayList<Topic> list = (ArrayList<Topic>) dbParamData.object;
			if (list != null) {
				topicList.addAll(list);
				myAlumAdapter.refresh(topicList);
			}
			getData(false);
			break;

		default:
			break;
		}

	}
}
