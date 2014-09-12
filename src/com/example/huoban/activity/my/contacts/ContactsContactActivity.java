package com.example.huoban.activity.my.contacts;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;

public class ContactsContactActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ContactsContactActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private ImageButton ib_add;

	private ListAdapter mAdapter;

	private ListView mListView;

	private HashMap<String, String> mapAddContact = new HashMap<String, String>();

	private int lastadd = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_contacts_contact);
		initData();
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("通讯录好友");
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
		ib_add.setImageResource(R.drawable.red_searcho);
		ib_add.setVisibility(View.VISIBLE);
		ib_add.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mListView = (ListView) findViewById(R.id.contacts_contact_list);
		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		mAdapter = new ListAdapter(this);
	}

	protected void onRestart() {
		super.onRestart();
		LogUtil.logI(TAG, "onPause");
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
		default:
			break;
		}
	}

	private final void addContact(int position) {
		Task task = new Task();
		lastadd = position;

		task.target = this;
		task.taskQuery = URLConstant.URL_ADD_CONTACT;
		task.resultDataClass = BaseResult.class;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getAddContactParam(mAdapter.getContacts().get(position).mobile);
		doTask(task);
	}

	private final Object getAddContactParam(String mobile) {
		mapAddContact.clear();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		mapAddContact.put("imei", imei);

		String loginname = mobile;
		sb.append("login_name=" + loginname + "&");
		mapAddContact.put("login_name", loginname);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		mapAddContact.put("user_id", userid);

		String sign = sb.toString();
		mapAddContact.put("sign", com.example.huoban.utils.MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return mapAddContact;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		// Task task = (Task) param[0];
		mAdapter.getContacts().get(lastadd).status = "2";
		mAdapter.notifyDataSetChanged();
	}

	protected void getDataFailed(Object... param) {
	}

	private static class ListAdapter extends BaseAdapter {

		private final ContactsContactActivity mContext;

		private ArrayList<String> mobileList = new ArrayList<String>();
		private ArrayList<Contact> mContacts = new ArrayList<Contact>();

		ListAdapter(ContactsContactActivity mContext) {
			this.mContext = mContext;
			try {
				getPhoneContacts(mContacts);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		public View getView(final int position, View consertView, ViewGroup parent) {
			View view = consertView;
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_contacts_contact_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) view.findViewById(R.id.contacts_contact_item_name);
				holder.desc = (TextView) view.findViewById(R.id.contacts_contact_item_desc);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final Contact mContact = getItem(position);
			if (mContact != null) {
				holder.name.setText(mContact.user_name);

				if (mContact.status != null && mContact.status.equals("1")) {
					holder.desc.setText("已添加");
					holder.desc.setTextColor(0xff333333);
					view.setOnClickListener(null);
				} else if (mContact.status != null && mContact.status.equals("0")) {
					holder.desc.setText("加为好友");
					holder.desc.setTextColor(0xffff4800);
					view.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							mContext.addContact(position);
						}
					});
				} else if (mContact.status != null && mContact.status.equals("2")) {
					holder.desc.setText("等待验证");
					holder.desc.setTextColor(0xff333333);
					view.setOnClickListener(null);
				}
			}
			return view;
		}

		private void getPhoneContacts(ArrayList<Contact> mContacts) throws Exception {
			if (mContacts == null)
				throw new NullPointerException(mContacts + " is null");
			mContacts.clear();
			ContentResolver resolver = mContext.getContentResolver();
			Cursor phoneCursor = null;
			phoneCursor = resolver.query(Phone.CONTENT_URI, null, null, null, " sort_key asc ");

			if (phoneCursor != null) {
				while (phoneCursor.moveToNext()) {

					String phonenumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
					if (!Utils.checkPhone(phonenumber)) {
						continue;
					}
					Contact mContact = new Contact();
					mContact.mobile = phonenumber;
					mContact.user_name = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));
					if (checkIsFriend(mContact)) {
						mContact.status = "1";
					} else {
						mContact.status = "0";
					}
					mContacts.add(mContact);
				}
			}
		}

		private boolean checkIsFriend(Contact mContact) {
			if (mContact == null || mContact.mobile == null || mContact.mobile.equals("")) {
				return false;
			}
			if (mobileList.size() == 0) {
				ArrayList<Contact> mContacts = HuoBanApplication.getInstance().getInfoResult().data.contacter_list;
				if (mContacts != null && mContacts.size() > 0)
					for (Contact iContact : mContacts) {
						mobileList.add(iContact.mobile);
					}
			}
			return mobileList.contains(mContact.mobile);
		}

		private static class ViewHolder {
			public TextView name, desc;
		}
	}

}
