package com.example.huoban.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

import com.example.huoban.constant.StringConstant;

/**
 * 时间格式化工具类
 * 
 * @author albel.lei
 *
 */

public class TimeFormatUtils {

	public static final SimpleDateFormat Y_M_D = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	public static final SimpleDateFormat M_D = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
	public static final SimpleDateFormat Y_M_D_H_M = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
	public static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	public static final SimpleDateFormat sdfDateB = new SimpleDateFormat("MM.dd", Locale.ENGLISH);
	public static final SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	public static final SimpleDateFormat hmDate = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	public static final SimpleDateFormat smdDate = new SimpleDateFormat("yyyy" + "年" + "MM" + "月" + "dd" + "日" + " HH:mm", Locale.ENGLISH);

	public synchronized static String formatLongToDate(String time) {
		if (!Utils.stringIsNotEmpty(time)) {
			return StringConstant.EMPTY_DEFAULT;
		}

		long milliseconds = 10;
		try {
			milliseconds = Long.parseLong(time);
		} catch (Exception e) {

		}

		Date date = new Date(milliseconds * 1000);

		return M_D.format(date);

	}

	public synchronized static String formatLongToDateYMD(String time) {
		if (!Utils.stringIsNotEmpty(time)) {
			return StringConstant.EMPTY_DEFAULT;
		}

		long milliseconds = 10;
		try {
			milliseconds = Long.parseLong(time);
		} catch (Exception e) {

		}

		Date date = new Date(milliseconds * 1000);

		return Y_M_D.format(date);

	}

	public synchronized static String formatToMD(String time) {

		try {
			Date date = Y_M_D_H_M.parse(time);
			return M_D.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return StringConstant.EMPTY_DEFAULT;
	}

	public synchronized static String timestamp2String(String strDate) {
		if (strDate.equals("")) {
			return "";
		}

		String dateTime = "";
		Date date = null;

		try {
			date = sdfDate.parse(strDate);
			dateTime = sdfDate.format(date);
			dateTime = dateTime.substring(5, dateTime.length());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (date == null) {
			return null;
		}
		return dateTime;
	}

	public synchronized static long formatYMD2Seconds(String time) {
		if (!Utils.stringIsNotEmpty(time)) {
			return 0;
		}

		long milliseconds = 0;
		try {
			Date date = Y_M_D.parse(time);
			milliseconds = date.getTime();
		} catch (Exception e) {

		}

		return milliseconds / 1000;

	}

	public synchronized static long getNowDateSeconds() {
		long milliseconds = 0;
		try {
			Date date = Y_M_D.parse(Y_M_D.format(new Date()));
			milliseconds = date.getTime();
		} catch (Exception e) {

		}

		return milliseconds / 1000;

	}

	public synchronized static String FormatSeconds2Days(long second, long alginSecond) {

		int day = (int) ((Math.abs(alginSecond - second)) / (3600 * 24));
		return String.valueOf(day);

	}

	public synchronized static String formatSecondToTime(String a) {

		String time = null;
		long l = 0;
		try {
			l = Long.valueOf(a + "000");
		} catch (Exception e) {

		}

		Date date = new Date(l);
		time = Y_M_D_H_M.format(date);
		return time;

	}

	public synchronized static String jiSuanAll(String endTime) {
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
			// long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60
			// * 1000 - min * 60 * 1000 - s * 1000);
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

	/**
	 * 
	 * 是否为今天 请求的
	 * 
	 * @param sdfDate
	 * @param time
	 * @return
	 */

	public synchronized static String isTodayTime(String time) {
		if (time == null || "".equals(time)) {
			return "";
		}
		Date date = null;
		try {
			date = sdfFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar yesterday = Calendar.getInstance(); // 昨天

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		current.setTime(date);

		if (current.after(today)) {
			return getFormatDate(hmDate, stringTimestamp(time));
		} else {
			// int index = time.indexOf("-")+1;
			// return time.substring(index, time.length());
			return getFormatDate(smdDate, stringTimestamp(time));
		}
	}

	/**
	 * java 时间戳
	 */
	public synchronized static String stringTimestamp(String dateString) {
		if (dateString.equals("")) {
			return "";
		}
		Date date = null;
		try {
			date = sdfFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date == null) {
			return null;
		}
		long temp = date.getTime();// JAVA的时间戳长度是13位
		return temp + "";
	}

	/**
	 * 获取转换日期格式.
	 */
	public synchronized static String getFormatDate(SimpleDateFormat sdfDate, String time) {
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

	public synchronized static String[] formatAlumDate(String time) {

		String[] times = new String[3];

		if (!Utils.stringIsNotEmpty(time)) {
			return times;
		}

		long milliseconds = 10;
		try {
			milliseconds = Long.parseLong(time);
		} catch (Exception e) {

		}

		Date date = new Date(milliseconds * 1000);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		times[0] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		times[1] = MONTH_CHINESE[calendar.get(Calendar.MONTH)] + "月";
		times[2] = hmDate.format(date);
		return times;
	}

	public synchronized static String[] formatPushDate(String time) {

		String[] times = new String[2];

		if (!Utils.stringIsNotEmpty(time)) {
			return times;
		}

		long milliseconds = 10;
		try {
			milliseconds = Long.parseLong(time);
		} catch (Exception e) {

		}

		Date date = new Date(milliseconds * 1000);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		times[0] = sdfDateB.format(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		times[1] = StringConstant.XINQI + WEEK_CHINESE[week];
		return times;
	}

	private static final String[] MONTH_CHINESE = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };
	private static final String[] WEEK_CHINESE = { "", "日", "一", "二", "三", "四", "五", "六" };

	public static String getFriendlyDate(String seconds) {
		
		if(seconds==null){
			return "";
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		long tmp = Long.parseLong(seconds + "000");
		long time = (System.currentTimeMillis() - tmp) / 1000;

		if (time >= 259200) {
			return df.format(new Date(tmp));
		}

		if (time >= 172800) {
			return "前天";
		}

		if (time >= 86400) {
			return "昨天";
		}
		if (time >= 3600) {
			return (time / 3600) + "小时前";
		}
		if (time >= 1800) {
			return "30分钟前";
		}
		if (time >= 900) {
			return "15分钟前";
		}
		if (time >= 600) {
			return "10分钟前";
		}
		if (time >= 300) {
			return "5分钟前";
		}
		if (time >= 60) {
			return (time / 60) + "分钟前";
		}
		if (time > 30) {
			return "30秒前";
		}
		return "刚刚";
	}
	
	/**
	 *將日期转换为毫秒
	 *@param date
	 * */
	@SuppressLint("SimpleDateFormat")
	public static String getDateToHm(String date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return String.valueOf(simpleDateFormat.parse(date).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;  
		}
	}
}
