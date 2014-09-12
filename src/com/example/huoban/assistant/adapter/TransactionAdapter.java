package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.Trade;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.utils.ViewHolder;
/**
 * 交易记录、充值记录、提现记录列表适配器
 * @author cwchun.chen
 *
 */
public class TransactionAdapter extends BaseAdapter{
	public static final String TAG = "TransactionAdapter";
	private ArrayList<Trade> transactionLists;
	 private Context context;

	public TransactionAdapter(Context context, ArrayList<Trade> tradeLists) {
		 this.context = context;
		 this.transactionLists = tradeLists;
	}

	@Override
	public int getCount() {
//		System.out.println("count:" + transactionLists.size());
		return transactionLists.size();
	}

	@Override
	public Object getItem(int position) {
		return transactionLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setAmount(TextView textView, int position) {
		if (transactionLists.get(position).getTradeType() == 1) {
			if (isBuyer(position)) {
				textView.setText("-"
						+ transactionLists.get(position).getTradeAmount());
			} else {
				textView.setText("+"
						+ transactionLists.get(position).getTradeAmount());
			}
		} else if (transactionLists.get(position).getTradeType() == 2) {
			textView.setText("+"
					+ transactionLists.get(position).getTradeAmount());
		} else if (transactionLists.get(position).getTradeType() == 3) {
			textView.setText("-"
					+ transactionLists.get(position).getTradeAmount());
		}
	}

	private void setStatus(TextView textView, int position) {
		int type = transactionLists.get(position).getTradeType();
		if (type == 1) {
			if (transactionLists.get(position).getTradeStatus().equals("100")) {
				if (isBuyer(position)) {
					textView.setText(R.string.trade_100);
				} else {
					textView.setText(R.string.trade_100_2);
				}
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("110")) {
				if (isBuyer(position)) {
					textView.setText(R.string.trade_100);
				} else {
					textView.setText(R.string.trade_100_2);
				}
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("111")) {
				textView.setText(R.string.trade_111);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("121")) {
				textView.setText(R.string.trade_121);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("201")) {
				textView.setText(R.string.trade_201);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("301")) {
				if (transactionLists.get(position).getTypeStatus()
						.equals("INSTANT_TRASFER")) {
					textView.setText(R.string.trade_301_2);
				} else {
					textView.setText(R.string.trade_301);
				}
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("401")) {
				if (transactionLists.get(position).getTypeStatus()
						.equals("INSTANT_TRASFER")) {
					textView.setText(R.string.trade_401_2);
				} else {
					textView.setText(R.string.trade_401);
				}
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("999")) {
				textView.setText(R.string.trade_999);
			}
		} else if (type == 2) {
			if (transactionLists.get(position).getTradeStatus().equals("S")) {
				textView.setText(R.string.pay_s);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("R")) {
				textView.setText(R.string.pay_r);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("F")) {
				textView.setText(R.string.pay_f);
			} else {
				textView.setText(R.string.pay_o);
			}
		} else if (type == 3) {
			if (transactionLists.get(position).getTradeStatus()
					.equals("submitted")) {
				textView.setText(R.string.status_submitted);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("success")) {
				textView.setText(R.string.status_success);
			} else if (transactionLists.get(position).getTradeStatus()
					.equals("failed")) {
				textView.setText(R.string.status_failed);
			}
		}
	}

	private void setType(TextView textView, int position) {
		int type = transactionLists.get(position).getTradeType();
		if (type == 1) {
			if ( transactionLists.get(position).getTradeMemo() == null || "".equals(transactionLists.get(position).getTradeMemo())) {
				textView.setText("");
			} else {
				textView.setText(R.string.p_acquiring);
			}
		} else if (type == 2) {
			textView.setText(R.string.deposit);
		} else if (type == 3) {
			textView.setText(R.string.money);
		}
	}

	private boolean isBuyer(int position) {
		String memberId = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_MEMBER_ID, context);//dataManager.readTempData("memberId");
		if (transactionLists.get(position).getBuyerId().equals(memberId)) {
			return true;
		}
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_transaction, null);
		}
		ViewHolder.get(convertView, R.id.image_view_layout).setVisibility(View.GONE);
		TextView tvTradeDate = ViewHolder.get(convertView, R.id.item_text_content);
		tvTradeDate.setText(transactionLists.get(position).getTradeDate());

		setAmount((TextView)ViewHolder.get(convertView, R.id.item_date), position);
		setStatus((TextView)ViewHolder.get(convertView,R.id.item_text_amount), position);
		setType((TextView)ViewHolder.get(convertView,R.id.item_text_view), position);
		return convertView;
	}

}
