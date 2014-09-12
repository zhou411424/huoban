package com.example.huoban.model;

import java.io.Serializable;
import java.util.List;

public class DiaryData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4707969055591177897L;
	public int diary_id;
	public String diary_title;
	public String poster_avatar;
	public String poster_name;
	public Summary summary;
	public List<Comment> comment_list;
	public String areaflag;
	public int visited;
	public int focus_num;
}
