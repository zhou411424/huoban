package com.example.huoban.model.userinfo;

import com.example.huoban.model.BaseQOResult;

public class SyncQOResult extends BaseQOResult {
	public String encrypt_method;
	public MsgEncrypted msg_encrypted;

	public class MsgEncrypted {
		public String costTime;
		public String msg;
		public String respcode;
		public String statusCode;
	}
}
