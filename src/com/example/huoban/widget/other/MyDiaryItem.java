package com.example.huoban.widget.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
/**
 * 我的日记单项布局
 * @author talent.zhao
 * */
public class MyDiaryItem extends RelativeLayout{
	private TextView titleTextView;
	private TextView timeTextView;
	private ImageView iconImageView;
	private ImageView updateImageView;

	public MyDiaryItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	public MyDiaryItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyDiaryItem(Context context) {
		this(context,null,0);
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.view_my_diary_items, this);
		titleTextView=(TextView) findViewById(R.id.tv_my_diary_title);
		iconImageView=(ImageView) findViewById(R.id.iv_my_diary_icon);
		updateImageView=(ImageView) findViewById(R.id.iv_my_diary_update);
	}

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public void setTitleTextView(TextView titleTextView) {
		this.titleTextView = titleTextView;
	}

	public TextView getTimeTextView() {
		return timeTextView;
	}

	public void setTimeTextView(TextView timeTextView) {
		this.timeTextView = timeTextView;
	}

	public ImageView getUpdateImageView() {
		return updateImageView;
	}

	public void setUpdateImageView(ImageView updateImageView) {
		this.updateImageView = updateImageView;
	}

	public ImageView getIconImageView() {
		return iconImageView;
	}

	public void setIconImageView(ImageView iconImageView) {
		this.iconImageView = iconImageView;
	}

}
