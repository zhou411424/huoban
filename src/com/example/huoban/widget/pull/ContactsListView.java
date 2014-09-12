package com.example.huoban.widget.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ListView;

public class ContactsListView extends PullToRefreshAdapterViewBase<SwipeListView> {

	class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			ContactsListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}
	}

	public ContactsListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public ContactsListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public ContactsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public ContextMenuInfo getContextMenuInfo() {
		return null;
	}

	protected final SwipeListView createRefreshableView(Context context, AttributeSet attrs) {
		SwipeListView lv = new SwipeListView(context, attrs);
		lv.setId(android.R.id.list);
		return lv;
	}

}
