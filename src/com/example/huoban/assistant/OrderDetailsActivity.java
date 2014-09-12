package com.example.huoban.assistant;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.adapter.OrderListAdapter;
import com.example.huoban.assistant.model.OrderResult;
import com.example.huoban.assistant.model.OrderSummary;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.pull.RefreshListView;
import com.example.huoban.widget.pull.RefreshListView.IXListViewListener;
/**
 * 齐家钱包——订单列表页
 * @author cwchun.chen
 *
 */
public class OrderDetailsActivity extends BaseActivity implements IXListViewListener{
	public static final String TAG = "OrderDetailsActivity";
	private static final int GET_USER_ORDER_LIST = 100;
	private static final int PROCESSING_ORDERS = 101;
	private RefreshListView listView;
	private OrderListAdapter orderListAdapter;
	private ArrayList<OrderSummary> orderLists;
	private int page = 1;
	private int size = 10;
	private int count;
	private int type ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		setupViews();
	}
	
	@Override
	protected void setupViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.order_summary);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				OrderDetailsActivity.this.finish();
			}});
		
		listView = (RefreshListView) findViewById(R.id.list_view);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		orderListAdapter = new OrderListAdapter(this, orderLists);
		listView.setAdapter(orderListAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("order_summary", orderLists.get(position-1));
				ActivityUtils.gotoOtherActivity(OrderDetailsActivity.this, OrderSummaryActivity.class, bundle);
			}
		});
		
		if (orderLists == null || orderLists.size() == 0) {
			loadData(1);
		}
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case GET_USER_ORDER_LIST:
			OrderResult result = (OrderResult) task.result;
			if(result.data != null && !"null".equals(result.data) && (count=result.data.count) !=0){
				orderLists = (ArrayList<OrderSummary>) result.data.list;
				orderListAdapter = new OrderListAdapter(this, orderLists);
				listView.setAdapter(orderListAdapter);
//				LogUtil.logI("count:" + count);
//				LogUtil.logI("orderLists:" + orderLists);
			}else{
				ToastUtil.showToast(this, R.string.no_order);
			}
			updateOrderLists();
			break;
		case PROCESSING_ORDERS:
			BaseResult processingResult = (BaseResult) task.result;
			if("success".equals(processingResult.msg)){
				if(type == 3){
					ToastUtil.showToast(this, R.string.cancel_order_success);
				} else if (type == 2) {
					ToastUtil.showToast(this, R.string.receipt_success);
				}else if(type == 1){
					ToastUtil.showToast(this, R.string.cancel_order_2_success);
				}
				tvStatus.setVisibility(View.GONE);
			}else{
				ToastUtil.showToast(this, processingResult.msg);
				tvStatus.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 更新记账记录.
	 */
	private void updateOrderLists() {
		orderListAdapter.notifyDataSetChanged();
		listView.setVisibility(View.VISIBLE);
		onLoad();
	}

	/**
	 * 获取订单明细
	 */
	private void getOrderLists(int pageNumber) {
		Task task = TasksHelper.getOrderListsTask(this, GET_USER_ORDER_LIST, pageNumber, size);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	private TextView tvStatus;
	/**
	 * 获取订单处理
	 */
	public void processingOrders(String orderId, int type, TextView textView) {
		this.tvStatus = textView;
		this.type = type;
		Task task = TasksHelper.getprocessingOrdersTask(this, PROCESSING_ORDERS, orderId, type);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void loadData(int pageCount) {
		if (pageCount == 1 && orderLists != null) {
			orderLists.clear();
			page = 1;
			listView.setVisibility(View.GONE);
		}
		getOrderLists(pageCount);
	}

	/**
	 * 隐藏控件.
	 */
	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (orderListAdapter != null && orderListAdapter.imageLoader != null) {
			orderListAdapter.imageLoader.clearDiscCache();
			orderListAdapter.imageLoader.clearMemoryCache();
		}
	}

	@Override
	public void onRefresh() {
		loadData(1);
	}

	@Override
	public void onLoadMore() {
		if (orderLists.size() != count) {
			page++;
			loadData(page);
		}
	}
}
