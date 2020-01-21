package com.rzb.pms.utils;

public class BeanUtil {

	public static String stripTrailingZero(String s) {

		return s.replaceAll("()\\.0+$|(\\..+?)0+$", "$2");

	}

}
