package com.ems.iot.manage.util;
import javax.servlet.http.HttpServletRequest;
/**
 * @author Barry
 * @date 2018年3月20日下午3:46:06  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class SessionUtil
{

	public static int getSellUid(HttpServletRequest request)
	{
		Object selluid = request.getSession().getAttribute("selluid");
		if(selluid!=null){
		  return Integer.parseInt(selluid.toString());
		}
		return 0;
	}
	public static void removeSessionAttbuter(HttpServletRequest request,String key)
	{
		request.getSession().removeAttribute(key);
	}
	public static void setSessionAttbuter(HttpServletRequest request,String key, Object value)
	{
		request.getSession().setAttribute(key, value);
	}

	public static Object getSessionAttbuter(HttpServletRequest request,String key)
	{
		return request.getSession().getAttribute(key);
	}
	public static String getRootpath(HttpServletRequest request)
	{
		return request.getSession().getServletContext().getRealPath("");
	}
}
