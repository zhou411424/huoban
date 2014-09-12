package com.example.huoban.model;
import java.io.Serializable;

/**
 * 数据结果基类
 * @author 
 *
 */
public class BaseResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String msg;
}
