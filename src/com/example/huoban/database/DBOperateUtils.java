package com.example.huoban.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baidu.push.PushArticle;
import com.example.huoban.BuildConfig;
import com.example.huoban.activity.my.contacts.chat.MessageModel;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.model.Bill;
import com.example.huoban.model.Face;
import com.example.huoban.model.FaceResult;
import com.example.huoban.model.Plan;
import com.example.huoban.model.Question;
import com.example.huoban.model.QuestionDetailInfo;
import com.example.huoban.model.QuestionDetailReply;
import com.example.huoban.model.QuestionDetailResultData;
import com.example.huoban.model.QuestionInfoDetail;
import com.example.huoban.model.Reply;
import com.example.huoban.model.ReplyResult;
import com.example.huoban.model.Topic;
import com.example.huoban.model.TopticAttachment;
import com.example.huoban.utils.LogUtil;

public class DBOperateUtils {
	/**
	 * 存储装修界列表
	 */

	public static ArrayList<Topic> readCircleListFromDb(Context context, String which) {

		ArrayList<Topic> topicList = new ArrayList<Topic>();
		if (context == null) {
			return topicList;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_CIRCLE);
			Cursor cursor = dbManager.query(null, DBConstant.COL_WHICH + "='" + which + "'", null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Topic topic = new Topic();
					topicList.add(topic);
					topic.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
					topic.user_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_NAME));
					topic.user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_ID));
					topic.user_avatar = cursor.getString(cursor.getColumnIndex(DBConstant.COL_AVATAR));
					topic.content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CONTENT));
					topic.create_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
					topic.reply_num = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_NUM));
					topic.status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_STATUS));
					topic.sync_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_SYNC_ID));
					topic.sync_origin = cursor.getString(cursor.getColumnIndex(DBConstant.COL_SYNC_ORIGIN));
					topic.last_modify_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_TIME));
				}
				cursor.close();
				cursor = null;
			}

			if (topicList.size() > 0) {
				for (Topic topic : topicList) {
					/**
					 * 读附件
					 */
					dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_ATTACHMENT);
					cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null, null, null);
					if (cursor != null && cursor.getCount() > 0) {
						ArrayList<TopticAttachment> attachmentList = new ArrayList<TopticAttachment>();
						topic.attachment = attachmentList;
						while (cursor.moveToNext()) {
							TopticAttachment attachment = new TopticAttachment();
							attachment.attachment_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_ID));
							attachment.attach_height = cursor.getInt(cursor.getColumnIndex(DBConstant.COL_ATTACH_HEIGTH));
							attachment.attach_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_NAME));
							attachment.attach_thumb_url = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_THUMB_URL));
							attachment.attach_url = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_URL));
							attachment.attach_width = cursor.getInt(cursor.getColumnIndex(DBConstant.COL_ATTACH_WIDTH));
							attachment.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
							attachmentList.add(attachment);
						}
						cursor.close();
						cursor = null;
					}
					/**
					 * 读回复
					 */

					dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_REPAL);
					cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null, null, null);
					if (cursor != null && cursor.getCount() > 0) {
						ArrayList<Reply> replyList = new ArrayList<Reply>();
						topic.reply_list = replyList;
						while (cursor.moveToNext()) {

							Reply reply = new Reply();
							reply.reply_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLYE_ID));
							reply.replyer_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLYER_ID));
							reply.replyer_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLYER_NAME));
							reply.create_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
							reply.last_modify_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_TIME));
							reply.p_replyer_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_P_REPLYER_ID));
							reply.p_replyer_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_P_REPLYER_NAME));
							reply.add_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ADD_TIME));
							reply.content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CONTENT));
							reply.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
							replyList.add(reply);
						}
						cursor.close();
						cursor = null;
					}
					/**
					 * 读赞
					 */
					dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_PAISE);
					cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null, DBConstant.COL_UPDATE_TIME, null);
					if (cursor != null && cursor.getCount() > 0) {
						ArrayList<Face> faceList = new ArrayList<Face>();
						topic.face_list = faceList;
						while (cursor.moveToNext()) {
							Face face = new Face();
							face.attitude_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTITUDE_ID));
							face.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
							face.type = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TYPE));
							face.is_delete = cursor.getString(cursor.getColumnIndex(DBConstant.COL_IS_DELETE));
							face.user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_ID));
							face.user_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_NAME));
							face.add_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ADD_TIME));
							face.update_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_UPDATE_TIME));
							faceList.add(face);
						}
						cursor.close();
						cursor = null;
					}
				}
			}

			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
		return topicList;
	}

	public static void saveCircleListToDb(Context context, List<Topic> topicList, String which) {
		if (context == null && topicList == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_CIRCLE);
			dbManager.delete(DBConstant.COL_WHICH + "='" + which + "'", null);
			/**
			 * 存列表
			 */
			for (Topic topic : topicList) {
				ContentValues contentValues = setTopicContentValues(topic, which);
				dbManager.insert(contentValues);
			}

			/**
			 * 存附件
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_ATTACHMENT);
			for (Topic topic : topicList) {
				dbManager.delete(DBConstant.COL_TOPIC_ID + " = '" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null);
				if (topic.attachment != null && topic.attachment.size() > 0) {
					for (TopticAttachment attachment : topic.attachment) {
						ContentValues contentValues = setAttachmentContentValues(attachment, which);
						dbManager.insert(contentValues);
					}
				}
			}
			/**
			 * 存回复
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_REPAL);
			for (Topic topic : topicList) {
				dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null);
				if (topic.reply_list != null && topic.reply_list.size() > 0) {
					for (Reply reply : topic.reply_list) {
						ContentValues contentValues = setReplyContentValues(reply, which);
						dbManager.insert(contentValues);
					}
				}
			}
			/**
			 * 存赞
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_PAISE);
			for (Topic topic : topicList) {
				dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null);
				if (topic.face_list != null && topic.face_list.size() > 0) {
					for (Face face : topic.face_list) {
						ContentValues contentValues = setFaceContentValues(face, which);
						dbManager.insert(contentValues);
					}
				}
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	/**
	 * 删除回复 装修界
	 * 
	 * @param context
	 * @param topic_id
	 */
	public static void delReplyForCircle(Context context, String topic_id) {
		if (context == null || topic_id == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_REPAL);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	/**
	 * 删除动态 装修界
	 * 
	 * @param context
	 * @param topic_id
	 */
	public static void delDynamicForCircle(Context context, String topic_id, String which) {
		if (context == null || topic_id == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_CIRCLE);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null);
			/**
			 * 删除相应的附件
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_ATTACHMENT);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "' and " + DBConstant.COL_WHICH + "='" + which + "'", null);
			/**
			 * 删除相应的回复
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_REPAL);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			/**
			 * 删除相应的赞
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_PAISE);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	/**
	 * 更新装修界回复
	 */

	public static void upDateReplyForCircle(Context context, String topic_id, ReplyResult replyResult, String which) {

		if (context == null || topic_id == null || replyResult == null || replyResult.data == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_REPAL);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			for (Reply reply : replyResult.data) {
				ContentValues contentValues = setReplyContentValues(reply, which);
				dbManager.insert(contentValues);
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}

	}

	/**
	 * 更新装修界赞
	 */

	public static void upDatePriseForCircle(Context context, String topic_id, FaceResult faceResult, String which) {

		if (context == null || topic_id == null || faceResult == null || faceResult.data == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_PAISE);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			for (Face face : faceResult.data) {
				ContentValues contentValues = setFaceContentValues(face, which);
				dbManager.insert(contentValues);
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}

	}

	/**
	 * 读取相册
	 */
	public static ArrayList<Topic> readAlumList(Context context, String uid) {
		ArrayList<Topic> topicList = new ArrayList<Topic>();
		if (context == null || uid == null) {
			return topicList;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_CIRCLE);
			Cursor cursor = dbManager.query(null, DBConstant.COL_WHICH + "='2' and " + DBConstant.COL_USER_ID + " = '" + uid + "'", null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Topic topic = new Topic();
					topicList.add(topic);
					topic.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ALUM_ID));
					topic.user_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_NAME));
					topic.user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_ID));
					topic.user_avatar = cursor.getString(cursor.getColumnIndex(DBConstant.COL_AVATAR));
					topic.content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CONTENT));
					topic.create_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
					topic.reply_num = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_NUM));
					topic.status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_STATUS));
					topic.sync_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_SYNC_ID));
					topic.sync_origin = cursor.getString(cursor.getColumnIndex(DBConstant.COL_SYNC_ORIGIN));
					topic.last_modify_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_TIME));
				}
				cursor.close();
				cursor = null;
			}
			if (topicList.size() > 0) {
				dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_ATTACHMENT);
				for (Topic topic : topicList) {
					/**
					 * 读附件
					 */
					cursor = dbManager.query(null, DBConstant.COL_ALUM_ID + "='" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='2'", null, null, null);
					if (cursor != null && cursor.getCount() > 0) {
						ArrayList<TopticAttachment> attachmentList = new ArrayList<TopticAttachment>();
						topic.attachment = attachmentList;
						while (cursor.moveToNext()) {
							TopticAttachment attachment = new TopticAttachment();
							attachment.attachment_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_ID));
							attachment.attach_height = cursor.getInt(cursor.getColumnIndex(DBConstant.COL_ATTACH_HEIGTH));
							attachment.attach_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_NAME));
							attachment.attach_thumb_url = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_THUMB_URL));
							attachment.attach_url = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACH_URL));
							attachment.attach_width = cursor.getInt(cursor.getColumnIndex(DBConstant.COL_ATTACH_WIDTH));
							attachment.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ALUM_ID));
							attachmentList.add(attachment);
						}
						cursor.close();
						cursor = null;
					}
				}
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
		return topicList;

	}

	/**
	 * 存储相册
	 */
	public static void saveAlumListToDb(Context context, List<Topic> topicList, String uid) {
		if (context == null || topicList == null || uid == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_CIRCLE);
			dbManager.delete(DBConstant.COL_WHICH + "='2' and " + DBConstant.COL_USER_ID + " = '" + uid + "'", null);
			/**
			 * 存列表
			 */
			for (Topic topic : topicList) {
				ContentValues contentValues = setTopicContentValues(topic, "2");
				dbManager.insert(contentValues);
			}
			/**
			 * 存附件
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_CIECLE_ATTACHMENT);
			for (Topic topic : topicList) {
				dbManager.delete(DBConstant.COL_ALUM_ID + " = '" + topic.topic_id + "' and " + DBConstant.COL_WHICH + "='2'", null);
				if (topic.attachment != null && topic.attachment.size() > 0) {
					for (TopticAttachment attachment : topic.attachment) {
						ContentValues contentValues = setAttachmentContentValues(attachment, "2");
						dbManager.insert(contentValues);
					}
				}
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	private static ContentValues setTopicContentValues(Topic topic, String which) {
		ContentValues contentValues = new ContentValues();
		if (which.equals(StringConstant.Two)) {
			/**
			 * 相册
			 */
			contentValues.put(DBConstant.COL_ALUM_ID, topic.topic_id);
		} else {
			/**
			 * 装修界
			 */
			contentValues.put(DBConstant.COL_TOPIC_ID, topic.topic_id);
		}
		/**
		 * 3广场
		 */
		contentValues.put(DBConstant.COL_USER_NAME, topic.user_name);
		contentValues.put(DBConstant.COL_USER_ID, topic.user_id);
		contentValues.put(DBConstant.COL_CONTENT, topic.content);
		contentValues.put(DBConstant.COL_CREATE_DATE, topic.create_time);
		contentValues.put(DBConstant.COL_LAST_MODIFY_TIME, topic.last_modify_time);
		contentValues.put(DBConstant.COL_AVATAR, topic.user_avatar);
		contentValues.put(DBConstant.COL_STATUS, topic.status);
		contentValues.put(DBConstant.COL_REPLY_NUM, topic.reply_num);
		contentValues.put(DBConstant.COL_SYNC_ID, topic.sync_id);
		contentValues.put(DBConstant.COL_WHICH, String.valueOf(which));
		contentValues.put(DBConstant.COL_SYNC_ORIGIN, topic.sync_origin);
		return contentValues;
	}

	/**
	 * 存储相册
	 */
	private static ContentValues setAttachmentContentValues(TopticAttachment attachment, String which) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_ATTACH_ID, attachment.attachment_id);
		contentValues.put(DBConstant.COL_POSTER_NAME, attachment.poster_name);
		contentValues.put(DBConstant.COL_POSTER_ID, attachment.poster_id);
		contentValues.put(DBConstant.COL_ATTACH_NAME, attachment.attach_name);
		contentValues.put(DBConstant.COL_ATTACH_SIZE, attachment.attach_size);
		contentValues.put(DBConstant.COL_ATTACH_WIDTH, attachment.attach_width);
		contentValues.put(DBConstant.COL_ATTACH_HEIGTH, attachment.attach_height);
		contentValues.put(DBConstant.COL_WHICH, which);
		contentValues.put(DBConstant.COL_ATTACH_THUMB_URL, attachment.attach_thumb_url);
		contentValues.put(DBConstant.COL_ATTACH_URL, attachment.attach_url);
		if (which.equals(StringConstant.Two)) {
			/**
			 * 相册
			 */
			contentValues.put(DBConstant.COL_ALUM_ID, attachment.topic_id);

		} else {
			/**
			 * 装修界
			 */
			contentValues.put(DBConstant.COL_TOPIC_ID, attachment.topic_id);
		}
		return contentValues;
	}

	private static ContentValues setReplyContentValues(Reply reply, String which) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_REPLYE_ID, reply.reply_id);
		contentValues.put(DBConstant.COL_TOPIC_ID, reply.topic_id);
		contentValues.put(DBConstant.COL_REPLYER_ID, reply.replyer_id);
		contentValues.put(DBConstant.COL_REPLYER_NAME, reply.replyer_name);
		contentValues.put(DBConstant.COL_CONTENT, reply.content);
		contentValues.put(DBConstant.COL_WHICH, which);
		contentValues.put(DBConstant.COL_LAST_MODIFY_TIME, reply.last_modify_time);
		contentValues.put(DBConstant.COL_CREATE_DATE, reply.create_date);
		contentValues.put(DBConstant.COL_ADD_TIME, reply.add_time);
		contentValues.put(DBConstant.COL_P_REPLYER_ID, reply.p_replyer_id);
		contentValues.put(DBConstant.COL_P_REPLYER_NAME, reply.p_replyer_name);
		contentValues.put(DBConstant.COL_STATUS, reply.status);
		return contentValues;
	}

	private static ContentValues setFaceContentValues(Face face, String which) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_ATTITUDE_ID, face.attitude_id);
		contentValues.put(DBConstant.COL_TOPIC_ID, face.topic_id);
		contentValues.put(DBConstant.COL_TYPE, face.type);
		contentValues.put(DBConstant.COL_USER_ID, face.user_id);
		contentValues.put(DBConstant.COL_WHICH, which);
		contentValues.put(DBConstant.COL_USER_NAME, face.user_name);
		contentValues.put(DBConstant.COL_ADD_TIME, face.add_time);
		contentValues.put(DBConstant.COL_IS_DELETE, face.is_delete);
		contentValues.put(DBConstant.COL_UPDATE_TIME, face.update_time);
		return contentValues;
	}

	/**
	 * 存储装修界列表
	 */

	/**
	 * 读取记账
	 */

	public static List<Bill> readBillFromDb(Context context) {
		List<Bill> billList = new ArrayList<Bill>();
		if (context == null) {
			return billList;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_PARTNER_BILL);
			Cursor cursor = dbManager.query(null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Bill bill = new Bill();
					bill.bill_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_BILL_ID));
					bill.bill_amount = cursor.getString(cursor.getColumnIndex(DBConstant.COL_BILL_ACCOUNT));
					bill.bill_content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_BILL_CONTENT));
					bill.bill_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_BILL_DATE));
					bill.bill_remark = cursor.getString(cursor.getColumnIndex(DBConstant.COL_BILL_REMARK));
					bill.create_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
					bill.family_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_FAMILY_ID));
					bill.last_modify_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_NAME));
					bill.last_modify_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_TIME));
					bill.last_modify_uid = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_UID));
					bill.status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_STATUS));
					bill.user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_ID));
					bill.user_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_NAME));
					billList.add(bill);
				}
				cursor.close();
				cursor = null;
			}

			dbManager.setTransactionSuccessful();
		}

		catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}

		return billList;
	}

	/**
	 * 存储记账
	 */

	public static void saveBillListToDb(Context context, List<Bill> billList) {
		if (context == null || billList == null) {
			return;
		}

		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_PARTNER_BILL);
			dbManager.delete(null, null);
			for (Bill bill : billList) {
				ContentValues contentValues = setBill(bill);
				dbManager.insert(contentValues);
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}

	}

	private static ContentValues setBill(Bill bill) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(DBConstant.COL_BILL_ID, bill.bill_id);
		contentValues.put(DBConstant.COL_USER_NAME, bill.user_name);
		contentValues.put(DBConstant.COL_USER_ID, bill.user_id);
		contentValues.put(DBConstant.COL_BILL_ACCOUNT, bill.bill_amount);
		contentValues.put(DBConstant.COL_BILL_CONTENT, bill.bill_content);
		contentValues.put(DBConstant.COL_CREATE_DATE, bill.create_date);
		contentValues.put(DBConstant.COL_BILL_DATE, bill.bill_date);
		contentValues.put(DBConstant.COL_LAST_MODIFY_TIME, bill.last_modify_time);
		contentValues.put(DBConstant.COL_BILL_REMARK, bill.bill_remark);
		contentValues.put(DBConstant.COL_FAMILY_ID, bill.family_id);
		contentValues.put(DBConstant.COL_STATUS, bill.status);
		contentValues.put(DBConstant.COL_LAST_MODIFY_NAME, bill.last_modify_name);
		contentValues.put(DBConstant.COL_LAST_MODIFY_UID, bill.last_modify_uid);
		return contentValues;
	}

	/**
	 * 记账
	 */
	/**
	 * 读取计划
	 */

	public static List<Plan> readPaln(Context context) {
		List<Plan> planList = new ArrayList<Plan>();
		if (context == null) {
			return planList;
		}

		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_PARTNER_PLAN);

			Cursor cursor = dbManager.query(null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Plan plan = new Plan();
					plan.plan_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_PLAN_ID));
					plan.create_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
					plan.dir_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_DIR_ID));
					plan.done_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_DONE_DATE));
					plan.done_user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_DONE_USER_ID));
					plan.family_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_FAMILY_ID));
					plan.last_modify_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_NAME));
					plan.last_modify_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_TIME));
					plan.last_modify_uid = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LAST_MODIFY_UID));
					plan.plan_content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_PLAN_CONTENT));
					plan.plan_done_date = cursor.getString(cursor.getColumnIndex(DBConstant.COL_PLAN_DONE_DATE));
					plan.remark = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REMARK));
					plan.status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_STATUS));
					plan.user_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_ID));
					plan.user_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_USER_NAME));
					planList.add(plan);
				}
			}

			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
		return planList;

	}

	/**
	 * 存储计划
	 */

	public static void savePlanToDB(Context context, List<Plan> planList) {
		if (context == null || planList == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_PARTNER_PLAN);
			dbManager.delete(null, null);
			for (Plan plan : planList) {
				ContentValues contentValues = setPlan(plan);
				dbManager.insert(contentValues);
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	/**
	 * 修改删除计划
	 */
	public static void modifyOrDelPlan(Context context, Plan plan, boolean isDel) {
		if (context == null || plan == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_PARTNER_PLAN);
			dbManager.delete(DBConstant.COL_PLAN_ID + "='" + plan.plan_id + "'", null);
			if (!isDel) {
				ContentValues contentValues = setPlan(plan);
				dbManager.insert(contentValues);
			}

			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	private static ContentValues setPlan(Plan plan) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_PLAN_ID, plan.plan_id);
		contentValues.put(DBConstant.COL_USER_NAME, plan.user_name);
		contentValues.put(DBConstant.COL_USER_ID, plan.user_id);
		contentValues.put(DBConstant.COL_DIR_ID, plan.dir_id);
		contentValues.put(DBConstant.COL_DONE_DATE, plan.done_date);
		contentValues.put(DBConstant.COL_CREATE_DATE, plan.create_date);
		contentValues.put(DBConstant.COL_DONE_USER_ID, plan.done_user_id);
		contentValues.put(DBConstant.COL_PLAN_CONTENT, plan.plan_content);
		contentValues.put(DBConstant.COL_PLAN_DONE_DATE, plan.plan_done_date);
		contentValues.put(DBConstant.COL_REMARK, plan.remark);
		contentValues.put(DBConstant.COL_FAMILY_ID, plan.family_id);
		contentValues.put(DBConstant.COL_STATUS, plan.status);
		contentValues.put(DBConstant.COL_LAST_MODIFY_NAME, plan.last_modify_name);
		contentValues.put(DBConstant.COL_LAST_MODIFY_TIME, plan.last_modify_time);
		contentValues.put(DBConstant.COL_LAST_MODIFY_UID, plan.last_modify_uid);
		return contentValues;
	}

	/**
	 * 存储聊天信息
	 */

	public static void saveMessageToDB(Context context, MessageModel model) {
		if (context == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_MESSAGE);

			ContentValues contentValues = new ContentValues();
			contentValues.put(DBConstant.COL_NAME_MESSAGE_USER_ID, HuoBanApplication.getInstance().getInfoResult().data.user_info.user_id);
			contentValues.put(DBConstant.COL_NAME_MESSAGE_FROMUSERID, model.fromUserId);
			contentValues.put(DBConstant.COL_NAME_MESSAGE_MESSAGESTR, model.messageStr);
			contentValues.put(DBConstant.COL_NAME_MESSAGE_STATUS, model.status);
			contentValues.put(DBConstant.COL_NAME_MESSAGE_TIMESTEMP, System.currentTimeMillis());
			contentValues.put(DBConstant.COL_NAME_MESSAGE_TOUSERID, model.toUserId);
			contentValues.put(DBConstant.COL_NAME_MESSAGE_TYPE, model.type);
			dbManager.insert(contentValues);
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	/**
	 * 推送
	 */
	public static void savePushList(Context context, ArrayList<PushArticle> articles) {
		if (context == null || articles == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_BAIDU_PUSH);
			for (PushArticle article : articles) {
				dbManager.delete(DBConstant.COL_MESSAGE_ID+ "='" + article.message_id + "'", null);
			}
			for (PushArticle article : articles) {
				ContentValues contentValues = setPushArticle(article);
				dbManager.insert(contentValues);
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
	}

	public static ArrayList<PushArticle> readPush(Context context) {
		ArrayList<PushArticle> articles = new ArrayList<PushArticle>();
		if (context == null) {
			return articles;
		}

		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_BAIDU_PUSH);

			Cursor cursor = dbManager.query(null, null, null, DBConstant.COL_CREATE_DATE, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					PushArticle pushArticle = new PushArticle();
					pushArticle.create_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_DATE));
					pushArticle.title = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TITLE));
					pushArticle.first_img = cursor.getString(cursor.getColumnIndex(DBConstant.COL_FIRST_IMAGE));
					pushArticle.message_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_MESSAGE_ID));
					pushArticle.display_order = cursor.getString(cursor.getColumnIndex(DBConstant.COL_DISPLAY_ORDER));
					pushArticle.index = cursor.getString(cursor.getColumnIndex(DBConstant.COL_INDEX));
					pushArticle.MD = cursor.getString(cursor.getColumnIndex(DBConstant.COL_MD));
					pushArticle.XQ = cursor.getString(cursor.getColumnIndex(DBConstant.COL_XQ));
					articles.add(pushArticle);
				}

				cursor.close();
				cursor = null;
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}

		return articles;
	}

	private static ContentValues setPushArticle(PushArticle pushArticle) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_FIRST_IMAGE, pushArticle.first_img);
		contentValues.put(DBConstant.COL_DISPLAY_ORDER, pushArticle.display_order);
		contentValues.put(DBConstant.COL_MESSAGE_ID, pushArticle.message_id);
		contentValues.put(DBConstant.COL_TITLE, pushArticle.title);
		contentValues.put(DBConstant.COL_MD, pushArticle.MD);
		contentValues.put(DBConstant.COL_INDEX, pushArticle.index);
		contentValues.put(DBConstant.COL_XQ, pushArticle.XQ);
		contentValues.put(DBConstant.COL_CREATE_DATE, pushArticle.create_time);
		return contentValues;
	}

	// select * from huoban_chat_message where message_user_id = 1173 and (
	// message_fromUserId = 1173 and message_toUserId = 1419 or
	// message_fromUserId = 1419 and message_toUserId = 1173) order by
	// message_timestemp asc Limit 10 offset 0

	public final static ArrayList<MessageModel> getMessage(Context context, String userid, String friendid, int limit, int offset) {
		if (context == null) {
			return null;
		}
		LogUtil.logI("dbutils", "getMessage " + userid + "   " + friendid + "  " + limit + "  " + offset);
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			SQLiteDatabase sqLiteDatabase = dbManager.open(true);
			sqLiteDatabase.beginTransaction();
			String sql = "select * from huoban_chat_message where message_user_id = " + userid + " and ( message_fromUserId = " + userid + " and message_toUserId = " + friendid + " or message_fromUserId = " + friendid + " and message_toUserId = " + userid + ") order by message_timestemp desc Limit " + limit + " offset " + offset;
			LogUtil.logI("dbutils", sql);
			Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
			ArrayList<MessageModel> arrayList = new ArrayList<MessageModel>();
			Log.i("dbutils", "count = " + cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					MessageModel model = new MessageModel();
					model.fromUserId = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_FROMUSERID));
					model.toUserId = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_TOUSERID));
					model.messageStr = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_MESSAGESTR));
					model.status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_STATUS));
					model.type = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_TYPE));
					model.timetemp = cursor.getString(cursor.getColumnIndex(DBConstant.COL_NAME_MESSAGE_TIMESTEMP));
					arrayList.add(model);
				}
			}
			LogUtil.logI("arraylist.size = " + arrayList.size());
			dbManager.setTransactionSuccessful();
			return arrayList;
		} catch (Exception e) {
		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param questions
	 * @param isTrue
	 *            true我的问题 false 问题广场
	 * @param postername
	 */

	public final static void upDateQuestion(Context context, ArrayList<Question> questions, boolean isTrue, String postername) {
		if (context == null || questions == null || postername == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_LIST);
			dbManager.beginTransaction();

			if (isTrue) {
				dbManager.delete(DBConstant.COL_POSTER_NAME + "='" + postername + "'", null);
			} else {
				dbManager.delete(DBConstant.COL_POSTER_NAME + "<>'" + postername + "'", null);
			}
			if (BuildConfig.DEBUG)
				LogUtil.logI("删除了数据");
			saveQuestionListToDB(dbManager, questions, context);
			dbManager.setTransactionSuccessful();
			dbManager.endTransaction();

		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.close();
				dbManager = null;
			}
		}

	}

	private final static void saveQuestionListToDB(DataBaseManager dbManager, ArrayList<Question> questions, Context context) {
		if (context == null || questions == null || questions.size() == 0) {
			return;
		}
		Cursor cursor = null;
		boolean isExit = false;

		for (Question q : questions) {
			cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + q.topic_id + "'", null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				isExit = true;
			}
			ContentValues values = new ContentValues();
			values.put(DBConstant.COL_APP_ID, q.app_id);
			values.put(DBConstant.COL_CREATE_TIME, q.create_time);
			values.put(DBConstant.COL_POSTER_NAME, q.poster_name);
			values.put(DBConstant.COL_REPLY_STATUS, q.reply_status);
			values.put(DBConstant.COL_TITLE, q.title);
			values.put(DBConstant.COL_TOPIC_ID, q.topic_id);
			values.put(DBConstant.COL_TYPE, q.type);
			values.put(DBConstant.COL_VIEW_NUM, q.view_num);
			if (isExit) {
				dbManager.update(values, DBConstant.COL_TOPIC_ID + "='" + q.topic_id + "'", null);
			} else {
				dbManager.insert(values);
			}
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public final static ArrayList<Question> getQuestions(Context context, String selection, String[] selectionArgs, String sortOrder, String limit) {
		ArrayList<Question> questions = new ArrayList<Question>();
		if (context == null || selectionArgs == null) {
			return null;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_LIST);
			dbManager.beginTransaction();

			Cursor cursor = dbManager.query(null, selection, selectionArgs, sortOrder, limit);
			while (cursor != null && cursor.moveToNext()) {
				Question q = new Question();
				q.app_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_APP_ID));
				q.create_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_TIME));
				q.poster_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_POSTER_NAME));
				q.reply_status = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_STATUS));
				q.title = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TITLE));
				q.type = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TYPE));
				q.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
				q.view_num = cursor.getString(cursor.getColumnIndex(DBConstant.COL_VIEW_NUM));
				questions.add(q);
			}
			if (cursor != null) {
				cursor.close();
			}
			dbManager.setTransactionSuccessful();
			dbManager.endTransaction();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.close();
				dbManager = null;
			}
		}
		return questions;
	}

	/**
	 * 删除问题
	 */
	public static void delQuestionDetail(Context context, String topic_id) {
		if (context == null || topic_id == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_DETAIL);
			dbManager.beginTransaction();
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			/**
			 * 删除问题对应的附件
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_ATTACHMENT);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			/**
			 * 删除对应的回复
			 */
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_REPLY);
			dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null);
			dbManager.setTransactionSuccessful();
			dbManager.endTransaction();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.close();
				dbManager = null;
			}
		}
	}

	/**
	 * 读取问题
	 */
	public static QuestionDetailResultData getQuestionDetail(Context context, String topicId) {

		QuestionDetailResultData detailData = null;

		if (context == null || topicId == null) {
			return null;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_DETAIL);
			dbManager.beginTransaction();
			Cursor cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topicId + "'", null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				detailData = new QuestionDetailResultData();
				QuestionDetailInfo result = new QuestionDetailInfo();
				detailData.result = result;
				result.reply_num = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_NUM));

				QuestionInfoDetail topic_info = new QuestionInfoDetail();
				result.topic_info = topic_info;
				topic_info.topic_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TOPIC_ID));
				topic_info.create_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CREATE_TIME));
				topic_info.app_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_APP_ID));
				topic_info.title = cursor.getString(cursor.getColumnIndex(DBConstant.COL_TITLE));
				topic_info.poster_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_POSTER_NAME));
				topic_info.view_num = cursor.getString(cursor.getColumnIndex(DBConstant.COL_VIEW_NUM));
				topic_info.pic_urls = getQuestionAttachment(dbManager, topic_info.topic_id);

				QuestionDetailReply detailReply = getQuestionReply(dbManager, topic_info.topic_id);
				if (detailReply != null) {
					List<QuestionDetailReply> replyList = new ArrayList<QuestionDetailReply>();
					result.reply_list = replyList;
					replyList.add(detailReply);
				}

			}

			if (cursor != null) {
				cursor.close();
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
				dbManager = null;
			}
		}

		return detailData;
	}

	public static QuestionDetailReply getQuestionReply(DataBaseManager dbManager, String topic_id) {
		QuestionDetailReply detailReply = null;
		dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_REPLY);
		Cursor cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			detailReply = new QuestionDetailReply();
			detailReply.reply_id = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLYE_ID));
			detailReply.reply_type = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_TYPE));
			detailReply.reply_time = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_TIME));
			detailReply.content = cursor.getString(cursor.getColumnIndex(DBConstant.COL_CONTENT));
			detailReply.like = cursor.getString(cursor.getColumnIndex(DBConstant.COL_LIKE));
			detailReply.is_like = cursor.getString(cursor.getColumnIndex(DBConstant.COL_IS_LIKE));
			detailReply.replyer_name = cursor.getString(cursor.getColumnIndex(DBConstant.COL_REPLY_NAME));
			cursor.close();
		}
		return detailReply;
	}

	public static List<String> getQuestionAttachment(DataBaseManager dbManager, String topic_id) {
		List<String> list = null;
		dbManager.setTable(DBConstant.TABLE_NAME_ATTACHMENT);
		Cursor cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + topic_id + "'", null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			list = new ArrayList<String>();
			while (cursor.moveToNext()) {
				String url = cursor.getString(cursor.getColumnIndex(DBConstant.COL_ATTACHMENT_URL));
				list.add(url);
			}
			cursor.close();
		}

		return list;
	}

	/**
	 * 存储问题详情到本地数据库
	 * 
	 * @param context
	 * @param detailData
	 */
	public static void saveQuestionDetailToDB(Context context, QuestionDetailResultData msg_plaintext, String uid) {
		if (context == null || msg_plaintext == null || msg_plaintext.result == null || msg_plaintext.result.topic_info == null) {
			return;
		}
		QuestionDetailInfo result = msg_plaintext.result;
		QuestionInfoDetail topic_info = result.topic_info;
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_DETAIL);
			Cursor cursor = dbManager.query(null, DBConstant.COL_TOPIC_ID + "='" + result.topic_info.topic_id + "'", null, null, null);
			boolean isExit = false;
			if (cursor != null && cursor.getCount() > 0) {
				isExit = true;
				cursor.close();
			}
			// dbManager.delete(DBConstant.COL_TOPIC_ID + "='"
			// + result.topic_info.topic_id+"'", null);
			ContentValues values = new ContentValues();
			values.put(DBConstant.COL_REPLY_NUM, result.reply_num);
			values.put(DBConstant.COL_TOPIC_ID, topic_info.topic_id);
			values.put(DBConstant.COL_APP_ID, topic_info.app_id);
			values.put(DBConstant.COL_VIEW_NUM, topic_info.view_num);
			values.put(DBConstant.COL_CREATE_TIME, topic_info.create_time);
			values.put(DBConstant.COL_TITLE, topic_info.title);
			values.put(DBConstant.COL_POSTER_NAME, topic_info.poster_name);
			if (isExit) {
				dbManager.update(values, DBConstant.COL_TOPIC_ID + "='" + result.topic_info.topic_id + "'", null);
			} else {
				dbManager.insert(values);
			}

			if (topic_info.pic_urls != null && topic_info.pic_urls.size() > 0) {
				/**
				 * 存储附件
				 */
				saveQuestionAttachmentToDB(topic_info.pic_urls, dbManager, topic_info.topic_id);
			}
			if (result.reply_list != null && result.reply_list.size() > 0) {
				QuestionDetailReply detailReply = result.reply_list.get(0);
				saveQuestionReplyToDB(dbManager, topic_info.topic_id, detailReply);
			}
			/**
			 * 已读问题id存入本地数据库
			 */
			saveReadIdToDB(dbManager, topic_info.topic_id, uid);

			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
				dbManager = null;
			}
		}
	}

	public static void saveQuestionAttachmentToDB(List<String> list, DataBaseManager dbManager, String topicId) {
		dbManager.setTable(DBConstant.TABLE_NAME_ATTACHMENT);
		dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topicId + "'", null);
		for (String str : list) {
			ContentValues values = new ContentValues();
			values.put(DBConstant.COL_TOPIC_ID, topicId);
			values.put(DBConstant.COL_ATTACHMENT_URL, str);
			dbManager.insert(values);
		}
	}

	public static void saveQuestionReplyToDB(DataBaseManager dbManager, String topicId, QuestionDetailReply detailReply) {
		dbManager.setTable(DBConstant.TABLE_NAME_QUESTION_REPLY);
		dbManager.delete(DBConstant.COL_TOPIC_ID + "='" + topicId + "'", null);
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_REPLYE_ID, detailReply.reply_id);
		contentValues.put(DBConstant.COL_TOPIC_ID, topicId);
		contentValues.put(DBConstant.COL_REPLY_TYPE, detailReply.reply_type);
		contentValues.put(DBConstant.COL_IS_LIKE, detailReply.is_like);
		contentValues.put(DBConstant.COL_LIKE, detailReply.like);
		contentValues.put(DBConstant.COL_REPLY_NAME, detailReply.replyer_name);
		contentValues.put(DBConstant.COL_REPLY_TIME, detailReply.reply_time);
		contentValues.put(DBConstant.COL_CONTENT, detailReply.content);
		dbManager.insert(contentValues);
	}

	public static void saveQuestionReplyToDB(String topicId, QuestionDetailReply detailReply, Context context) {
		if (context == null || topicId == null || detailReply == null) {
			return;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(true);
			dbManager.beginTransaction();
			saveQuestionReplyToDB(dbManager, topicId, detailReply);

			dbManager.setTransactionSuccessful();
			dbManager.endTransaction();
		} catch (Exception e) {

		}
	}

	public static void saveReadIdToDB(DataBaseManager dbManager, String topic_id, String uid) {
		dbManager.setTable(DBConstant.TABLE_NAME_READ_IDS);
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBConstant.COL_QUESTION_ID, topic_id);
		contentValues.put("uid", uid);
		boolean isExit = false;
		Cursor cursor = dbManager.query(null, DBConstant.COL_QUESTION_ID + "='" + topic_id + "' and uid ='" + uid + "'", null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			isExit = true;
			cursor.close();
		}
		if (isExit) {
			dbManager.update(contentValues, DBConstant.COL_QUESTION_ID + "='" + topic_id + "'", null);
		} else {
			dbManager.insert(contentValues);
		}
	}

	public static List<String> readLocalQuestionIDS(Context context, String uid) {
		ArrayList<String> list = new ArrayList<String>();
		;
		if (context == null) {
			return list;
		}
		DataBaseManager dbManager = null;
		try {
			dbManager = new DataBaseManager(context);
			dbManager.open(false);
			dbManager.setTable(DBConstant.TABLE_NAME_READ_IDS);
			dbManager.beginTransaction();
			Cursor cursor = dbManager.query(null, "uid ='" + uid + "'", null, null, null);

			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<String>();
				while (cursor.moveToNext()) {
					list.add(cursor.getString(cursor.getColumnIndex(DBConstant.COL_QUESTION_ID)));
				}
			}
			if (cursor != null) {
				cursor.close();
			}
			dbManager.setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			if (dbManager != null) {
				dbManager.endTransaction();
				dbManager.close();
			}
		}
		return list;
	}

}
