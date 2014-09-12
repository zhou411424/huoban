package com.baidu.push;

import java.util.ArrayList;

import com.example.huoban.model.BaseResult;

public class PushListResult extends BaseResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrayList<Push> data;
	public int total_pages; 
}
