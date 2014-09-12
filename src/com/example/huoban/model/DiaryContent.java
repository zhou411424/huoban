package com.example.huoban.model;

import java.io.Serializable;

public class DiaryContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4221502365517722037L;
	public String  replyer_avatar;
	public String  replyer_name;
	public String  reply_content;
	public String  reply_pid;
	public String  reply_time;
	public String  description;
	public int good_num;
	public int comment_count;
	public int like;
	public int type;  //1为文字块，2为图片块
	public int color;
	public String date; //日期
	public int category;  //日期分类
}
