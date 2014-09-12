package com.example.huoban.assistant.model;

import java.util.ArrayList;

public class Discuss {
	private int userId;
	
	private int discussId;

	private int discussNum;

	private int goodNum;

	private int badNum;

	private String userName;

	private String content;
	
	private String badContent;

	private String addTime;
	
	private String discussTime;

	private ArrayList<Content> contentLists;
	
    private String userUrl;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDiscussId() {
		return discussId;
	}

	public void setDiscussId(int discussId) {
		this.discussId = discussId;
	}

	public int getDiscussNum() {
		return discussNum;
	}

	public void setDiscussNum(int discussNum) {
		this.discussNum = discussNum;
	}

	public int getGoodNum() {
		return goodNum;
	}

	public void setGoodNum(int goodNum) {
		this.goodNum = goodNum;
	}

	public int getBadNum() {
		return badNum;
	}

	public void setBadNum(int badNum) {
		this.badNum = badNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ArrayList<Content> getContentLists() {
		return contentLists;
	}

	public void setContentLists(ArrayList<Content> contentLists) {
		this.contentLists = contentLists;
	}

	public String getBadContent() {
		return badContent;
	}

	public void setBadContent(String badContent) {
		this.badContent = badContent;
	}

	public String getDiscussTime() {
		return discussTime;
	}

	public void setDiscussTime(String discussTime) {
		this.discussTime = discussTime;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}
	
	

}
