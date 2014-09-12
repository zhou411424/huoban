package com.example.huoban.activity.my;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.LogUtil;

public class MoreSourceActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "MoreSourceActivity";

	private ListView mListView;

	private MoreSourceAdapter mAdapter;

	private TextView tv_title;

	private ImageButton ib_back;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_more_source);
		initData();
		initView();
	}

	private void initView() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("更多齐家资源");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.more_source_list);
		mListView.setAdapter(mAdapter);

	}

	private void initData() {
		mAdapter = new MoreSourceAdapter(this);
		/**
		 * 齐家网
		 */
		ListItem mListItem = new ListItem();
		mListItem.title = "齐家网";
		mListItem.descString = "让装修像喝茶一样轻松！";
		mListItem.packageName = "com.qijia.o2o";
		mListItem.urlToDownLoad = "http://m.jia.com/qjzx";
		mListItem.isInstall = InstalledOrNot(mListItem.packageName);
		mListItem.icon = res.getDrawable(R.drawable.qijianet_icon);
		mAdapter.getListItems().add(mListItem);
		/**
		 * 齐家图库
		 */
		mListItem = new ListItem();
		mListItem.title = "齐家图库";
		mListItem.descString = "海量高清装修效果图尽在掌握！";
		mListItem.packageName = "com.suryani.jiagallery";
		mListItem.urlToDownLoad = "http://m.jia.com/qjtk";
		mListItem.isInstall = InstalledOrNot(mListItem.packageName);
		mListItem.icon = res.getDrawable(R.drawable.qijiapic_icon);
		mAdapter.getListItems().add(mListItem);
		/**
		 * 齐家wap
		 */
		mListItem = new ListItem();
		mListItem.title = "齐家WAP";
		mListItem.descString = "让装修像喝茶一样轻松！";
		mListItem.packageName = "";
		mListItem.urlToDownLoad = "http://m.jia.com/";
		mListItem.isInstall = true;
		mListItem.icon = res.getDrawable(R.drawable.qijiawap_icon);
		mAdapter.getListItems().add(mListItem);
	}

	private boolean InstalledOrNot(String packageName) {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
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
		default:
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
		public String packageName;
		public String urlToDownLoad;
		public Drawable icon;
		public String descString;
		public boolean isInstall;
	}

	private class MoreSourceAdapter extends BaseAdapter {

		private Context mContext;

		private ArrayList<ListItem> mListItems = new ArrayList<MoreSourceActivity.ListItem>();

		public ArrayList<ListItem> getListItems() {
			return mListItems;
		}

		public MoreSourceAdapter(Context mContext) {
			this.mContext = mContext;
		}

		public int getCount() {
			return mListItems.size();
		}

		public Object getItem(int arg0) {
			return mListItems.get(arg0);
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = View.inflate(mContext, R.layout.layout_more_source_item, null);
			}
			final ListItem mListItem = mListItems.get(arg0);
			ImageView icon = (ImageView) arg1.findViewById(R.id.more_source_item_icon);
			icon.setImageDrawable(mListItem.icon);

			TextView title = (TextView) arg1.findViewById(R.id.more_source_item_title);
			title.setText(mListItem.title);

			TextView desc = (TextView) arg1.findViewById(R.id.more_source_item_desc);
			desc.setText(mListItem.descString);

			ImageButton operate = (ImageButton) arg1.findViewById(R.id.more_source_item_operate);
			if (mListItem.isInstall) {
				operate.setImageDrawable(res.getDrawable(R.drawable.more_source_open));
			} else {
				operate.setImageDrawable(res.getDrawable(R.drawable.more_source_download));
			}
			operate.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent mIntent;
					if (mListItem.isInstall && mListItem.packageName != null && !mListItem.packageName.equals("")) {
						mIntent = getPackageManager().getLaunchIntentForPackage(mListItem.packageName);
						startActivity(mIntent);
					} else {
						try {
							mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mListItem.urlToDownLoad));
							startActivity(mIntent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(mContext, "无法打开网页." + "请安装web浏览器", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				}
			});
			return arg1;
		}
	}
}
