package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.model.DiaryContentList;
import com.example.huoban.model.DiaryDetailData;

public class DiaryDrawerAdapter extends BaseAdapter {

	private Context context;
	private int selectedItem = -1;
	private OnComponentSelectedListener callBack;
	private List<String> items = new ArrayList<String>();
	private int type;

	public DiaryDrawerAdapter(Context context, OnComponentSelectedListener callBack, int type) {
		this.context = context;
		this.callBack = callBack;
		this.type = type;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			view = View.inflate(context, R.layout.item_diary_drawer_listview, null);
			holder = new ViewHolder();
			holder.top = (TextView) view.findViewById(R.id.line_top);
			holder.bottom = (TextView) view.findViewById(R.id.line_bottom);
			holder.mark = (TextView) view.findViewById(R.id.tvPosition);
			holder.circle = (ImageView) view.findViewById(R.id.black_circle);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (position == selectedItem) {
			view.setBackgroundColor(0xffff4800);
			holder.circle.setImageResource(R.drawable.icon_diary_drawer_item_selected);
			holder.top.setVisibility(View.INVISIBLE);
			holder.bottom.setVisibility(View.INVISIBLE);
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
			holder.circle.setImageResource(R.drawable.icon_diary_drawer_item);
			holder.top.setVisibility(View.VISIBLE);
			holder.bottom.setVisibility(View.VISIBLE);
		}

		if (position == 0) {
			holder.top.setVisibility(View.INVISIBLE);
		} else if (position == getCount() - 1) {
			holder.bottom.setVisibility(View.INVISIBLE);
		}

		holder.mark.setText(items.get(position));

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedItem = position;
				DiaryDrawerAdapter.this.notifyDataSetChanged();
				if (callBack != null) {
					callBack.onComponentSelected(0, position);
				}
			}
		});
		return view;
	}

	class ViewHolder {
		TextView top, bottom;
		TextView mark;
		ImageView circle;
	}

	public void refresh(DiaryDetailData diaryDetailData) {
		items.add("毕业照");
		if (type == 0) {
			if (diaryDetailData.item_list != null) {
				items.add("商品清单");
			}
		}
		for (DiaryContentList d : diaryDetailData.diary_list) {
			if (d.content != null)
				items.add(d.date);
		}
		this.notifyDataSetChanged();
	}

	public void refresh(List<DiaryContentList> diary_list) {
		if (type == 1) {
			items.add("毕业照");
		}
		if (diary_list != null) {
			for (DiaryContentList diaryContentList : diary_list) {
				if (diaryContentList.content != null)
					items.add(diaryContentList.date);
			}
		}
		this.notifyDataSetChanged();
	}

	public void setSelectedItem(int position, boolean isGraduate) {
		if (isGraduate) {
			selectedItem = position;
		} else {
			selectedItem = position + 1;
		}
		this.notifyDataSetChanged();

	}
}
