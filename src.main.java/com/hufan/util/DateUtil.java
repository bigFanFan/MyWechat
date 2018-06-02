package com.hufan.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Date convertString2Date(String datestr) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date = null;
		try {
			date = dateformat.parse(datestr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date convertString2Date(String str, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static String convertDate2String(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat formatter = null;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.format(date);
		} catch (Exception e) {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
		}
		return formatter.format(date);
	}

	public static String convertDate2String(Date date, String format) {
		if (date == null)
			return "";
		SimpleDateFormat formatter = null;
		try {
			formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		} catch (Exception e) {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
		}
		return formatter.format(date);
	}

	public static Long getCurMis() {
		return Long.valueOf(System.currentTimeMillis());
	}
	
	public static Date dayAdd(int day){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	} 
	
}