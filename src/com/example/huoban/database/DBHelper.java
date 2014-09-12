package com.example.huoban.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.huoban.data.DataClass;
import com.example.huoban.data.Discusses;
import com.example.huoban.data.SupervisorBean;
import com.example.huoban.data.WebViewBean;

/**
 * 数据库帮助类，继承SQLiteOpenHelper
 * 
 * @author Administrator
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private Context mContext;

	private static final String[] mTables = { Discusses.class.getName(), 
			SupervisorBean.class.getName(), WebViewBean.class.getName() };

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.mContext = context;
	}

	public DBHelper(Context context) {
		this(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DBConstant.createTables(db);
		try {
			Log.i("TURNTO", "createDataBase");
			for (int i = 0; i < mTables.length; i++) {
				Class<?> columns = Class.forName(mTables[i]);
				DataClass dc = (DataClass) columns.newInstance();
				db.execSQL(dc.getCreateQuery());
			}

		} catch (ClassNotFoundException e) {
			Log.e(mContext.getClass().toString(), e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e(mContext.getClass().toString(), e.getMessage());
		} catch (InstantiationException e) {
			Log.e(mContext.getClass().toString(), e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
