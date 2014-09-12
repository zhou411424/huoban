package com.example.huoban.model;

import java.io.Serializable;

public class Comment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String replyer_avatar;
	public String replyer_name;
	public String reply_content;
	public int type;
	public String reply_pid;
	public int floor;
	public String reply_time;
	public int user_id;
}
