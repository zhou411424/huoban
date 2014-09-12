package com.example.huoban.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.adapter.QuestionAdapter;
import com.example.huoban.assistant.adapter.RelationAdapter;
import com.example.huoban.assistant.adapter.ServiceAdapter;
import com.example.huoban.assistant.adapter.SupervisorAdapter;
import com.example.huoban.assistant.dao.SupervisorDao;
import com.example.huoban.assistant.model.MenuListResult;
import com.example.huoban.assistant.model.MenuSupervisorBean;
import com.example.huoban.assistant.model.QuestionBean;
import com.example.huoban.assistant.model.TipInfoResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.data.SupervisorBean;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.other.CustomButton;
import com.example.huoban.widget.pull.RefreshListView;
import com.example.huoban.widget.pull.RefreshListView.IXListViewListener;
//import org.codehaus.jackson.map.ObjectMapper;

/**
 * 在线客服入口
 * 
 * @author cwchun.chen
 * 
 */
public class ConsultActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private RefreshListView mXListView;
	private CustomButton tvService, tvQusetion, tvContactMy;
	private LinearLayout viewService, viewQusetion, viewContactMy;
	private Animation popuOut, popuIn;
	private ArrayList<MenuSupervisorBean> serviceList;
	private ArrayList<QuestionBean> questionList;
	private ArrayList<SupervisorBean> supervisorlist;
	private static final int TAB_SERVICE = 1;
	private static final int TAB_QUESTION = 2;
	private static final int TAB_CONTACT = 3;
	private static final int GET_MENU_LIST = 100;
	private static final int GET_TIP_INFO = 110;
	private static final int APPLY_VIP = 120;
	private int show_tab = 0;// 表示什么都不显示；
	private int pageIndex = 1;
	private ServiceAdapter adapter;
	private QuestionAdapter qusertionAdapter;
	private SupervisorAdapter supervisorAdapter;
	private String flage;// 表示是推送进入到该界面
	private int type = 1;// 1 是非详情 2 是详情 -1 默认内容 -2 推送的监理信息
	private int tipId;
	private String updateTime;// 当前时间戳
	private SupervisorDao supervisorDao;

	/**
	 * 更新齐家服务菜单列表
	 * @param sList
	 */
	private void refreshServiceListData(ArrayList<MenuSupervisorBean> sList) {
		serviceList.clear();
		serviceList = sList;
		adapter.updateAdapter(serviceList);
	}
	/**
	 * 刷新常见问题菜单列表
	 * @param qList
	 */
	private void refreshQuestionListData(ArrayList<QuestionBean> qList) {
		questionList.clear();
		questionList = qList;
		qusertionAdapter.updateAdapter(questionList);
	}

	/**
	 * 刷新监理列表信息
	 * @param supList
	 */
	private void refreshSupervisorListData() {
		sortSupervisorListAsc(supervisorlist);
		getData(false);
		supervisorAdapter.updataAdapter(supervisorlist);
		mXListView.setSelection(supervisorlist.size());
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consult);
		flage = getIntent().getAction();
		LogUtil.logI("flage:" + flage);
		setupViews();
		initData();
		initServicePopu();
		initQuestionPopu();
		initContactPopu();
	}

	@Override
	protected void setupViews() {
		supervisorlist = new ArrayList<SupervisorBean>();

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.assistant_title);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConsultActivity.this.finish();
			}
		});

//		supervisorlist = new ArrayList<SupervisorBean>();

		mXListView = (RefreshListView) findViewById(R.id.xListView);
//		mXListView.setPullRefreshEnable(true);//下拉刷新
		mXListView.setPullLoadEnable(true);//上拉刷新
		mXListView.setXListViewListener(this);
		supervisorAdapter = new SupervisorAdapter(this, supervisorlist,
				mXListView);
		mXListView.setAdapter(supervisorAdapter);
		
		tvService = (CustomButton) findViewById(R.id.tvService);
		tvService.setOnClickListener(this);
		tvQusetion = (CustomButton) findViewById(R.id.tvQusetion);
		tvQusetion.setOnClickListener(this);
		tvContactMy = (CustomButton) findViewById(R.id.tvContactMy);
		tvContactMy.setOnClickListener(this);

		viewService = (LinearLayout) findViewById(R.id.viewService);
		viewQusetion = (LinearLayout) findViewById(R.id.viewQusetion);
		viewContactMy = (LinearLayout) findViewById(R.id.viewContactMy);

		// 数据库查询
		supervisorDao = new SupervisorDao(this);
		if (supervisorDao.querySupervisor(1, "0").size() == 0) {
			tipId = -1;
			getTipInfo(tipId, type);
		} else {
			queryData(true, 1, "0");
		}
		if (flage != null && flage.equals("1")) {
			tipId = -2;
			getTipInfo(tipId, type);
		}

		mXListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				dissmissPopu();
				return false;
			}
		});

		mXListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SupervisorBean bean = supervisorlist.get(arg2 - 1);
				if (bean.getApi_type() != 1) {
					Intent intent = new Intent();
					intent.putExtra("tip_id", bean.getTip_id());
					intent.setClass(ConsultActivity.this,
							QuestionDetailsWebActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch (task.taskID) {
		case GET_MENU_LIST:
			MenuListResult menuListResult = (MenuListResult) task.result;
			serviceList = new ArrayList<MenuSupervisorBean>();
			// LogUtil.logI("data:" + menuListResult.data);
			if ("success".equals(menuListResult.msg)) {
				if (menuListResult.data != null
						&& !"".equals(menuListResult.data)
						&& !"null".equals(menuListResult.data)) {
					genMenuList(menuListResult.data.toString());
				}
			}
			break;
		case GET_TIP_INFO:
			TipInfoResult tipInfoResult = (TipInfoResult) task.result;
			if ("success".equals(tipInfoResult.msg)) {
				String strReturn = tipInfoResult.data.get("return").toString();
				try {
					JSONObject supervisorObj = new JSONObject(strReturn);
					SupervisorBean bean = new SupervisorBean();
					bean.setApi_type(supervisorObj.getInt("api_type"));
					if (type == 1) {

						if (supervisorObj.has("thumb_url")) {
							bean.setThumb_url(supervisorObj
									.getString("thumb_url"));
						}
						if (supervisorObj.has("summary")) {
							bean.setSummary(supervisorObj.getString("summary"));
						}
						if (supervisorObj.has("title")) {
							bean.setTitle(supervisorObj.getString("title"));
						}
						if (supervisorObj.has("tip_id")) {
							bean.setTip_id(supervisorObj.getInt("tip_id"));
						}
						if (supervisorObj.has("avatar")) {
							bean.setSuprtvisorUrl(supervisorObj
									.getString("avatar"));
						}
						if (supervisorObj.has("name")) {
							bean.setSuprtvisorName(supervisorObj
									.getString("name"));
						}
						if (supervisorObj.has("sex")) {
							if (supervisorObj.getString("sex").equals("1")) {
								bean.setSuprtvisorSex("男");
							} else {
								bean.setSuprtvisorSex("女");
							}
						}
						if (supervisorObj.has("id_num")) { // 省份证号
							if (!supervisorObj.getString("id_num").equals("")) {
								bean.setSuprtvisorIdCard(supervisorObj
										.getString("id_num"));
							}
						}
						if (supervisorObj.has("mobile")) {
							bean.setPhoneNumber(supervisorObj
									.getString("mobile"));
						}
						if (supervisorObj.has("card_id")) {
							bean.setSuprtvisorId(supervisorObj
									.getString("card_id"));
						}
						bean.setUpdateTiem(System.currentTimeMillis() + "");
						// TODO 插入数据库
						supervisorDao.inserSupervisor(bean);
						supervisorlist.add(bean);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				refreshSupervisorListData();
			}
			break;

		case APPLY_VIP:
			BaseResult baseResult = (BaseResult) task.result;
			if ("success".equals(baseResult.msg)) {
				ToastUtil.showToast(this,R.string.toast_apply_success);
			} else {
				ToastUtil.showToast(this, R.string.toast_apply_fail);
			}
			break;
		}
	}

	private void genMenuList(String data) {
		JSONObject dataJson;
		try {
			dataJson = new JSONObject(data);
			JSONObject menuList = dataJson.getJSONObject("return");
			HuoBanApplication.getInstance().saveTempToSharedPreferences(
					StringConstant.SP_KEY_MENU_LIST, menuList.toString(), this);
			AnalysisAssistant(menuList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void AnalysisAssistant(JSONObject menuList) throws JSONException {
		ObjectMapper mapper = null;
		if (menuList.has("1") || menuList.has("2")) {
			mapper = new ObjectMapper();
			try {
				ArrayList<MenuSupervisorBean> serviceList = mapper.readValue(
						menuList.getString("1"),
						mapper.getTypeFactory().constructParametricType(
								ArrayList.class, MenuSupervisorBean.class));

				ArrayList<QuestionBean> qusetionList = mapper.readValue(
						menuList.getString("2"),
						mapper.getTypeFactory().constructParametricType(
								ArrayList.class, QuestionBean.class));
				// 更新数据
				refreshServiceListData(serviceList);
				refreshQuestionListData(qusetionList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.showToast(this, R.string.json_parser_fail);
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		Task task = (Task) param[0];
		switch (task.taskID) {
		case GET_MENU_LIST:
			JSONObject obj;
			try {
				obj = new JSONObject(HuoBanApplication.getInstance()
						.getTempFromSharedPreferences(
								StringConstant.SP_KEY_MENU_LIST, this));
				AnalysisAssistant(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private void getData(boolean isSetSelectio) {
//		supervisorAdapter = new SupervisorAdapter(this, supervisorlist,
//				mXListView);
//		mXListView.setAdapter(supervisorAdapter);
		if (isSetSelectio) {
//			mXListView.clearFocus();
			mXListView.post(new Runnable(){
				@Override
				public void run() {
					//包含Header、Footer两个控件
					mXListView.setSelection(supervisorAdapter.getCount() + 1);
				}});
//			 mXListView.setStackFromBottom(true);
		}
	}

	/**
	 * 让弹出来的界面消失
	 */
	private void dissmissPopu() {
		switch (show_tab) {
		case TAB_SERVICE:
			viewService.startAnimation(popuOut);
			viewService.setVisibility(View.INVISIBLE);
			show_tab = 0;
			break;
		case TAB_QUESTION:
			viewQusetion.startAnimation(popuOut);
			viewQusetion.setVisibility(View.INVISIBLE);
			show_tab = 0;
			break;
		case TAB_CONTACT:
			viewContactMy.startAnimation(popuOut);
			viewContactMy.setVisibility(View.INVISIBLE);
			show_tab = 0;
			break;
		default:
			break;
		}
	}

	private void initData() {
		popuOut = AnimationUtils.loadAnimation(this, R.anim.pop_out_location);
		popuIn = AnimationUtils.loadAnimation(this, R.anim.pop_in_location);
		serviceList = new ArrayList<MenuSupervisorBean>();
		questionList = new ArrayList<QuestionBean>();
		getMenuList();
//		tipId = -1;
//		getTipInfo(tipId, type);
	}

	/**
	 * 获取齐家监理menu_title 数据
	 */
	private void getMenuList() {
		Task task = TasksHelper.getMenuListTask(this, GET_MENU_LIST);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	/**
	 * 获取服务信息
	 * @param tipId
	 * @param type
	 */
	private void getTipInfo(int tipId, int type) {
		Task task = TasksHelper.getTipInfoTask(this, GET_TIP_INFO, tipId, type);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	/**
	 * 齐家服务
	 */
	private void initServicePopu() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.popupwindow_list, null);
		ListView lvService = (ListView) view
				.findViewById(R.id.listView_supervisor);
		adapter = new ServiceAdapter(serviceList, this);
		lvService.setAdapter(adapter);
		viewService.addView(view);
		lvService.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showPopu(TAB_SERVICE);
				MenuSupervisorBean bean = serviceList.get(arg2);
				type = 1;
				getTipInfo(bean.getTip_id(), type);
			}

		});
	}
	/**
	 * 常见问题
	 */
	private void initQuestionPopu() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.popupwindow_list, null);
		ListView lvQuestion = (ListView) view
				.findViewById(R.id.listView_supervisor);
		qusertionAdapter = new QuestionAdapter(questionList, this);
		lvQuestion.setAdapter(qusertionAdapter);
		viewQusetion.addView(view);

		lvQuestion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showPopu(TAB_QUESTION);
				type = 1;
				QuestionBean bean = questionList.get(arg2);
				tipId = bean.getTip_id();
				getTipInfo(tipId, type);
			}
		});
	}
	/**
	 * 联系我们
	 */
	private void initContactPopu() {
		String[] contactList = this.getResources().getStringArray(R.array.contact_us);
		View view = LayoutInflater.from(this).inflate(
				R.layout.popupwindow_list, null);
		ListView lvContact = (ListView) view
				.findViewById(R.id.listView_supervisor);
		RelationAdapter adapter = new RelationAdapter(contactList, this);
		lvContact.setAdapter(adapter);
		viewContactMy.addView(view);

		lvContact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showPopu(TAB_CONTACT);
				switch (arg2) {
				case 0: // 打开申请VIP服务对话框
					showApplyForVipDialog();
					break;
				case 1: // 打开在线客服咨询页面
					/**
					 * 统计装修助手-联系我们-联系客服按钮点击数/点击uv
					 */
					 StatService.onEvent(getApplicationContext(), "btn_contact_us",
					 "装修助手-联系我们-联系客服按钮点击数");
					ActivityUtils.gotoOtherActivity(ConsultActivity.this,
							OnlineServiceActivity.class);
					break;
				case 2: // 打开电话咨询对话框
					showServiceDialog();
					break;
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvService:
			showPopu(TAB_SERVICE);
			break;
		case R.id.tvQusetion:
			showPopu(TAB_QUESTION);
			break;
		case R.id.tvContactMy:
			showPopu(TAB_CONTACT);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示相应的popu
	 * 
	 * @param tab_type
	 */
	private void showPopu(int tab_type) {
		switch (tab_type) {
		case TAB_SERVICE:
			if (show_tab == TAB_QUESTION) {
				viewQusetion.startAnimation(popuOut);
				viewQusetion.setVisibility(View.INVISIBLE);
			} else if (show_tab == TAB_CONTACT) {
				viewContactMy.startAnimation(popuOut);
				viewContactMy.setVisibility(View.INVISIBLE);
			}
			if (show_tab == TAB_SERVICE) {
				viewService.startAnimation(popuOut);
				viewService.setVisibility(View.INVISIBLE);
				show_tab = 0;
			} else {
				viewService.startAnimation(popuIn);
				viewService.setVisibility(View.VISIBLE);
				show_tab = TAB_SERVICE;
			}
			break;
		case TAB_QUESTION:
			if (show_tab == TAB_SERVICE) {
				viewService.startAnimation(popuOut);
				viewService.setVisibility(View.INVISIBLE);
			} else if (show_tab == TAB_CONTACT) {
				viewContactMy.startAnimation(popuOut);
				viewContactMy.setVisibility(View.INVISIBLE);
			}
			if (show_tab == TAB_QUESTION) {
				viewQusetion.startAnimation(popuOut);
				viewQusetion.setVisibility(View.INVISIBLE);
				show_tab = 0;
			} else {
				viewQusetion.startAnimation(popuIn);
				viewQusetion.setVisibility(View.VISIBLE);
				show_tab = TAB_QUESTION;
			}
			break;
		case TAB_CONTACT:
			if (show_tab == TAB_SERVICE) {
				viewService.startAnimation(popuOut);
				viewService.setVisibility(View.INVISIBLE);
			} else if (show_tab == TAB_QUESTION) {
				viewQusetion.startAnimation(popuOut);
				viewQusetion.setVisibility(View.INVISIBLE);
			}

			if (show_tab == TAB_CONTACT) {
				viewContactMy.startAnimation(popuOut);
				viewContactMy.setVisibility(View.INVISIBLE);
				show_tab = 0;
			} else {
				viewContactMy.startAnimation(popuIn);
				viewContactMy.setVisibility(View.VISIBLE);
				show_tab = TAB_CONTACT;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		pageIndex++;
		updateTime = System.currentTimeMillis() + "";
		queryData(false, pageIndex, updateTime);
	}

	@Override
	public void onLoadMore() {

	}

	/**
	 * 
	 */
	private void onLoad() {
		mXListView.stopRefresh();
		mXListView.setRefreshTime("刚刚");
	}

	private void queryData(boolean isSetSelectio, int pageIndex,
			String updateTime) {
		ArrayList<SupervisorBean> tempList = supervisorDao.querySupervisor(
				pageIndex, updateTime);
		if (tempList.size() > 0) {
			if (isSetSelectio) {
				supervisorlist.clear();
			}
			supervisorlist.addAll(tempList);
			sortSupervisorListAsc(supervisorlist);
			supervisorAdapter.notifyDataSetChanged();
			getData(isSetSelectio);
		}
		onLoad();
	}

	/**
	 * 监理时间降序；升序
	 * 
	 * @param list
	 */
	private void sortSupervisorListAsc(List<SupervisorBean> list) {
		Collections.sort(list, new Comparator<SupervisorBean>() {
			@Override
			public int compare(SupervisorBean obj1, SupervisorBean obj2) {
				try {
					long time1 = Long.parseLong(obj1.getUpdateTiem());
					long time2 = Long.parseLong(obj2.getUpdateTiem());
					if (time1 > time2) {
						return 1;
					} else {
						return -1;
					}
				} catch (NumberFormatException e) {
					return 1;
				}
			}
		});
	}

	/**
	 * 显示服务dialog
	 * 
	 * */
	private void showServiceDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Dialog dlg = new Dialog(this, R.style.dialog1);
		View layout = inflater.inflate(R.layout.contact_service, null);
		Button btnService = (Button) layout.findViewById(R.id.btnService);
		Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
		dlg.setContentView(layout);
		DialogUtils.setShareDialogParam(this, dlg);
		dlg.show();

		btnService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 StatService.onEvent(getApplicationContext(),
				 "btn_contact_us_make_call", "装修助手-联系我们-联系客服-拨打电话按钮点击数/点击uv");
				Intent callIntent = new Intent();
				callIntent.setAction(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:" + StringConstant.ASSISTANT_PHONENUMBER));
				try{
					
					startActivity(callIntent);
				}
				catch(Exception e){
					ToastUtil.showToast(ConsultActivity.this, "您的设备不能拨打电话!");
				}
				dlg.dismiss();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}

	/**
	 * 显示申请Vip服务dialog
	 * 
	 * */
	private void showApplyForVipDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Dialog dlg = new Dialog(this, R.style.dialog1);
		View layout = inflater.inflate(R.layout.apply_for_dialog, null);
		Button btnApplyVip = (Button) layout.findViewById(R.id.btnApplyVip);
		Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
		dlg.setContentView(layout);

		DialogUtils.setShareDialogParam(this, dlg);
		dlg.show();

		btnApplyVip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				StatService.onEvent(getApplicationContext(), "btn_apply_vip",
						"装修助手-联系我们-申请VIP服务按钮点击数");
				applyVIP();
				dlg.dismiss();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}

	protected void applyVIP() {
		Task task = TasksHelper.getpplyVIPTask(this,APPLY_VIP);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

}
