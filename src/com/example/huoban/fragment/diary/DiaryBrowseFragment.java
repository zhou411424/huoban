package com.example.huoban.fragment.diary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.huoban.R;
import com.example.huoban.activity.diary.DiaryBrowseActivity;
import com.example.huoban.activity.diary.DiaryCommentActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.model.DiaryContentList;
import com.example.huoban.model.GraduationPhoto;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ShareUtil;

public class DiaryBrowseFragment extends BaseFragment implements OnClickListener {
	private ProgressBar pb;
	private ViewPager mViewPager;
	private List<BaseFragment> listFragment = new ArrayList<BaseFragment>();
	private int currentItem;
	private DiaryBrowseActivity diaryBrowseActivity;
	private MyPagerAdapter myPagerAdapter;
	private ImageButton ibtnComment, ibtnLike, ibtnShare, ibtnDrawer;
	private List<DiaryContentList> diary_list;
	private CompanyDetail companyDetail;
	private List<GraduationPhoto> graduate;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		diaryBrowseActivity = (DiaryBrowseActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diary_browse, container, false);
		currentItem = getArguments() == null ? 0 : getArguments().getInt("currentItem", 0);
		// diaryDetailData = (DiaryDetailData) getArguments().getSerializable(
		// "diaryDetailData");
		diary_list = application.getDiaryDetailData();
		companyDetail = (CompanyDetail) getArguments().getSerializable("companyDetail");
		graduate = (List<GraduationPhoto>) getArguments().getSerializable("graduate");
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		ibtnComment = (ImageButton) diaryBrowseActivity.findViewById(R.id.ibtn_comment);
		ibtnComment.setOnClickListener(this);
		ibtnLike = (ImageButton) diaryBrowseActivity.findViewById(R.id.ibtn_like);
		ibtnShare = (ImageButton) diaryBrowseActivity.findViewById(R.id.ibtn_share);
		ibtnDrawer = (ImageButton) diaryBrowseActivity.findViewById(R.id.ibtn_drawer);
		getActivity().findViewById(R.id.ibtn_share).setOnClickListener(this);

		pb = (ProgressBar) view.findViewById(R.id.pbDiary);
		mViewPager = (ViewPager) view.findViewById(R.id.vPDiary);
		initListFragment();
		myPagerAdapter = new MyPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setCurrentItem(currentItem, false);
		hideOrShowTitleButton(currentItem);
		pb.setMax(listFragment.size());
		pb.setProgress(currentItem + 1);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pb.setProgress(position + 1);
				hideOrShowTitleButton(position);
				if (position != 0 && position != 1 && position != listFragment.size() - 1)
					refreshDrawerListViewPosition(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void initListFragment() {
		DiaryBrowseCoverFragment coverFragment = DiaryBrowseCoverFragment.getInstance(companyDetail);
		listFragment.add(coverFragment);
		DiaryBrowseBaseInfoFragment baseInfoFragment = new DiaryBrowseBaseInfoFragment();
		listFragment.add(baseInfoFragment);
		if (graduate != null) {
			GraduationPhotosFragment graduateFragment = GraduationPhotosFragment.getInstance(graduate);
			listFragment.add(graduateFragment);
		}
		for (int i = 0; diary_list != null && i < diary_list.size(); i++) {
			if (diary_list.get(i).content != null) {
				DiaryBrowseCalendarFragment calendarFragment = DiaryBrowseCalendarFragment.getInstance(diary_list.get(i).date, i);
				listFragment.add(calendarFragment);
				for (int j = 0; j < diary_list.get(i).content.size(); j++) {
					if (diary_list.get(i).content.get(j).type == 1) {
						DiaryBrowseArticleFragment articleFragment = new DiaryBrowseArticleFragment();
						Bundle args = new Bundle();
						args.putSerializable("diaryContent", diary_list.get(i).content.get(j));
						articleFragment.setArguments(args);
						listFragment.add(articleFragment);
					} else {
						DiaryBrowseImageFragment imageFragment = new DiaryBrowseImageFragment();
						Bundle args = new Bundle();
						args.putSerializable("diaryContent", diary_list.get(i).content.get(j));
						imageFragment.setArguments(args);
						listFragment.add(imageFragment);
					}
				}
			}
		}
		listFragment.add(DiaryBrowseLastPageFragment.getInstance(companyDetail));
	}

	protected void hideOrShowTitleButton(int position) {
		Fragment fragment = myPagerAdapter.getItem(position);
		if (fragment instanceof DiaryBrowseCoverFragment || fragment instanceof DiaryBrowseBaseInfoFragment || fragment instanceof DiaryBrowseCalendarFragment || fragment instanceof GraduationPhotosFragment) {
			ibtnComment.setVisibility(View.GONE);
			ibtnShare.setVisibility(View.GONE);
		} else if (fragment instanceof DiaryBrowseLastPageFragment) {
			ibtnComment.setVisibility(View.GONE);
			ibtnShare.setVisibility(View.GONE);
			ibtnLike.setVisibility(View.GONE);
			ibtnDrawer.setVisibility(View.GONE);
		} else {
			ibtnComment.setVisibility(View.VISIBLE);
			ibtnShare.setVisibility(View.VISIBLE);
			ibtnLike.setVisibility(View.VISIBLE);
			ibtnDrawer.setVisibility(View.VISIBLE);
		}
	}

	private void refreshDrawerListViewPosition(int position) {
		LogUtil.logE("TAG", "position=" + position);
		int selected = -1;
		Fragment fragment = myPagerAdapter.getItem(position);
		if (fragment instanceof GraduationPhotosFragment) {
			selected = 0;
			diaryBrowseActivity.diaryDrawerAdapter.setSelectedItem(selected, true);
			diaryBrowseActivity.mDrawerListView.setSelection(selected);
			return;
		} else if (fragment instanceof DiaryBrowseCalendarFragment) {
			DiaryBrowseCalendarFragment dcf = (DiaryBrowseCalendarFragment) fragment;
			selected = dcf.position;
		} else if (fragment instanceof DiaryBrowseArticleFragment) {
			DiaryBrowseArticleFragment daf = (DiaryBrowseArticleFragment) fragment;
			selected = daf.diaryContent.category;
		} else if (fragment instanceof DiaryBrowseImageFragment) {
			DiaryBrowseImageFragment dif = (DiaryBrowseImageFragment) fragment;
			selected = dif.diaryContent.category;
		}
		diaryBrowseActivity.diaryDrawerAdapter.setSelectedItem(selected, false);
		diaryBrowseActivity.mDrawerListView.setSelection(selected);
	}

	@Override
	protected void refresh(Object... param) {

	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return listFragment.get(position);
		}

		@Override
		public int getCount() {
			return listFragment.size();
		}

	}

	public void setCurrentItem(int item) {
		mViewPager.setCurrentItem(item, false);
	}

	@Override
	public void onClick(View v) {
		Fragment fragment = myPagerAdapter.getItem(mViewPager.getCurrentItem());
		switch (v.getId()) {
		case R.id.ibtn_comment:
			if (fragment instanceof DiaryBrowseArticleFragment) {
				DiaryBrowseArticleFragment dbf = (DiaryBrowseArticleFragment) fragment;
				Intent intent = new Intent(diaryBrowseActivity, DiaryCommentActivity.class);
				intent.putExtra("diaryContent", dbf.diaryContent);
				startActivity(intent);
			} else if (fragment instanceof DiaryBrowseImageFragment) {
				DiaryBrowseImageFragment dbif = (DiaryBrowseImageFragment) fragment;
				Intent intent = new Intent(diaryBrowseActivity, DiaryCommentActivity.class);
				intent.putExtra("diaryContent", dbif.diaryContent);
				startActivity(intent);
			}
			break;
		case R.id.ibtn_share:
			String url = null;
			if (fragment instanceof DiaryBrowseArticleFragment) {
				DiaryBrowseArticleFragment dbf = (DiaryBrowseArticleFragment) fragment;
				url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=diary&id=" + dbf.diaryContent.reply_pid + "&date=" + dbf.diaryContent.date;
			} else if (fragment instanceof DiaryBrowseImageFragment) {
				DiaryBrowseImageFragment dbif = (DiaryBrowseImageFragment) fragment;
				url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=diary&id=" + dbif.diaryContent.reply_pid + "&date=" + dbif.diaryContent.date;
			}
			String title = "推荐装修日记：" + application.getDiaryModel().diary_title;
			ShareUtil.showOnekeyshare(getActivity(), null, false, title, url);
			break;
		}
	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseFragment";
	}
}
