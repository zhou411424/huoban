package com.example.huoban.activity.my;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.contacts.ContactAddActivity;
import com.example.huoban.activity.my.contacts.ContactInfoActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.model.contacts.ContactReslut;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.BaseSwipeListViewListener;
import com.example.huoban.widget.pull.ContactsListView;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.SwipeListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactsActivity extends BaseActivity implements OnClickListener, OnRefreshListener {

	private final static String TAG = "ContactsActivity";

	private ContactsListView mContactsListView;

	private ContactsAdapter mContactsAdapter;

	private SwipeListView mSwipeListView;

	private TextView tv_title;

	private ImageButton ib_back;

	private ImageButton ib_add;

	private Task taskGetList = new Task();

	private Task taskDelete = new Task();

	private Intent mIntent = new Intent();

	private HashMap<String, String> mapGetList = new HashMap<String, String>();

	private HashMap<String, String> mapDelete = new HashMap<String, String>();

	private int delPosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_contacts);
		initData();
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("联系人");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
		/**
		 * 添加按钮
		 */
		ib_add = (ImageButton) findViewById(R.id.ibtn_right);
		ib_add.setImageResource(R.drawable.red_addo);
		ib_add.setVisibility(View.VISIBLE);
		ib_add.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mContactsListView = (ContactsListView) findViewById(R.id.contacts_listview);
		mContactsListView.setLoadMoreEnable(false);
		mContactsListView.setOnRefreshListener(this);

		mContactsAdapter = new ContactsAdapter(this);

		mSwipeListView = mContactsListView.getRefreshableView();
		mSwipeListView.setAdapter(mContactsAdapter);

		DisplayMetrics dm = new DisplayMetrics();

		((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

		mSwipeListView.setOffsetLeft(dm.widthPixels - 100 * dm.densityDpi / 160);
		mSwipeListView.setSwipeListViewListener(new ContactBaseSwipeListViewListener(this, mContactsAdapter.getContacts()));

		getList();
	}

	private void getList() {
		taskGetList.target = this;
		taskGetList.taskQuery = URLConstant.URL_GET_CONTACTS_LIST;
		taskGetList.resultDataClass = ContactReslut.class;
		taskGetList.taskParam = getGetListParam();
		taskGetList.taskHttpTpye = HTTPConfig.HTTP_GET;
		doTask(taskGetList);
	}

	private Object getGetListParam() {
		StringBuffer sb = new StringBuffer();

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		mapGetList.put("imei", imei);

		String sync = "" + 0;
		sb.append("sync=" + sync + "&");
		mapGetList.put("sync", sync);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		mapGetList.put("user_id", userid);

		String sign = sb.toString();
		mapGetList.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));

		return mapGetList;
	}

	private Object getDelteParam(int position) {

		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		mapDelete.put("imei", imei);

		String relationid = mContactsAdapter.getContacts().get(position).relation_id;
		sb.append("relation_id=" + relationid + "&");
		mapDelete.put("relation_id", relationid);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		mapDelete.put("user_id", userid);

		String sign = sb.toString();
		mapDelete.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));

		return mapDelete;
	}

	private void deleteContact(int position) {
		taskDelete.target = this;
		taskDelete.taskQuery = URLConstant.URL_DELETE_CONTACT;
		taskDelete.resultDataClass = BaseResult.class;
		taskDelete.taskParam = getDelteParam(position);
		taskDelete.taskHttpTpye = HTTPConfig.HTTP_GET;
		doTask(taskDelete);
	}

	private void initData() {

	}

	protected void onRestart() {
		super.onRestart();
		LogUtil.logI(TAG, "onPause");
		getList();
	}

	protected void onStop() {
		super.onStop();
		LogUtil.logI(TAG, "onStop");
	}

	protected void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			/**
			 * 添加好友
			 */
			mIntent.setClass(this, ContactAddActivity.class);
			startActivity(mIntent);
			// ToastUtil.showToast(this, "添加好友", Toast.LENGTH_SHORT);
			break;
		default:
			delPosition = (Integer) v.getTag();
			deleteContact(delPosition);
			mSwipeListView.closeOpenedItems();
			break;
		}
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		if (task.result instanceof ContactReslut) {
			mContactsAdapter.getContacts().clear();
			mContactsAdapter.getContacts().addAll(((ContactReslut) task.result).data.contact_list);
			mContactsAdapter.notifyDataSetChanged();
			mContactsListView.onRefreshComplete();
			application.refreshNewRemakName(null, mContactsAdapter.getContacts());
		} else {
			mContactsAdapter.getContacts().remove(delPosition);
			mContactsAdapter.notifyDataSetChanged();
			ToastUtil.showToast(this, "删除成功");
		}
	}

	protected void getDataFailed(Object... param) {
		Task task = (Task) param[0];
		if (task.resultDataClass == ContactReslut.class) {
			mContactsListView.onRefreshComplete();
			ToastUtil.showToast(this, "刷新失败");
		} else if (task.resultDataClass == BaseResult.class) {
			ToastUtil.showToast(this, "删除失败");
		}
	}

	private static class ContactBaseSwipeListViewListener extends BaseSwipeListViewListener {

		private ArrayList<Contact> mContacts;
		private Context mContext;

		public ContactBaseSwipeListViewListener(Context mContext, ArrayList<Contact> mContacts) {
			this.mContacts = mContacts;
			this.mContext = mContext;
		}

		public void onClickFrontView(int position) {
			super.onClickFrontView(position);
			if (mContacts != null && position < mContacts.size()) {
				Contact mContact = mContacts.get(position);

				Intent mIntent = new Intent(mContext, ContactInfoActivity.class);
				mIntent.putExtra("contact", mContact);
				mContext.startActivity(mIntent);
			}
		}
	}

	private static class ContactsAdapter extends BaseAdapter {

		private Context mContext;

		private ArrayList<Contact> mContacts = new ArrayList<Contact>();

		private final ImageLoader mImageLoader = ImageLoader.getInstance();

		private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).showImageOnLoading(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

		public ContactsAdapter(Context mContext) {
			this.mContext = mContext;
			mContacts = HuoBanApplication.getInstance().getInfoResult().data.contacter_list;
		}

		public ArrayList<Contact> getContacts() {
			return mContacts;
		}

		public int getCount() {
			return mContacts.size();
		}

		public Contact getItem(int position) {
			return mContacts.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		private static class ViewHolder {
			ImageView mIcon;
			TextView mName, tvRight;
			Button mDeleteBt;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_contacts_item, null);
				holder = new ViewHolder();

				holder.mIcon = (ImageView) view.findViewById(R.id.contacts_front_icon);
				holder.mName = (TextView) view.findViewById(R.id.contacts_front_name);
				holder.tvRight = (TextView) view.findViewById(R.id.contacts_front_tv_right);
				holder.mDeleteBt = (Button) view.findViewById(R.id.contacts_back_delete);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			Contact mContact = getItem(position);

			if (mContact != null) {

				mImageLoader.displayImage(mContact.avatar, holder.mIcon, options);

				if (mContact.remark_name != null && !"".equals(mContact.remark_name)) {
					holder.mName.setText(mContact.remark_name);
				} else if (mContact.nick != null && !"".equals(mContact.nick)) {
					holder.mName.setText(mContact.nick);
				} else {
					holder.mName.setText(mContact.user_name);
				}

				if ("1".equals(mContact.type) && "1".equals(mContact.status)) {
					holder.tvRight.setText("家庭成员");
				} else if ("2".equals(mContact.type) && "2".equals(mContact.status)) {
					holder.tvRight.setText("已邀请");
				} else {
					holder.tvRight.setText("");
				}

				if (mContact.status != null && mContact.status.equals("1")) {
					holder.mDeleteBt.setTag(position);
					holder.mDeleteBt.setOnClickListener((ContactsActivity) mContext);
				}
			}
			return view;
		}
	}

	public void onRefresh() {
		getList();
	}

	public void onLoadMore() {

	}

}
