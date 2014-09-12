package com.example.huoban.fragment.diary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.huoban.R;
import com.example.huoban.base.BaseFragment;

public class SearchDiaryDefaultFragment extends BaseFragment {

	private RadioGroup mTab;
	public ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_diary_normal,
				container, false);
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		mTab = (RadioGroup) view.findViewById(R.id.rg);
		mViewPager = (ViewPager) view.findViewById(R.id.search_viewpager);
		mViewPager.setAdapter(new MyFragmentAdapter(getFragmentManager()));
		mTab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int item = 0;
				switch (checkedId) {
				case R.id.rb_style:
					item = 0;
					break;
				case R.id.rb_room:
					item = 1;
					break;
				case R.id.rb_budget:
					item = 2;
					break;
				}
				mViewPager.setCurrentItem(item);
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				((RadioButton) mTab.getChildAt(position)).setChecked(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	protected void refresh(Object... param) {

	}

	private class MyFragmentAdapter extends FragmentPagerAdapter {

		public MyFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			BaseFragment fragment = null;
			switch (position) {
			case 0:
				fragment = new SearchDiaryStyleFragment();
				break;
			case 1:
				fragment=SearchDiaryTabFragment.getInstance(1);
				break;
			case 2:
				fragment=SearchDiaryTabFragment.getInstance(2);
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return super.instantiateItem(container, position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	protected String setFragmentName() {
		return "SearchDiaryDefaultFragment";
	}
}
