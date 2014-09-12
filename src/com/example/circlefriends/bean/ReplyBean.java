package com.example.circlefriends.bean;

import java.io.Serializable;

import com.example.huoban.data.DataClass;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 评论
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyBean extends DataClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4233078916693545830L;
	
	/**
	 * REPLYS TABLE_NAME.
	 */
	public static final String TABLE_NAME = "replys";
	/**
	 * REPLY_ID.
	 */
	public static final String LOC_REPLY_ID = "reply_id";
	/**
	 * TOPIC_ID.
	 */
	public static final String LOC_TOPIC_ID = "topic_id"; 
	/**
	 * P_REPLYER_ID.
	 */
	public static final String LOC_P_REPLYER_ID = "p_replyer_id";
	/**
	 * P_REPLYER_NAME.
	 */
	public static final String LOC_P_REPLYER_NAME = "p_replyer_name";
	/**
	 * REPLYER_ID.
	 */
	public static final String LOC_REPLYER_ID = "replyer_id";
	/**
	 * REPLYER_NAME.
	 */
	public static final String LOC_REPLYER_NAME = "replyer_name";
	/**
	 * CONTENT.
	 */
	public static final String LOC_CONTENT = "content";
	/**
	 * CREATE_DATE.
	 */
	public static final String LOC_CREATE_DATE = "create_date";
	/**
	 * DELETE_FLAG.
	 */
	public static final String LOC_DELETE_FLAG = "deleteflag";
	
	/**
	 * AREA_DELETE_FLAG.
	 */
	public static final String LOC_CURRENT_USER_ID = "current_user_id";//当前登录的用户的id
	
	private int topic_id;
	//回复人
	private int replyer_id;//回复人userid
	private String replyer_name;
	//被回复人（可能是发动态，或者是评论动态的人）
	private int p_replyer_id;//被回复人userid
	private String p_replyer_name;
	private String content;
	private int create_date;
	private int last_modify_time;
	private int status;
	private int	reply_id;//回复编号
	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
				+ LOC_REPLY_ID + " INTEGER NOT NULL PRIMARY KEY ," 
	            + LOC_TOPIC_ID + " INTEGER ," 
	            + LOC_P_REPLYER_ID + " INTEGER ," 
				+ LOC_P_REPLYER_NAME + " TEXT ," 
				+ LOC_REPLYER_ID + " INTEGER ,"  
	            + LOC_REPLYER_NAME + " TEXT ,"
				+ LOC_CONTENT + " TEXT,"  
				+ LOC_CREATE_DATE + " INTEGER," 
	            + LOC_DELETE_FLAG + " INTEGER ,"
	            + LOC_CURRENT_USER_ID + " INTEGER "+ ");";
	}
	public int getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	public int getReplyer_id() {
		return replyer_id;
	}
	public void setReplyer_id(int replyer_id) {
		this.replyer_id = replyer_id;
	}
	public String getReplyer_name() {
		return replyer_name;
	}
	public void setReplyer_name(String replyer_name) {
		this.replyer_name = replyer_name;
	}
	public int getP_replyer_id() {
		return p_replyer_id;
	}
	public void setP_replyer_id(int p_replyer_id) {
		this.p_replyer_id = p_replyer_id;
	}
	public String getP_replyer_name() {
		return p_replyer_name;
	}
	public void setP_replyer_name(String p_replyer_name) {
		this.p_replyer_name = p_replyer_name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCreate_date() {
		return create_date;
	}
	public void setCreate_date(int create_date) {
		this.create_date = create_date;
	}
	public int getLast_modify_time() {
		return last_modify_time;
	}
	public void setLast_modify_time(int last_modify_time) {
		this.last_modify_time = last_modify_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getReply_id() {
		return reply_id;
	}
	public void setReply_id(int reply_id) {
		this.reply_id = reply_id;
	}
	
}
