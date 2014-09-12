package com.example.huoban.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Diary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String blockurl;
	public ArrayList<String> comments;
	public int floor;
	public int good_num;
	public String reply_content;
	public String reply_pid;
	public String reply_time;
	public String reply_avatar;
	public String reply_name;
	public int type;

}
