package com.example.huoban.activity.my.contacts.chat;

import java.io.Serializable;

public class MessageModel implements Serializable {
	/**
	 * 已读状态 暂时不用
	 */
	public String status;
	/**
	 * 发送者的userId
	 */
	public String fromUserId;
	/**
	 * 接收者的userId
	 */
	public String toUserId;
	/**
	 * 文本信息
	 */
	public String messageStr;
	/**
	 * 消息类型 0当前用户发送 1当前用户接收
	 */
	public String type;
	/**
	 * 时间戳
	 */
	public String timetemp;
	
	public String fromUserName;
	
	public String fromUserAvatar;
	
	public int unReadCount = 1;

	public String toString() {
		return "["+this.fromUserId + "," + this.messageStr+"," + this.status +","+ this.timetemp+"," + this.toUserId+"," + this.type+"]";

	}
}
