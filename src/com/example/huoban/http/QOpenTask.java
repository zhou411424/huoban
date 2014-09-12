package com.example.huoban.http;

import java.util.HashMap;

import android.content.Context;

import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.model.SaltResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;

/**
 * QOpen httpTask设置
 * @author cwchun.chen
 *
 */
public class QOpenTask extends Task{
	
	/**
	 * 参数 Map
	 */
	public HashMap<String, String> taskParam; 
	
	public QOpenTask(Context context){
		SaltResult salt =HuoBanApplication.getInstance().getSalt(context);
		authInfo = Utils.objectToJson(salt.auth_info);
		saltKey = salt.salt_key;
		taskParam = new HashMap<String, String>();
		taskParam.put("auth_info", authInfo);
		taskParam.put("sign_method", "MD5");	//签名方法
		taskParam.put("timestamp", Utils.getTimeStamp());
		super.taskParam = taskParam;
	}
	private String authInfo;	//服务请求方的认证信息
	private String saltKey;
	
	private String msgPlainText = "";
	/**
	 * 设置请求消息内容
	 * @param value
	 */
	public void setMsgPlainText(String value){
		if(value != null){
			msgPlainText = value;
		}
		taskParam.put("msg_plaintext", msgPlainText);
	}
	/**
	 * 签名,生成签名信息
	 */
	public void sign(){
		taskParam.put("sign_info", MD5Util.getMD5String(authInfo + msgPlainText + saltKey));
		LogUtil.logI("auth_info:"+authInfo +"\nmagPlaintext:"+ msgPlainText+"\nsalt_key"+saltKey);
	}
}
