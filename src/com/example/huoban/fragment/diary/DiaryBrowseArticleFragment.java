package com.example.huoban.fragment.diary;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.DiaryBrowseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.model.DiaryContent;

public class DiaryBrowseArticleFragment extends BaseFragment {

	public DiaryContent diaryContent;
	private DiaryBrowseActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (DiaryBrowseActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browse_article,
				container, false);
		diaryContent=(DiaryContent) getArguments().getSerializable("diaryContent");
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		((TextView) view.findViewById(R.id.tvArticle))
				.setText(diaryContent.reply_content);
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseArticleFragment";
	}

}
