package com.example.huoban.widget.pull;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;


public class PullToRefreshPinnedHeaderListView extends PullToRefreshAdapterViewBase<PinnedHeaderListView> {


	public PullToRefreshPinnedHeaderListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}
	
	public PullToRefreshPinnedHeaderListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshPinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
	}


	@Override
	protected final PinnedHeaderListView createRefreshableView(Context context, AttributeSet attrs) {
		PinnedHeaderListView lv = new PinnedHeaderListView(context, attrs);
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		// TODO Auto-generated method stub
		return null;
	}


}
