package com.example.huoban.fragment.diary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.baidu.push.BaiDuPushUtils;
import com.example.huoban.R;
import com.example.huoban.activity.company.CompanyActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.CompanyAppointMsgEncrypted;
import com.example.huoban.model.CompanyAppointServiceMsgPlainText;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.RSAUtil;
import com.example.huoban.utils.ShareUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.AppointCompanyPopupWindow;

public class DiaryBrowseLastPageFragment extends BaseFragment implements OnClickListener, OnComponentSelectedListener {
	private CompanyDetail companyDetail;
	private AppointCompanyPopupWindow mPopupWindow;
	private CompanyAppointServiceMsgPlainText msgPlain;
	private static final int APPOINT_COMPANY_SERVICE = 1;

	public static DiaryBrowseLastPageFragment getInstance(CompanyDetail companyDetail) {
		DiaryBrowseLastPageFragment fragment = new DiaryBrowseLastPageFragment();
		fragment.companyDetail = companyDetail;
		fragment.msgPlain = new CompanyAppointServiceMsgPlainText();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browse_lastpage, container, false);
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		view.findViewById(R.id.btnShare).setOnClickListener(this);
		view.findViewById(R.id.btnSchedule).setOnClickListener(this);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		switch (task.taskID) {

		case APPOINT_COMPANY_SERVICE:
			CompanyAppointMsgEncrypted came = (CompanyAppointMsgEncrypted) task.result;
			if (came.msg_encrypted.status == 200) {
				ToastUtil.showToast(getActivity(), "预约装修公司成功");
			} else {
				ToastUtil.showToast(getActivity(), "预约装修公司失败");
			}
			break;
		}
	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseLastPageFragment";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShare:
			String title = "推荐装修日记：" + application.getDiaryModel().diary_title;
			String url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=topic&id=" + application.getDiaryModel().diary_id;
			ShareUtil.showOnekeyshare(getActivity(), null, false, title, url);
			break;
		case R.id.btnSchedule:
			if (companyDetail != null) {
				Intent intent = new Intent(getActivity(), CompanyActivity.class);
				intent.putExtra("companyDetail", companyDetail);
				startActivity(intent);
			} else {
				if (mPopupWindow == null) {
					mPopupWindow = new AppointCompanyPopupWindow(getActivity(), msgPlain, this, companyDetail);
				}
				mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
			}
			break;
		}
	}

	@Override
	public void onComponentSelected(int nResId, Object... obj) {
		try {
			doCompanyServiceAppoint();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// 预约装修公司服务
	public void doCompanyServiceAppoint() throws UnsupportedEncodingException {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_APPOINT_COMPANY_SERVICE;
		task.taskID = APPOINT_COMPANY_SERVICE;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(getActivity()).auth_info);
		map.put("auth_info", auth_info);
		map.put("encrypt_method", "RSA");

		StringBuffer sb = new StringBuffer();
		sb.append("Android ");
		sb.append(android.os.Build.VERSION.RELEASE);
		sb.append(",装修伙伴,");
		try {
			sb.append("V" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			sb.append("V2.9.1");
			e.printStackTrace();
		}
		sb.append("-");
		sb.append(BaiDuPushUtils.getMetaValue(getActivity(), "BaiduMobAd_CHANNEL"));
		sb.append(",");
		sb.append(Utils.getDeviceId(getActivity()));
		sb.append(",齐家服务");
		LogUtil.logE("TAG", sb.toString() + "=============");
		msgPlain.referer_url = sb.toString();
		// msgPlain.unit = getUnit();
		// msgPlain.budget = getBudget();

		String msgPlaintext = getMsgplaintext(msgPlain);

		// 加密后的密文
		byte[] msg_encrypted_byte = RSAUtil.encryptByPublicKey(msgPlaintext.getBytes("UTF-8"), application.getSalt(getActivity()).public_key);
		String msg_encrypted = Base64.encodeToString(msg_encrypted_byte, Base64.DEFAULT);

		map.put("msg_encrypted", msg_encrypted);

		map.put("timestamp", Utils.getTimeStamp());
		task.taskParam = map;
		task.resultDataClass = CompanyAppointMsgEncrypted.class;
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	private String getAuthInfoJson(AuthInfo authInfo) {
		return Utils.objectToJson(authInfo);
	}

	private String getMsgplaintext(Object object) {
		return Utils.objectToJson(object);
	}
}
