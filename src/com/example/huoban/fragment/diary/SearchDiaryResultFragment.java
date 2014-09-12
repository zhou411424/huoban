package com.example.huoban.fragment.diary;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.adapter.DiaryAdapter;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.DiaryData;
import com.example.huoban.model.DiaryListResult;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class SearchDiaryResultFragment extends BaseFragment implements OnRefreshListener, OnComponentSelectedListener {
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private DiaryAdapter mAdapter;
	private String keyWord;
	private int page = 1;
	private List<DiaryData> diaryList;
	private View tempView;
	private int s, e;
	private int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		keyWord = getArguments() == null ? "" : getArguments().getString("keyWord");
		s = getArguments() == null ? 0 : getArguments().getInt("s");
		e = getArguments() == null ? 0 : getArguments().getInt("e");
		type = getArguments() == null ? 0 : getArguments().getInt("type");

		if (savedInstanceState != null) {
			keyWord = savedInstanceState.getString("keyWord");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_diary_result, container, false);
		setupViews(view);
		doSearchDiary();
		return view;
	}

	private void doSearchDiary() {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_diary/search?";
		HashMap<String, String> params = new HashMap<String, String>();
		switch (type) {
		case 0:
			params.put("kw", keyWord);
			break;
		case 1:
			params.put("kw", keyWord);
			params.put("type", "style");
			break;
		case 2:
			params.put("kw", keyWord);
			params.put("type", "house_type");
			break;
		case 3:
			params.put("s", s + "");
			if (e != 0)
				params.put("e", e + "");
			params.put("type", "budget");
			break;
		}
		params.put("page", page + "");
		params.put("limit", 5 + "");
		task.taskParam = params;
		task.resultDataClass = DiaryListResult.class;
		if (page == 1)
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
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setRefreshEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		mAdapter = new DiaryAdapter(getActivity(), this);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		Task task = (Task) param[0];
		DiaryListResult result = (DiaryListResult) task.result;
		if (page == 1) {
			diaryList = result.data;
		} else {
			diaryList.addAll(result.data);
		}
		if (result.count > diaryList.size()) {
			mPullToRefreshListView.setLoadMoreEnable(true);
		} else {
			mPullToRefreshListView.setLoadMoreEnable(false);
		}
		initDiary(diaryList, result);
	}

	private void initDiary(List<DiaryData> diaryList, DiaryListResult result) {

		if (getActivity() == null) {
			return;
		}

		View headView = null;
		if (result.status == 2) {
			headView = View.inflate(getActivity(), R.layout.headview_null_result, null);
		} else {
			headView = View.inflate(getActivity(), R.layout.headview_has_result, null);
			TextView hint = (TextView) headView.findViewById(R.id.result_hint);
			String word = getActivity().getResources().getString(R.string.has_result, "\"" + keyWord + "\"", result.count);
			int a = word.indexOf("为");
			int b = word.indexOf("到");
			int c = word.indexOf("个");
			SpannableString spannableString = new SpannableString(word);
			spannableString.setSpan(new ForegroundColorSpan(0xffff4800), 0, a, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(0xffff4800), b + 1, c, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			hint.setText(spannableString);
		}
		if (tempView != null) {
			mListView.removeHeaderView(tempView);
		}
		if (headView != null) {
			mListView.addHeaderView(headView);
			tempView = headView;
		}
		mListView.setAdapter(mAdapter);
		mAdapter.refresh(diaryList);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("keyWord", keyWord);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		page++;
		doSearchDiary();
	}

	@Override
	protected String setFragmentName() {
		return "SearchDiaryResultFragment";
	}

	@Override
	public void onComponentSelected(int nResId, Object... obj) {
		TextView view_count = (TextView) obj[0];
		view_count.setText((Integer.valueOf(view_count.getText().toString()) + 1) + "");
	}
}
