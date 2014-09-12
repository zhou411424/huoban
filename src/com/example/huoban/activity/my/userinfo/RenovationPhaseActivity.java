package com.example.huoban.activity.my.userinfo;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.UserInfoCate;
import com.example.huoban.model.userinfo.Cates;
import com.example.huoban.model.userinfo.PhaseReslut;
import com.example.huoban.model.userinfo.PhaseSetResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;

public class RenovationPhaseActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "RenovationPhaseActivity";

	private Intent mIntent = new Intent();

	private TextView tv_title;

	private ImageButton ib_back;

	private TextView tv_save;

	private HashMap<String, String> map = new HashMap<String, String>();

	private GridView mGridView;

	private RenovationPhaseAdapter mAdapter;

	private Task task = new Task();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info_renovation_phase);
		initView();
		initData();
	}

	private void initData() {

		showProgress("正在获取网络数据", 0, false);

		task.target = this;

		task.resultDataClass = PhaseReslut.class;

		task.taskQuery = URLConstant.URL_GET_PHASE_LIST;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskParam = getGetCateParams(map);

		doTask(task);
	}

	private void updateData() {

		showProgress("正在上传", 0, false);

		task.target = this;

		task.resultDataClass = PhaseSetResult.class;

		task.taskQuery = URLConstant.URL_CHANGE_PHASE_SET;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskParam = getUpdateCateParam(map);

		doTask(task);
	}

	public Object getGetCateParams(HashMap<String, String> map) {
		map.clear();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));

		return map;
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("装修阶段");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

		tv_save = (TextView) findViewById(R.id.tv_right);
		tv_save.setText("保存");
		tv_save.setVisibility(View.VISIBLE);
		tv_save.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mGridView = (GridView) findViewById(R.id.renovation_phase_grid);
		mAdapter = new RenovationPhaseAdapter(this);
		mGridView.setAdapter(mAdapter);
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

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		if (task.result instanceof PhaseReslut) {
			mAdapter.getArrayList().clear();
			mAdapter.getArrayList().addAll(((PhaseReslut) task.result).date);
			mAdapter.notifyDataSetChanged();
		} else if (task.result instanceof PhaseSetResult) {
			UserInfoCate mUserInfoCate = application.getInfoResult().data.user_info.cate_id;

			if (mUserInfoCate == null) {
				mUserInfoCate = new UserInfoCate();
				application.getInfoResult().data.user_info.cate_id = mUserInfoCate;
				mUserInfoCate.last_cate_id = mAdapter.getLaseCateId();
				mUserInfoCate.cate_id = ((PhaseSetResult) task.result).cate_id;
			} else {
				mUserInfoCate.last_cate_id = mAdapter.getLaseCateId();
				mUserInfoCate.cate_id = ((PhaseSetResult) task.result).cate_id;
			}
			ToastUtil.showToast(this, "设置成功");
			LogUtil.logI(TAG, "cateName " + mAdapter.getArrayList().get(mAdapter.getCatePosition()).cate_name);
			mIntent.putExtra("resultStr", mAdapter.getArrayList().get(mAdapter.getCatePosition()).cate_name);
			setResult(1, mIntent);
			finish();
		}
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	public Object getUpdateCateParam(HashMap<String, String> map) {
		map.clear();

		StringBuffer sb = new StringBuffer();
		String cateId = mAdapter.getLaseCateId();
		sb.append("cate_id=" + cateId + "&");
		map.put("cate_id", cateId);

		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String userId = application.getUserId(this);
		sb.append("user_id=" + userId);
		map.put("user_id", userId);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));

		return map;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			UserInfoCate mUserInfoCate = application.getInfoResult().data.user_info.cate_id;
			if (mUserInfoCate != null) {
				if (!mUserInfoCate.last_cate_id.equals(mAdapter.getLaseCateId())) {
					updateData();
				}
			} else {
				updateData();
			}
			break;
		}
	}

	static class RenovationPhaseAdapter extends BaseAdapter {

		private Context mContext;

		private String last_cate_id = "";

		private int mCates;

		private ArrayList<Cates> mArrayList = new ArrayList<Cates>();

		public RenovationPhaseAdapter(Context mContext) {
			this.mContext = mContext;
			if (HuoBanApplication.getInstance().getInfoResult().data.user_info.cate_id != null) {
				if (HuoBanApplication.getInstance().getInfoResult().data.user_info.cate_id.last_cate_id != null) {
					last_cate_id = HuoBanApplication.getInstance().getInfoResult().data.user_info.cate_id.last_cate_id;
				}
			}
		}

		public int getCatePosition() {
			return this.mCates;
		}

		public ArrayList<Cates> getArrayList() {
			return mArrayList;
		}

		public int getCount() {
			return mArrayList.size();
		}

		public Cates getItem(int arg0) {
			return mArrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;

			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_user_info_renovation_phase_item, null);
				holder = new ViewHolder();
				holder.renovationphaseName = (TextView) view.findViewById(R.id.renovation_phase_item_name);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			Cates cate = getItem(position);
			if (cate != null) {
				holder.renovationphaseName.setText(cate.cate_name);

				if (last_cate_id.equals(cate.cate_id)) {
					holder.renovationphaseName.setBackgroundResource(R.drawable.cate_seleted);
					mCates = position;
				} else {
					holder.renovationphaseName.setBackgroundResource(R.drawable.cate_unselected);
				}
				view.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						saveData(position);
					}
				});
			}
			return view;
		}

		static class ViewHolder {
			TextView renovationphaseName;
		}

		public String getLaseCateId() {
			return last_cate_id;
		}

		public void saveData(int position) {
			last_cate_id = getItem(position).cate_id;
			notifyDataSetChanged();
		}
	}

}
