package com.example.huoban.activity.my.contacts;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchResultActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "SearchResultActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private ListView mListView;

	private String query;

	private ListAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		LogUtil.logI(TAG, " query =  " + getIntent().getStringExtra("query"));
		if (getIntent().getSerializableExtra("result") == null || getIntent().getStringExtra("query") == null)
			finish();
		setContentView(R.layout.layout_contacts_search_result);
		initTitleBar();
		query = getIntent().getStringExtra("query");
		mListView = (ListView) findViewById(R.id.layout_contacts_search_list);
		mAdapter = new ListAdapter(this);
		mAdapter.setContacts((ArrayList<Contact>) getIntent().getSerializableExtra("result"));
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

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索结果");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		}
	}

	private static class ViewHolder {
		ImageView icon;
		TextView name;
	}

	private class ListAdapter extends BaseAdapter {

		private ArrayList<Contact> mContacts = new ArrayList<Contact>();

		private final Context mContext;

		private ImageLoader mImageLoader = ImageLoader.getInstance();

		private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).showImageOnLoading(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

		private Intent mIntent = new Intent();

		public ListAdapter(Context mContext) {
			this.mContext = mContext;
		}

		public void setContacts(ArrayList<Contact> mContacts) {
			this.mContacts = mContacts;
		}

		public ArrayList<Contact> getContacts() {
			return mContacts;
		}

		public int getCount() {
			return mContacts.size();
		}

		public Contact getItem(int arg0) {
			return mContacts.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			ViewHolder holder;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_contacts_search_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) view.findViewById(R.id.contacts_search_item_icon);
				holder.name = (TextView) view.findViewById(R.id.contacts_search_item_name);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final Contact mContact = getItem(arg0);

			if (mContact != null) {

				mImageLoader.displayImage(mContact.avatar, holder.icon, options);

				String text = "";
				if ("".equals(mContact.mobile) || "null".equals(mContact.mobile)) {
					if (mContact.remark_name != null && !mContact.remark_name.equals("null") && !mContact.remark_name.equals("")) {
						text = mContact.remark_name;
					} else if (mContact.nick != null && !mContact.nick.equals("null") && !mContact.nick.equals("")) {
						text = mContact.nick;
					} else {
						text = mContact.user_name;
					}
				} else {
					StringBuilder mobile = new StringBuilder();
					mobile.append(mContact.mobile.subSequence(0, 3));
					mobile.append("*****");
					mobile.append(mContact.mobile.substring(8, 11));
					if (mContact.remark_name != null && !mContact.remark_name.equals("null") && !mContact.remark_name.equals("")) {
						text = mContact.remark_name + "(" + mobile.toString() + ")";
					} else if (mContact.nick != null && !mContact.nick.equals("null") && !mContact.nick.equals("")) {
						text = mContact.nick + "(" + mobile.toString() + ")";
					} else {
						text = mContact.user_name + "(" + mobile.toString() + ")";
					}
				}
				int index = text.toLowerCase(Locale.CHINA).indexOf(query.toLowerCase(Locale.CHINA));

				SpannableString sp = new SpannableString(text);

				if (index >= 0 && index < text.length())
					sp.setSpan(new ForegroundColorSpan(res.getColor(R.color.foot_orange)), index, index + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				holder.name.setText(sp);

				view.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mIntent.setClass(mContext, ContactInfoActivity.class);
						mIntent.putExtra("contact", mContact);
						mContext.startActivity(mIntent);
					}
				});
			}
			return view;
		}
	}
}
