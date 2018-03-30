package com.ems.iot.manage.entity;

import java.util.Date;

/**
 * @author Barry
 * @date 2018年3月20日下午3:37:43  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class Cookies {

	private int id;

	private String cookie;

	private String username;

	private int expireTime;

	private Date addTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
}
