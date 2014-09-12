package com.example.huoban.model;

import java.io.Serializable;
import java.util.ArrayList;


public class MyComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String diary_title;
	public int diary_id;
	public ArrayList<Comment> comments;
	public String cover_url;

}
