package com.example.huoban.assistant;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.model.Order;
import com.example.huoban.assistant.model.OrderSummary;
import com.example.huoban.assistant.model.OrderUtils;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.widget.other.SummaryLayout;

/**
 * 订单详细信息页
 * 
 * @author cwchun.chen
 * 
 */
public class OrderSummaryActivity extends BaseActivity {
	/**
	 * TAG.
	 */
	public static final String TAG = "OrderSummaryActivity";
	private OrderSummary orderSummary;
	private Order mOrder;
	private TextView sumarryUsername, sumarryPhone, summaryAddress,
			summaryMessage, summaryDingjin, summaryInputamount, summaryFright,
			summaryTotal, summaryDiscountTotal, summaryDiscount,
			summaryTotalText, summaryDiscountText;
	private LinearLayout summaryLayout;
	private ArrayList<SummaryLayout> layoutLists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_summary);
		initData();
		setupViews();
	}

	/**
	 * 初始化数据.
	 */
	private void initData() {
		orderSummary = (OrderSummary) getIntent().getExtras().getSerializable(
				"order_summary");
		mOrder = OrderUtils.genOrder(this, orderSummary);// 构造订单实体
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		for (int i = 0; i < layoutLists.size(); i++) {
			SummaryLayout layout = layoutLists.get(i);
			if (layout != null) {
				layout.clearCache();
			}
		}
	}

	@Override
	protected void setupViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.order_summary);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OrderSummaryActivity.this.finish();
			}
		});

		sumarryUsername = (TextView) findViewById(R.id.summary_username);
		sumarryPhone = (TextView) findViewById(R.id.summary_phone);
		summaryAddress = (TextView) findViewById(R.id.summary_address);
		summaryMessage = (TextView) findViewById(R.id.summary_message);
		summaryDingjin = (TextView) findViewById(R.id.summary_dingjin);
		summaryInputamount = (TextView) findViewById(R.id.summary_inputamount);
		summaryFright = (TextView) findViewById(R.id.summary_fright);
		summaryTotal = (TextView) findViewById(R.id.summary_total);
		summaryDiscountTotal = (TextView) findViewById(R.id.summary_discount_total);
		summaryDiscount = (TextView) findViewById(R.id.summary_discount);

		summaryTotalText = (TextView) findViewById(R.id.summary_total_text);
		summaryDiscountText = (TextView) findViewById(R.id.summary_discount_text);

		summaryLayout = (LinearLayout) findViewById(R.id.summary_layout);

		String userName = " " + mOrder.getUserName();

		String address = " " + mOrder.getUserAddress();

		String message = " " + mOrder.getUserComment();

		String dingjin = "￥" + mOrder.getDingjin();

		String inputAmount = "￥" + mOrder.getInputAmount();

		String fright = "￥" + mOrder.getrShippingFee();

		String total = "￥" + mOrder.getOrderAmount();

		String amount = "￥" + mOrder.getAmount();

		String discount = "";
		int discountOnThousand = 100;
		if (mOrder.getDiscountOnThousand() != null
				&& !mOrder.getDiscountOnThousand().equals("")) {
			discountOnThousand = (int) (discountOnThousand - Double
					.parseDouble(mOrder.getDiscountOnThousand()));
		}
		if (discountOnThousand != 100 && !mOrder.getAmount().equals("0.00")) {
			discount = discountOnThousand + "折";

			summaryDiscount.setText(discount);
			summaryTotal.setText(amount);
			summaryDiscount.setVisibility(View.VISIBLE);
			summaryTotal.setVisibility(View.VISIBLE);
			summaryTotalText.setVisibility(View.VISIBLE);
			summaryDiscountText.setVisibility(View.VISIBLE);
		} else {
			summaryDiscount.setText("");
			summaryTotal.setText("");
			summaryDiscount.setVisibility(View.GONE);
			summaryTotal.setVisibility(View.GONE);
			summaryTotalText.setVisibility(View.GONE);
			summaryDiscountText.setVisibility(View.GONE);
		}

		summaryDingjin.setText(dingjin);
		summaryInputamount.setText(inputAmount);
		summaryFright.setText(fright);
		summaryDiscountTotal.setText(total);
		sumarryPhone.setText(mOrder.getUserMobile());
		sumarryUsername.setText(userName);
		summaryAddress.setText(address);
		summaryMessage.setText(message);

		layoutLists = new ArrayList<SummaryLayout>();

		for (int i = 0; i < mOrder.getImageLists().size(); i++) {
			SummaryLayout summary = new SummaryLayout(this);
			summary.setParam(mOrder.getImageLists().get(i), mOrder
					.getCommodityLists().get(i));
			layoutLists.add(summary);
			summaryLayout.addView(summary);
		}
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}

}
