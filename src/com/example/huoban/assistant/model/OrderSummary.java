package com.example.huoban.assistant.model;

import java.io.Serializable;
import java.util.List;
/**
 * 订单概要
 * @author cwchun.chen
 *
 */
public class OrderSummary implements Serializable{
//	public int is_online ;
//	public int total_refund_fee ;
//	public int company_id ;
	public double r_total_amount ;
//	public int appId ;
	public double r_shipping_fee ;
	public int has_preOrder ;
//	public int provider_id ;
//	public int subStatus_id ;
	public int attribute ;
//	public int pre_order_fee ;
//	public int act_id ;
//	public int pay_count ;
//	public int city_id ;
	public int id ;
	public String userAddress ;
//	public int is_refunding ;
//	public String company_name ;
//	public String user_mobile ;
	public String last_modify_time ;
//	public int settled_rate ;
//	public long user_id ;
	public int processStatus_id ;
//	public long auto_cancel_time ;
//	public int settled_type ;
//	public int pay_amount ;
//	public int shop_id ;
//	public String substation ;
//	public int province_id ;
	public String act_name ;
//	public int settled_amount ;
//	public long pay_time ;
//	public int pre_credits_fee ;
//	public int r_final_amount ;
//	public String source_entry ;
	public String orderGroup_id ;
	public List<OrderItem> orderList ;
//	public int channel_type ;
	public String shop_name ;
//	public long cancel_time ;
//	public String user_name ;
//	public int source ;
	public String user_comment ;
//	public String package_id ;
	public String add_time ;
	public String telephone ;
	
	public double dingjin_amount;
	public int discount_on_thousand;
	public int input_amount;
}
