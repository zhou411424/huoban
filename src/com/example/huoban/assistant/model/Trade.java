package com.example.huoban.assistant.model;

import java.text.DecimalFormat;

public class Trade {

	private String tradeDate;

	private String tradeAmount;

	private String tradeStatus;
	
	private String tradeMemo;
	
	private String buyerId;
	
	private String sellerId;
	
	private int tradeType;
	
	private String typeStatus;

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

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(String tradeAmount) {
		tradeAmount = StringIoDouble(tradeAmount);
		this.tradeAmount = tradeAmount;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getTradeMemo() {
		return tradeMemo;
	}

	public void setTradeMemo(String tradeMemo) {
		this.tradeMemo = tradeMemo;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public int getTradeType() {
		return tradeType;
	}

	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}

	public String getTypeStatus() {
		return typeStatus;
	}

	public void setTypeStatus(String typeStatus) {
		this.typeStatus = typeStatus;
	}

}
