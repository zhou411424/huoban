package cn.sharesdk.onekeyshare;

import java.io.Serializable;

import com.baidu.push.Push;

public class PushMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String message_id;
	public Push message;
}
