package com.ems.iot.manage.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ems.iot.manage.dao.CookieMapper;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.EncodeUtil;

/**
 * @author Barry
 * @date 2018年3月20日下午3:44:53  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Service
public class CookieServiceImpl implements CookieService {

	@Autowired
	private CookieMapper cookieDao;

	@Override
	public String insertCookie(String username) {
		Date date = new Date();
		Cookies cookieEntity = new Cookies();

		String encode = EncodeUtil.encode("sha1", String.format("%s%s", username, date.getTime()));
		cookieEntity.setUsername(username);
		cookieEntity.setCookie(encode);
		cookieEntity.setExpireTime(EXPIERD_TIME);
		cookieDao.insertCookie(cookieEntity);

		return encode;
	}

	@Override
	public Cookies findEffectiveCookie(String cookie) {
		return cookieDao.findEffectiveCookie(cookie);
	}

	@Override
	public void deleteCookie(String cookie) {
		cookieDao.deleteCookie(cookie);
	}

}
