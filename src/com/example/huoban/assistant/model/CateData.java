package com.example.huoban.assistant.model;

import java.io.Serializable;
import java.util.List;

public class CateData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4983423522899700141L;
	public long add_time;
	public String author;
	public int bad_num;
	public int cate_garde;
	public int cate_id;
	public String cate_name;
	public String content;
	public int discuss_num;
	public int display_order;
	public int good_num;
	public String html_page;
	public int is_delete;
	public int last_cate_id;
	public int material_num;
	public String share_url;
	public String time;
	public long update_time;
	public List<MaterialInfo> material_info;
	public int is_set;
	public String time_age;
	
	public int cate_num;
	public List<CateData> cate_info;
	public List<MaterialInfo> next_material;
}
