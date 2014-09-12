package com.example.huoban.widget.other;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.utils.LogUtil;

public class OutFromBottomPopupWindow extends PopupWindow {
	private TextView mCancel;
	private View mMenuView;
	private NoScrollListView mListView;
	private String[] items;

	public OutFromBottomPopupWindow(Context context, OnItemClickListener itemsClickListener, String[] items, String title) {
		super(context);
		mMenuView = View.inflate(context, R.layout.addphoto_popupwindow_layout, null);
		mListView = (NoScrollListView) mMenuView.findViewById(R.id.choiseItemList);
		mCancel = (TextView) mMenuView.findViewById(R.id.btn_cancel);
		// 取消按钮
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 设置按钮监听
		mListView.setOnItemClickListener(itemsClickListener);
		// 设置SharePopupWindow的View
		this.setContentView(mMenuView);
		// 设置SharePopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SharePopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SharePopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		this.items = items;
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.layout_menu).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}

		});
		LogUtil.logE("title:"+title);
		TextView tvTitle = (TextView) mMenuView.findViewById(R.id.tv_title);
		if (title != null) {
			LogUtil.logE("title:"+title);
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		}
		mListView.setAdapter(new myAdapter(context));
	}

	private class myAdapter extends BaseAdapter {
		private Context mContext;

		public myAdapter(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.add_photo_pw_item, null);
			textView.setText(items[position]);
			return textView;
		}
	}
}
