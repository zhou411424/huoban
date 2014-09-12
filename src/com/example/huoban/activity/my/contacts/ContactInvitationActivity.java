package com.example.huoban.activity.my.contacts;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.adapter.MyInvitationAdapter;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.fragment.circle.BackResultInterFace;
import com.example.huoban.fragment.circle.CircleInterface;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.InvitationResult;
import com.example.huoban.model.MemberInvitation;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.contacts.ContactReslut;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class ContactInvitationActivity extends BaseActivity implements OnClickListener, OnRefreshListener, CircleInterface {

	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private MyInvitationAdapter myInvitationAdapter;
	private ArrayList<MemberInvitation> invitation_list;
	private MemberInvitation memberInvitation = null;
	public static final int AGREE = 10;
	public static final int IGNORE = 11;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invtation);
		setupViews();
	}

	protected void setupViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.invitation);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		invitation_list = new ArrayList<MemberInvitation>();
		myInvitationAdapter = new MyInvitationAdapter(this, invitation_list, this);

		mListView.setAdapter(myInvitationAdapter);
		getInvitation();
	}

	private void getInvitation() {
		Task task = new Task();
		task.target = this;
		task.taskID = 100;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_INVITATION_LIST;
		task.resultDataClass = InvitationResult.class;
		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void agreeInvitation(MemberInvitation memberInvitation) {
		Task task = new Task();
		task.target = this;
		task.taskID = AGREE;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		String op = StringConstant.ONE;
		;
		String main_uid = null;
		if (StringConstant.FIVE.equals(memberInvitation.type)) {
			task.taskQuery = URLConstant.URL_AGREE_INVITATION;
		} else {

			task.taskQuery = "api_contact/process_contact_invite?";
			main_uid = memberInvitation.user_id;
		}
		task.resultDataClass = InvitationResult.class;
		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);
		String family_log_id = memberInvitation.family_log_id;
		StringBuffer sb = new StringBuffer();
		sb.append("family_log_id=");
		sb.append(family_log_id);
		sb.append("&imei=");
		sb.append(imei);
		if (main_uid != null) {
			sb.append("&main_uid=");
			sb.append(main_uid);
		}
		sb.append("&op=");
		sb.append(op);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("op", op);
		map.put("family_log_id", family_log_id);
		if (main_uid != null) {
			map.put("main_uid", main_uid);
		}
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void igreeInvitation(MemberInvitation memberInvitation) {
		Task task = new Task();
		task.target = this;
		task.taskID = AGREE;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		String op = StringConstant.Two;
		;
		String main_uid = null;
		if (StringConstant.FIVE.equals(memberInvitation.type)) {
			task.taskQuery = URLConstant.URL_AGREE_INVITATION;
		} else {

			task.taskQuery = "api_contact/process_contact_invite?";
			main_uid = memberInvitation.user_id;
		}
		task.resultDataClass = BaseResult.class;
		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);
		String family_log_id = memberInvitation.family_log_id;
		StringBuffer sb = new StringBuffer();
		sb.append("family_log_id=");
		sb.append(family_log_id);
		sb.append("&imei=");
		sb.append(imei);
		if (main_uid != null) {
			sb.append("&main_uid=");
			sb.append(main_uid);
		}
		sb.append("&op=");
		sb.append(op);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("op", op);
		map.put("family_log_id", family_log_id);
		if (main_uid != null) {
			map.put("main_uid", main_uid);
		}
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		mPullToRefreshListView.onRefreshComplete();
		if (task.taskID == 100 && task.result instanceof InvitationResult) {
			dismissProgress();
			InvitationResult invitationResult = (InvitationResult) task.result;
			if (invitationResult.data != null && invitationResult.data.invitation_list != null) {
				invitation_list.clear();
				invitation_list.addAll(invitationResult.data.invitation_list);
				myInvitationAdapter.refresh(invitation_list);
			}
		} else if (task.result instanceof ContactReslut) {
			dismissProgress();
			ContactReslut contactReslut = (ContactReslut) task.result;
			if (application.getInfoResult() != null) {
				UserInfoResult infoResult = application.getInfoResult();
				if (infoResult != null && infoResult.data != null && infoResult.data.contacter_list != null) {
					if(contactReslut.data!=null&&contactReslut.data.contact_list!=null){
						infoResult.data.contacter_list.clear();
						infoResult.data.contacter_list.addAll(contactReslut.data.contact_list);
						application.refreshNewRemakName(null, infoResult.data.contacter_list);
					}
				}
			}
			if(invitation_list.size()==0){
				finish();
			}
			
		} else {
			boolean getContactList = false;
			if (task.result instanceof InvitationResult) {
				/**
				 * 刷新联系人
				 */
				getContactList = true;
				getList();
			}

			if (invitation_list.contains(memberInvitation)) {
				invitation_list.remove(memberInvitation);
			}
			if (invitation_list.size() == 0&&!getContactList) {
				finish();
			} else {
				myInvitationAdapter.refresh(invitation_list);
			}
		}

	}

	private void getList() {
		Task taskGetList = new Task();
		taskGetList.target = this;
		taskGetList.taskQuery = URLConstant.URL_GET_CONTACTS_LIST;
		taskGetList.resultDataClass = ContactReslut.class;
		taskGetList.taskParam = getGetListParam();
		taskGetList.taskHttpTpye = HTTPConfig.HTTP_GET;
		doTask(taskGetList);
	}

	private Object getGetListParam() {
		StringBuffer sb = new StringBuffer();
		HashMap<String, String> mapGetList = new HashMap<String, String>();
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

	@Override
	protected void getDataFailed(Object... param) {
		mPullToRefreshListView.onRefreshComplete();

	}

	@Override
	public void onClick(View v) {
		finish();

	}

	@Override
	public void onRefresh() {
		getInvitation();

	}

	public void onLoadMore() {

	}

	public void doMethord(int id, Object object, View v, BackResultInterFace backResultInterFace) {

		switch (id) {
		case AGREE:
			memberInvitation = (MemberInvitation) object;
			agreeInvitation(memberInvitation);
			break;
		case IGNORE:
			memberInvitation = (MemberInvitation) object;
			igreeInvitation(memberInvitation);
			break;

		default:
			break;
		}
	}

}
