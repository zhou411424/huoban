package com.example.huoban.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.push.BaiDuPushUtils;
import com.example.huoban.R;
import com.example.huoban.assistant.AssistantFragment;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.custominterface.DiaryLikeBroadcastReceiver;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.fragment.diary.DiaryFragment;
import com.example.huoban.fragment.my.MyFragment;
import com.example.huoban.fragment.partner.PartentFragment;
import com.example.huoban.service.PushService;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.widget.other.NoMoveViewPager;
import com.nostra13.universalimageloader.utils.L;

public class HomeActivity extends BaseActivity implements OnClickListener,
		DBOperaterInterFace {
	private NoMoveViewPager mViewPager = null;
	private List<RadioButton> radioButtons = null;
	public List<BaseFragment> fragmentsList = null;
	private MyPagerAdapter myPagerAdapter = null;
	private int currentItem;
	private int recordLastItem;
	private static final String FILE_NAME = "logo.png";
	public static String TEST_IMAGE;
	/**
	 * 用于判断当前打开了app
	 */
	public static boolean isInApplication;
	private DiaryLikeBroadcastReceiver receiver;
	public static final String ACTION_DIARY_FOCUS="com.example.huoban.fragment.diary.DiaryFragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		/**
		 * 从本地读取已读问题id
		 */
		getHasReadedQuestionsID();
		setupViews();
		/**
		 * 去掉ImageLoad log
		 */
		L.disableLogging();
		/**
		 * 开启聊天服务
		 */
		bindService(application.getServiceIntent(), application.getConn(),
				BIND_AUTO_CREATE);
		
		new ShareLogoTask().execute(RESULT_OK);
		/**
		 * 日记关注广播
		 */
		receiver=new DiaryLikeBroadcastReceiver();
		registerReceiver(receiver, new IntentFilter(ACTION_DIARY_FOCUS));
	}

	/**
	 * 分享app，所需app icon
	 */
	private void initImagePath() {
		try {
			String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this,
					null);
			TEST_IMAGE = cachePath + FILE_NAME;
			File file = new File(TEST_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(),
						R.drawable.huoban);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
	}
	/**
	 * 从本地读取已读问题id
	 */
	private void getHasReadedQuestionsID() {
		DbParamData dbParamData = new DbParamData();
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	@Override
	protected void setupViews() {
		initBottomClick();
		initRadioButtons();
		initFragment();
		startForPushing();
		isInApplication = true;
	}

	/**
	 * 开始baidu推送服务
	 * 
	 */
	private void startForPushing() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String channel_id = sp.getString("channel_id", null);
		String user_id = sp.getString("user_id", null);
		if (application.getIsPushMessage("featured_question", true)
				&& (channel_id == null || user_id == null
				|| !PushManager.isPushEnabled(getApplicationContext()))) {
			// 以apikey的方式登录，一般放在主Activity的onCreate中
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					BaiDuPushUtils.getMetaValue(HomeActivity.this, "api_key"));
		} else {
			PushManager.stopWork(this);
		}
		/**
		 * 开启服务.mqtt
		 */
		startServer();

	}

	/**
	 * 开启服务.mqtt
	 */
	private void startServer() {
		Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE)
				.edit();
		editor.putString(PushService.PREF_DEVICE_ID,
				application.getMobile(this));
		editor.commit();
		PushService.actionStart(this);
	}

	private void initFragment() {
		mViewPager = (NoMoveViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(4);
		fragmentsList = new ArrayList<BaseFragment>();
		/**
		 * 伙伴
		 */
		PartentFragment partentFragment = new PartentFragment();
		fragmentsList.add(partentFragment);
		/**
		 * 装修助手
		 */
		AssistantFragment assistantFragment = new AssistantFragment();
		fragmentsList.add(assistantFragment);
		/**
		 * 日记
		 */
		DiaryFragment diaryFragment = new DiaryFragment();
		fragmentsList.add(diaryFragment);

		/**
		 * 我的
		 */
		MyFragment mMyFragment = new MyFragment();
		fragmentsList.add(mMyFragment);

		myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(myPagerAdapter);

	}

	private void initBottomClick() {
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_parter);
		rl.setOnClickListener(this);
		rl = (RelativeLayout) findViewById(R.id.rl_helper);
		rl.setOnClickListener(this);
		rl = (RelativeLayout) findViewById(R.id.rl_circle);
		rl.setOnClickListener(this);
		rl = (RelativeLayout) findViewById(R.id.rl_mine);
		rl.setOnClickListener(this);
	}

	private void initRadioButtons() {
		radioButtons = new ArrayList<RadioButton>();
		RadioButton rb = (RadioButton) findViewById(R.id.rb_parter);
		radioButtons.add(rb);
		rb = (RadioButton) findViewById(R.id.rb_helper);
		radioButtons.add(rb);
		rb = (RadioButton) findViewById(R.id.rb_circle);
		radioButtons.add(rb);
		rb = (RadioButton) findViewById(R.id.rb_mine);
		radioButtons.add(rb);
	}

	@Override
	public void onBackPressed() {
		// 装修助手首页打开了popupwindow,则先关闭
		if (fragmentsList.get(mViewPager.getCurrentItem()) instanceof AssistantFragment) {
			AssistantFragment af = (AssistantFragment) fragmentsList
					.get(mViewPager.getCurrentItem());
			if (af.isThePoupShowing()) {
				af.dismissPopup();
				return;
			}
		}
		// if(mViewPager.getCurrentItem()==1){
		// if(((AssistantFragment)fragmentsList.get(1)).isThePoupShowing()){
		// ((AssistantFragment)fragmentsList.get(1)).dismissPopup();
		// return;
		// }
		// }
		DialogUtils.twoButtonShow(this, 0, R.string.read_for_out,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						HomeActivity.super.onBackPressed();

					}
				}, null);

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_parter:
			currentItem = 0;
			break;
		case R.id.rl_helper:
			currentItem = 1;
			break;
		case R.id.rl_circle:
			currentItem = 2;
			break;
		case R.id.rl_mine:
			currentItem = 3;
			break;

		default:
			break;
		}
		if (getWindow() != null)
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mViewPager.setCurrentItem(currentItem, false);
		setRadioButtonsSatus(currentItem);
		if (recordLastItem != currentItem) {
			recordLastItem = currentItem;
			fragmentsList.get(currentItem).initDataForChoised();
		}
	}

	private void setRadioButtonsSatus(int currentItem) {
		if (radioButtons != null) {
			for (int i = 0; i < radioButtons.size(); i++) {
				RadioButton rb = radioButtons.get(i);
				if (i == currentItem) {
					rb.setChecked(true);
				} else {
					rb.setChecked(false);
				}
			}
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			if (arg0 < fragmentsList.size()) {
				return fragmentsList.get(arg0);
			}
			return null;
		}

		@Override
		public int getCount() {

			return fragmentsList.size();
		}
	}

	protected void onDestroy() {
		LogUtil.logI("homeactivity", "onDestroy");
		isInApplication = false;
		unbindService(application.getConn());
		PushService.actionStop(this);
		super.onDestroy();
		unregisterReceiver(receiver);
	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (fragmentsList != null && currentItem < fragmentsList.size()) {
			fragmentsList.get(currentItem).onActivityResult(arg0, arg1, arg2);
		}
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		return DBOperateUtils.readLocalQuestionIDS(this,
				application.getUserId(this));
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {

		ArrayList<String> hasReadQSIdS = (ArrayList<String>) dbParamData.object;
		application.setReadQSIdS(hasReadQSIdS);
	}
	
	private class ShareLogoTask extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			initImagePath();
			return null;
		}
		
	}
	
}
