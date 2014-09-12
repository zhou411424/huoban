package com.example.huoban.assistant.model;

import java.text.DecimalFormat;

public class Commodity {

	private String itemName;
	
	private String itemPrice;
	
	private String itemNum;
	
	private String StringIoDouble(String param){
		if(param == null || param.equals("null") || param.equals("")){
			param = "";
			return param; 
		}
		double amount = Double.parseDouble(param);
		DecimalFormat df = new DecimalFormat("0.00");
		param = df.format(amount); 
        return param;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(String itemPrice) {
		itemPrice = StringIoDouble(itemPrice);
		this.itemPrice = itemPrice;
	}

	public String getItemNum() {
		return itemNum;
	}

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	
}
