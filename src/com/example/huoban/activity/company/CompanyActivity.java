package com.example.huoban.activity.company;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.push.BaiDuPushUtils;
import com.example.huoban.R;
import com.example.huoban.activity.diary.DiaryDetailActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.CompanyAppointMsgEncrypted;
import com.example.huoban.model.CompanyAppointMsgPlainText;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.model.DiaryData;
import com.example.huoban.model.DiaryListResult;
import com.example.huoban.model.DiaryModel;
import com.example.huoban.utils.DynamicSetListViewUtil;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.RSAUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.other.AppointCompanyPopupWindow;
import com.example.huoban.widget.other.ListViewEx;
import com.example.huoban.widget.other.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CompanyActivity extends BaseActivity implements OnComponentSelectedListener {
	private TextView tvTitle;
	private ImageButton ibBack;
	private ScrollView scrollView;
	private TextView koubei;
	private TextView tvNumber;
	private ListView lvRecommend;
	private List<DiaryData> mDiary;
	public static final String COMPANY_ACTIVITY = "CompanyActivity";
	private static final String COMPANY_TAG = "com.huoban.CompanyActivity.error";
	private static final int GET_DIARY = 0;
	private static final int APPOINT_COMPANY = 1;
	private CompanyDetail companyDetail;

	private ListViewEx lvService;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.icon_company_default).showImageOnFail(R.drawable.icon_company_default).showImageOnLoading(R.drawable.icon_company_default).cacheInMemory(true).considerExifParams(true).build();
	private DiaryAdapter diaryAdapter;
	private DiaryModel diaryModel;
	private AppointCompanyPopupWindow mPopupWindow;
	private CompanyAppointMsgPlainText msgPlain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		companyDetail = (CompanyDetail) getIntent().getSerializableExtra("companyDetail");
		// diaryDetailData = (DiaryDetailData) getIntent().getSerializableExtra(
		// "diaryDetailData");
		diaryModel = application.getDiaryModel();
		msgPlain = new CompanyAppointMsgPlainText();
		setupViews();
		doGetDiaryList();
	}

	private void doGetDiaryList() {
		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_diary/diary?";
		task.taskID = GET_DIARY;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("limit", 2 + "");
		params.put("page", 1 + "");
		params.put("type", "recommend");
		params.put("company", companyDetail.shop_name);
		task.taskParam = params;
		task.resultDataClass = DiaryListResult.class;
		showProgress(null, 0, false);
		doTask(task);
	}

	@Override
	protected void setupViews() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.company_title);

		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);
		if (companyDetail != null && companyDetail.shop_logo != null)
			imageLoader.displayImage(companyDetail.shop_logo, (RoundImageView) findViewById(R.id.iv_company_img), options);
		if (companyDetail != null)
			((TextView) findViewById(R.id.tv_company_name)).setText(companyDetail.shop_name);

		scrollView = (ScrollView) findViewById(R.id.sv_company);
		scrollView.smoothScrollTo(0, 0);
		koubei = (TextView) findViewById(R.id.layout_company_grade);
		koubei.setText(companyDetail.total_koubei);
		lvService = (ListViewEx) findViewById(R.id.lv_company_service);
		if (companyDetail.service == null || companyDetail.service.size() == 0) {
			findViewById(R.id.layout_company_service).setVisibility(View.GONE);
		} else {
			lvService.setAdapter(new MyServiceAdapter());
		}
		tvNumber = (TextView) findViewById(R.id.tv_company_number);
		tvNumber.setText(companyDetail.page_views);
		lvRecommend = (ListView) findViewById(R.id.lv_company_recommend);
		diaryAdapter = new DiaryAdapter();
		lvRecommend.setAdapter(diaryAdapter);
		DynamicSetListViewUtil.setListViewHeightBasedOnChildren(lvRecommend);
		findViewById(R.id.btn_company_appoint).setOnClickListener(onClickListener);
	}

	private class MyServiceAdapter extends BaseAdapter {

		private HashMap<String, String> services = new HashMap<String, String>();

		public MyServiceAdapter() {
			services.put("49", "免费三方监理");
			services.put("50", "装修齐家保—9周年预约送水电光盘！");
			services.put("51", "齐家老娘舅");
			services.put("455", "金牌施工队");
			services.put("456", "三套方案PK");
		}

		@Override
		public int getCount() {
			return companyDetail.service == null ? 0 : companyDetail.service.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(CompanyActivity.this, R.layout.item_listview_company_service, null);
				TextView service = (TextView) view.findViewById(R.id.tvService);
				service.setText(services.get(companyDetail.service.get(position)));
			}
			return view;
		}

	}

	/**
	 * 初始化店铺星级
	 * */
	// private void initStar(){
	// for(int i=0;i<5;i++){
	// ImageView iv=new ImageView(this);
	// iv.setLayoutParams(new LayoutParams(12, 12));
	// mStar[i]=iv;
	// mStar[i].setBackgroundResource(R.drawable.img_start_dark);
	//
	// LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	// new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT));
	// layoutParams.rightMargin = 5;
	// layout.addView(iv,layoutParams);
	// }
	// }

	// /**
	// * 设置店铺星级
	// *
	// * @param grade为小于等于5的店铺等级
	// * */
	// private void setStar(int grade) {
	// if (grade > 5) {
	// Log.e(COMPANY_TAG, "the company grade error");
	// return;
	// }
	//
	// for (int i = 0; i < 5; i++) {
	// if (i < grade) {
	// mStar[i].setBackgroundResource(R.drawable.img_start_shine);
	// } else {
	// mStar[i].setBackgroundResource(R.drawable.img_start_dark);
	// }
	// }
	// }

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		switch (task.taskID) {
		case GET_DIARY:
			DiaryListResult result = (DiaryListResult) task.result;
			if (result == null || result.data == null) {
				return;
			}
			mDiary = result.data;
			diaryAdapter.notifyDataSetChanged();
			break;
		case APPOINT_COMPANY:
			CompanyAppointMsgEncrypted came = (CompanyAppointMsgEncrypted) task.result;
			if (came.msg_encrypted.status == 200) {
				ToastUtil.showToast(this, "预约装修公司成功");
			} else {
				ToastUtil.showToast(this, "预约装修公司失败");
			}
			break;
		}

	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtn_left:
				finish();
				break;
			case R.id.btn_company_appoint:
				// try {
				// doCompanyAppoint();
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// }
				if (mPopupWindow == null) {
					mPopupWindow = new AppointCompanyPopupWindow(CompanyActivity.this, msgPlain, CompanyActivity.this, companyDetail);
				}
				mPopupWindow.showAtLocation(arg0, Gravity.CENTER, 0, 0);
				break;
			default:
				break;
			}
		}

	};

	private class DiaryAdapter extends BaseAdapter {
		private DisplayImageOptions Coveroption = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();

		@Override
		public int getCount() {
			return mDiary == null ? 0 : mDiary.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.view_my_diary_attention_items, arg2, false);
			}
			RoundImageView ivImg = ViewHolder.get(view, R.id.iv_my_diary_attention_img);
			TextView tvTitle = ViewHolder.get(view, R.id.tv_my_diary_attention_title);

			imageLoader.displayImage(mDiary.get(position).summary.cover_url, ivImg, Coveroption);
			tvTitle.setText(mDiary.get(position).diary_title);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CompanyActivity.this, DiaryDetailActivity.class);
					intent.putExtra("diary_id", mDiary.get(position).diary_id);
					CompanyActivity.this.startActivity(intent);
				}
			});
			return view;
		}

	}

	// 预约装修公司
	public void doCompanyAppoint() throws UnsupportedEncodingException {
		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_APPOINT_COMPANY;
		task.taskID = APPOINT_COMPANY;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("encrypt_method", "RSA");

		StringBuffer sb = new StringBuffer();
		sb.append("Android ");
		sb.append(android.os.Build.VERSION.RELEASE);
		sb.append(",装修伙伴,");
		try {
			sb.append("V" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			sb.append("V2.9.1");
			e.printStackTrace();
		}
		sb.append("-");
		sb.append(BaiDuPushUtils.getMetaValue(this, "BaiduMobAd_CHANNEL"));
		sb.append(",");
		sb.append(Utils.getDeviceId(this));
		sb.append(",装修公司");
		LogUtil.logE("TAG", sb.toString() + "=============");
		msgPlain.referer_url = sb.toString();
		msgPlain.shop_id = companyDetail.shop_id;
		// msgPlain.unit = getUnit();
		// msgPlain.budget = getBudget();

		String msgPlaintext = getMsgplaintext(msgPlain);

		// 加密后的密文
		byte[] msg_encrypted_byte = RSAUtil.encryptByPublicKey(msgPlaintext.getBytes("UTF-8"), application.getSalt(this).public_key);
		String msg_encrypted = Base64.encodeToString(msg_encrypted_byte, Base64.DEFAULT);

		map.put("msg_encrypted", msg_encrypted);

		map.put("timestamp", Utils.getTimeStamp());
		task.taskParam = map;
		task.resultDataClass = CompanyAppointMsgEncrypted.class;
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	// private class CompanyAppointAsync extends AsyncTask<Void, Void, String> {
	//
	// @Override
	// protected String doInBackground(Void... params) {
	//
	// ClientUtils c = new ClientUtils();
	// ClientRequest request = new ClientRequest();
	// request.put("areaflag", "shanghai");
	// request.put("user_name",
	// application.getUserName(CompanyActivity.this));
	// request.put("mobile", application.getMobile(CompanyActivity.this));
	// request.put("referer_url", getRefererUrl());
	// request.put("self_url", "s");
	// request.put("shop_id", companyDetail.shop_id);
	// request.put("unit", getUnit() + "");
	// request.put("budget", getBudget() + "");
	// String response = null;
	// try {
	// response = c
	// .encryptRequest(
	// URLConstant.URL_APPOINT_COMPANY,
	// StringConstant.APP_KEY,
	// Utils.getDeviceId(CompanyActivity.this),
	// application.getSalt(CompanyActivity.this).auth_info.sessionid,
	// request.toString(),
	// application.getSalt(CompanyActivity.this).public_key,
	// application.getSalt(CompanyActivity.this).private_key);
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// }
	// return response;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// LogUtil.logE("TAG", result + "===========================");
	// }
	//
	// }

	private int getUnit() {
		int unit = 8;
		String houseType = diaryModel.summary.house_type;
		if (null != houseType && !"".equals(houseType)) {
			if (houseType.contains("一房")) {
				unit = 8;
			} else if (houseType.contains("二房")) {
				unit = 9;
			} else if (houseType.contains("三房")) {
				unit = 10;
			} else if (houseType.contains("四房")) {
				unit = 11;
			} else if (houseType.contains("别墅")) {
				unit = 13;
			}
		}
		return unit;
	}

	private int getBudget() {
		int budget = 27;
		String budgetStr = diaryModel.summary.budget;
		if (null != budgetStr && !"".equals(budgetStr)) {
			if (budgetStr.contains("万")) {
				int i = 0;
				try {
					i = Integer.valueOf(budgetStr.substring(0, budgetStr.indexOf("万")));
				} catch (Exception e) {
					i = 5;
				}
				if (i < 5) {
					budget = 27;
				} else if (5 <= i && i <= 10) {
					budget = 29;
				} else if (10 < i && i <= 15) {
					budget = 30;
				} else {
					budget = 31;
				}
			}
		}
		return budget;
	}

	private String getAuthInfoJson(AuthInfo authInfo) {
		return Utils.objectToJson(authInfo);
	}

	private String getMsgplaintext(Object object) {
		return Utils.objectToJson(object);
	}

	@Override
	public void onComponentSelected(int nResId, Object... obj) {
		try {
			doCompanyAppoint();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
