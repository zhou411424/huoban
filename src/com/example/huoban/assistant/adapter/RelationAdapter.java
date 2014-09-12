package com.example.huoban.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.utils.ViewHolder;

/**
 * @Description: 联系我们适配器
 * 
 */
public class RelationAdapter extends BaseAdapter {
	private String[] menuList;
	private Activity activity;

	public RelationAdapter(String[] Str, Activity activity) {
		this.menuList = Str;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return menuList == null ? 0 : menuList.length;
	}

	@Override
	public Object getItem(int position) {
		return menuList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.popupwindow_item, null);
		}
		TextView tvName = ViewHolder.get(convertView, R.id.tvName);
		tvName.setText(menuList[position]);

		return convertView;
	}
}
