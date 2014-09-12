package com.baidu.push;

import java.io.Serializable;

import cn.sharesdk.onekeyshare.PushMessage;

public class PushModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String business_type;
	public PushMessage business_param;
}
