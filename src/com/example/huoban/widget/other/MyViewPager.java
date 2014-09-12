package com.example.huoban.widget.other;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {


	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			
			
			return super.onInterceptTouchEvent(arg0);
		} catch (Exception e) {
			return false;
		}

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		try {
			

			return super.onTouchEvent(arg0);
		} catch (Exception ex) {
			return false;
		}

	}
	
	
}
