package com.ems.iot.manage.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Barry
 * @date 2018年3月20日下午3:46:22  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class SetUtil
{

	/**
	 * 判断list是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNullList(List<?> list)
	{
		return list == null || list.isEmpty() ? true : false;
	}

	public static String[] listtoArray(List<String> value)
	{
		return value.toArray(new String[value.size()]);
	}

	/**
	 * 判断list是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isnotNullList(List<?> list)
	{
		return list == null || list.isEmpty() ? false : true;
	}

	/**
	 * 判断set是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNullSet(Set<?> set)
	{
		return set == null || set.isEmpty() ? true : false;
	}

	/**
	 * 判断map是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNullMap(Map<?, ?> map)
	{
		return map == null || map.isEmpty() ? true : false;
	}
	
	/**
	 * 判断map是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNotNullMap(Map<?, ?> map)
	{
		return map == null || map.isEmpty() ? false : true;
	}

}
