package com.example.huoban.model;

import java.io.Serializable;

public class Plan implements Serializable{
	public String create_date;
	public String dir_id;
	public String done_date;
	public String done_user_id;
	public String family_id;
	public String last_modify_name;
	public String last_modify_time;
	public String last_modify_uid;
	public String plan_content;
	public String plan_done_date;
	public String plan_id;
	public String remark;
	public String status;
	public String user_id;
	public String user_name;
	public String avatar;
	/**
	 * 用于判断ListView空间显示,api无此字段
	 */
	public boolean isShowKind;
	public boolean noMsg;
	public String kind;
	public String outDay;
}
