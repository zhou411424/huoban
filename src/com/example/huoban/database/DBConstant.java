package com.example.huoban.database;

import android.database.sqlite.SQLiteDatabase;

public class DBConstant {
	public static final String DB_NAME = "com.huoban.local.db";
	public static final int DB_VERSION = 1;
	/**
	 * 数据库查询分页；
	 */
	public static final int SQL_LIMIT_PAGE_SIZE = 10;

	/**
	 * 装修界STAR
	 */
	/**
	 * 装修界表名
	 */
	public static final String TABLE_NAME_CIECLE_CIRCLE = "huoban_circle_clrcle";

	public static final String COL_TOPIC_ID = "topic_id";
	public static final String COL_USER_NAME = "user_name";
	public static final String COL_USER_ID = "user_id";
	public static final String COL_CONTENT = "content";
	public static final String COL_CREATE_DATE = "create_time";
	public static final String COL_LAST_MODIFY_TIME = "last_modify_time";
	public static final String COL_STATUS = "status";
	public static final String COL_AVATAR = "user_avatar";
	public static final String COL_REPLY_NUM = "reply_num";
	public static final String COL_ALUM_ID = "alum_id";
	public static final String COL_SYNC_ID = "sync_id";
	public static final String COL_SYNC_ORIGIN = "sync_origin";
	public static final String COL_WHICH = "data_which";
	/**
	 * 装修界回复表名
	 */
	public static final String TABLE_NAME_CIECLE_REPAL = "huoban_circle_reply";

	public static final String COL_REPLYER_ID = "replyer_id";
	public static final String COL_REPLYE_ID = "reply_id";
	public static final String COL_REPLYER_NAME = "replyer_name";
	public static final String COL_P_REPLYER_ID = "p_replyer_id";
	public static final String COL_P_REPLYER_NAME = "p_replyer_name";
	public static final String COL_ADD_TIME = "add_time";
	/**
	 * 装修界附件表名
	 */
	public static final String TABLE_NAME_CIECLE_ATTACHMENT = "huoban_circle_attachment";

	public static final String COL_POSTER_NAME = "poster_name";
	public static final String COL_POSTER_ID = "poster_id";
	public static final String COL_ATTACH_NAME = "attach_name";
	public static final String COL_ATTACH_SIZE = "attach_size";
	public static final String COL_ATTACH_WIDTH = "attach_width";
	public static final String COL_ATTACH_HEIGTH = "attach_height";
	public static final String COL_ATTACH_THUMB_URL = "attach_thumb_url";
	public static final String COL_ATTACH_URL = "attach_url";
	public static final String COL_ATTACH_ID = "attachment_id";

	/**
	 * 装修界赞表名
	 */
	public static final String TABLE_NAME_CIECLE_PAISE = "huoban_circle_prise";
	public static final String COL_TYPE = "type";
	public static final String COL_IS_DELETE = "is_delete";
	public static final String COL_UPDATE_TIME = "update_time";
	public static final String COL_ATTITUDE_ID = "attitude_id";

	/**
	 * 装修界END
	 */

	/**
	 * 记账STAR
	 */
	public static final String TABLE_NAME_PARTNER_BILL = "huoban_partner_bill";
	/**
	 * 记账END
	 */
	public static final String COL_BILL_ACCOUNT = "bill_amount";
	public static final String COL_BILL_CONTENT = "bill_content";
	public static final String COL_BILL_DATE = "bill_date";
	public static final String COL_BILL_ID = "bill_id";
	public static final String COL_BILL_REMARK = "bill_remark";
	public static final String COL_FAMILY_ID = "family_id";
	public static final String COL_LAST_MODIFY_NAME = "last_modify_name";
	public static final String COL_LAST_MODIFY_UID = "last_modify_uid";
	/**
	 * 计划STAR
	 */
	public static final String TABLE_NAME_PARTNER_PLAN = "huoban_partner_plan";

	public static final String COL_DIR_ID = "dir_id";
	public static final String COL_DONE_DATE = "done_date";
	public static final String COL_PLAN_ID = "plan_id";
	public static final String COL_DONE_USER_ID = "done_user_id";
	public static final String COL_PLAN_CONTENT = "plan_content";
	public static final String COL_PLAN_DONE_DATE = "plan_done_date";
	public static final String COL_REMARK = "remark";
	/**
	 * 计划END
	 */
	/**
	 * 聊天记录
	 */
	public static final String TABLE_NAME_MESSAGE = "huoban_chat_message";
	public static final String COL_NAME_MESSAGE_USER_ID = "message_user_id";
	public static final String COL_NAME_MESSAGE_STATUS = "message_status";
	public static final String COL_NAME_MESSAGE_FROMUSERID = "message_fromUserId";
	public static final String COL_NAME_MESSAGE_TOUSERID = "message_toUserId";
	public static final String COL_NAME_MESSAGE_MESSAGESTR = "message_messageStr";
	public static final String COL_NAME_MESSAGE_TYPE = "message_type";
	public static final String COL_NAME_MESSAGE_TIMESTEMP = "message_timestemp";

	/**
	 * 推送
	 */

	public static final String TABLE_NAME_BAIDU_PUSH = "huoban_baidu_push";
	public static final String COL_FIRST_IMAGE = "first_img";
	public static final String COL_DISPLAY_ORDER = "display_order";
	public static final String COL_MESSAGE_ID = "message_id";
	public static final String COL_TITLE = "title";
	public static final String COL_MD = "md";
	public static final String COL_XQ = "xq";
	public static final String COL_INDEX = "indexnum";

	/**
	 * 推送
	 */
	/*************问题列表**************/
	public static final String TABLE_NAME_QUESTION_LIST = "table_name_question_list";

	public static final String COL_APP_ID = "app_id";
	public static final String COL_CREATE_TIME = "create_time";
	public static final String COL_REPLY_STATUS = "reply_status";
	public static final String COL_VIEW_NUM = "view_num";

	/**
	 * 问题详情
	 */
	public static final String TABLE_NAME_QUESTION_DETAIL = "table_name_question_detail_a";
	
	
	public static final String COL_ANSWER_DATE = "answer_date";
	public static final String COL_ANSWER = "answer";
	public static final String COL_QUESTION = "question";
	public static final String COL_QUESTION_ID = "question_id";
	public static final String COL_READ_ANSWER = "read_answer";
	/**
	 * 问题回复
	 */
	public static final String TABLE_NAME_QUESTION_REPLY = "table_name_question_reply";
	public static final String COL_REPLY_TYPE = "reply_type";
	public static final String COL_IS_LIKE = "is_like";
	public static final String COL_LIKE = "like";
	public static final String COL_REPLY_NAME = "replyer_name";
	public static final String COL_REPLY_TIME = "reply_time";
	/**
	 * 问题详情图片附件表名
	 */
	public static final String TABLE_NAME_ATTACHMENT = "table_name_qs_attachment_a";
	public static final String COL_ATTACHMENT_URL = "pic_urls";
	/**
	 * 已读问题id表
	 */
	public static final String TABLE_NAME_READ_IDS = "table_name_qs_read_ids";
	/**
	 * 问题详情
	 */
	
	
	
	
	/**
	 * 创建表
	 */
	public static void createTables(SQLiteDatabase db){
		String sql = null;
		
		/**
		 * 创建已读问题ids表
		 */
		sql = "create table if not exists "
				+ TABLE_NAME_READ_IDS
				+ "(" 
				+ COL_QUESTION_ID +" varchar PRIMARY KEY,"
				+ "uid varchar"
				+ ")";
		
		db.execSQL(sql);
		
		/**
		 * 创建问题详情表
		 */
		sql = "create table if not exists "
				+ TABLE_NAME_QUESTION_DETAIL
				+ "(" 
				+ COL_TOPIC_ID +" varchar,"
				+ COL_APP_ID +" varchar,"
				+ COL_TITLE +" varchar,"
				+ COL_VIEW_NUM +" varchar,"
				+ COL_CREATE_TIME +" varchar,"
				+ COL_POSTER_NAME +" varchar,"
				+ COL_REPLY_NUM +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建问题回复表
		 */
		sql = "create table if not exists "
				+ TABLE_NAME_QUESTION_REPLY
				+ "(" 
				+ COL_REPLYE_ID +" varchar,"
				+ COL_TOPIC_ID +" varchar,"
				+ COL_REPLY_TYPE +" varchar,"
				+ COL_IS_LIKE +" varchar,"
				+ COL_LIKE +" varchar,"
				+ COL_REPLY_NAME +" varchar,"
				+ COL_REPLY_TIME +" varchar,"
				+ COL_CONTENT +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建问题附件表
		 */
		sql = "create table if not exists "
				+ TABLE_NAME_ATTACHMENT
				+ "(" 
				+ COL_TOPIC_ID +" varchar,"
				+ COL_ATTACHMENT_URL +" varchar"
				+ ")";
		
		db.execSQL(sql);
		
		/**
		 * 创建推送
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_BAIDU_PUSH
				+ "(" 
				+  DBConstant.COL_FIRST_IMAGE +" varchar,"
				+  DBConstant.COL_DISPLAY_ORDER +" varchar,"
				+  DBConstant.COL_MESSAGE_ID +" varchar,"
				+  DBConstant.COL_TITLE +" varchar,"
				+  DBConstant.COL_MD +" varchar,"
				+  DBConstant.COL_INDEX +" varchar,"
				+  DBConstant.COL_XQ +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建计划表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_PARTNER_PLAN
				+ "(" 
				+ DBConstant.COL_PLAN_ID +" varchar PRIMARY KEY,"
				+  DBConstant.COL_USER_NAME +" varchar,"
				+  DBConstant.COL_USER_ID +" varchar,"
				+  DBConstant.COL_DIR_ID +" varchar,"
				+  DBConstant.COL_DONE_DATE +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar,"
				+  DBConstant.COL_DONE_USER_ID +" varchar,"
				+  DBConstant.COL_PLAN_CONTENT +" varchar,"
				+  DBConstant.COL_PLAN_DONE_DATE +" varchar,"
				+  DBConstant.COL_REMARK +" varchar,"
				+  DBConstant.COL_FAMILY_ID +" varchar,"
				+  DBConstant.COL_STATUS +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_NAME +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_TIME +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_UID +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建记账表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_PARTNER_BILL
				+ "(" 
				+ DBConstant.COL_BILL_ID +" varchar PRIMARY KEY,"
				+  DBConstant.COL_USER_NAME +" varchar,"
				+  DBConstant.COL_USER_ID +" varchar,"
				+  DBConstant.COL_BILL_ACCOUNT +" varchar,"
				+  DBConstant.COL_BILL_CONTENT +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar,"
				+  DBConstant.COL_BILL_DATE +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_TIME +" varchar,"
				+  DBConstant.COL_BILL_REMARK +" varchar,"
				+  DBConstant.COL_FAMILY_ID +" varchar,"
				+  DBConstant.COL_STATUS +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_NAME +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_UID +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建装修界动态表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_CIECLE_CIRCLE
				+ "(" 
				+ DBConstant.COL_TOPIC_ID +" varchar,"
				+  DBConstant.COL_USER_NAME +" varchar,"
				+  DBConstant.COL_USER_ID +" varchar,"
				+  DBConstant.COL_CONTENT +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar,"
				+  DBConstant.COL_REPLY_NUM +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_TIME +" varchar,"
				+  DBConstant.COL_AVATAR +" varchar,"
				+  DBConstant.COL_SYNC_ID +" varchar,"
				+  DBConstant.COL_ALUM_ID +" varchar,"
				+  DBConstant.COL_WHICH +" varchar,"
				+  DBConstant.COL_SYNC_ORIGIN +" varchar,"
				+  DBConstant.COL_STATUS +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建装修界回复表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_CIECLE_REPAL
				+ "(" 
				+ DBConstant.COL_REPLYE_ID +" varchar,"
				+  DBConstant.COL_TOPIC_ID +" varchar,"
				+  DBConstant.COL_REPLYER_ID +" varchar,"
				+  DBConstant.COL_REPLYER_NAME +" varchar,"
				+  DBConstant.COL_CONTENT +" varchar,"
				+  DBConstant.COL_LAST_MODIFY_TIME +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar,"
				+  DBConstant.COL_WHICH +" varchar,"
				+  DBConstant.COL_ADD_TIME +" varchar,"
				+  DBConstant.COL_P_REPLYER_ID +" varchar,"
				+  DBConstant.COL_P_REPLYER_NAME +" varchar,"
				+  DBConstant.COL_STATUS +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建装修界附件表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_CIECLE_ATTACHMENT
				+ "(" 
				+ DBConstant.COL_ATTACH_ID +" varchar,"
				+  DBConstant.COL_TOPIC_ID +" varchar,"
				+  DBConstant.COL_CREATE_DATE +" varchar,"
				+  DBConstant.COL_POSTER_ID +" varchar,"
				+  DBConstant.COL_WHICH +" varchar,"
				+  DBConstant.COL_POSTER_NAME +" varchar,"
				+  DBConstant.COL_ATTACH_SIZE +" varchar,"
				+  DBConstant.COL_ATTACH_WIDTH +" varchar,"
				+  DBConstant.COL_ATTACH_HEIGTH +" varchar,"
				+  DBConstant.COL_ALUM_ID +" varchar,"
				+  DBConstant.COL_ATTACH_URL +" varchar,"
				+  DBConstant.COL_ATTACH_NAME +" varchar,"
				+  DBConstant.COL_ATTACH_THUMB_URL +" varchar,"
				+  DBConstant.COL_STATUS +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建装修界点赞表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_CIECLE_PAISE
				+ "(" 
				+ DBConstant.COL_ATTITUDE_ID +" varchar,"
				+  DBConstant.COL_TOPIC_ID +" varchar,"
				+  DBConstant.COL_TYPE +" varchar,"
				+  DBConstant.COL_USER_ID +" varchar,"
				+  DBConstant.COL_USER_NAME +" varchar,"
				+  DBConstant.COL_IS_DELETE +" varchar,"
				+  DBConstant.COL_WHICH +" varchar,"
				+  DBConstant.COL_ADD_TIME +" varchar,"
				+  DBConstant.COL_UPDATE_TIME +" varchar"
				+ ")";
		
		db.execSQL(sql);
		
		/**
		 * 创建聊天信息表
		 */
		sql = "create table if not exists "
				+ DBConstant.TABLE_NAME_MESSAGE
				+ "(" 
				+  DBConstant.COL_NAME_MESSAGE_USER_ID +" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_FROMUSERID+" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_MESSAGESTR +" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_STATUS +" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_TIMESTEMP +" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_TOUSERID +" varchar,"
				+  DBConstant.COL_NAME_MESSAGE_TYPE +" varchar"
				+ ")";
		
		db.execSQL(sql);
		/**
		 * 创建问题列表
		 */
		sql = "create table if not exists "
		        + TABLE_NAME_QUESTION_LIST 
		        + "("
		        
		        + COL_TOPIC_ID + " varchar PRIMARY KEY,"
		        + COL_APP_ID + " varchar,"
				+ COL_TITLE + " varchar,"
		        + COL_POSTER_NAME + " varchar,"
				+ COL_CREATE_TIME + " varchar,"
		        + COL_REPLY_STATUS+ " varchar,"
				+ COL_TYPE + " varchar,"
		        + COL_VIEW_NUM	+ " varchar"
				
		        + ")";
		
		db.execSQL(sql);
	}
	
	
	
}
