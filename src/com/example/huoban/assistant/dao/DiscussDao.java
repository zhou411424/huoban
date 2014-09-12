package com.example.huoban.assistant.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.Content;
import com.example.huoban.assistant.model.Discuss;
import com.example.huoban.assistant.model.DiscussData;
import com.example.huoban.assistant.model.DiscussListData;
import com.example.huoban.data.Discusses;
import com.example.huoban.database.DataBaseManager;

public class DiscussDao {
	private DataBaseManager dbm;
	private Context context;
	
	public DiscussDao(Context context){
		this.context = context;
		dbm = new DataBaseManager(context);
	}
	/**
	 * 删除讨论区的某一条评论或文章的评论
	 * 
	 * @param contentId
	 */
	public void deleteDiscuss(int contentId) {
		String userId = HuoBanApplication.getInstance().getUserId(context);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(Discusses.TABLE_NAME);
		sql.append(" where ");
		sql.append(Discusses.LOC_DISCUSS_ID);
		sql.append(" = ");
		sql.append(contentId);
		sql.append(" and ");
		sql.append(Discusses.LOC_USER_ID);
		sql.append(" = ");
		sql.append(userId);
		try {
//			dbm.execSQL("delete from discusses where discuss_id = "
//					+ contentId + " and user_id = " + userId);
			dbm.execSQL(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(dbm != null){
				dbm.close();
			}
		}
	}
	
	public void saveDiscussData(DiscussListData data){
		if(data == null){
			return;
		}
		if(data.discuss_list != null && data.discuss_list.size() > 0){
			saveDiscussList(data.discuss_list);
		}
	}
	

	private void saveDiscussList(List<DiscussData> discussList) {
		// TODO Auto-generated method stub
		int size = discussList.size();
		ContentValues[] valueArray = new ContentValues[size];
		String[] discussesId = new String[size];
		StringBuffer selection = new StringBuffer();
		selection.append(Discusses.LOC_DISCUSS_ID + " IN (");
		for(int i=0; i<size; i++){
			DiscussData discussData = discussList.get(i);
			
			Discusses discusses = new Discusses();
			discusses.setAddTime(discussData.add_time);
			discusses.setBadNum(discussData.bad_num);
			discusses.setContent(discussData.content);
			discusses.setDiscussGarde(discussData.discuss_garde);
			discusses.setDiscussId(discussData.discuss_id);
			discusses.setDiscussNum(discussData.discuss_num);
			discusses.setGoodNum(discussData.good_num);
			discusses.setLastDiscussId(discussData.last_discuss_id);
			discusses.setType(discussData.type);
			discusses.setCateId(discussData.cate_id);
			discusses.setBadContent(discussData.bad_content);
			discusses.setUserId(discussData.user_id);
			discusses.setUserName(discussData.user_name);
			discusses.setIsDelete(discussData.is_delete);
            discusses.setUserUrl(discussData.avatar);
			selection.append("?,");
			discussesId[i] = String.valueOf(discussData.discuss_id);
			valueArray[i] = getDiscussValues(discusses);
		}
		selection.setLength(selection.length() - 1);
		selection.append(")");
		
		try {
			dbm.open(true);
			dbm.beginTransaction();
			dbm.delete(Discusses.TABLE_NAME, selection.toString(), discussesId);
			dbm.insert(Discusses.TABLE_NAME, valueArray);
			dbm.setTransactionSuccessful();
		} catch (SQLiteException e) {

		} finally {
			if (dbm != null) {
				dbm.endTransaction();
				dbm.close();
			}
		}
	}
	
	public ContentValues getDiscussValues(Discusses discusses) {
		ContentValues values = new ContentValues();
		values.put(Discusses.LOC_ADD_TIME, discusses.getAddTime());
		values.put(Discusses.LOC_BAD_NUM, discusses.getBadNum());
		values.put(Discusses.LOC_CATE_ID, discusses.getCateId());
		values.put(Discusses.LOC_BAD_CONTENT, discusses.getBadContent());
		values.put(Discusses.LOC_CONTENT, discusses.getContent());
		values.put(Discusses.LOC_DISCUSS_GARDE, discusses.getDiscussGarde());
		values.put(Discusses.LOC_DISCUSS_ID, discusses.getDiscussId());
		values.put(Discusses.LOC_DISCUSS_NUM, discusses.getDiscussNum());
		values.put(Discusses.LOC_GOOD_NUM, discusses.getGoodNum());
		values.put(Discusses.LOC_LAST_DISCUSS_ID, discusses.getLastDiscussId());
		values.put(Discusses.LOC_TYPE, discusses.getType());
		values.put(Discusses.LOC_USER_ID, discusses.getUserId());
		values.put(Discusses.LOC_USER_NAME, discusses.getUserName());
		values.put(Discusses.LOC_IS_DELETE, discusses.getIsDelete());
		values.put(Discusses.LOC_USER_Url, discusses.getUserUrl());

		return values;
	}
	
	public void queryDiscussData(int cateId,ArrayList<Discuss> discussLists) {
		Cursor cur = null;
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(Discusses.TABLE_NAME);
		sql.append(" where ");
		sql.append(Discusses.LOC_CATE_ID);
		sql.append(" = ");
		sql.append(cateId);
		sql.append(" and ");
		sql.append(Discusses.LOC_LAST_DISCUSS_ID);
		sql.append(" = 0 and ");
		sql.append(Discusses.LOC_IS_DELETE);
		sql.append(" = 0 order by ");
		sql.append(Discusses.LOC_ADD_TIME);
		sql.append(" desc");
//		String selection = "select * from discusses where cate_id = "
//				+ cateId
//				+ " and last_discuss_id = 0 and is_delete = 0 order by add_time desc";

		try {
			cur = dbm.rawQuery(sql.toString());
			while (cur.moveToNext()) {
				Discuss discuss = new Discuss();

				String timeAgo = this.jiSuanAll(cur.getString(8));
				discuss.setAddTime(timeAgo);
				discuss.setDiscussId(cur.getInt(0));
				discuss.setBadContent(cur.getString(3));
				discuss.setContent(cur.getString(2));
				discuss.setUserId(cur.getInt(5));
				discuss.setUserName(cur.getString(6));
				discuss.setDiscussTime(cur.getString(8));
				discuss.setUserUrl(cur.getString(14));

				int discussNum = cur.getInt(12);
				int badNum = cur.getInt(11);
				int goodNum = cur.getInt(10);

				if (discussNum != -1) {
					discuss.setDiscussNum(discussNum);
				}

				if (badNum != -1) {
					discuss.setBadNum(badNum);
				}

				if (goodNum != -1) {
					discuss.setGoodNum(goodNum);
				}

				discuss.setContentLists(queryContentData(discuss.getDiscussId()));

				discussLists.add(discuss);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			if(dbm != null){
				dbm.close();
			}
		}
	}
	
	public String jiSuanAll(String endTime) {
		if (endTime == null || endTime.equals("") || endTime.equals("null")) {
			return "";
		}

		if (endTime.length() < 13) {
			endTime = endTime + "000";
		}

		long between = 0;
		String currentTime = null;
		try {
			String now = System.currentTimeMillis() + "";
			between = (Long.parseLong(now) - Long.parseLong(endTime));

			long day = between / (24 * 60 * 60 * 1000);
			long hour = (between / (60 * 60 * 1000) - day * 24);
			long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60
					* 1000 - min * 60 * 1000 - s * 1000);
			if (day > 0) {
				currentTime = (day + 1) + "天前";
			} else if (hour > 0) {
				currentTime = hour + "小时前";
			} else if (min > 0) {
				currentTime = min + "分钟前";
			} else {
				currentTime = "刚刚";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return currentTime;
	}
	
	private ArrayList<Content> queryContentData(int discussId) {
		ArrayList<Content> lists = new ArrayList<Content>();
		Cursor cur = null;
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(Discusses.TABLE_NAME);
		sql.append(" where ");
		sql.append(Discusses.LOC_LAST_DISCUSS_ID);
		sql.append(" = ");
		sql.append(discussId);
		sql.append(" and ");
		sql.append(Discusses.LOC_IS_DELETE);
		sql.append(" = 0 order by ");
		sql.append(Discusses.LOC_ADD_TIME);
		sql.append(" desc");
//		String selection = "select * from discusses where last_discuss_id = "
//				+ discussId + " and is_delete = 0 order by add_time desc";

		try {
			cur = dbm.rawQuery(sql.toString());
			while (cur.moveToNext()) {
				Content contents = new Content();
				contents.setContent(cur.getString(2));
				contents.setDiscussUserId(cur.getInt(5));
				contents.setUserName(cur.getString(6));
				String dateTime = this.jiSuanAll(cur.getString(8));
				contents.setDateTime(dateTime);
				contents.setDiscussTime(cur.getString(8));
				contents.setContentId(cur.getInt(0));

				lists.add(contents);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			if(dbm != null){
				dbm.close();
			}
		}
		return lists;
	}
	
}
