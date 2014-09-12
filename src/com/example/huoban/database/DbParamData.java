package com.example.huoban.database;

import java.util.ArrayList;

import com.example.huoban.model.Question;

public class DbParamData {
	public int taskId;
	public Object object;

	public String userid;
	public String friendid;
	public int limit;
	public int offset;
	public boolean isLoadMore;

	public ArrayList<Question> questions;
}
