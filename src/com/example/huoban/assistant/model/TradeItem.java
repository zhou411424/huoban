package com.example.huoban.assistant.model;

public class TradeItem {
	//交易记录返回数据
	public String bizNo ;
	public String bizProductCode ;
	public String buyerId ;
	public String buyerName ;
	public Amount coinAmount;
	public String gmtModified ;
	public String gmtSubmit ;
	public Amount payAmount ;
	public Amount prepaidAmount ;
	public Amount refundInstSettledAmount ;
	public String sellerId ;
	public String sellerName ;
	public Amount settledAmount;
	public String status ;
	public Amount tradeAmount;
	public String tradeSourceVoucherNo ;
	public String tradeType ;
	public String tradeVoucherNo ;
	
	public String tradeMemo;
	
	//充值记录返回数据
	public Amount amount;
	public String extension ;
	public Amount fee;
	public String gmtPaid ;
	public String gmtPaySubmit ;
	public String memberId ;
	public String payMode ;
	public String paymentChannel ;
	public String paymentStatus ;
	public String paymentVoucherNo ;
	public String remark;
	
	//提现记录返回数据
	public String orderTime;
	public String name;
	
}
