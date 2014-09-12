package com.example.huoban.activity.diary;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.HomeActivity;
import com.example.huoban.adapter.DiaryDrawerAdapter;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.fragment.diary.GraduationPhotosFragment;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.DiaryContentList;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ShareUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class DiaryGraduationPhotosActivity extends BaseActivity implements OnClickListener, OnComponentSelectedListener {

	private DrawerLayout mDrawerLayout;
	private ImageButton mDrawerButton;
	private FragmentManager manager;
	private ListView mDrawerListView;
	private DiaryDrawerAdapter diaryDrawerAdapter;
	private View headViewDrawer;
	private ImageButton ibtnLike;
	private List<DiaryContentList> diary_list;
	private int diary_id;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_detail);
		diary_id = application.getDiaryModel().diary_id;
		position = getIntent().getIntExtra("position", 0);
		diary_list = application.getDiaryDetailData();
		setupViews();
		initDiaryGraduationPhotos();
		initDrawerList();
	}

	private void initDrawerList() {
		// diaryDetailData = (DiaryDetailData) getIntent().getSerializableExtra(
		// "diaryDetailData");
		((TextView) headViewDrawer.findViewById(R.id.tvTitle)).setText(application.getDiaryModel().diary_title);
		diaryDrawerAdapter.refresh(diary_list);
	}

	private void initDiaryGraduationPhotos() {
		manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		GraduationPhotosFragment fragment = new GraduationPhotosFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("companyDetail", getIntent().getSerializableExtra("companyDetail"));
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
		mDrawerListView = (ListView) findViewById(R.id.left_drawer);
		headViewDrawer = View.inflate(this, R.layout.headview_drawer_listview, null);
		mDrawerListView.addHeaderView(headViewDrawer);
		diaryDrawerAdapter = new DiaryDrawerAdapter(this, this, 1);
		mDrawerListView.setAdapter(diaryDrawerAdapter);
		ibtnLike = (ImageButton) findViewById(R.id.ibtn_like);
		if (DiaryDetailActivity.ISFOCUS == 1) {
			ibtnLike.setImageResource(R.drawable.icon_diary_detail_liked);
		}
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
		case DiaryDetailActivity.ADD_FOCUS:
			if (result.status == 1) {
				DiaryDetailActivity.ISFOCUS = 1;
				ToastUtil.showToast(this, "成功关注", Gravity.BOTTOM);
				((ImageButton) findViewById(R.id.ibtn_like)).setImageResource(R.drawable.icon_diary_detail_liked);
				intent.putExtra("like", true);
			}
			break;
		case DiaryDetailActivity.DEL_FOCUS:
			if (result.status == 1) {
				DiaryDetailActivity.ISFOCUS = 0;
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
		case R.id.ibtn_like:
			if (DiaryDetailActivity.ISFOCUS == 0) {
				doAddOrDelFocus(DiaryDetailActivity.ADD_FOCUS);
			} else {
				doAddOrDelFocus(DiaryDetailActivity.DEL_FOCUS);
			}
			break;
		case R.id.ibtn_comment:
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("diary_id", diary_id);
			startActivity(intent);
			break;
		case R.id.ibtn_share:
			String title = "推荐装修日记：" + application.getDiaryModel().diary_title;
			String url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=topic&id=" + diary_id;
			ShareUtil.showOnekeyshare(this, null, false, title, url);
			break;
		}
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
	public void onComponentSelected(int nResId, Object... obj) {
		int categroy = (Integer) obj[0];
		if (categroy == 0) {
			closeOrOpenDrawer(true);
			return;
		} else {
			Intent intent = new Intent(this, DiaryBrowseActivity.class);
			int l = 0;
			if (categroy == 1) {
				intent.putExtra("currentItem", categroy + 1);
			} else {
				for (int i = 0; i < categroy - 1; i++) {
					if (diary_list.get(i).content != null)
						l += diary_list.get(i).content.size();
				}
				intent.putExtra("currentItem", l + categroy - 1 + 2);
			}
			intent.putExtra("companyDetail", getIntent().getSerializableExtra("companyDetail"));
			startActivity(intent);
		}
		closeOrOpenDrawer(true);
	}

	private void doAddOrDelFocus(int type) {
		Task task = new Task();
		task.taskID = type;
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		switch (type) {
		case 0:
			task.taskQuery = "api_diary/add_focus?";
			break;
		case 1:
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

	@Override
	public void onResume() {
		super.onResume();
		if (DiaryDetailActivity.ISFOCUS == 0) {
			ibtnLike.setImageResource(R.drawable.icon_diary_detail_like);
		} else {
			ibtnLike.setImageResource(R.drawable.icon_diary_detail_liked);
		}
	}
}
