package com.example.huoban.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DataBaseManager {

	private SQLiteDatabase sqLiteDatabase;
	private DBHelper dbHelper;
	private String msTable;

	public DataBaseManager(Context context) {
		dbHelper = new DBHelper(context);
	}

	public SQLiteDatabase open(boolean bWritable) {
		if (sqLiteDatabase != null) {
			if (sqLiteDatabase.isReadOnly() && bWritable) {
				return null;
			}

			return sqLiteDatabase;
		}

		if (bWritable) {
			sqLiteDatabase = dbHelper.getWritableDatabase();
		} else {
			sqLiteDatabase = dbHelper.getReadableDatabase();
		}

		return sqLiteDatabase;
	}

	public DBHelper getDBHelper(boolean bWritable) {
		if (sqLiteDatabase != null) {
			if (sqLiteDatabase.isReadOnly() && bWritable) {
				return null;
			}

			return dbHelper;
		}

		if (bWritable) {
			sqLiteDatabase = dbHelper.getWritableDatabase();
		} else {
			sqLiteDatabase = dbHelper.getReadableDatabase();
		}

		return dbHelper;
	}

	public void beginTransaction() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.beginTransaction();
		}
	}

	public void setTransactionSuccessful() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.setTransactionSuccessful();
		}
	}

	public void endTransaction() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.endTransaction();
		}
	}

	// 插入数据
	public long insert(ContentValues values) {
		try {
			long id = sqLiteDatabase.insert(msTable, null, values);
			return id;
		} catch (SQLiteException e) {
			return 0;
		}
	}

	// 插入数据
	public long insert(String table, ContentValues values) {
		return sqLiteDatabase.insert(table, null, values);
	}

	// 插入数据
	public void insert(String table, ContentValues[] values) {
		for (int i = 0; i < values.length; i++) {
			insert(table, values[i]);
		}
	}

	// 查询数据
	public Cursor query(String[] columns, String selection, String[] selectionArgs, String sortOrder, String limit) {

		try {
			// 设置order by参数
			Cursor cursor = sqLiteDatabase.query(msTable, columns, selection, selectionArgs, null, null, sortOrder, limit);

			return cursor;
		} catch (SQLiteException e) {
			return null;
		}
	}

	// 修改数据
	public int update(ContentValues values, String selection, String[] selectionArgs) {
		try {
			return sqLiteDatabase.update(msTable, values, selection, selectionArgs);
		} catch (SQLiteException e) {
			return 0;
		}
	}

	// 删除数据
	public int delete(String selection, String[] selectionArgs) {

		try {
			return sqLiteDatabase.delete(msTable, selection, selectionArgs);
		} catch (SQLiteException e) {
			return 0;
		}
	}

	// 删除数据
	public int delete(String table, String selection, String[] selectionArgs) {

		try {
			return sqLiteDatabase.delete(table, selection, selectionArgs);
		} catch (SQLiteException e) {
			return 0;
		}
	}

	public void setTable(String sTable) {
		msTable = sTable;
	}

	public void close() {
		if (sqLiteDatabase != null) {
			try {
				sqLiteDatabase.close();
			} catch (Exception e) {
			} finally {
				sqLiteDatabase = null;
			}
		}
	}

	/**
	 * execSQL.
	 * 
	 * @param sql
	 *            param
	 */
	public void execSQL(String sql) {
		this.open(false).execSQL(sql);
	}

	public Cursor rawQuery(String sql) {
		return this.open(false).rawQuery(sql, null);
	}

	/**
	 * 操作数据库
	 * 
	 * @param dbOperaterInterFace
	 */
	public static void operateDataBase(DBOperaterInterFace dbOperaterInterFace, DbParamData dbParamData) {

		if (dbOperaterInterFace == null || dbParamData == null) {
			return;
		}
		Object[] object = { dbOperaterInterFace, dbParamData };
		new DataBaseTask().execute(object);
	}

}
