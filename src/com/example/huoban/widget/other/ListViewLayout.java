package com.example.huoban.widget.other;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.example.huoban.R;
import com.example.huoban.assistant.adapter.ContentAdapter;
import com.example.huoban.assistant.model.Content;
import com.example.huoban.utils.ListViewUtils;

public class ListViewLayout extends LinearLayout {

	private Context mContext;

	private ArrayList<Content> contentLists;

	private NoScrollListView listView;

	private ContentAdapter contentAdapter;

	private LinearLayout moreView;

	public ListViewLayout(Context context) {
		super(context);
		this.mContext = context;
	}

	public ListViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void setParam(ArrayList<Content> contentLists) {
		this.contentLists = contentLists;
		initView(mContext);
	}  
    
	private void initView(Context context) {  
		mContext = context; 
		if (moreView == null) {
			moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.content, null);
			addView(moreView);
			moreView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			listView = (NoScrollListView) this.findViewById(R.id.list_view);
			contentAdapter = new ContentAdapter(mContext);
			contentAdapter.setParam(contentLists);
			listView.setAdapter(contentAdapter);
		} else {
			contentAdapter.setParam(contentLists);
			contentAdapter.notifyDataSetChanged();
		} 
		ListViewUtils.setListViewHeightBasedOnChildren(listView);
	}

	public ArrayList<Content> getList(){
		return contentLists;
	}
	public void updateData() {
		contentAdapter.notifyDataSetChanged();
	}
	
	public BaseAdapter getAdapter(){
		return contentAdapter;
	}
}
