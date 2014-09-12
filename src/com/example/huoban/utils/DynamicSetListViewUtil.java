package com.example.huoban.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 动态设置ListView高度
 * @author talent.zhao
 * */
public class DynamicSetListViewUtil {
	/**
	 * 获取并设置ListView高度的方法
	 * @param listView
	 * */
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		
		if (listAdapter == null) {
			return;
		}
			
		int totalHeight = 0;

			for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
			
		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
			
		listView.setLayoutParams(params);
	}

}
