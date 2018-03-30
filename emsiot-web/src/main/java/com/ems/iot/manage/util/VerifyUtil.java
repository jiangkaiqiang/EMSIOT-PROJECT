package com.ems.iot.manage.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author Barry
 * @date 2018年3月20日下午3:47:09  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class VerifyUtil {
	static boolean flag = false;
	static String regex = "";

	public static boolean check(String str, String regex) {
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static boolean checkCellphone(String cellphone) {
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
		return check(cellphone, regex);
	}
	
	public static boolean checkRemark(String remark) {
		if (remark.length()>250) {
			return false;
		}
		return true;
	}
}
