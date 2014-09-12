package com.example.huoban.fragment.diary;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseFragment;

public class DiaryBrowseCalendarFragment extends BaseFragment {
	
	private String calendar;
	public int position;
	
	public static DiaryBrowseCalendarFragment getInstance(String calendar,int position){
		DiaryBrowseCalendarFragment fragment=new DiaryBrowseCalendarFragment();
		fragment.calendar=calendar;
		fragment.position=position;
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_browse_date,container,false);
		setupViews(view);
		return view;
	}
	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		Typeface tfArial = Typeface.createFromAsset(getActivity().getAssets(), "arial.ttf");
		TextView c=(TextView) view.findViewById(R.id.tvDate);
		c.setTypeface(tfArial);
		c.setText(calendar);
	}

	@Override
	protected void refresh(Object... param) {

	}
	@Override
	protected String setFragmentName() {
		return "DiaryBrowseCalendarFragment";
	}

}
