package com.example.huoban.widget.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;

public class MyListItemView extends RelativeLayout {

	private ImageView iconLeft;
	private ImageView iconRight;
	private TextView title;

	public MyListItemView(Context context) {
		super(context);
		initView(context);
	}

	public MyListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MyListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_my_item, this);
		// layout = (RelativeLayout) findViewById(R.id.layout_my_item);
		iconLeft = (ImageView) findViewById(R.id.iv_item_icon);
		iconRight = (ImageView) findViewById(R.id.iv_item_right);
		title = (TextView) findViewById(R.id.tv_item_title);
	}

	public ImageView getIconLeft() {
		return iconLeft;
	}

	public void setIconLeft(ImageView iconLeft) {
		this.iconLeft = iconLeft;
	}

	public ImageView getIconRight() {
		return iconRight;
	}

	public void setIconRight(ImageView iconRight) {
		this.iconRight = iconRight;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}
}