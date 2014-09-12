package com.example.huoban.assistant.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
/*
 * viewpager适配器
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private List<View> views;
	
	public MyViewPagerAdapter(List<View> views){
		this.views = views;
	}
	
	@Override
	public int getCount() {
		return views == null ? 0 : views.size();
	}

	@Override
	public Object instantiateItem(View v, int position) {
		View view = views.get((position % views.size()));
		((ViewPager) v).addView(view);
		
		return views.get(position % views.size());
	}

	@Override
	public void destroyItem(View arg0, int position, Object arg2) {
		((ViewPager) arg0).removeView(views.get(position % views.size()));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}
}
