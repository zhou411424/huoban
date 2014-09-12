package com.example.huoban.assistant.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.data.SupervisorBean;
import com.example.huoban.data.WebViewBean;
import com.example.huoban.database.DBConstant;
import com.example.huoban.database.DataBaseManager;

public class SupervisorDao {
	private Context context;

	public SupervisorDao(Context context) {
		this.context = context;
	}
	
	
	/**
	 * 查询监理信息
	 * 
	 * @return
	 */
	public ArrayList<SupervisorBean> querySupervisor(int pageIndex,
			String updateTime) {
		DataBaseManager	dbm = new DataBaseManager(context);
		Cursor supervisorCur = null;
		String userId = HuoBanApplication.getInstance().getUserId(context);
		ArrayList<SupervisorBean> superisorList = new ArrayList<SupervisorBean>();
		StringBuilder sql = new StringBuilder();
		if (updateTime.equals("0")) {
			sql.append("select * from ");
			sql.append(SupervisorBean.TABLE_NAME);
			sql.append(" where ");
			sql.append(SupervisorBean.CURRENT_USER_ID);
			sql.append(" = ");
			sql.append(userId);
			sql.append(" order by ");
			sql.append(SupervisorBean.UPDATE_TIME);
			sql.append(" desc limit ");
			sql.append((pageIndex - 1) * DBConstant.SQL_LIMIT_PAGE_SIZE);
			sql.append(" , ");
			sql.append(DBConstant.SQL_LIMIT_PAGE_SIZE);
//			supervisorCur = dbm.rawQuery("select * from supervisor where current_user_id = "
//					+ userId
//					+ " order by update_time desc limit "
//					+ (pageIndex - 1) * DBConstant.SQL_LIMIT_PAGE_SIZE + " , "
//					+ DBConstant.SQL_LIMIT_PAGE_SIZE);
			supervisorCur = dbm.rawQuery(sql.toString());
		} else {
			sql.append("select * from ");
			sql.append(SupervisorBean.TABLE_NAME);
			sql.append(" where ");
			sql.append(SupervisorBean.UPDATE_TIME);
			sql.append(" < '");
			sql.append(updateTime);
			sql.append("' and ");
			sql.append(SupervisorBean.CURRENT_USER_ID);
			sql.append(" = ");
			sql.append(userId);
			sql.append(" order by ");
			sql.append(SupervisorBean.UPDATE_TIME);
			sql.append(" desc limit ");
			sql.append((pageIndex - 1) * DBConstant.SQL_LIMIT_PAGE_SIZE);
			sql.append(" , ");
			sql.append(DBConstant.SQL_LIMIT_PAGE_SIZE);
//			supervisorCur = dbm.rawQuery("select * from supervisor where update_time < "
//							+ "'" + updateTime + "'"
//							+ " and current_user_id = "
//							+ userId
//							+ " order by update_time desc limit "
//							+ (pageIndex - 1) * DBConstant.SQL_LIMIT_PAGE_SIZE + " , "
//							+ DBConstant.SQL_LIMIT_PAGE_SIZE);
			supervisorCur = dbm.rawQuery(sql.toString());
		}
		while (supervisorCur.moveToNext()) {
			SupervisorBean bean = new SupervisorBean();
			bean.setTitle(supervisorCur.getString(1));
			bean.setThumb_url(supervisorCur.getString(2));
			bean.setSummary(supervisorCur.getString(3));
			bean.setTip_id(supervisorCur.getInt(4));
			bean.setSuprtvisorUrl(supervisorCur.getString(5));
			bean.setSuprtvisorName(supervisorCur.getString(6));
			bean.setSuprtvisorSex(supervisorCur.getString(7));
			bean.setSuprtvisorIdCard(supervisorCur.getString(8));
			bean.setPhoneNumber(supervisorCur.getString(9));
			bean.setSuprtvisorId(supervisorCur.getString(10));
			bean.setUpdateTiem(supervisorCur.getString(11));
			bean.setApi_type(supervisorCur.getInt(12));
			superisorList.add(bean);
		}
		dbm.close();
		return superisorList;
	}
	
	public synchronized void inserSupervisor(SupervisorBean bean) {
		DataBaseManager	dbm = new DataBaseManager(context);
		String userId = HuoBanApplication.getInstance().getUserId(context);
		String comma = " ,";
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ");
			sql.append(SupervisorBean.TABLE_NAME);
			sql.append("( ");
			sql.append(SupervisorBean.TITLE);
			sql.append(comma);
			sql.append(SupervisorBean.THUMB_URl);
			sql.append(comma);
			sql.append(SupervisorBean.CONTENT);
			sql.append(comma);
			sql.append(SupervisorBean.TIP_ID);
			sql.append(comma);
			sql.append(SupervisorBean.SUPRTVISOR_URL);
			sql.append(comma);
			sql.append(SupervisorBean.SUPRTVISOR_NAME);
			sql.append(comma);
			sql.append(SupervisorBean.SUPRTVISOR_SEX);
			sql.append(comma);
			sql.append(SupervisorBean.SUPRTVISOR_ID_CARD);
			sql.append(comma);
			sql.append(SupervisorBean.PHONE_NUMBER);
			sql.append(comma);
			sql.append(SupervisorBean.SUPRTVISOR_ID);
			sql.append(comma);
			sql.append(SupervisorBean.UPDATE_TIME);
			sql.append(comma);
			sql.append(SupervisorBean.FLAGE);
			sql.append(comma);
			sql.append(SupervisorBean.CURRENT_USER_ID);
			sql.append(" ) values ( '");
			sql.append(bean.getTitle());
			sql.append("','");
			sql.append(bean.getThumb_url());
			sql.append("','");
			sql.append(bean.getSummary());
			sql.append("',");
			sql.append(bean.getTip_id());
			sql.append(",'");
			sql.append(bean.getSuprtvisorUrl());
			sql.append("','");
			sql.append(bean.getSuprtvisorName());
			sql.append("','");
			sql.append(bean.getSuprtvisorSex());
			sql.append("','");
			sql.append(bean.getSuprtvisorIdCard());
			sql.append("','");
			sql.append(bean.getPhoneNumber());
			sql.append("','");
			sql.append(bean.getSuprtvisorId());
			sql.append("','");
			sql.append(System.currentTimeMillis());
			sql.append("',");
			sql.append(bean.getApi_type());
			sql.append(",'");
			sql.append(userId);
			sql.append("')");
			
//			String sql = "insert into "
//					+ SupervisorBean.TABLE_NAME
//					+ "( title ,thumb_url ,content ,tip_id ,suprtvisor_url ,suprtvisor_name ,suprtvisor_sex ,suprtvisor_id_card ,phone_number ,suprtvisor_id ,update_time ,api_type_flage ,current_user_id )"
//					+ " values ( " + "'" + bean.getTitle() + "'" + "," + "'"
//					+ bean.getThumb_url() + "'" + "," + "'" + bean.getSummary()
//					+ "'" + "," + bean.getTip_id() + "," + "'"
//					+ bean.getSuprtvisorUrl() + "'" + "," + "'"
//					+ bean.getSuprtvisorName() + "'" + "," + "'"
//					+ bean.getSuprtvisorSex() + "'" + "," + "'"
//					+ bean.getSuprtvisorIdCard() + "'" + "," + "'"
//					+ bean.getPhoneNumber() + "'" + "," + "'"
//					+ bean.getSuprtvisorId() + "'" + "," + "'"
//					+ System.currentTimeMillis() + "" + "'" + ","
//					+ bean.getApi_type() + "," + readTempData("userid") + ")";
			dbm.execSQL(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.close();
		}

	}
	
	/**
	 * 插入监理webview 信息
	 * 
	 * V 6.0版本
	 * 
	 * @return
	 */
	public void inserWebView(WebViewBean viewBean) {
		DataBaseManager	dbm = new DataBaseManager(context);
		String comma = " ,";
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ");
			sql.append(WebViewBean.TABLE_NAME);
			sql.append(" ( ");
			sql.append(WebViewBean.AIP_TYPE);
			sql.append(comma);
			sql.append(WebViewBean.AIP_ID);
			sql.append(comma);
			sql.append(WebViewBean.TITLE);
			sql.append(comma);
			sql.append(WebViewBean.CONTENT);
			sql.append(" ) values ( ");
			sql.append(viewBean.getAip_type());
			sql.append(comma);
			sql.append(viewBean.getAip_id());
			sql.append(" ,'");
			sql.append(viewBean.getTitle());
			sql.append("','");
			sql.append(viewBean.getContent());
			sql.append("' )");
			
//			String sql = "insert into " + WebViewBean.TABLE_NAME
//					+ " ( aip_type ,aip_id ,title ,content )" + " values ( "
//					+ viewBean.getAip_type() + "," + viewBean.getAip_id() + ","
//					+ "'" + viewBean.getTitle() + "'" + "," + "'"
//					+ viewBean.getContent() + "'" + " )";
			dbm.execSQL(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.close();
		}
	}
	
	/**
	 * 查监理webview 信息
	 * 
	 * @return
	 */
	public WebViewBean queryWebView(int aip_id) {
		DataBaseManager	dbm = new DataBaseManager(context);
		WebViewBean viewBean = null;
		Cursor supervisorCur = null;
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(WebViewBean.TABLE_NAME);
		sql.append(" where ");
		sql.append(WebViewBean.AIP_ID);
		sql.append(" = ");
		sql.append(aip_id);
		supervisorCur = dbm.rawQuery(sql.toString());
//				.query("select * from webview_contrnt where aip_id = " + aip_id);
		while (supervisorCur.moveToNext()) {
			viewBean = new WebViewBean();
			viewBean.setTitle(supervisorCur.getString(2));
			viewBean.setContent(supervisorCur.getString(3));
		}
		dbm.close();
		return viewBean;
	}
}
