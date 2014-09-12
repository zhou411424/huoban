package com.example.huoban.model;

import java.io.Serializable;
import java.util.List;

public class QuestionNoUpdateResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String content;
	public List<String>imageURLs;
	public List<String>compressURLs;
	public boolean isUpdateToCircle;

}
