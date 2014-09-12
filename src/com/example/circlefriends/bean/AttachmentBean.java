package com.example.circlefriends.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.example.huoban.data.DataClass;

/**
 * 朋友圈 动态图片对象
 * 
 * @author zhuoyong
 * @data 创建时间：2014-4-3  下午1:18:57
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentBean extends DataClass implements Serializable {
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1892454002185132421L;
	/**
	 * ATTACHMENT TABLE_NAME.
	 */
	public static final String TABLE_NAME = "attachments";
	/**
	 * ATTACHMENT_ID.
	 */
	public static final String LOC_ATTACHMENT_ID = "attachment_id";
	/**
	 * ATTACH_NAME.
	 */
	public static final String LOC_ATTACH_NAME = "attach_name";
	
	/**
	 * ATTACH_URL.
	 */
	public static final String LOC_ATTACH_URL = "attach_url";
	/**
	 * ATTACH_THUMB_URL.
	 */
	public static final String LOC_ATTACH_THUMB_URL = "attach_thumb_url";
	/**
	 * POSTER_ID.
	 */
	public static final String LOC_POSTER_ID = "poster_id";
	/**
	 * POSTER_NAME.
	 */
	public static final String LOC_POSTER_NAME = "poster_name";
	/**
	 * CREATE_DATE.
	 */
	public static final String LOC_CREATE_DATE = "create_date";
	/**
	 * DELETE_FLAG.
	 */
	public static final String LOC_DELETE_FLAG = "deleteflag";
	/**
	 * topic_id
	 */
	public static final String LOC_TOPIC_ID = "topic_id";
	
	/**
	 * AREA_DELETE_FLAG.
	 */
	public static final String LOC_CURRENT_USER_ID = "current_user_id";//当前登录的用户的id
	
	private String poster_name;// 发送者的名字
	private int poster_id;//发送者的id
	private String attach_name;//图片名字
	private String attach_url;//原图
	private String attach_thumb_url;// 缩略图
	private long create_date;//创建的时间
	private int attachment_id;//图片的id
	private int topic_id;//动态id
	private int status;
	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
	            + LOC_ATTACHMENT_ID + " INTEGER NOT NULL PRIMARY KEY ," 
				+ LOC_ATTACH_NAME + " TEXT ," 
				+ LOC_ATTACH_URL + " TEXT ," 
				+ LOC_ATTACH_THUMB_URL + " TEXT ," 
				+ LOC_POSTER_ID + " INTEGER ," 
				+ LOC_POSTER_NAME + " TEXT ,"
				+ LOC_CREATE_DATE + " INTEGER ," 
	            + LOC_DELETE_FLAG + " INTEGER ," 
	            + LOC_TOPIC_ID + " INTEGER ," 
	            + LOC_CURRENT_USER_ID + " INTEGER "+ ");";
	}
	public String getPoster_name() {
		return poster_name;
	}
	public void setPoster_name(String poster_name) {
		this.poster_name = poster_name;
	}
	public int getPoster_id() {
		return poster_id;
	}
	public void setPoster_id(int poster_id) {
		this.poster_id = poster_id;
	}
	
	public String getAttach_name() {
		return attach_name;
	}
	public void setAttach_name(String attach_name) {
		this.attach_name = attach_name;
	}
	public String getAttach_url() {
		return attach_url;
	}
	public void setAttach_url(String attach_url) {
		this.attach_url = attach_url;
	}
	public String getAttach_thumb_url() {
		return attach_thumb_url;
	}
	public void setAttach_thumb_url(String attach_thumb_url) {
		this.attach_thumb_url = attach_thumb_url;
	}
	public long getCreate_date() {
		return create_date;
	}
	public void setCreate_date(long create_date) {
		this.create_date = create_date;
	}
	public int getAttachment_id() {
		return attachment_id;
	}
	public void setAttachment_id(int attachment_id) {
		this.attachment_id = attachment_id;
	}
	public int getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
