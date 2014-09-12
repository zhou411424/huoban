package com.example.circlefriends.bean;

import java.io.Serializable;

import com.example.huoban.data.DataClass;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 赞实体类
 * 
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class PraiseBean extends DataClass implements Serializable {
	private static final long serialVersionUID = 1892454002185132421L;
	/**
	 * PRAISE TABLE_NAME.
	 */
	public static final String TABLE_NAME = "praise";
	/**
	 * PRAISE_ID.
	 */
	public static final String LOC_PRAISE_ID = "attitude_id";
	/**
	 * TOPIC_ID.
	 */
	public static final String LOC_TOPIC_ID = "topic_id";  
	/**
	 * LOC_USER_NAME
	 */
	public static final String LOC_USER_NAME = "user_name";
	/**
	 * USER_ID
	 */
	public static final String LOC_USER_ID = "user_id";
	/**
	 * UPDATE_TIME
	 */
	public static final String LOC_UPDATE_TIME = "update_time";
	/**
	 * DELETE_FLAG
	 */
	public static final String LOC_DELETE_FLAG = "is_delete";
	/**
	 * ADD_TIME
	 */
	public static final String LOC_ADD_TIME = "add_time";
	
	/**
	 * AREA_DELETE_FLAG.
	 */
	public static final String LOC_CURRENT_USER_ID = "current_user_id";//当前登录的用户的id
	
	private String user_name;
	private int update_time;
	private int user_id;
	private int is_delete;
	private int add_time;
	private int topic_id;
	private int type;
	private int attitude_id;
	
	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
	            + LOC_PRAISE_ID + " INTEGER NOT NULL PRIMARY KEY ," 
	            + LOC_TOPIC_ID + " INTEGER ," 
				+ LOC_USER_NAME + " TEXT ," 
				+ LOC_USER_ID + " INTEGER ," 
				+ LOC_UPDATE_TIME + " TEXT ," 
	            + LOC_DELETE_FLAG + " INTEGER , " 
	            + LOC_ADD_TIME + " INTEGER ," 
	            + LOC_CURRENT_USER_ID + " INTEGER "+ ");";
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(int update_time) {
		this.update_time = update_time;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(int is_delete) {
		this.is_delete = is_delete;
	}
	public int getAdd_time() {
		return add_time;
	}
	public void setAdd_time(int add_time) {
		this.add_time = add_time;
	}
	public int getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAttitude_id() {
		return attitude_id;
	}
	public void setAttitude_id(int attitude_id) {
		this.attitude_id = attitude_id;
	}
	
	
	/**
	 * hashCode.
	 * 
	 * @return 0
	 */
	public int hashCode() {
		if (user_name == null) {
			return 0;
		}
		return user_name.hashCode();
	}
	/**
	 * equals.
	 * 用来比较两个对象是否有相同的参数  目前只能比较String 类型的
	 * @return false
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else {
			if (this.getClass() == obj.getClass()) {
				PraiseBean comment = (PraiseBean) obj;
				if (this.user_name.equals(comment.user_name)) { 
					
					return true;
				} else {
					return false;
				}
			} else { 
				return false;
			}
		}
	}
}
