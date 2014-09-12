package com.example.huoban.utils;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;

import com.example.huoban.constant.NumberConstant;
import com.example.huoban.constant.StringConstant;
import com.google.gson.Gson;

/**
 * 常用工具类
 * 
 * @author adam.huang
 * 
 */
public class Utils {

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {

		boolean wifiConnected = false;
		boolean mobileConnected = false;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected()) {
			wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
		} else {
			wifiConnected = false;
			mobileConnected = false;
		}

		return (wifiConnected | mobileConnected);
	}

	/**
	 * 将一个类转化成json字符串
	 * 
	 * @return
	 */
	public static String objectToJson(Object src) {
		return new Gson().toJson(src);
	}

	/**
	 * 将json字符串转化成一个对象
	 * 
	 * @return
	 */
	public static <T> Object jsonToObject(String jsonStr, Class<T> classOfT) {
		return new Gson().fromJson(jsonStr, classOfT);
	}

	/**
	 * 获得路径
	 */
	public static File getDir(Context context) {
		File fileDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory();
		} else {
			fileDir = context.getFilesDir();
		}
		
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		return fileDir;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean stringIsNotEmpty(String str) {
		if (str != null && !str.trim().equals(StringConstant.EMPTY_DEFAULT)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取本地版本号
	 * 
	 * @return
	 */
	public static int getVersion(Context context) {
		int version = -1;
		try {
			version = context.getPackageManager().getPackageInfo(context.getPackageName(), NumberConstant.ZERO).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取本地版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), NumberConstant.ZERO).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	private static String phoneDevice = null;

	public static String getDeviceId(Context context) {
		if (!Utils.stringIsNotEmpty(phoneDevice)) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			phoneDevice = tm.getDeviceId();
			if (!Utils.stringIsNotEmpty(phoneDevice)) {
				phoneDevice = UUID.randomUUID().toString();
			}
		}

		return phoneDevice;
	}

	/**
	 * wifi方法.
	 * 
	 * @return 0
	 */
	private static WifiInfo getWifiInfo(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo();
	}

	/**
	 * 读取ip地址.
	 * 
	 * @return 0
	 */
	public static String readIp(Context context) {
		WifiInfo info = getWifiInfo(context);
		String ip = "";
		if (info != null) {
			ip = intToIp(info.getIpAddress());
		}
		return ip;
	}

	/**
	 * IP方法.
	 * 
	 * @return 0
	 */
	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	/**
	 * 检查手机号码
	 */
	public static boolean checkIsMobileNumber(String number) {
		if (!isNumber(number)) {
			return false;
		}
		int len = number.length();
		if (len == 1) {
			if (1 == Integer.valueOf(number)) {
				return true;
			} else {
				return false;
			}
		} else if (len == 2) {
			if (number.substring(1, 2).matches("[3458]")) {
				return true;
			} else {
				return false;
			}
		} else if (len == 11) {
			if (number.matches("^13.*|^14.*|^15.*|^18.*")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 输入的是否为数字
	 */
	public static boolean isNumber(String barcode) {
		if (barcode.replaceAll("[^0-9]", "").equals(barcode)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 参数设置
	 * 
	 * @param sb
	 * @param param
	 * @param key
	 * @param value
	 * @return
	 * @throws JSONException
	 */
	public static StringBuffer jsonPut(StringBuffer sb, JSONObject param, String key, String value) throws JSONException {
		param.put(key, value);
		sb.append(key + "=" + value + "&");
		return sb;
	}

	/**
	 * 获取时间戳，定长17位，精确到毫秒
	 * 
	 * @return
	 */
	public static String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		long currentTimeMillis = System.currentTimeMillis();
		Date date = new Date(currentTimeMillis);
		String format = sdf.format(date);
		Log.e("TAG", format);
		return format;
	}

	public static long parseString2Long(String str) {
		long a = 0;
		if (Utils.stringIsNotEmpty(str)) {
			try {
				a = Long.parseLong(str);
			} catch (Exception e) {
			}
		}

		return a;

	}

	/**
	 * 获取转换日期格式.
	 */
	public static String getFormatDate(SimpleDateFormat sdfDate, String time) {
		if (time.equals("") || time.equals("0")) {
			return "";
		}

		if (time.length() < 13) {

			time = time + "000";
		}

		long endDate = Long.parseLong(time);
		Date date = new Date(endDate);
		String defaultTime = sdfDate.format(date);
		return defaultTime;
	}

	/**
	 * 金额格式化
	 * 
	 * @param param
	 * @return
	 */
	public static String StringIoDouble(String param) {
		if (param == null || param.equals("null") || param.equals("")) {
			param = "";
			return param;
		}
		double amount = Double.parseDouble(param);
		DecimalFormat df = new DecimalFormat("0.00");
		param = df.format(amount);
		return param;
	}

	/**
	 * 设置imageview高宽
	 * 
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public static void resetViewSize(final View view, int width, int height) {
		LayoutParams lp = view.getLayoutParams();
		lp.width = width;
		lp.height = height;
		view.setLayoutParams(lp);
	}

	public static void resetViewSize(final View view, int height) {
		LayoutParams lp = view.getLayoutParams();
		lp.height = height;
		view.setLayoutParams(lp);
	}

	/**
	 * 获取屏幕宽度.
	 */
	public static int getDeviceWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕高度.
	 */
	public static int getDeviceHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取屏幕高度.
	 */
	public static int getDeviceDensity(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	public static boolean checkPhone(String number) {
		if (!isNumber(number)) {
			return false;
		}
		if (number.length() != 11) {
			return false;
		} else {
			if (number.matches("^13.*|^14.*|^15.*|^18.*")) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideInputKeyboard(Activity instance) {
		InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			View focus = instance.getCurrentFocus();
			if (focus != null) {
				IBinder ibinder = focus.getWindowToken();
				if (ibinder != null) {
					imm.hideSoftInputFromWindow(ibinder, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
	}

	public static final boolean checkNumber(String number) {
		if (number == null)
			return false;
		// 表达式的功能：验证必须为数字（整数或小数）
		String pattern = "[0-9]+(.[0-9]+)?";
		// 对()的用法总结：将()中的表达式作为一个整体进行处理，必须满足他的整体结构才可以。 (.[0-9]+)? ：表示()中的整体出现一次或一次也不出现
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(number);
		return m.matches();
	}
}
