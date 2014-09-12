package com.example.huoban.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity工具类
 * @author cwchun.chen
 *
 */
public class ActivityUtils {
	
	/**
	 * 打开其他页面
	 * 
	 * @param that
	 * @param clazz
	 */
	public static void gotoOtherActivity(Activity that,
			Class<? extends Activity> clazz) {
		Intent intent = new Intent(that, clazz);
		that.startActivity(intent);
	}

	/**
	 * 打开其他页面，附带额外数据
	 * 
	 * @param that
	 * @param clazz
	 * @param extra
	 */
	public static void gotoOtherActivity(Activity that,
			Class<? extends Activity> clazz, Bundle extra) {
		Intent intent = new Intent(that, clazz);
		intent.putExtras(extra);
		that.startActivity(intent);
	}

	/**
	 * 打开一个新的页面，并可以获取从该页面返回的数据
	 * 
	 * @param that
	 * @param clazz
	 * @param requestCode
	 */
	public static void gotoOtherActivityForResult(Activity that,
			Class<? extends Activity> clazz, int requestCode) {
		Intent intent = new Intent(that, clazz);
		that.startActivityForResult(intent, requestCode);
	}

	/**
	 * 附带额外数据，打开一个新的页面，并可以获取从该页面返回的数据
	 * 
	 * @param that
	 * @param clazz
	 * @param extra
	 * @param requestCode
	 */
	public static void gotoOtherActivityForResult(Activity that,
			Class<? extends Activity> clazz, Bundle extra, int requestCode) {
		Intent intent = new Intent(that, clazz);
		intent.putExtras(extra);
		that.startActivityForResult(intent, requestCode);
	}
}
