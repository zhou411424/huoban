package com.example.huoban.fragment.diary;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.SearchDiaryActivity;
import com.example.huoban.adapter.DiaryAdapter;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.DiaryData;
import com.example.huoban.model.DiaryListResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class DiaryFragment extends BaseFragment implements OnRefreshListener, OnClickListener, OnComponentSelectedListener {

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private DiaryAdapter mAdapter;
	private List<DiaryData> diaryList;
	private int page = 1;
	private boolean isFirst;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diary, container, false);
		setupViews(view);
		return view;
	}

	@Override
	public void initDataForChoised() {
		super.initDataForChoised();
		if (!isFirst) {
			isFirst = true;
			doGetDiaryList(true);
		}
	}

	private void doGetDiaryList(boolean showProgress) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_diary/diary?";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("limit", 5 + "");
		params.put("page", page + "");
		task.taskParam = params;
		task.resultDataClass = DiaryListResult.class;
		if (showProgress)
			showProgress(null, 0, false);
		doTask(task);
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null)
			mPullToRefreshListView.onRefreshComplete();
	}

	@Override
	protected void setupViews(View view) {
		TextView title = (TextView) view.findViewById(R.id.tv_title);
		title.setText(R.string.diary);
		ImageButton ibtn = (ImageButton) view.findViewById(R.id.ibtn_right);
		ibtn.setImageResource(R.drawable.icon_search_diary);
		ibtn.setVisibility(View.VISIBLE);
		ibtn.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.PullToRefreshListView);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		mAdapter = new DiaryAdapter(getActivity(), this);
		mListView.setAdapter(mAdapter);
		// mListView.setOnItemClickListener(this);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		Task task = (Task) param[0];
		DiaryListResult result = (DiaryListResult) task.result;
		if (result == null || result.data == null) {
			return;
		}
		if (page == 1) {
			diaryList = result.data;
		} else {
			diaryList.addAll(result.data);
		}
		if (result.total > diaryList.size()) {
			mPullToRefreshListView.setLoadMoreEnable(true);
		} else {
			mPullToRefreshListView.setLoadMoreEnable(false);
		}
		mAdapter.refresh(diaryList);
	}

	@Override
	public void onRefresh() {
		page = 1;
		doGetDiaryList(false);
	}

	@Override
	public void onLoadMore() {
		page++;
		doGetDiaryList(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_right:
			Intent intent = new Intent(getActivity(), SearchDiaryActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected String setFragmentName() {
		return "DiaryFragment";
	}

	@Override
	public void onComponentSelected(int nResId, Object... obj) {
		TextView view_count = (TextView) obj[0];
		view_count.setText((Integer.valueOf(view_count.getText().toString()) + 1) + "");
	}

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// Intent intent = new Intent(getActivity(), DiaryDetailActivity.class);
	// intent.putExtra("diary_id", diaryList.get(position).diary_id);
	// startActivity(intent);
	// }

	public void refreshLikeCount(int position, boolean like) {
		// if (like && position < diaryList.size()) {
		// diaryList.get(position).focus_num += 1;
		// } else if (position < diaryList.size()) {
		// if (diaryList.get(position).focus_num != 0)
		// diaryList.get(position).focus_num -= 1;
		// }
		LogUtil.logE("TAG", "diaryfragment position="+position);
		if (like) {
			diaryList.get(position).focus_num += 1;
		} else {
			if (diaryList.get(position).focus_num != 0)
				diaryList.get(position).focus_num -= 1;
		}
		mAdapter.refresh(diaryList);
	}
}
