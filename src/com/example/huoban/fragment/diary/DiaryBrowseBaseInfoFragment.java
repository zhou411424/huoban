package com.example.huoban.fragment.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.model.DiaryModel;

public class DiaryBrowseBaseInfoFragment extends BaseFragment {

	 

	private DiaryModel diaryModel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browse_baseinfo,
				container, false);
		diaryModel=application.getDiaryModel();
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		((TextView)view.findViewById(R.id.tvHouseStyle)).setText(diaryModel.summary.house_type);
		((TextView)view.findViewById(R.id.tvHouseArea)).setText(diaryModel.summary.area);
		((TextView)view.findViewById(R.id.tvStyle)).setText(diaryModel.summary.style);
		((TextView)view.findViewById(R.id.tvHouseBudget)).setText(diaryModel.summary.budget);
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseBaseInfoFragment";
	}

}
