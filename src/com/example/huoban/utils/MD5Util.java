package com.example.huoban.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
	private final static String TAG="MD5Util";
	public static String MD5KEY = "qijiazhuangxiuhuobanwap";
	/**
	 * 默认的密码字符串组合，用来将字节转换�?16 进制表示的字�?apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			nsaex.printStackTrace();
		}
	}
	
	/**
	 * 生成字符串的md5校验�?
	 * 
	 * @param s
	 * @return
	 */
	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}
	
	public static String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}
	
	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}
	
	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}
	
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中�?4 位的数字转换, >>> 为�?辑右移，将符号位�?��右移,此处未发现两种符号有何不�?
		char c1 = hexDigits[bt & 0xf];// 取字节中�?4 位的数字转换 
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
}
