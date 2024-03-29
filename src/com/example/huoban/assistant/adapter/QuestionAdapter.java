package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.model.QuestionBean;
import com.example.huoban.utils.ViewHolder;

/**
 * @ClassName: SupervisorAdapter
 * @Description: 问题适配器
 * 
 */
public class QuestionAdapter extends BaseAdapter {
	private ArrayList<QuestionBean> menuList;
	private Activity activity;

	public QuestionAdapter(ArrayList<QuestionBean> Str, Activity activity) {
		this.menuList = Str;
		this.activity = activity;
	}
	
	public void updateAdapter(ArrayList<QuestionBean> Str){
		this.menuList = Str;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return menuList.size();
	}

	@Override
	public Object getItem(int position) {
		return menuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		QuestionBean bean = menuList.get(position);
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.popupwindow_item, null);
		}
		TextView tvName = ViewHolder.get(convertView, R.id.tvName);
		tvName.setText(bean.getMenu_title());
		return convertView;
	}
}
