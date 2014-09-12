package com.example.huoban.activity.my.other;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.LogUtil;

public class SetPushMessage extends BaseActivity implements OnClickListener {

	private final static String TAG = "SetPushMessage";

	private TextView tv_title;

	private ImageButton ib_back;

	private ListView mListView;

	private ListAdapter mAdapter;

	private static final String[] keys = { "bill_changed", "has_new_answer", "featured_question", "system_message" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_other_message_push);
		initData();
		initView();
	}

	private void initData() {
		mAdapter = new ListAdapter(this);

		ListItem mListItem = new ListItem();
		mListItem.title = "钱包金额变动";
		mListItem.desc = "若关闭，钱包金额发生变动时，将不进行通知提示";
		mListItem.isChecked = application.getIsPushMessage("bill_changed", true);
		mAdapter.getArrayList().add(mListItem);

		mListItem = new ListItem();
		mListItem.title = "提问有新回答";
		mListItem.desc = "关闭后，您的问题有新回答时，仅在“装修助手”界面问答板块进行亮点提示";
		mListItem.isChecked = application.getIsPushMessage("has_new_answer", true);
		mAdapter.getArrayList().add(mListItem);
		mListItem = new ListItem();
		mListItem.title = "问题精选";
		mListItem.desc = "关闭后，问题精选有新内容时，仅在“装修助手”界面问答板块进行亮点提示";
		mListItem.isChecked = application.getIsPushMessage("featured_question", true);
		mAdapter.getArrayList().add(mListItem);
		mListItem = new ListItem();
		mListItem.title = "系统消息";
		mListItem.desc = "若关闭，有新的系统维护、升级等消息时，将不进行通知提示";
		mListItem.isChecked = application.getIsPushMessage("system_message", true);
		mAdapter.getArrayList().add(mListItem);
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("消息通知");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mListView = (ListView) findViewById(R.id.message_push_list);
		mListView.setAdapter(mAdapter);
	}

	public void onResume() {
		super.onResume();
		LogUtil.logI(TAG, "onResume");
	}

	public void onPause() {
		super.onPause();
		LogUtil.logI(TAG, "onPause");
	}

	public void onStop() {
		super.onStop();
		LogUtil.logI(TAG, "onStop");
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		}

	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	private class ListItem {
		public String title;
		public boolean isChecked;
		public String desc;
	}

	private static class ListAdapter extends BaseAdapter {

		private Context mContext;

		private ArrayList<ListItem> mArrayList = new ArrayList<ListItem>();

		public ListAdapter(Context mContext) {
			this.mContext = mContext;
		}

		public ArrayList<ListItem> getArrayList() {
			return mArrayList;
		}

		public int getCount() {
			return mArrayList.size();
		}

		public ListItem getItem(int arg0) {
			return mArrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int position, View consertView, ViewGroup parent) {
			View view = consertView;
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_other_message_push_item, null);
				holder = new ViewHolder();
				holder.title = (TextView) view.findViewById(R.id.message_push_item_title);
				holder.isChecked = (CheckBox) view.findViewById(R.id.message_push_item_ischecked);
				holder.desc = (TextView) view.findViewById(R.id.message_push_item_desc);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			final ListItem mListItem = getItem(position);
			if (mListItem != null) {
				holder.title.setText(mListItem.title);
				holder.title.getPaint().setFakeBoldText(true);

				holder.isChecked.setChecked(mListItem.isChecked);
				holder.isChecked.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mListItem.isChecked = !mListItem.isChecked;
						HuoBanApplication.getInstance().setIsPushMessage(keys[position], mListItem.isChecked);
					}
				});
				holder.desc.setText(mListItem.desc);
			}
			return view;
		}

		private static class ViewHolder {
			public TextView title, desc;
			public CheckBox isChecked;
		}

	}

}
