package com.ems.iot.manage.util;

import java.util.HashMap;

/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Utils 工具类->缓存, 為前台提供重要數據
 * Create on MaQiang 2016-6-25 09:28:36
 */
public class CacheTool {

	private static  HashMap<String,Object> cache=new HashMap<String, Object>();
	
	public static boolean hasCache(String key){
		return CacheTool.cache.containsKey(key);
	}
	public static Object getdate(String key){
		System.err.println("获得缓存数据："+key);
		return CacheTool.cache.get(key);
	}
	public static Object remove(String key){
		return CacheTool.cache.remove(key);
	}
	public static  void setData(String key,Object data){
		System.err.println("缓存大小："+cache.size());
		if(CacheTool.cache.size()>1000){
			CacheTool.clearAllCach();
		}
		CacheTool.cache.put(key,data);
	}
	public static void clearAllCach(){
		CacheTool.cache.clear();
		System.gc();
	}

}