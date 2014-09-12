package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.model.Content;
import com.example.huoban.utils.ViewHolder;

public class ContentAdapter extends BaseAdapter {

	public static final String TAG = "ContentAdapter";

	private ArrayList<Content> contentLists;

	private Context context;

	public ContentAdapter(Context context) {
		this.context = context;
	}

	public void setParam(ArrayList<Content> contentLists) {
		this.contentLists = contentLists;
	}

	@Override
	public int getCount() {
		return contentLists == null ? 0 : contentLists.size();
	}

	@Override
	public Object getItem(int position) {
		return contentLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_content, null);
		}
		
		TextView tvView = ViewHolder.get(convertView, R.id.text_view);
		TextView tvContent = ViewHolder.get(convertView, R.id.text_content);
		
		StringBuffer sb = new StringBuffer();
		int start = 0;
		if (!contentLists.get(position).getUserName().equals("")) {
			sb.append(contentLists.get(position).getUserName() + ": ");
		} 
		int end = sb.length();
		int secondStart = sb.length() + 1;
		sb.append(contentLists.get(position).getContent());
		int secondEnd = sb.length();
		
		tvView.setText(sb.toString());
		setTextViewColor(tvView, sb.toString(),
				Color.argb(255, 255, 72, 0), start, end, secondStart,
				secondEnd, Color.argb(255, 74, 74, 74));
		tvContent.setText(contentLists.get(position).getDateTime());
		return convertView;
	}
	
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}
	
	/**
	 * 设置字体样式.
	 */
	private void setTextViewColor(TextView textView, String str, int color,
			int start, int end, int start2, int end2, int color2) {
		SpannableStringBuilder style = new SpannableStringBuilder(str);

		style.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		if (start2 != 0 && end2 != 0) {
			style.setSpan(new ForegroundColorSpan(color2), start2, end2,
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		if (textView != null) {
			textView.setText(style);
		}
	}

}
