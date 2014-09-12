package com.example.huoban.http;

import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;






/**
 * httpTask设置
 *
 */
public class Task {
	
	/**
	 * id
	 */
	public int taskID; 
	/**
	 * 参数 Map或者 LinkedList或者MultipartEntity
	 */
	public Object taskParam; 
	/**
	 * 请求url地址 接口Url不含hostName
	 */
	public String taskQuery;
	/**
	 * 请求类型  GET POST
	 */
	public int taskHttpTpye;
	
	public BaseActivity target; 
	public BaseFragment fragment;  
	/**
	 * 请求结果
	 */
	public Object result;		
	@SuppressWarnings("rawtypes")
	/**
	 * 请求结果类型
	 */
	public Class resultDataClass;
	/**
	 * 请求完整地址
	 */
	public String taskUrl;
	/**
	 * 请求是否超时
	 */
	public boolean bTimeout;
	/**
	 * 请求时网络连接是否正常
	 */
	public boolean networkIsNotOk;
	/**
	 * 不显示toast
	 */
	public boolean noShowToast;
	/**
	 * true--解析失败  
	 */
	public boolean failed;
}
