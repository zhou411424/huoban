package com.example.huoban.utils;

public class MessageUtil {
	public final static String MEMBER_LIST = "20003";

	public final static String MEMBER_LOGIN = "20001";

	public final static String MEMBER_CONTENT = "20002";

	public final static String sendLogin(String userid, String userName) {

		return "{\"action\":\"" + MEMBER_LOGIN + "\",\"data\":[\"" + userid + "\",\"" + userid + "\"]}";

	}

	public final static String sendMessageToContact(String cid, String msg) {

		return "{\"action\":\"" + MEMBER_CONTENT + "\",\"data\":[\"" + cid + "\",\"" + msg + "\"]}";

	}

}
