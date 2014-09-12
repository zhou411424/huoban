package com.example.huoban.data;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;

/**
 * DataClass.
 */
public class DataClass {
	/**
	 * getCreateQuery.
	 */
	public String getCreateQuery() {
		return null;
	}

	/**
	 * getInsertQuery.
	 */
	public String getInsertQuery() {
		return null;
	}

	/**
	 * getColumnCount.
	 */
	public int getColumnCount() {
		return 0;
	}

	/**
	 * bind.
	 */
	public void bind(SQLiteStatement statement, String[] data, Context context) {

	}

	/**
	 * bindStringOrNull.
	 */
	protected void bindStringOrNull(SQLiteStatement statement, int index,
			String data) {
		if (data.equals("")) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, data);
		}
	}

	/**
	 * bindLongOrNull.
	 */
	protected void bindLongOrNull(SQLiteStatement statement, int index,
			String data) {
		if (data.equals("")) {
			statement.bindNull(index);
		} else {
			long l = Long.parseLong(data);
			statement.bindLong(index, l);
		}
	}
}
