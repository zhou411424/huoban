package com.example.huoban.utils;

import android.util.Log;

public class LogUtil {
	private static final String TAG = "resultInfoLog";
	private static boolean doLog = true;

	public static void logE(String msg) {
		if (doLog) {
			Log.e(TAG, msg + "");
		}
	}

	public static void logE(String tag, String msg) {
		if (doLog) {
			Log.e(tag, msg + "");
		}
	}

	public static void logI(String tag, String msg) {
		if (doLog) {
			Log.i(tag, msg + "");
		}
	}

	public static void logI(String msg) {
		if (doLog) {
			Log.i(TAG, msg + "");
		}
	}
}
