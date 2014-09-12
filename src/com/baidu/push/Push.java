package com.baidu.push;

import java.io.Serializable;
import java.util.ArrayList;

public class Push implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String author;
	public String update_time;
	public String status;
	public String subject;
	public String create_time;
	public String message_id;
	public String user_id;

	public ArrayList<PushArticle> articles;
}
