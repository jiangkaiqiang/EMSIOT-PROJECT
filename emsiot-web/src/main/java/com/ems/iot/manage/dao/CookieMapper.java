package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Cookies;

/**
 * @author Barry
 * @date 2018年3月20日下午3:34:24  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface CookieMapper {

	public void insertCookie(Cookies cookieEntity);
	
	public void deleteCookie(@Param("cookie") String cookie);
	
	public Cookies findEffectiveCookie(@Param("cookie") String cookie);
}
