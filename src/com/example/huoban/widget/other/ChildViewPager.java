package com.example.huoban.widget.other;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {
	public ChildViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private int abc = 1;
	private float mLastMotionX;
	String TAG = "@";

	private float firstDownX;
	private float firstDownY;
	private boolean flag = false;

	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final float x = ev.getX();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			abc = 1;
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (abc == 1) {
				if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
					abc = 0;
					getParent().requestDisallowInterceptTouchEvent(false);
				}

				if (x - mLastMotionX < -5 && getCurrentItem() == getAdapter().getCount() - 1) {
					abc = 0;
					getParent().requestDisallowInterceptTouchEvent(false);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
			
		} catch (Exception e) {
			return true;
		} 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			return super.onTouchEvent(event);
			
		} catch (Exception e) {
			return true;
		}
	}
}
