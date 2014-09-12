package com.example.huoban.model;

import java.util.List;

public class QuestionDetailData 
{
	public String answer_date;
	public String answer;
	public String create_date;
	public String question;
	public String question_id;
	public String read_answer;
	public String status;
	public String type;
	public String user_id;
	public String user_name;
	public String view_num;
	public String favour_num;
	public String is_favour;
	public String user_avatar;
	public String expert_id;
	public List<AttachmentData>attachment;
	public Expert expert;
	
	public static QuestionDetailData creatNewOne(QuestionDetailData data)
	{
		QuestionDetailData data2 = new QuestionDetailData();
		data2.answer_date = data.answer_date;
		data2.answer = data.answer;
		data2.create_date = data.create_date;
		data2.question = data.question;
		data2.question_id = data.question_id;
		data2.read_answer = data.read_answer;
		data2.status = data.status;
		data2.type = data.type;
		data2.user_id = data.user_id;
		data2.user_name = data.user_name;
		data2.view_num = data.view_num;
		data2.favour_num = data.favour_num;
		data2.is_favour = data.is_favour;
		data2.user_avatar = data.user_avatar;
		data2.expert_id = data.expert_id;
		data2.attachment = data.attachment;
		data2.expert = data.expert;
		return data2;
	}
	
}
