package com.example.huoban.fragment.diary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.company.CompanyActivity;
import com.example.huoban.activity.diary.DiaryBrowseActivity;
import com.example.huoban.activity.diary.DiaryCommentActivity;
import com.example.huoban.activity.diary.DiaryDetailActivity;
import com.example.huoban.adapter.DiaryDetailAdapter;
import com.example.huoban.adapter.DiaryDrawerAdapter;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.model.CompanyDetailResult;
import com.example.huoban.model.CompanyInfoMsgPlainText;
import com.example.huoban.model.DiaryContent;
import com.example.huoban.model.DiaryContentList;
import com.example.huoban.model.DiaryDetailData;
import com.example.huoban.model.DiaryDetailResult;
import com.example.huoban.model.DiaryModel;
import com.example.huoban.model.GraduationPhoto;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ShareUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.LightView;
import com.example.huoban.widget.other.LightView.PullToGraduationListener;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiaryDetailFragment extends BaseFragment implements OnRefreshListener, OnComponentSelectedListener, OnClickListener {

	private LightView mLightView;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private ListView mDrawerListView;
	private DiaryDetailActivity diaryDetailActivity;
	private View headViewBaseInfo, headViewProductList, headViewDrawer;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DiaryDetailAdapter diaryDetailAdapter;
	private DiaryDrawerAdapter diaryDrawerAdapter;
	private final static int DO_GET_DIARY_DETAIL = 0;
	private final static int DO_PRAISE = 1;
	private final static int DO_CANCEL_PRAISE = 2;
	private final static int DO_GET_COMPANY = 3;
	private TextView praise;
	private DiaryContent diaryContent;
	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
	private CompanyDetail companyDetail;
	private ImageView companylogo;
	private int page = 1;
	private int diary_id;
	private List<DiaryContent> dataList = new ArrayList<DiaryContent>();
	private List<DiaryContentList> diaryList;
	public DiaryDetailData diaryDetailData;
	private int position;
	private List<GraduationPhoto> graduate;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		diaryDetailActivity = (DiaryDetailActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diary_detail, container, false);
		diary_id = getArguments().getInt("diary_id");
		position = getArguments().getInt("position");
		setupViews(view);
		getDiaryDetail();
		return view;
	}

	/**
	 * 获取日记详情
	 */
	private void getDiaryDetail() {
		Task task = new Task();
		task.fragment = this;
		task.taskID = DO_GET_DIARY_DETAIL;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_diary/get_diary_info?";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", diary_id + "");
		params.put("user_id", application.getUserId(getActivity()));
		params.put("page", page + "");
		task.taskParam = params;
		task.resultDataClass = DiaryDetailResult.class;
		showProgress(null, 0, false);
		doTask(task);
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}
	}

	@Override
	protected void setupViews(View view) {
		mLightView = (LightView) view.findViewById(R.id.lightView);
		PullToGraduationListener pullToGraduationListener = new PullToGraduationListener() {

			@Override
			public void goToGraduation() {
				// if (companyDetail == null) {
				// return;
				// }
				Intent intent = new Intent(getActivity(), DiaryBrowseActivity.class);
				intent.putExtra("companyDetail", companyDetail);
				intent.putExtra("position", position);
				intent.putExtra("graduate", (Serializable) graduate);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			}
		};
		mLightView.setPullToGraduation(pullToGraduationListener);
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		initHeadView(mListView);
		diaryDetailAdapter = new DiaryDetailAdapter(getActivity(), this);
		mListView.setAdapter(diaryDetailAdapter);
		// mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				System.gc();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					mLightView.setVisibility(View.VISIBLE);
				} else {
					mLightView.setVisibility(View.INVISIBLE);
				}
			}
		});

		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setRefreshEnable(false);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mDrawerListView = (ListView) getActivity().findViewById(R.id.left_drawer);
		headViewDrawer = View.inflate(getActivity(), R.layout.headview_drawer_listview, null);
		mDrawerListView.addHeaderView(headViewDrawer);
		diaryDrawerAdapter = new DiaryDrawerAdapter(getActivity(), this, 0);
		mDrawerListView.setAdapter(diaryDrawerAdapter);
	}

	private void initHeadView(ListView listView) {
		headViewBaseInfo = View.inflate(getActivity(), R.layout.headview_diary_detail, null);
		headViewBaseInfo.findViewById(R.id.iv_headview_bg).setOnClickListener(this);
		headViewBaseInfo.findViewById(R.id.ll_baseinfo).setOnClickListener(this);

		headViewBaseInfo.findViewById(R.id.ll_company).setOnClickListener(this);
		headViewProductList = View.inflate(getActivity(), R.layout.headview_diary_detail_product_list, null);
		listView.addHeaderView(headViewBaseInfo);
		listView.addHeaderView(headViewProductList);
	}

	@Override
	protected void refresh(Object... param) {

		Task task = (Task) param[0];
		switch (task.taskID) {
		case DO_GET_DIARY_DETAIL:
			mPullToRefreshListView.onRefreshComplete();
			DiaryDetailResult result = (DiaryDetailResult) task.result;
			diaryDetailData = result.data;
			if (page == 1) {
				// 存储日记信息
				setDiaryModel(diaryDetailData);
				diaryList = diaryDetailData.diary_list;
				// 毕业照
				graduate = diaryDetailData.graduate;
				DiaryDetailActivity.ISFOCUS = result.isfocus;
				if (result.isfocus == 1) {
					((ImageButton) diaryDetailActivity.findViewById(R.id.ibtn_like)).setImageResource(R.drawable.icon_diary_detail_liked);
				}
				refreshHeadView(diaryDetailData);
				if (diaryDetailData.diary_list == null) {
					dismissProgress();
					return;
				}
				refreshListView(diaryDetailData.diary_list);
				refreshDrawerHeadView(diaryDetailData);
				refreshDrawerListView(diaryDetailData);
				if (null != diaryDetailData.summary.company_id && !"".equals(diaryDetailData.summary.company_id))
					getCompanyDetail(diaryDetailData.summary.company_id);
				else
					dismissProgress();
			} else {
				diaryList.addAll(diaryDetailData.diary_list);
				refreshListView(diaryDetailData.diary_list);
				refreshDrawerListView(diaryDetailData.diary_list);
			}

			if (diaryList.size() < diaryDetailData.diary_count) {
				mPullToRefreshListView.setLoadMoreEnable(true);
			} else {
				mPullToRefreshListView.setLoadMoreEnable(false);
			}
			// 日记列表存储
			application.setDiaryDetaiData(diaryList);
			break;
		case DO_PRAISE:
			BaseResult b = (BaseResult) task.result;
			if (b.status == 1) {
				diaryContent.like = 1;
				praise.setCompoundDrawablesWithIntrinsicBounds(diaryDetailActivity.getResources().getDrawable(R.drawable.icon_diary_detail_item_praised), null, null, null);
				praise.setText(Integer.parseInt(praise.getText().toString()) + 1 + "");
				praise.setTextColor(0xffff4800);
			}
			break;
		case DO_CANCEL_PRAISE:
			BaseResult br = (BaseResult) task.result;
			if (br.status == 1) {
				diaryContent.like = 0;
				praise.setCompoundDrawablesWithIntrinsicBounds(diaryDetailActivity.getResources().getDrawable(R.drawable.icon_diary_detail_item_praise), null, null, null);
				praise.setText(Integer.parseInt(praise.getText().toString()) - 1 + "");
				praise.setTextColor(0xff999999);
			}
			break;
		case DO_GET_COMPANY:
			dismissProgress();
			if (task.failed) {
				return;
			}
			CompanyDetailResult cdr = (CompanyDetailResult) task.result;
			companyDetail = cdr.msg_plaintext.result.list;
			if (companyDetail != null) {
				imageLoader.displayImage(companyDetail.shop_logo, companylogo);
			}
			break;
		}
	}

	private void setDiaryModel(DiaryDetailData diaryDetailData) {
		DiaryModel diaryModel = new DiaryModel();
		diaryModel.diary_id = diaryDetailData.diary_id;
		diaryModel.diary_title = diaryDetailData.diary_title;
		diaryModel.poster_name = diaryDetailData.poster_name;
		diaryModel.poster_avatar = diaryDetailData.poster_avatar;
		diaryModel.summary = diaryDetailData.summary;
		application.setDiaryModel(diaryModel);
	}

	// 获取装修公司信息
	private void getCompanyDetail(String company_id) {

		Task task = new Task();
		task.fragment = this;
		task.taskID = DO_GET_COMPANY;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_COMPANY_INFO;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(diaryDetailActivity).auth_info);
		map.put("auth_info", auth_info);
		map.put("sign_method", "MD5");

		CompanyInfoMsgPlainText msg = new CompanyInfoMsgPlainText();
		msg.shop_id = company_id;
		msg.content_type = "company_pic";

		String magPlaintext = getMsgplaintext(msg);
		map.put("msg_plaintext", magPlaintext);
		map.put("sign_info", MD5Util.getMD5String(auth_info + magPlaintext + application.getSalt(diaryDetailActivity).salt_key));
		map.put("timestamp", Utils.getTimeStamp());
		task.taskParam = map;
		task.resultDataClass = CompanyDetailResult.class;
		// showProgress(null, 0, false);
		doTask(task);
	}

	private String getAuthInfoJson(AuthInfo authInfo) {
		return Utils.objectToJson(authInfo);
	}

	private String getMsgplaintext(Object object) {
		return Utils.objectToJson(object);
	}

	private void refreshDrawerHeadView(DiaryDetailData diaryDetailData) {
		((TextView) headViewDrawer.findViewById(R.id.tvTitle)).setText(diaryDetailData.diary_title);
	}

	// 刷侧边栏
	private void refreshDrawerListView(DiaryDetailData diaryDetailData) {
		diaryDrawerAdapter.refresh(diaryDetailData);
	}

	// 加载更多侧边栏
	private void refreshDrawerListView(List<DiaryContentList> diary_list) {
		diaryDrawerAdapter.refresh(diary_list);
	}

	// 刷新日记列表
	private void refreshListView(List<DiaryContentList> diary_list) {
		for (int i = 0; i < diary_list.size(); i++) {
			if (diary_list.get(i).content != null) {
				for (int j = 0; j < diary_list.get(i).content.size(); j++) {
					diary_list.get(i).content.get(j).color = i % 4;
					diary_list.get(i).content.get(j).date = diary_list.get(i).date;
					// diary_list.get(i).content.get(j).category = i +
					// (diaryList.size() < 4 ? 0 : (diaryList.size() - 4));
					diary_list.get(i).content.get(j).category = i;
				}
				dataList.addAll(diary_list.get(i).content);
			}
		}
		diaryDetailAdapter.refresh(dataList);
	}

	// 刷新房屋基本信息，装修公司信息，商品清单等
	private void refreshHeadView(DiaryDetailData diaryDetailData) {
		ImageView bg = (ImageView) headViewBaseInfo.findViewById(R.id.iv_headview_bg);
		imageLoader.displayImage(diaryDetailData.summary.cover_url, bg, options);
		ImageView avatar = (ImageView) headViewBaseInfo.findViewById(R.id.iv_headview_avatar);
		imageLoader.displayImage(diaryDetailData.poster_avatar, avatar);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_title)).setText(diaryDetailData.diary_title);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_name)).setText(diaryDetailData.poster_name);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_place)).setText(diaryDetailData.areaflag);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_room)).setText(diaryDetailData.summary.house_type);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_style)).setText(diaryDetailData.summary.style);
		((TextView) headViewBaseInfo.findViewById(R.id.tv_headview_budget)).setText(diaryDetailData.summary.budget);
		companylogo = (ImageView) headViewBaseInfo.findViewById(R.id.company_logo);
		if ( !"".equals(diaryDetailData.summary.company))
			((TextView) headViewBaseInfo.findViewById(R.id.company_name)).setText(diaryDetailData.summary.company);

		if (diaryDetailData.item_list == null || diaryDetailData.item_list.size() == 0) {
			mListView.removeHeaderView(headViewProductList);
		} else {
			LinearLayout ll = (LinearLayout) headViewProductList.findViewById(R.id.product_list);
			for (int i = 0; i < diaryDetailData.item_list.size(); i++) {
				View view = View.inflate(getActivity(), R.layout.item_diary_detail_product_list, null);
				((TextView) view.findViewById(R.id.tvProductPlace)).setText(diaryDetailData.item_list.get(i).pplace);
				((TextView) view.findViewById(R.id.tvProductPrice)).setText(diaryDetailData.item_list.get(i).pprice);
				((TextView) view.findViewById(R.id.tvProductName)).setText(diaryDetailData.item_list.get(i).pname);
				((TextView) view.findViewById(R.id.tvProductDate)).setText(diaryDetailData.item_list.get(i).pdate);
				if (i == diaryDetailData.item_list.size() - 1) {
					view.findViewById(R.id.line).setVisibility(View.GONE);
				}
				ll.addView(view);
			}
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		page++;
		getDiaryDetail();
	}

	@Override
	public void onComponentSelected(int nResId, Object... obj) {
		// mListView.setSelection((Integer) obj);
		int category = 0;
		int position = 0;
		if (nResId == 2) {
			if (obj.length > 1) {
				position = (Integer) obj[1];
			}
		}
		Intent intent = null;
		switch (nResId) {
		case 0: // 点击侧边导航栏
			category = (Integer) obj[0];
			LogUtil.logE("TAG", "category==" + category);
			int length = 0;
			if (category == 0) { // 点击毕业照
				// Intent i = new Intent(getActivity(),
				// DiaryGraduationPhotosActivity.class);
				// i.putExtra("companyDetail", companyDetail);
				// i.putExtra("position", position);
				// startActivity(i);
				if (graduate == null) {
					ToastUtil.showToast(getActivity(), "暂无毕业照");
					diaryDetailActivity.closeOrOpenDrawer(true);
					return;
				}
				intent = new Intent(diaryDetailActivity, DiaryBrowseActivity.class);
				intent.putExtra("currentItem", 2);
				intent.putExtra("isGraduate", true);

			} else if (category == 1) {
				mListView.setSelection(category);
			} else {
				if (diaryDetailData.item_list != null) {
					for (int i = 0; i < category - 2; i++) {
						if (diaryList.get(i).content != null)
							length += diaryList.get(i).content.size();
					}
					mListView.setSelection(length + 2);
				} else {
					for (int i = 0; i <= category - 2; i++) {

						if (diaryList.get(i).content != null)
							length += diaryList.get(i).content.size();
					}
					LogUtil.logE("TAG", "length==" + length);
					mListView.setSelection(length + 1);
				}
			}
			diaryDetailActivity.closeOrOpenDrawer(true);
			break;
		case 1: // 点击日期
			category = (Integer) obj[0];
			LogUtil.logE("TAG", category + "++++++++");
			int l = 0;
			intent = new Intent(diaryDetailActivity, DiaryBrowseActivity.class);
			if (category == 0) {
				intent.putExtra("currentItem", category + 2 + (graduate == null ? 0 : 1));
			} else {
				for (int i = 0; i < category; i++) {
					if (diaryList.get(i).content != null)
						l += diaryList.get(i).content.size();
				}
				intent.putExtra("currentItem", l + category + 2 + (graduate == null ? 0 : 1));
			}
			break;
		case 2: // 点击item
			category = (Integer) obj[0];
			intent = new Intent(diaryDetailActivity, DiaryBrowseActivity.class);
			if (category == 0) {
				intent.putExtra("currentItem", position + 3 + (graduate == null ? 0 : 1));
			} else {
				intent.putExtra("currentItem", position + 3 + category + (graduate == null ? 0 : 1));
			}
			LogUtil.logE("position", position + "*****************");
			break;
		case 3: // 点击item中的评论
			Intent commentIntent = new Intent(diaryDetailActivity, DiaryCommentActivity.class);
			commentIntent.putExtra("diaryContent", (DiaryContent) obj[0]);
			commentIntent.putExtra("position", (Integer) obj[1]);
			startActivity(commentIntent);
			break;
		case 4: // 点赞
			diaryContent = (DiaryContent) obj[0];
			praise = (TextView) obj[1];
			if (diaryContent.like == 0) {
				doPraise(DO_PRAISE, diaryContent);
			} else {
				doPraise(DO_CANCEL_PRAISE, diaryContent);
				diaryContent.like = 0;
			}
			break;
		case 5: // 分享
			String title = "推荐装修日记：" + diaryDetailData.diary_title;
			String url = URLConstant.HOST_NAME.substring(0, URLConstant.HOST_NAME.length() - 5) + "/share/?c=share/diary&m=diary&id=" + ((DiaryContent) obj[0]).reply_pid + "&date=" + ((DiaryContent) obj[0]).date;
			ShareUtil.showOnekeyshare(getActivity(), null, false, title, url);
			break;
		}
		if (intent != null) {
			intent.putExtra("companyDetail", companyDetail);
			intent.putExtra("position", position);
			intent.putExtra("graduate", (Serializable) graduate);
			intent.putExtra("category", category);
			startActivity(intent);
		}
	}

	private void doPraise(int face, DiaryContent diaryContent) {
		Task task = new Task();
		task.fragment = this;
		task.taskID = face;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		switch (face) {
		case DO_PRAISE:
			task.taskQuery = "api_diary/set_face_diary?";
			break;
		case DO_CANCEL_PRAISE:
			task.taskQuery = "api_diary/del_face_diary?";
			break;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		String imei = Utils.getDeviceId(diaryDetailActivity);
		String user_id = application.getUserId(diaryDetailActivity);

		StringBuffer sb = new StringBuffer();
		sb.append("date=");
		sb.append(diaryContent.date.substring(0, 10));
		sb.append("&id=");
		sb.append(diaryContent.reply_pid);
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		params.put("imei", imei);
		params.put("sign", sign);
		params.put("user_id", user_id);
		params.put("id", diaryContent.reply_pid);
		params.put("date", diaryContent.date.substring(0, 10));
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		doTask(task);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		int currentItem = 0;
		switch (v.getId()) {
		case R.id.iv_headview_bg: // 点击封面进毕业照
			// currentItem = 0;
			// intent = new Intent(diaryDetailActivity,
			// DiaryBrowseActivity.class);
			// if (companyDetail == null)
			// return;
			// Intent i = new Intent(getActivity(),
			// DiaryGraduationPhotosActivity.class);
			// i.putExtra("companyDetail", companyDetail);
			// i.putExtra("position", position);
			// startActivity(i);
			if (graduate == null) {
				ToastUtil.showToast(getActivity(), "暂无毕业照");
				return;
			}
			currentItem = 2;
			intent = new Intent(diaryDetailActivity, DiaryBrowseActivity.class);
			intent.putExtra("category", 0);
			intent.putExtra("isGraduate", true);
			break;
		case R.id.ll_company:
			if (companyDetail != null)
				intent = new Intent(diaryDetailActivity, CompanyActivity.class);
			break;
		case R.id.ll_baseinfo:
			currentItem = 1;
			intent = new Intent(diaryDetailActivity, DiaryBrowseActivity.class);
			break;
		}
		// Intent intent = new Intent(diaryDetailActivity,
		// DiaryBrowseActivity.class);

		if (intent != null && companyDetail != null) {
			intent.putExtra("currentItem", currentItem);
			intent.putExtra("companyDetail", companyDetail);
			intent.putExtra("position", position);
			intent.putExtra("graduate", (Serializable) graduate);
			startActivity(intent);
		}
	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseLastPageFragment";
	}

	public void refreshComment(int position) {
		dataList.get(position).comment_count += 1;
		diaryDetailAdapter.notifyDataSetChanged();
	}
}
