package com.example.huoban.widget.other;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyDrawerLayout extends DrawerLayout {

	public MyDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
			
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		} 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			return super.onTouchEvent(event);
			
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
