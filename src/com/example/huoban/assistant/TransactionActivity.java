package com.example.huoban.assistant;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.adapter.TransactionAdapter;
import com.example.huoban.assistant.model.Trade;
import com.example.huoban.assistant.model.TradeData;
import com.example.huoban.assistant.model.TradeItem;
import com.example.huoban.assistant.model.TradeResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.pull.RefreshListView;
import com.example.huoban.widget.pull.RefreshListView.IXListViewListener;
/**
 * 齐家钱包——交易记录
 * @author cwchun.chen
 *
 */
public class TransactionActivity extends BaseActivity implements IXListViewListener{
	public static final String TAG = "TransactionActivity";
	private TransactionActivity that = this;
	private static final int GET_USER_TRADE_LIST = 100;
	private static TransactionActivity instance;
	private TextView tranAmount, tranLog, tranRecharge, tranWithdrawal;
	private RefreshListView listView;
	private TransactionAdapter transactionAdapter;
	private ArrayList<Trade> tradeLists;
	private int page = 0;
	private int type = 1;
	private int total ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		initData();
		setupViews();
	}

	@Override
	protected void setupViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.buy_log);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				that.finish();
			}});
		
		tranAmount = (TextView) findViewById(R.id.tran_amount);
		tranLog = (TextView)findViewById(R.id.tran_log);
		tranRecharge = (TextView) findViewById(R.id.tran_recharge);
		tranWithdrawal = (TextView) findViewById(
				R.id.tran_withdrawal);

		listView = (RefreshListView) findViewById(R.id.list_view);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		tranLog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tranLog.setTextColor(that.getResources().getColor(R.color.color_orange));
				tranLog.setBackgroundColor(Color.WHITE);
				tranRecharge.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranRecharge.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				tranWithdrawal.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranWithdrawal.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				type = 1;
				loadData(1, type); 
			}
		});

		tranRecharge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tranLog.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranLog.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				tranRecharge.setTextColor(that.getResources().getColor(R.color.color_orange));
				tranRecharge.setBackgroundColor(Color.WHITE);
				tranWithdrawal.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranWithdrawal.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				type = 2;
				loadData(1, type);
			}
		});

		tranWithdrawal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tranLog.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranLog.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				tranRecharge.setTextColor(that.getResources().getColor(R.color.foot_black));
				tranRecharge.setBackgroundColor(that.getResources().getColor(R.color.grey2));
				tranWithdrawal.setTextColor(that.getResources().getColor(R.color.color_orange));
				tranWithdrawal.setBackgroundColor(Color.WHITE);
				type = 3;
				loadData(1, type);
			}
		});

		transactionAdapter = new TransactionAdapter(this, tradeLists);
		listView.setAdapter(transactionAdapter);

		loadData(1, type);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case GET_USER_TRADE_LIST:
			TradeResult result = (TradeResult) task.result;
			if("success".equals(result.msg)){
				if(type == 1){	//交易记录
					setTradeData(result.data);
				}else if(type == 2){	//充值记录
					setDepositData(result.data);
				}else if(type == 3){	//提现记录
					setMoneyData(result.data);
				}
				updateTradeLists();
			}else{
				ToastUtil.showToast(this, result.msg);
			}
			break;
		}

	}

	private void setMoneyData(TradeData data) {
		// TODO Auto-generated method stub
		if(data == null || "".equals(data) || "null".equals(data)){
			//Toast.makeText(this, R.string.transaction_error, Toast.LENGTH_SHORT).show();
		}else{
			List<TradeItem> list = data.list;
			if(list != null && list.size() > 0){
				total = data.total;
				for(TradeItem item : data.list){
					Trade trade = new Trade();
					trade.setTradeMemo(item.name);
					trade.setTradeType(3);
					trade.setTradeStatus(item.status);
					trade.setTradeAmount(item.amount.amount);
					trade.setTradeDate(TimeFormatUtils.timestamp2String(item.gmtPaySubmit));
					tradeLists.add(trade);
				}
			}
		}
		
	}

	private void setDepositData(TradeData data) {
		// TODO Auto-generated method stub
		if(data == null || "".equals(data) || "null".equals(data)){
			//Toast.makeText(this, R.string.transaction_error, Toast.LENGTH_SHORT).show();
		}else{
			List<TradeItem> list = data.list;
			if(list != null && list.size() > 0){
				total = data.total;
				for(TradeItem item : data.list){
					Trade trade = new Trade();

					trade.setTradeAmount(item.amount.amount);
					trade.setTradeMemo(item.remark);
					trade.setTradeType(2);
					trade.setTradeDate(TimeFormatUtils.timestamp2String(item.gmtPaySubmit));
					trade.setTradeStatus(item.paymentStatus);

					tradeLists.add(trade);
				}
			}
		}
	}

	private void setTradeData(TradeData data) {
		// TODO Auto-generated method stub
		if(data == null || "".equals(data) || "null".equals(data)){
			Toast.makeText(this, R.string.transaction_error, Toast.LENGTH_SHORT).show();
		}else{
			List<TradeItem> list = data.list;
			if(list != null && list.size() > 0){
				total = data.total;
//				if(transactionAdapter.getCount() != 0 && total % transactionAdapter.getCount() == 1){
					for(TradeItem item : data.list){
						String amount = item.payAmount.amount;
						String tradeStatus = item.status;
						String tradeDate = item.gmtSubmit;
						String tradeMemo = item.tradeMemo;
						String buyerId = item.buyerId;
						String sellerId = item.sellerId;
						String typeStatus = item.tradeType;
						
						Trade trade = new Trade();
						trade.setTradeAmount(amount + "");
						trade.setBuyerId(buyerId + "");
						trade.setSellerId(sellerId + "");
						trade.setTradeMemo(tradeMemo);
						trade.setTradeType(1);
						trade.setTypeStatus(typeStatus);
						trade.setTradeDate(TimeFormatUtils.timestamp2String(tradeDate));
						trade.setTradeStatus(tradeStatus);
						tradeLists.add(trade);
					}
					
//				}
			}
		}
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	/**
	 * 更新记账记录.
	 */
	private void updateTradeLists() {
		transactionAdapter.notifyDataSetChanged();
		listView.setVisibility(View.VISIBLE);
		onLoad();
	}

	/**
	 * newInstance.
	 */
	public static final TransactionActivity newInstance() {
		if (instance == null) {
			instance = new TransactionActivity();
		}
		return instance;
	}

	/**
	 * 初始化数据.
	 */
	private void initData() {
		tradeLists = new ArrayList<Trade>();
	}

	/**
	 * 隐藏控件.
	 */
	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}

	private void loadData(int pageCount, int type) {
		if (pageCount == 1) {
			tradeLists.clear();
			page = 1;
			listView.setVisibility(View.GONE);
		}

		getTradeList(pageCount, type);
	}

	private void getTradeList(int pageCount, int type) {
		Task task = TasksHelper.getUserTradeListTask(this, GET_USER_TRADE_LIST, pageCount, type);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void onStart() {
		super.onStart();
		tranAmount.setText("￥" + HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_BALANCE,this));
	}

	@Override
	public void onRefresh() {
		loadData(1, type);
	}

	@Override
	public void onLoadMore() {
		Log.i("log",
				"size=" + tradeLists.size() + ",adapterCount="
						+ transactionAdapter.getCount() + ",total="
						+ total);
		if (tradeLists.size() != total) {
			page++;
			loadData(page, type);
		}
	}

}
