package com.example.huoban.widget.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;

public class OtherListItemView extends RelativeLayout {

	private TextView title;
	private TextView desc;

	public OtherListItemView(Context context) {
		super(context);
		initView(context);
	}

	public OtherListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public OtherListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_other_item, this);
		title = (TextView) findViewById(R.id.other_item_title);
		desc = (TextView) findViewById(R.id.other_item_version_text);
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public TextView getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc.setText(desc);
	}

}