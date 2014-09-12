package com.example.huoban.widget.other;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class ResizeLayout extends RelativeLayout {

	private OnResizeListener mListener;

	public interface OnResizeListener {
		void resetLayout(boolean changed, int l, int t, int r, int b);
	}

	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.i("log", changed+","+l+","+t+","+r+","+b);
		if (mListener != null) {
			mListener.resetLayout(changed, l, t, r, b);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	} 
}
