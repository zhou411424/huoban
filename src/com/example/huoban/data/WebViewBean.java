package com.example.huoban.data;

import java.io.Serializable;


/**
 *  监理服务全文 webView
 * 
 */
public class WebViewBean extends DataClass implements Serializable {
	private static final long serialVersionUID = 1L;

   private int aip_type;
   private int aip_id;
   private String title;
   private String content;
   
   
   
   public static final String TABLE_NAME = "webview_contrnt";
   
   public static final String AIP_TYPE = "aip_type";
   
   public static final String AIP_ID = "aip_id";

   public static final String TITLE = "title";

   public static final String CONTENT = "content";
   
   
     @Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
				+ AIP_ID + " INTEGER NOT NULL PRIMARY KEY ," 
	            + AIP_TYPE + " INTEGER ," 
				+ TITLE + " TEXT ," 
				+ CONTENT + " TEXT "+ ");"; 
				
	}


	public int getAip_type() {
		return aip_type;
	}


	public void setAip_type(int aip_type) {
		this.aip_type = aip_type;
	}


	public int getAip_id() {
		return aip_id;
	}


	public void setAip_id(int aip_id) {
		this.aip_id = aip_id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}
     
}
