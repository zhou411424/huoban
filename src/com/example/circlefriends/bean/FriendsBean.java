package com.example.circlefriends.bean;

import java.io.Serializable;

/**
 * 查询好友表 存放数据bean
 * 
 */
public class FriendsBean implements Serializable {
	
	private static final long serialVersionUID = 1892454002185132421L;
	
	private int friends_id;// 好友的id
	private String remark_name;//好友的备注名字
	private String nick_name;// 好友昵称
	private String friends_name;//好友的名字
   
    
    private String friendUrl;//好友朋友圈背景图	
    
	public int getFriends_id() {
		return friends_id;
	}
	public void setFriends_id(int friends_id) {
		this.friends_id = friends_id;
	}
	public String getFriends_name() {
		return friends_name;
	}
	public void setFriends_name(String friends_name) {
		this.friends_name = friends_name;
	}
	public String getRemark_name() {
		return remark_name;
	}
	public void setRemark_name(String remark_name) {
		this.remark_name = remark_name;
	}
	public String getFriendUrl() {
		return friendUrl;
	}
	public void setFriendUrl(String friendUrl) {
		this.friendUrl = friendUrl;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	
	
}
