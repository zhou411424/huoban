package com.example.huoban.model;

import java.io.Serializable;

public class Bill implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String user_id;
	public String user_name;
	public String family_id;
	public String bill_content;
	public String bill_remark;
	public String bill_amount;
	public String last_modify_uid;
	public String last_modify_name;
	public String bill_date;
	public String status;
	public String last_modify_time;
	public String create_date;
	public String bill_id;
	public String avatar;
	public boolean isCreateFromPlan;
}
