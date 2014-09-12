package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.OrderDetailsActivity;
import com.example.huoban.assistant.model.OrderItem;
import com.example.huoban.assistant.model.OrderSummary;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.other.GalleryLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单明细——订单概况列表适配器
 * @author cwchun.chen
 *
 */
public class OrderListAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<OrderSummary> orderLists;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	public OrderListAdapter(Context context ,ArrayList<OrderSummary> orderLists){
		this.context = context;
		this.orderLists = orderLists;
	}
	
	@Override
	public int getCount() {
		return orderLists == null ? 0 : orderLists.size();
	}

	@Override
	public Object getItem(int position) {
		return orderLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_order,
					null);
		}
		//订单编号
		TextView tvOrderId = ViewHolder.get(convertView, R.id.order_id);
		tvOrderId.setText(" "
				+ orderLists.get(position).orderGroup_id);
		//下订时间
		TextView tvDate = ViewHolder.get(convertView, R.id.order_time);
		tvDate.setText(" " + Utils.getFormatDate(TimeFormatUtils.sdfFormat, orderLists.get(position).add_time));
		//订单金额
		TextView tvAmount = ViewHolder.get(convertView, R.id.order_amount);
		tvAmount.setText(" ￥"
				+ Utils.StringIoDouble(String.valueOf(orderLists.get(position).r_total_amount / 100)));
		//商家名称
		TextView tvShop = ViewHolder.get(convertView, R.id.order_shop);
		tvShop.setText(" " + orderLists.get(position).shop_name);
		//订单状态
		TextView tvStatus = ViewHolder.get(convertView, R.id.order_status);
		tvStatus.setText(" " + swtichOrderStatus(orderLists.get(position).processStatus_id));
		
		ArrayList<String> lists = getImageLists(position, orderLists.get(position).attribute);//orderLists.get(position).getImageLists();
		GalleryLayout galleryLayout = ViewHolder.get(convertView, R.id.order_layout);
		galleryLayout.setParam(imageLoader, lists);
		
		final TextView tvConfirm = ViewHolder.get(convertView, R.id.order_confirm);
		int receipetAndCancel = isReceiptAndCancel(position);
		if (receipetAndCancel == 1) {
			tvConfirm.setText(R.string.cancel_order_2);
			tvConfirm.setVisibility(View.VISIBLE);
		} else if (receipetAndCancel == 2) {
			tvConfirm.setText(R.string.receipet);
			tvConfirm.setVisibility(View.VISIBLE);
		} else if (receipetAndCancel == 3) {
			tvConfirm.setText(R.string.cancel_order_1);
			tvConfirm.setVisibility(View.VISIBLE);
		} else {
			tvConfirm.setVisibility(View.GONE);
		}
		tvConfirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO 订单处理
				String currentText = tvConfirm.getText().toString();
				String orderId = orderLists.get(position).id + "";
				int type = 0;
				if (currentText.equals(context
						.getString(R.string.cancel_order_1))) {
					type = 3;
				} else if (currentText.equals(context
						.getString(R.string.receipet))) {
					type = 2;
				} else if (currentText.equals(context
						.getString(R.string.cancel_order_2))) {
					type = 1;
				}
				
				((OrderDetailsActivity)context).processingOrders(orderId, type, tvConfirm);
			}});
		return convertView;
	}
	private ArrayList<String> getImageLists(int position ,int attribute) {
		ArrayList<String> imageLists = new ArrayList<String>();
		if (attribute == 2) {
			for(OrderItem item : orderLists.get(position).orderList){
				//图片地址
				imageLists.add("http://imgmall.tg.com.cn/" + item.image_url);
				LogUtil.logI("image url:" + item.image_url);
			}
		}else{
			imageLists.add("");
		}
		return imageLists;
	}

	private int isReceiptAndCancel(int position) {
		int processStatusId = orderLists.get(position).processStatus_id;
		if (isCancel(position, processStatusId)) {
			return 1; // 取消 
		} else if (uNsubscribe(position, processStatusId)) {
			return 3; // 退订
		} else if (processStatusId == 71 || processStatusId == 81) {
			return 2; // 确认收货
		}
		return 0;
	}

	private boolean isCancel(int position, int processStatusId) {
		int attribute = orderLists.get(position).attribute;
		int hasPreOrder = orderLists.get(position).has_preOrder;
		if (attribute == 2) {
			if (processStatusId == 21) {
				return true;
			} else if (hasPreOrder == 0 && processStatusId == 41) {
				return true;
			}
		} else if (attribute == 3) {
			if (processStatusId == 20) {
				return true;
			} else if (hasPreOrder == 0 && processStatusId == 41) {
				return true;
			}
		}

		return false;
	}

	private boolean uNsubscribe(int position, int processStatusId) {
		int attribute = orderLists.get(position).attribute;
		int hasPreOrder = orderLists.get(position).has_preOrder;
		if (attribute == 3) {
			if (processStatusId == 21) {
				return true;
			} else if (hasPreOrder == 1 && processStatusId == 41) {
				return true;
			}
		}

		return false;
	}
	
	private String swtichOrderStatus(int position) {
		String orderStatus = "";
		switch (position) {
		case 20:
			orderStatus = context.getString(R.string.processtatid20);
			break;
		case 21:
			orderStatus = context.getString(R.string.processtatid21);
			break;
		case 41:
			orderStatus = context.getString(R.string.processtatid41);
			break;
		case 51:
			orderStatus = context.getString(R.string.processtatid51);
			break;
		case 71:
			orderStatus = context.getString(R.string.processtatid71);
			break;
		case 81:
			orderStatus = context.getString(R.string.processtatid81);
			break;
		case 91:
			orderStatus = context.getString(R.string.processtatid91);
			break;
		case 100:
			orderStatus = context.getString(R.string.processtatid100);
			break;
		default:
			break;
		}
		return orderStatus;
	}
}
