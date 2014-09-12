package com.example.huoban.assistant.model;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class MaterialInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8983433797555618269L;
	public JsonObject _id;
	public long add_time;
	public String author;
	public int cate_garde;
	public int cate_id;
	public String content;
	public int display_order;
	public String html_page;
	public int is_delete;
	public String lead;
	public int material_id;
	public String material_name;
	public String share_url;
	public long update_time;
	
}
