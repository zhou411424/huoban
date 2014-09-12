package com.example.huoban.fragment.my;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.circle.CircleActivity;
import com.example.huoban.activity.diary.MyDiaryActivity;
import com.example.huoban.activity.my.AlbumActivity;
import com.example.huoban.activity.my.ContactsActivity;
import com.example.huoban.activity.my.MoreSourceActivity;
import com.example.huoban.activity.my.OtherActivity;
import com.example.huoban.activity.my.TreasureBoxActivity;
import com.example.huoban.activity.my.UserInfoActivity;
import com.example.huoban.activity.my.contacts.ContactAddActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.my.CheckedOKResult;
import com.example.huoban.assistant.model.my.RegistrationResult;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.UserInfo;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.MyListItemView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyFragment extends BaseFragment {

	private final static String TAG = "MyFragment";

	private ArrayList<ListItem> mListItems = new ArrayList<ListItem>();

	private ArrayList<MyListItemView> mListItemViews = new ArrayList<MyListItemView>();

	private TextView title;

	private RelativeLayout mInfo, mHeadView, mRegisterView;

	private ImageView userIcon;

	private UserInfo userInfo;

	private TextView userName, tv_allGold, tv_addGold, tv_register;

	private Intent mIntent = new Intent();

	private HashMap<String, String> map = new HashMap<String, String>();

	private boolean isTrue = false;

	private int totalCoin;

	private Button logout;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtil.logI(TAG, "onAttach");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.logI(TAG, "onCreateView");
		return inflater.inflate(R.layout.layout_my, container, false);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.logI(TAG, "onActivityCreated");
		if (application.getInfoResult() == null || application.getInfoResult().data == null) {
			// HuoBanApplication.exitOrRelogin(getActivity(), true);
			getActivity().finish();
		}
		userInfo = application.getInfoResult().data.user_info;
		initData();
		initView();

	}

	public void initDataForChoised() {
		super.initDataForChoised();
		loadData();
	}

	public void onStart() {
		super.onStart();
		LogUtil.logI(TAG, "onStart");
		mImageLoader.displayImage(application.getInfoResult().data.user_info.avatar, userIcon, defaultOptions);
		setUserName();
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

	public void onDestroyView() {
		super.onDestroyView();
		LogUtil.logI(TAG, "onDestroyView");
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
	}

	public void onDetach() {
		super.onDetach();
		LogUtil.logI(TAG, "onDetach");
	}

	private void setUserName() {
		if (!"".equals(userInfo.nick) && userInfo.nick != null) {
			userName.setText(userInfo.nick);
		} else {
			userName.setText(userInfo.user_name);
		}
	}

	private void initView() {
		title = (TextView) getView().findViewById(R.id.tv_title);
		title.setText("我的");

		mHeadView = (RelativeLayout) getView().findViewById(R.id.my_head);

		mInfo = (RelativeLayout) mHeadView.findViewById(R.id.layout_my_info);
		mInfo.setOnClickListener(mOnClickListener);

		userIcon = (ImageView) mInfo.findViewById(R.id.layout_my_user_icon);
		mImageLoader.displayImage(application.getInfoResult().data.user_info.avatar, userIcon, defaultOptions);

		userName = (TextView) mInfo.findViewById(R.id.layout_my_username);
		if (!"".equals(userInfo.nick) && userInfo.nick != null) {
			userName.setText(userInfo.nick);
		} else {
			userName.setText(userInfo.user_name);
		}
		/**
		 * 签到控件
		 */
		mRegisterView = (RelativeLayout) mHeadView.findViewById(R.id.layout_my_registration);
		mRegisterView.setOnClickListener(mOnClickListener);

		tv_addGold = (TextView) mRegisterView.findViewById(R.id.layout_my_add_gold);
		tv_register = (TextView) mRegisterView.findViewById(R.id.layout_my_tv_text_registration);
		tv_allGold = (TextView) mHeadView.findViewById(R.id.layout_my_tv_allgoldnum);

		MyListItemView mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_contacts);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_diary);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_album);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_zhuangxiujie);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_treasurebox);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_moresource);
		mListItemViews.add(mListItemView);

		mListItemView = (MyListItemView) getView().findViewById(R.id.my_item_other);
		mListItemViews.add(mListItemView);
		int i = 0;
		for (MyListItemView iListItemView : mListItemViews) {
			ListItem iListItem = mListItems.get(i++);
			iListItemView.getIconLeft().setImageDrawable(iListItem.iconLeft);
			iListItemView.getTitle().setText(iListItem.title);
			iListItemView.setOnClickListener(mOnClickListener);
		}

		logout = (Button) getView().findViewById(R.id.log_out);
		logout.setOnClickListener(mOnClickListener);

	}

	private void initData() {
		ListItem mListItem = new ListItem();
		/**
		 * 联系人
		 */
		mListItem.iconLeft = res.getDrawable(R.drawable.my_contacts_icon);
		mListItem.title = res.getString(R.string.my_contacts);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 我的日记
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.icon_my_diary);
		mListItem.title = res.getString(R.string.my_diary);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 我的相册
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.my_album_icon);
		mListItem.title = res.getString(R.string.my_album);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 装修界
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.my_icon_cicle);
		mListItem.title = res.getString(R.string.my_zhuangxiujie);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 百宝箱
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.my_baibaoxiang_icon);
		mListItem.title = res.getString(R.string.my_treasurebox);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 更多齐家资源
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.my_more_icon);
		mListItem.title = res.getString(R.string.my_moresource);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
		/**
		 * 其他
		 */
		mListItem = new ListItem();
		mListItem.iconLeft = res.getDrawable(R.drawable.my_other_icon);
		mListItem.title = res.getString(R.string.my_other);
		mListItem.iconRight = res.getDrawable(R.drawable.order_more);
		mListItems.add(mListItem);
	}

	private void loadData() {
		Task task = new Task();
		task.fragment = this;
		task.taskQuery = URLConstant.URL_CHECKIN_STATUS;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getParam(map);
		task.resultDataClass = RegistrationResult.class;
		doTask(task);
	}

	private void checkedIn() {
		Task task = new Task();
		task.fragment = this;
		task.taskQuery = URLConstant.URL_CHECK_IN;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getParam(map);
		task.resultDataClass = CheckedOKResult.class;
		showProgress("正在签到", 0, false);
		doTask(task);

	}

	private HashMap<String, String> getParam(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(getActivity());
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String userid = application.getInfoResult().data.user_info.user_id;
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = MD5Util.getMD5String(sb.toString() + MD5Util.MD5KEY);
		map.put("sign", sign);

		return map;
	}

	protected void getDataFailed(Object... param) {

	}

	protected void setupViews(View view) {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		if (task.result instanceof RegistrationResult) {
			isTrue = ((RegistrationResult) task.result).can_checkin.equals("true");
			int getCoin = Integer.parseInt(((RegistrationResult) task.result).get_coin);
			totalCoin = Integer.parseInt(((RegistrationResult) task.result).total_coin);
			updateUI(isTrue, totalCoin, getCoin);
		} else {
			// Toast.makeText(getActivity(), ((CheckedOKResult)
			// task.result).msg, Toast.LENGTH_SHORT);
			isTrue = false;
			int getCoin = Integer.parseInt(((CheckedOKResult) task.result).got_coin);
			if (getCoin == 0) {
				DialogUtils.oneButtonShow(getActivity(), "签到成功", "因系统维护，金币不能马上到账，稍后将充值到您的账户中，还请留意。", null);
			}
			updateUI(isTrue, totalCoin, getCoin);
		}
		dismissProgress();
	}

	private void updateUI(boolean isTrue, int totalCoin, int getCoin) {
		if (isTrue) {
			mRegisterView.setOnClickListener(mOnClickListener);
			mRegisterView.setBackgroundResource(R.drawable.my_round_right);
			tv_register.setText("签到");
			tv_allGold.setText("" + totalCoin);
			tv_addGold.setText("+" + getCoin);
		} else {
			mRegisterView.setOnClickListener(null);
			mRegisterView.setBackgroundResource(R.drawable.my_round_right_checked);
			tv_register.setText("已签到");
			tv_allGold.setText("" + (totalCoin + getCoin));
			tv_addGold.setText("+0");
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			Class<?> mClass = null;
			switch (v.getId()) {
			case R.id.layout_my_info:
				// Toast.makeText(getActivity(), "我的信息",
				// Toast.LENGTH_SHORT).show();
				mClass = UserInfoActivity.class;
				break;
			case R.id.layout_my_registration:
				// Toast.makeText(getActivity(), "签到",
				// Toast.LENGTH_SHORT).show();
				checkedIn();
				mClass = null;
				break;
			case R.id.my_item_contacts:
				// Toast.makeText(getActivity(), "联系人",
				// Toast.LENGTH_SHORT).show();
				if (application.getInfoResult().data.contacter_list != null && application.getInfoResult().data.contacter_list.size() > 0) {
					mClass = ContactsActivity.class;
				} else {
					mClass = ContactAddActivity.class;
				}
				break;
			case R.id.my_item_diary:
				// Toast.makeText(getActivity(), "我的日记",
				// Toast.LENGTH_SHORT).show();
				mClass = MyDiaryActivity.class;
				break;
			case R.id.my_item_album:
				// Toast.makeText(getActivity(), "我的相册",
				// Toast.LENGTH_SHORT).show();
				mClass = AlbumActivity.class;
				mIntent.putExtra("see_id", application.getUserId(getActivity()));
				break;
			case R.id.my_item_zhuangxiujie:
				// Toast.makeText(getActivity(), "装修界",
				// Toast.LENGTH_SHORT).show();
				mClass = CircleActivity.class;
				break;
			case R.id.my_item_treasurebox:
				// Toast.makeText(getActivity(), "百宝箱",
				// Toast.LENGTH_SHORT).show();
				mClass = TreasureBoxActivity.class;
				break;
			case R.id.my_item_moresource:
				// Toast.makeText(getActivity(), "更多资源",
				// Toast.LENGTH_SHORT).show();
				mClass = MoreSourceActivity.class;
				break;
			case R.id.my_item_other:
				// Toast.makeText(getActivity(), "其他",
				// Toast.LENGTH_SHORT).show();
				mClass = OtherActivity.class;
				break;
			case R.id.log_out:
				mClass = null;
				DialogUtils.twoButtonShow(getActivity(), "退出登录", "是否要退出登录", "取消", "确定", null, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						HuoBanApplication.exitOrRelogin(getActivity(), true);
					}

				});
				// Toast.makeText(getActivity(), "退出",
				// Toast.LENGTH_SHORT).show();
				break;
			default:
				// Toast.makeText(getActivity(), "------",
				// Toast.LENGTH_SHORT).show();
				mClass = null;
				break;
			}
			if (mClass != null) {
				mIntent.setClass(getActivity(), mClass);
				getActivity().startActivity(mIntent);
			}
		}
	};

	private class ListItem {
		public Drawable iconLeft;
		@SuppressWarnings("unused")
		public Drawable iconRight;
		public String title;
	}

	protected String setFragmentName() {
		return "MyFragment";
	}
}