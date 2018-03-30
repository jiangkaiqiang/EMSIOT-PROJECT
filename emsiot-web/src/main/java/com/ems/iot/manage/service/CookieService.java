package com.ems.iot.manage.service;

import com.ems.iot.manage.entity.Cookies;

/**
 * @author Barry
 * @date 2018年3月20日下午3:44:20  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface CookieService {

	static int EXPIERD_TIME = 60;

	public String insertCookie(String username);

	public Cookies findEffectiveCookie(String cookie);

	public void deleteCookie(String cookie);
}
