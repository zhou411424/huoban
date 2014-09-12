package com.example.huoban.activity.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.huoban.R;
import com.example.huoban.activity.account.AddAcountActivity;
import com.example.huoban.adapter.MyPlanAdapter;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.Bill;
import com.example.huoban.model.Plan;
import com.example.huoban.model.PlanResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class PlanActivity extends BaseActivity implements OnRefreshListener, OnClickListener, PlanEditInterface, DBOperaterInterFace {

	private HuoBanApplication app = null;
	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private MyPlanAdapter myPlanAdapter = null;
	private List<Plan> planList = null;
	private Plan currentPlan = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);
		setupViews();
	}

	@Override
	protected void setupViews() {
		app = HuoBanApplication.getInstance();
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setHeaderDividersEnabled(false);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		planList = new ArrayList<Plan>();
		myPlanAdapter = new MyPlanAdapter(this, planList, 0, 0);
		mListView.setAdapter(myPlanAdapter);

		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn = (ImageButton) findViewById(R.id.ibtn_right);
		ibtn.setOnClickListener(this);
		ibtn = (ImageButton) findViewById(R.id.ibtn_center);
		ibtn.setOnClickListener(this);
		showProgress(null, R.string.waiting, false);
		operateDb(1, null);
	}

	private void operateDb(int id, Object object) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		mPullToRefreshListView.onRefreshComplete();
		if (task.result instanceof PlanResult) {
			PlanResult planResult = (PlanResult) task.result;
			if (planResult.data != null && planResult.data.plan_res != null) {
				planList.clear();
				planList.addAll(planResult.data.plan_res);
				processData();
				operateDb(2, planList);
			}
		} else {
			switch (task.taskID) {
			case 15:
				ToastUtil.showToast(this, R.string.complete_plan, Gravity.CENTER);
				/**
				 * 更改计划状态
				 */
				if (currentPlan != null) {
					currentPlan.status = StringConstant.Two;

				}
				
				operateDb(15, currentPlan);
				break;
			case 16:
				if (currentPlan != null) {
					currentPlan.status = StringConstant.ONE;
				}
				ToastUtil.showToast(this, R.string.resert_plan, Gravity.CENTER);
				operateDb(16, currentPlan);
				break;
			case 17:
				if (currentPlan != null) {
					planList.remove(currentPlan);
				}
				ToastUtil.showToast(this, R.string.del_success, Gravity.CENTER);
				operateDb(17, currentPlan);
				break;
			default:
				break;
			}

			processData();
		}

	}

	private void processData() {
		/**
		 * 对于被删除的计划需要本地剔除
		 */
		List<Plan> delList = new ArrayList<Plan>();
		for (Plan plan : planList) {
			plan.noMsg = false;
			plan.isShowKind = false;
			plan.kind = null;
			plan.outDay = null;
			if (StringConstant.Three.equals(plan.status)) {
				delList.add(plan);
			}
		}
		planList.removeAll(delList);
		UserInfoResult infoResult = app.getInfoResult();
		if(infoResult!=null&&infoResult.data!=null&&infoResult.data.contacter_list!=null){
			/**
			 * 设置头像
			 */
			for (Plan plan : planList) {
				for (Contact contact : infoResult.data.contacter_list) {
					if(plan.user_id!=null&&plan.user_id.equals(contact.user_id)){
						plan.avatar = contact.avatar;
						break;
					}
				}
			}
			
		}
		
		
		List<Plan> now = new ArrayList<Plan>();
		List<Plan> later = new ArrayList<Plan>();
		List<Plan> complete = new ArrayList<Plan>();
		
		Comparator<Plan> comparatorDoneDate = new Comparator<Plan>() {
			
			@Override
			public int compare(Plan lhs, Plan rhs) {
				return lhs.plan_done_date.compareTo(rhs.plan_done_date);
			}
		};
		Comparator<Plan> comparatorCreateDate = new Comparator<Plan>() {
			
			@Override
			public int compare(Plan lhs, Plan rhs) {
				return lhs.create_date.compareTo(rhs.create_date);
			}
		};
		/**
		 * 分类:今天 后续 已完成
		 * 
		 */
		int unDone = 0;
		long timeNow = TimeFormatUtils.getNowDateSeconds();
		for (Plan plan : planList) {
			if (StringConstant.Two.equals(plan.status)) {
				/**
				 * 已完成
				 */
				complete.add(plan);
			} else {
				/**
				 * 未完成的分为今天和后续 还要区分未完成但已经过期的
				 */
				long time = Utils.parseString2Long(plan.plan_done_date);

				if (time <= timeNow&&!StringConstant.ZERO.equals(plan.plan_done_date)) {
					/**
					 * 全部归为今天
					 */
					now.add(plan);
					if (time < timeNow) {
						/**
						 * 过期的
						 */
						plan.outDay = TimeFormatUtils.FormatSeconds2Days(time, timeNow);
					}

				} else {
					later.add(plan);
				}
				if (StringConstant.ZERO.equals(plan.plan_done_date)) {
					/**
					 * 未完成的计划
					 */
					unDone++;
				}
			}

		}
		
		
		/**
		 * 排序
		 */
		Collections.sort(now, comparatorDoneDate);
		sort(later, comparatorDoneDate, comparatorCreateDate);
		sort(complete, comparatorDoneDate, comparatorCreateDate);
		List<Plan> all = new ArrayList<Plan>();
		/**
		 * 设置每个分类的第一项
		 */
		Plan planNow = null;
		if (now.size() > 0) {
			planNow = now.get(0);
			all.addAll(now);
		} else {
			planNow = new Plan();
			planNow.noMsg = true;
			all.add(planNow);
		}
		planNow.isShowKind = true;
		planNow.kind = res.getString(R.string.plan_now);

		Plan planLater = null;
		if (later.size() > 0) {
			planLater = later.get(0);
			all.addAll(later);
		} else {
			planLater = new Plan();
			planLater.noMsg = true;
			all.add(planLater);
		}
		planLater.isShowKind = true;
		planLater.kind = res.getString(R.string.plan_later);

		Plan planComplete = null;
		if (complete.size() > 0) {
			planComplete = complete.get(0);
			all.addAll(complete);
		} else {
			planComplete = new Plan();
			planComplete.noMsg = true;
			all.add(planComplete);
		}
		planComplete.isShowKind = true;
		planComplete.kind = res.getString(R.string.plan_complete);
		myPlanAdapter.refresh(all, unDone, now.size());
	}
	
	
	private void sort(List<Plan>planList,Comparator<Plan> comparatorDoneDate,Comparator<Plan> comparatorCreateDate){
		List<Plan>doneDate = new ArrayList<Plan>();
		List<Plan>createDate = new ArrayList<Plan>();
		for (Plan plan : planList) {
			if(!StringConstant.ZERO.equals(plan.plan_done_date)){
				doneDate.add(plan);
			}else{
				createDate.add(plan);
			}
		}
		Collections.sort(doneDate, comparatorDoneDate);
		Collections.sort(createDate, comparatorCreateDate);
		planList.clear();
		planList.addAll(doneDate);
		planList.addAll(createDate);
	}
	
	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	private void getPlan(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_PLAN_LIST;
		task.resultDataClass = PlanResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String imei = Utils.getDeviceId(this);
		String updateDate = app.getTempFromSharedPreferences("plan_time", this);
		if (!Utils.stringIsNotEmpty(updateDate)) {
			updateDate = res.getString(R.string.zero);
		}
		String user_id = app.getUserId(this);
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&sync=");
		sb.append(updateDate);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("imei", imei);
		map.put("sign", sign);
		map.put("sync", updateDate);
		map.put("user_id", user_id);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void onRefresh() {
		getPlan(false);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			intent = new Intent(this, AddOrUpdatePlanActivity.class);
			startActivityForResult(intent, 123);
			break;
		case R.id.ibtn_center:
			intent = new Intent(this, SearchPlanActivity.class);
			intent.putExtra("planList", (Serializable) planList);
			startActivityForResult(intent, 124);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 123 && arg1 == RESULT_OK) {
			/**
			 * 修改了或者穿件了
			 */
			getPlan(true);
		} else if (arg0 == 124 && arg1 == RESULT_OK) {
			List<Plan> list = (List<Plan>) arg2.getSerializableExtra("planList");
			if (list != null) {
				this.planList = list;
				processData();
			}
		}
	}

	@Override
	public void doPlanMethord(Plan plan, int MethordId, Object tag) {
		currentPlan = plan;
		switch (MethordId) {
		case 15:
			/**
			 * 完成计划
			 */
			completeOrResertPlan(plan, 15);
			break;
		case 16:
			/**
			 * 恢复计划
			 */
			completeOrResertPlan(plan, 16);
			break;
		case 17:
			/**
			 * 删除计划
			 */
			delPlan(plan, 17);
			break;
		case 18:
			/**
			 * 添加记账
			 */
			Bill bill = new Bill();
			bill.bill_content = plan.plan_content;
			bill.bill_remark = plan.remark;
			bill.avatar = plan.avatar;
			bill.isCreateFromPlan = true;
			Intent intent = new Intent(this, AddAcountActivity.class);
			intent.putExtra("bill", bill);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	/**
	 * 完成恢复计划
	 * 
	 * @param plan
	 * @param id
	 */
	private void completeOrResertPlan(Plan plan, int id) {
		Task task = new Task();
		task.taskID = id;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_COMPLETE_PLAN;
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		String plan_id = plan.plan_id;
		String status = null;
		if (StringConstant.Two.equals(plan.status)) {
			status = StringConstant.ONE;
		} else {
			status = StringConstant.Two;
		}
		String user_id = app.getUserId(this);

		sb.append("imei=");
		sb.append(imei);
		sb.append("&plan_id=");
		sb.append(plan_id);
		sb.append("&status=");
		sb.append(status);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("imei", imei);
		map.put("plan_id", plan_id);
		map.put("status", status);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 删除计划
	 */
	private void delPlan(Plan plan, int id) {
		Task task = new Task();
		task.taskID = id;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.target = this;
		task.taskQuery = URLConstant.URL_DEL_PLAN;
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		String plan_id = plan.plan_id;
		String user_id = app.getUserId(this);
		sb.append("imei=");
		sb.append(imei);
		sb.append("&plan_id=");
		sb.append(plan_id);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("imei", imei);
		map.put("plan_id", plan_id);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		Object object = null;

		switch (dbParamData.taskId) {
		case 1:
			/**
			 * 读取
			 */
			object = DBOperateUtils.readPaln(this);

			break;
		case 2:
			/**
			 * 存储
			 */
			
			List<Plan> list = (List<Plan>) dbParamData.object;
			DBOperateUtils.savePlanToDB(this, list);
			
			break;

		case 15:
		case 16:
			Plan plan = (Plan) dbParamData.object;
			DBOperateUtils.modifyOrDelPlan(this, plan, false);
			break;
		case 17:
			Plan planA = (Plan) dbParamData.object;
			DBOperateUtils.modifyOrDelPlan(this, planA, true);
			break;
		default:
			break;
		}

		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		switch (dbParamData.taskId) {
		case 1:
			List<Plan> list  =(List<Plan>) dbParamData.object;
			if(list!=null){
				planList.addAll(list);
				processData();
			}
			getPlan(false);
			break;

		default:
			break;
		}

	}
}
