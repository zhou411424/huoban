package com.example.huoban.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 
 * @author adam.huang
 *
 */
public class ToastUtil {
	public static void showToast(Context context, String message,int gravity){
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(gravity, 0, 0);
		toast.show();
	}
	
	public static void showToast(Context context, int resId,int gravity){
		Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.setGravity(gravity, 0, 0);
		toast.show();
	}
	public static void showToast(Context context, String message){
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public static void showToast(Context context, int resId){
		Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
