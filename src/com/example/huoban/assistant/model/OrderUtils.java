package com.example.huoban.assistant.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;

import com.example.huoban.R;
import com.example.huoban.utils.Utils;

/**
 * 订单工具类
 * 
 * @author cwchun.chen
 * 
 */
public class OrderUtils {
	
	/**
	 * 构造订单实体
	 * @param context
	 * @param orderSummary
	 * @return
	 */
	public static Order genOrder(Context context, OrderSummary orderSummary) {
		Order order = new Order();
		order.setLastModifyTime(orderSummary.last_modify_time);
		order.setShopName(orderSummary.shop_name);
		order.setProcessStatusId(orderSummary.processStatus_id);
		order.setOrderGroupId(orderSummary.orderGroup_id);
		order.setAttribute(orderSummary.attribute);
		order.setOrderId(orderSummary.id);
		String userComment = orderSummary.user_comment;
		if (userComment == null || userComment.equals("")) {
			order.setUserComment(context.getString(R.string.no_input));
		} else {
			order.setUserComment(userComment);
		}
		order.setHasPreOrder(orderSummary.has_preOrder);
		order.setUserMobile(orderSummary.telephone);
		order.setAddTime(Utils.getFormatDate(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss"), orderSummary.add_time));
		String userAddress = orderSummary.userAddress;
		String result[] = userAddress.split(",");
		if (result.length > 4) {
			order.setUserAddress(result[4]);
			if (result[0].equals("")) {
				order.setUserName(context.getString(R.string.no_input));
			} else {
				order.setUserName(result[0]);
			}
		} else if (result.length > 2) {
			order.setUserAddress(context.getString(R.string.no_input));
			if (result[0].equals("")) {
				order.setUserName(context.getString(R.string.no_input));
			} else {
				order.setUserName(result[0]);
			}
		} else {
			order.setUserAddress(context.getString(R.string.no_input));
			order.setUserName(context.getString(R.string.no_input));
		}

		String orderStatus = swtichOrderStatus(context,
				orderSummary.processStatus_id);
		order.setOrderStatus(orderStatus);
		setOrderInfo(orderSummary, order);
		return order;
	}

	public static String swtichOrderStatus(Context context, int position) {
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

	private static void setOrderInfo(OrderSummary orderSummary, Order order) {
		String actName = null;
		if (orderSummary.attribute == 3) {
			actName = orderSummary.act_name;
		} else if (orderSummary.attribute == 2) {

		}

		ArrayList<Commodity> commodityLists = new ArrayList<Commodity>();
		ArrayList<String> imageLists = new ArrayList<String>();
		double dingjinAmount = 0;
		int discountOnThousand = 0;
		int amount = 0;
		for (OrderItem item : orderSummary.orderList) {
			String itemName = "";
			String itemNum = "";
			String imageUrl = "";
			double itemPrice = 0;
			if (orderSummary.attribute == 2) {
				itemName = item.item_name;
				itemPrice = item.r_item_price;
				itemNum = item.product_account;
				imageUrl = item.image_url;
				imageUrl = "http://imgmall.tg.com.cn/" + imageUrl;
			} else if (orderSummary.attribute == 3) {
				if (actName != null) {
					itemPrice = orderSummary.r_total_amount;
					itemName = actName;
					itemNum = "1";
					dingjinAmount = orderSummary.dingjin_amount;
					discountOnThousand = orderSummary.discount_on_thousand;
					amount = orderSummary.input_amount;
				}
			}
			Commodity commodity = new Commodity();

			commodity.setItemName(itemName);
			commodity.setItemPrice((itemPrice / 100) + "");
			commodity.setItemNum(itemNum);
			commodityLists.add(commodity);
			imageLists.add(imageUrl);

		}
		order.setImageLists(imageLists);
		order.setCommodityLists(commodityLists);

		if (discountOnThousand == -1) {
			discountOnThousand = 0;
		}
		order.setDiscountOnThousand(discountOnThousand + "");
		order.setAmount((amount / 100) + "");

		if (orderSummary.attribute == 2) {
			order.setrShippingFee((orderSummary.r_shipping_fee / 100) + "");
		} else if (orderSummary.attribute == 3) {
			order.setrShippingFee("0.00");
		}
		double inputAmount = 0;
		if (amount > 0.01) {
			inputAmount = amount - dingjinAmount;
		} else {
			inputAmount = orderSummary.r_total_amount - dingjinAmount;
		}
		order.setDingjin((dingjinAmount / 100) + "");
		order.setOrderAmount((orderSummary.r_total_amount / 100) + "");
		order.setInputAmount((inputAmount / 100) + "");
	}
}
