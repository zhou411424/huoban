package com.example.huoban.activity.diary;

import java.util.HashMap;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import cn.sharesdk.framework.ShareSDK;

import com.example.huoban.R;
import com.example.huoban.activity.HomeActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.custominterface.DiaryCommentBroadcastReceiver;
import com.example.huoban.fragment.diary.DiaryDetailFragment;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ShareUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class DiaryDetailActivity extends BaseActivity implements OnClickListener {

	private DrawerLayout mDrawerLayout;
	private ImageButton mDrawerButton;
	private FragmentManager manager;
	private int diary_id;
	public final static int ADD_FOCUS = 0;
	public final static int DEL_FOCUS = 1;
	public DiaryDetailFragment fragment;
	public static int ISFOCUS = 0;
	private ImageButton ibtnLike;
	private int position;
	public static final String ACTION_DIARY_COMMENT = "com.example.huoban.diarycomment";
	private DiaryCommentBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_detail);
		diary_id = getIntent().getIntExtra("diary_id", 0);
		position = getIntent().getIntExtra("position", 0);

		ShareSDK.initSDK(this);
		setupViews();
		initDiaryDetail();
		receiver = new DiaryCommentBroadcastReceiver();
		registerReceiver(receiver, new IntentFilter(ACTION_DIARY_COMMENT));
	}

	private void initDiaryDetail() {
		manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		fragment = new DiaryDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("diary_id", diary_id);
		bundle.putInt("position", position);
		fragment.setArguments(bundle);
		transaction.add(R.id.content_frame, fragment);
		transaction.commit();
	}

	@Override
	protected void setupViews() {
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {

			}

			@Override
			public void onDrawerOpened(View arg0) {
				mDrawerButton.setImageResource(R.drawable.icon_diary_detail_drawer_selected);
			}

			@Override
			public void onDrawerClosed(View arg0) {
				mDrawerButton.setImageResource(R.drawable.icon_diary_detail_drawer_normal);
			}
		});
		mDrawerButton = (ImageButton) findViewById(R.id.ibtn_drawer);
		mDrawerButton.setOnClickListener(this);
		ibtnLike = (ImageButton) findViewById(R.id.ibtn_like);
		ibtnLike.setOnClickListener(this);
		findViewById(R.id.ibtn_comment).setOnClickListener(this);
		findViewById(R.id.ibtn_share).setOnClickListener(this);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		BaseResult result = (BaseResult) task.result;
		Intent intent = new Intent();
		intent.setAction(HomeActivity.ACTION_DIARY_FOCUS);
		intent.putExtra("position", position);
		switch (task.taskID) {
		case ADD_FOCUS:
			if (result.status == 1) {
				ISFOCUS = 1;
				ToastUtil.showToast(this, "成功关注", Gravity.BOTTOM);
				((ImageButton) findViewById(R.id.ibtn_like)).setImageResource(R.drawable.icon_diary_detail_liked);
				intent.putExtra("like", true);
			}
			break;
		case DEL_FOCUS:
			if (result.status == 1) {
				ISFOCUS = 0;
				ToastUtil.showToast(this, "取消关注", Gravity.BOTTOM);
				((ImageButton) findViewById(R.id.ibtn_like)).setImageResource(R.drawable.icon_diary_detail_like);
				intent.putExtra("like", false);
			}
			break;
		}
		sendBroadcast(intent);
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_drawer:
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				closeOrOpenDrawer(true);
			} else {
				closeOrOpenDrawer(false);
			}
			break;
		case R.id.ibtn_left:
			goBack();
			break;
		case R.id.ibtn_like: // 关注
			if (ISFOCUS == 0) {
				doAddOrDelFocus(ADD_FOCUS);
			} else {
				doAddOrDelFocus(DEL_FOCUS);
			}
			break;
		case R.id.ibtn_comment:
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("diary_id", diary_id);
			startActivity(intent);
			break;
		case R.id.ibtn_share:
			String title = "推荐装修日记：" + fragment.diaryDetailData.diary_title;
			String url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=topic&id=" + diary_id;
			LogUtil.logE("TAG",url+"----------------");
			ShareUtil.showOnekeyshare(this, null, false, title, url);
			break;
		}
	}

	private void doAddOrDelFocus(int type) {
		Task task = new Task();
		task.taskID = type;
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		switch (type) {
		case ADD_FOCUS:
			task.taskQuery = "api_diary/add_focus?";
			break;
		case DEL_FOCUS:
			task.taskQuery = "api_diary/del_focus?";
			break;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(diary_id);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		params.put("imei", imei);
		params.put("sign", sign);
		params.put("user_id", user_id);
		params.put("id", diary_id + "");
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		doTask(task);
	}

	public void closeOrOpenDrawer(boolean flag) {
		if (flag) {
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			mDrawerButton.setImageResource(R.drawable.icon_diary_detail_drawer_normal);
		} else {
			mDrawerLayout.openDrawer(Gravity.RIGHT);
			mDrawerButton.setImageResource(R.drawable.icon_diary_detail_drawer_selected);
		}
	}

	// 控制是否finish掉activity还是把fragment推出栈
	public void goBack() {
		// Activity中加入back栈中fragment的数量
		if (manager.getBackStackEntryCount() > 0) {
			manager.popBackStack();
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ISFOCUS == 0) {
			ibtnLike.setImageResource(R.drawable.icon_diary_detail_like);
		} else {
			ibtnLike.setImageResource(R.drawable.icon_diary_detail_liked);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
		ISFOCUS = 0;
		unregisterReceiver(receiver);
	}
}
