package com.example.huoban.assistant.model;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Order {

	private int orderId;

	private String orderGroupId;

	private String orderTime;

	private String orderAmount;

	private String orderFreight;

	private String orderStatus;

	private ArrayList<String> imageLists;

	private String userAddress;

	private String userMobile;

	private String userName;

	private int processStatusId;
	
	private int attribute;

	private String userComment;

	private String dingjin;

	private String inputAmount;

	private String lastModifyTime;

	private String rShippingFee;

	private String addTime;

	private String shopName;

	private String discountOnThousand;

	private String amount;
	
	private int hasPreOrder;

	private ArrayList<Commodity> commodityLists;

	private String StringIoDouble(String param) {
		if (param == null || param.equals("null") || param.equals("")) {
			param = "";
			return param;
		}
		double amount = Double.parseDouble(param);
		DecimalFormat df = new DecimalFormat("0.00");
		param = df.format(amount);
		return param;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderGroupId() {
		return orderGroupId;
	}

	public void setOrderGroupId(String orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		orderAmount = StringIoDouble(orderAmount);
		this.orderAmount = orderAmount;
	}

	public String getOrderFreight() {
		return orderFreight;
	}

	public void setOrderFreight(String orderFreight) {
		orderFreight = StringIoDouble(orderFreight);
		this.orderFreight = orderFreight;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public ArrayList<String> getImageLists() {
		return imageLists;
	}

	public void setImageLists(ArrayList<String> imageLists) {
		this.imageLists = imageLists;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		if (userAddress == null || userAddress.equals("null")) {
			userAddress = "";
		}
		this.userAddress = userAddress;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if (userName == null || userName.equals("null")) {
			userName = "";
		}
		this.userName = userName;
	}

	public int getProcessStatusId() {
		return processStatusId;
	}

	public void setProcessStatusId(int processStatusId) {
		this.processStatusId = processStatusId;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getDingjin() {
		return dingjin;
	}

	public void setDingjin(String dingjin) {
		dingjin = StringIoDouble(dingjin);
		this.dingjin = dingjin;
	}

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		inputAmount = StringIoDouble(inputAmount);
		this.inputAmount = inputAmount;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public ArrayList<Commodity> getCommodityLists() {
		return commodityLists;
	}

	public void setCommodityLists(ArrayList<Commodity> commodityLists) {
		this.commodityLists = commodityLists;
	}

	public String getrShippingFee() {
		return rShippingFee;
	}

	public void setrShippingFee(String rShippingFee) {
		rShippingFee = StringIoDouble(rShippingFee);
		this.rShippingFee = rShippingFee;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getDiscountOnThousand() {
		return discountOnThousand;
	}

	public void setDiscountOnThousand(String discountOnThousand) {
		discountOnThousand = StringIoDouble(discountOnThousand);
		this.discountOnThousand = discountOnThousand;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		amount = StringIoDouble(amount);
		this.amount = amount;
	}

	public int getHasPreOrder() {
		return hasPreOrder;
	}

	public void setHasPreOrder(int hasPreOrder) {
		this.hasPreOrder = hasPreOrder;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

}
