package com.example.huoban.activity.my.contacts;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.contacts.chat.ChatActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.model.userinfo.CateResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactInfoActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ContactInfoActivity";

	private TextView tv_title;

	private ImageButton ib_back, ib_right;

	private Contact mContact;

	private ImageView mIcon;

	private TextView mName, mDesc, mId, operate;

	private Intent mIntent = new Intent();

	private ImageLoader mImageLoader = ImageLoader.getInstance();

	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).showImageOnLoading(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

	private HashMap<String, String> mapCateName = new HashMap<String, String>();

	private HashMap<String, String> mapAddContact = new HashMap<String, String>();

	private HashMap<String, String> mapAddFamily = new HashMap<String, String>();

	private HashMap<String, String> mapdeleteFamily = new HashMap<String, String>();

	private DelPopupWindow dpw;

	private DelPopupWindow dpwSecond;

	private boolean isFamily = false;

	private View.OnClickListener addfamilyListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.contact_info_positiveButton:
				addfamily();
				dpwSecond.dismiss();
				break;
			default:
				break;
			}
		}
	};

	private View.OnClickListener deleteFamilyListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.contact_info_negativeButton:
				deleteFamily();
				dpwSecond.dismiss();
				break;
			default:
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		if (getIntent().getSerializableExtra("contact") == null)
			finish();
		mContact = (Contact) getIntent().getSerializableExtra("contact");
		isFamily = "1".equals(mContact.type) && "1".equals(mContact.status);
		setContentView(R.layout.layout_contacts_contact_info);
		initTitleBar();
		initView();
	}

	private void initView() {
		LogUtil.logI(TAG, "initView()");

		mIcon = (ImageView) findViewById(R.id.layout_contact_info_icon);
		mImageLoader.displayImage(mContact.avatar, mIcon, options);

		mName = (TextView) findViewById(R.id.layout_contact_info_name);
		initName(mContact, mName);
		initSex(mContact, mName);

		mDesc = (TextView) findViewById(R.id.layout_contact_info_stage);
		loadData();

		mId = (TextView) findViewById(R.id.layout_contact_info_id);
		initUserid(mContact, mId);

		operate = (TextView) findViewById(R.id.layout_contact_info_operate);
		initOperateBtn(mContact, operate);
	}

	private void initName(Contact mContact, TextView mName) {
		if (mContact.remark_name != null && !mContact.remark_name.equals("null") && !mContact.remark_name.equals("")) {
			mName.setText(substring(mContact.remark_name));
		} else if (mContact.nick != null && !mContact.nick.equals("null") && !mContact.nick.equals("")) {
			mName.setText(substring(mContact.nick));
		} else {
			mName.setText(substring(mContact.user_name));
		}
	}

	private void initSex(Contact mContact, TextView mName) {
		if (mContact.sex != null && mContact.sex.equals("1")) {
			// 男
			setViewIcon(mName, R.drawable.sex_nan);
		} else if (mContact.sex != null && mContact.sex.equals("2")) {
			// 女
			setViewIcon(mName, R.drawable.sex_nv);
		} else {
			setViewIcon(mName, 0);
		}
	}

	private void setViewIcon(TextView textView, int imageId) {
		Drawable mGouDrawable = null;
		if (imageId != 0) {
			mGouDrawable = getResources().getDrawable(imageId);

			mGouDrawable.setBounds(0, 0, mGouDrawable.getIntrinsicWidth(), mGouDrawable.getIntrinsicHeight());
		}
		textView.setCompoundDrawables(textView.getCompoundDrawables()[0], textView.getCompoundDrawables()[1], mGouDrawable, textView.getCompoundDrawables()[3]);
	}

	private void initUserid(Contact mContact, TextView mId) {
		if (mContact.user_name != null && !mContact.user_name.equals("null") && !mContact.user_name.equals("")) {
			mId.setText("用户名:" + mContact.user_name);
			mId.setVisibility(View.VISIBLE);
		} else {
			mId.setVisibility(View.GONE);
		}
	}

	private void initOperateBtn(Contact mContact, TextView operate) {
		if ("true".equals(mContact.is_friend) || "1".equals(mContact.status)) {
			operate.setText("发送消息");
			operate.setOnClickListener(this);
			operate.setBackgroundResource(R.drawable.my_round_right);
		} else if ("2".equals(mContact.status)) {
			operate.setText("已邀请");
			operate.setBackgroundResource(R.drawable.my_round_right_checked);
		} else {
			operate.setText("加为好友");
			operate.setOnClickListener(this);
			operate.setBackgroundResource(R.drawable.my_round_right);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			mContact = (Contact) intent.getSerializableExtra("contact");
			initView();
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

	protected void onRestart() {
		super.onRestart();
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
		tv_title.setText("查看信息");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
		/**
		 * 右边按钮
		 */
		ib_right = (ImageButton) findViewById(R.id.ibtn_right);
		if ("1".equals(mContact.status) || "true".equals(mContact.is_friend)) {
			ib_right.setVisibility(View.VISIBLE);
		}
		ib_right.setImageResource(R.drawable.red_more);
		ib_right.setOnClickListener(this);
	}

	/**
	 * 获取用户装修阶段名字
	 */
	private void loadData() {
		Task task = new Task();
		task.target = this;

		task.taskID = 4;

		task.taskQuery = URLConstant.URL_GET_CATE_NAME;

		task.resultDataClass = CateResult.class;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskParam = getCateNameParam();

		if (task.taskParam != null) {
			doTask(task);
		}

	}

	private final Object getCateNameParam() {
		mapCateName.clear();
		if (mContact.cate_id != null) {
			String cate_id = mContact.cate_id.last_cate_id;
			mapCateName.put("cate_id", cate_id);
			return mapCateName;
		}
		return null;
	}

	private final void addContact() {
		Task task = new Task();
		task.taskID = 3;
		task.target = this;
		task.taskQuery = URLConstant.URL_ADD_CONTACT;
		task.resultDataClass = BaseResult.class;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getAddContactParam();
		doTask(task);
	}

	private final Object getAddContactParam() {
		mapAddContact.clear();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		mapAddContact.put("imei", imei);

		String loginname = mContact.mobile;
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
		Task task = (Task) param[0];
		if (task.taskID ==4) {
			mDesc.setText("(" + ((CateResult) task.result).cate_list.cate_name + "进行中...)");
		} else if(task.taskID ==1 ||task.taskID ==3) {
			 ToastUtil.showToast(this, "发送请求成功");
			mContact.status = "2";
			mContact.is_friend = "true";
			if (application.getInfoResult().data.contacter_list != null) {
				application.getInfoResult().data.contacter_list.add(mContact);
			} else {
				application.getInfoResult().data.contacter_list = new ArrayList<Contact>();
				application.getInfoResult().data.contacter_list.add(mContact);
			}

			finish();
		}else if(task.taskID ==2){
			 ToastUtil.showToast(this, "删除成功");
			 finish();
		}
	}

	protected void getDataFailed(Object... param) {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.layout_contact_info_operate:
			if ("true".equals(mContact.is_friend) || "1".equals(mContact.status)) {
				// ToastUtil.showToast(this, "发送消息", Toast.LENGTH_SHORT);
				mIntent.setClass(this, ChatActivity.class);
				mIntent.putExtra("contact", mContact);
				startActivity(mIntent);
			} else {
				addContact();
			}
			break;
		case R.id.ibtn_right:
			LogUtil.logI("isfamily =  " + isFamily);
			dpw = new DelPopupWindow(this, this, isFamily);
			dpw.showAtLocation(ib_right, Gravity.CENTER, 0, 0);
			break;
		case R.id.contact_info_remarkname:// 备注
			mIntent.setClass(this, RemarkContactActivity.class);
			mIntent.putExtra("contact", mContact);
			startActivityForResult(mIntent, 1);
			dpw.dismiss();
			break;
		case R.id.contact_info_positiveButton:
			goAddFamily(ib_right);
			dpw.dismiss();
			break;
		case R.id.contact_info_negativeButton:
			goDelelteFamily(ib_right);
			dpw.dismiss();
			break;
		}
	}

	private void goDelelteFamily(View v) {
		dpwSecond = new DelPopupWindow(ContactInfoActivity.this, deleteFamilyListener, false, isFamily);
		dpwSecond.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	private void goAddFamily(View v) {
		dpwSecond = new DelPopupWindow(ContactInfoActivity.this, addfamilyListener, true, isFamily);
		dpwSecond.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	private void deleteFamily() {
		Task task = new Task();
		task.taskID = 2;
		task.target = this;
		task.resultDataClass = BaseResult.class;
		task.taskQuery = URLConstant.URL_DELETE_FAMILY;
		task.taskParam = getDeleteFamilyParam(mapdeleteFamily);
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		doTask(task);
	}

	private HashMap<String, String> getDeleteFamilyParam(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String relationid = mContact.relation_id;
		sb.append("relation_id=" + relationid + "&");
		map.put("relation_id", relationid);

		String userid = application.getInfoResult().data.user_info.user_id;
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	private void addfamily() {
		Task task = new Task();
		task.taskID = 1;
		task.target = this;
		task.resultDataClass = BaseResult.class;
		task.taskQuery = URLConstant.URL_ADD_TO_FAMILY;
		task.taskParam = getaddFamilyParam(mapAddFamily);
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		doTask(task);
	}

	private HashMap<String, String> getaddFamilyParam(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();
		String friendid = mContact.user_id;
		sb.append("friend_uid=" + friendid + "&");
		map.put("friend_uid", friendid);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String userid = application.getInfoResult().data.user_info.user_id;
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	private static String substring(String orignal) {

		StringBuilder sb = new StringBuilder(10);
		for (int i = 0, j = 0; j < 10 && i < orignal.length(); i++) {
			int ascii = Character.codePointAt(orignal, i);
			if (ascii >= 0 && ascii <= 255)
				j++;
			else
				j += 2;
			sb.append(orignal.charAt(i));
		}

		return sb.toString().equals(orignal) ? sb.toString() : sb.append("...").toString();
	}
}
