package com.example.huoban.assistant.model;

public class Content {
	
	private int contentId;

	private String userName;

	private String content;

	private String dateTime;
	
	private String discussTime;
	
	private int discussUserId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getDiscussTime() {
		return discussTime;
	}

	public void setDiscussTime(String discussTime) {
		this.discussTime = discussTime;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public int getDiscussUserId() {
		return discussUserId;
	}

	public void setDiscussUserId(int discussUserId) {
		this.discussUserId = discussUserId;
	}
	

}
