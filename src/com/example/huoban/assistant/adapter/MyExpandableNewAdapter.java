package com.example.huoban.assistant.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.ViewFlipperActivity;
import com.example.huoban.assistant.model.CateData;
import com.example.huoban.assistant.model.MaterialInfo;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.utils.ViewHolder;

public class MyExpandableNewAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<CateData> groupLists;
	private SparseArray<View> groupMap;
	private Activity activity;
	public MyExpandableNewAdapter(Context context){
		this.context = context;
	}

	public void setParam(Activity activity, List<CateData> groupLists) {
		this.groupLists = groupLists;
		this.activity = activity;
		this.groupMap = new SparseArray<View>();
	}

	@Override
	public int getGroupCount() {
		return groupLists == null ? 0 : groupLists.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupLists.get(groupPosition).material_info == null) {
			return 0;
		}
		return groupLists.get(groupPosition).material_info.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupLists.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groupLists.get(groupPosition).material_info == null) {
			return null;
		}
		return groupLists.get(groupPosition).material_info.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupMap.get(groupPosition) == null) {
//			// 布局文件
			convertView = LayoutInflater.from(context).inflate(
					R.layout.assistant_group_new, null);
			groupMap.put(groupPosition, convertView);
		} else {
			convertView = groupMap.get(groupPosition);
		}
		TextView tv = ViewHolder.get(convertView, R.id.group_text_view);
		tv.setText((groupPosition + 1) + "、" + groupLists.get(groupPosition).cate_name);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<CateData> cateList = new ArrayList<CateData>();
				int position = 0;
				for (int i = 0; i < groupLists.size(); i++) {
					if (i == groupPosition) {
						position = cateList.size() + groupPosition;
					}
				}
				cateList.addAll(groupLists);
				HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CURRENT_PAGE, position, context);
				Intent intent = new Intent();
				intent.putExtra("cateList", cateList);
				intent.setClass(activity, ViewFlipperActivity.class);
				activity.startActivityForResult(intent, 1);
			}
		});
		int cateId = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_SET_CATE_ID, -1, activity);
		if(groupLists.get(groupPosition).cate_id == cateId){
			ViewHolder.get(convertView, R.id.item_image_icon).setVisibility(View.VISIBLE);
		}else{
			ViewHolder.get(convertView, R.id.item_image_icon).setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// 布局文件
			convertView = LayoutInflater.from(context).inflate(
					R.layout.assistant_child_new, null);
		}
		TextView childTv = ViewHolder.get(convertView, R.id.child_text_view);
		childTv.setText("- " + groupLists.get(groupPosition).material_info
		.get(childPosition).material_name);

		childTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<MaterialInfo> materialList = new ArrayList<MaterialInfo>();
//				Log.i("log", "groupPosition=" + groupPosition);
				int position = 0;
				for (int i = 0; i < groupLists.size(); i++) {
					if (i == groupPosition) {
						position = materialList.size() + childPosition;
					}
				}
				if (groupLists.get(groupPosition).material_info != null) {
					materialList.addAll(groupLists.get(groupPosition).material_info);
				}
				HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CURRENT_PAGE, position, context);
				Intent intent = new Intent();
				intent.putExtra("materialList", materialList);
				intent.setClass(activity, ViewFlipperActivity.class);
				activity.startActivityForResult(intent, 1);

			}
		});

		if(isLastChild){
			ViewHolder.get(convertView, R.id.viewFullLine).setVisibility(View.VISIBLE);
			ViewHolder.get(convertView, R.id.materialLine).setVisibility(View.GONE);
		}else{
			ViewHolder.get(convertView, R.id.materialLine).setVisibility(View.VISIBLE);
			ViewHolder.get(convertView, R.id.viewFullLine).setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
