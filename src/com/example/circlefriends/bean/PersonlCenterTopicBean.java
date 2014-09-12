package com.example.circlefriends.bean;

import java.io.Serializable;

/**
 * 朋友圈个人中心实体类
 * 
 */
public class PersonlCenterTopicBean extends TopicBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6117719353386596258L;
	/**
	 * TOPICS TABLE_NAME.
	 */
	public static final String TABLE_NAME = "personal_topics";
	
	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
				+ LOC_TOPIC_ID + " INTEGER NOT NULL PRIMARY KEY ," 
	            + LOC_USER_ID + " INTEGER ," 
				+ LOC_USER_NAME + " TEXT ," 
				+ LOC_USER_AVATAR + " TEXT ," 
	            + LOC_CONTENT + " TEXT ,"
				+ LOC_CREATE_DATE + " INTEGER ," 
	            + LOC_DELETE_FLAG + " INTEGER ," 
				+ LOC_CURRENT_USER_ID + " INTEGER "+ ");";
	}
	
}
