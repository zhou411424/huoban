package com.example.huoban.model.contacts;

import java.io.Serializable;

import com.example.huoban.model.Id;
import com.example.huoban.model.UserInfoCate;

public class Contact implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Id _id;

	public String avatar;
	public UserInfoCate cate_id;
	public String con_id;
	public String cover_url;
	public String[] family_id;
	public String from;
	public String group;
	public String imei;
	public String ip;
	public String is_family;
	public String jia_user_id;
	public String last_login_ip;
	public String last_login_time;
	public String mobile;
	public String nick;
	public String register_date;
	public String sex;
	public String state;
	public String user_id;
	public String user_name;
	public String is_friend;
	public String friend_type;
	public String friend_remark;

	public String relation_id;
	public String type;
	public String remark_name;
	public String status;
	public String isSearch; // 1 好友 2 手机搜索的好友 3 名字搜索的好友

}
