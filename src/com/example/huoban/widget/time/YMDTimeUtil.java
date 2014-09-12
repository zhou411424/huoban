package com.example.huoban.widget.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.huoban.R;
import com.example.huoban.utils.Utils;

/**
 * @author albel.lei
 */
public class YMDTimeUtil extends AlertDialog implements OnClickListener {
	private final static int ANIMAL_STAR_YEAR = 1804;// 定义生肖起始年，1804年为甲子年属鼠
	public static int START_YEAR = 1900, END_YEAR = 2100;
	private final OnDateTimeSetListener mCallBack;
	private final Calendar mCalendar;
	private int curr_year, curr_month, curr_day;
	// , curr_hour, curr_minute;
	// 添加大小月月份并将其转换为list,方便之后的判断
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };
	final WheelView wv_year, wv_month, wv_day;
	// wv_hours, wv_mins;
	final List<String> list_big, list_little;

	public YMDTimeUtil(Context context, String initStr, OnDateTimeSetListener callBack) {
		this(context, START_YEAR, END_YEAR, initStr, callBack);
	}

	public YMDTimeUtil(Context context, OnDateTimeSetListener callBack) {
		this(context, START_YEAR, END_YEAR, null, callBack);
	}

	public YMDTimeUtil(Context context, final int START_YEAR, final int END_YEAR, String initStr, OnDateTimeSetListener callBack) {
		super(context);
		this.START_YEAR = START_YEAR;
		mCalendar = Calendar.getInstance();

		if (Utils.stringIsNotEmpty(initStr)) {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).parse(initStr);
				mCalendar.setTime(date);
			} catch (ParseException e) {
			}

		}
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		int day = mCalendar.get(Calendar.DATE);

		this.END_YEAR = END_YEAR;
		mCallBack = callBack;

		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);
		setButton(context.getText(R.string.ok), this);
		setButton2(context.getText(R.string.cancel), (OnClickListener) null);
		setIcon(R.drawable.huoban);
		setTitle(context.getResources().getString(R.string.time_choise));
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.birth_time_layout, null);

		int textSize = 0;
		textSize = adjustFontSize(getWindow().getWindowManager());
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel(context.getResources().getString(R.string.year));// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel(context.getResources().getString(R.string.month));
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel(context.getResources().getString(R.string.day));
		wv_day.setCurrentItem(day - 1);
		// // 时
		// wv_hours = (WheelView) view.findViewById(R.id.hour);
		// wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		// wv_hours.setCyclic(true);
		// wv_hours.setCurrentItem(hour);
		//
		// // 分
		// wv_mins = (WheelView) view.findViewById(R.id.mins);
		// wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		// wv_mins.setCyclic(true);
		// wv_mins.setCurrentItem(minute);
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				// if(year_num==START_YEAR)
				// {
				// wv_year.setCyclic(false);
				// }
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0) || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.TEXT_SIZE = textSize;
		// wv_hours.TEXT_SIZE = textSize;
		// wv_mins.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		setView(view);
	}

	public void onClick(DialogInterface dialog, int which) {

		curr_year = wv_year.getCurrentItem() + START_YEAR;
		curr_month = wv_month.getCurrentItem() + 1;
		curr_day = wv_day.getCurrentItem() + 1;
		// curr_hour = wv_hours.getCurrentItem();
		// curr_minute = wv_mins.getCurrentItem();
		if (mCallBack != null) {
			mCallBack.onDateTimeSet(curr_year, curr_month, curr_day);
		}
	}

	public void show() {
		super.show();
	}

	public interface OnDateTimeSetListener {
		void onDateTimeSet(int year, int monthOfYear, int dayOfMonth);
	}

	public static int adjustFontSize(WindowManager windowmanager) {

		int screenWidth = windowmanager.getDefaultDisplay().getWidth();
		// int screenHeight = windowmanager.getDefaultDisplay().getHeight();
		/*
		 * DisplayMetrics dm = new DisplayMetrics(); dm =
		 * windowmanager.getApplicationContext
		 * ().getResources().getDisplayMetrics(); int widthPixels =
		 * dm.widthPixels; int heightPixels = dm.heightPixels; float density =
		 * dm.density; fullScreenWidth = (int)(widthPixels * density);
		 * fullScreenHeight = (int)(heightPixels * density);
		 */
		
		int size =28;
		if (screenWidth <= 240) { // 240X320 屏幕
			return size+2;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			return size+4;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			return size+14;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			return size+16;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return size+20;
		} else { // 大于 800X1280
			return size+22;
		}
	}


	/** 获取当前年份与起始年之间的差值 **/
	public static int subtractYear(int year) {
		int jiaziYear = ANIMAL_STAR_YEAR;
		if (year < jiaziYear) {
			/**
			 * 如果年份小于起始的甲子年(startYear = 1804),则起始甲子年往前偏移 60年一个周期
			 */
			jiaziYear = jiaziYear - (60 + 60 * ((jiaziYear - year) / 60));
		}
		return year - jiaziYear;
	}
}
