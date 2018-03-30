package com.ems.iot.manage.dto;

import java.util.Date;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:41  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class BaseDto {

	private int status;

	private long time;

	public BaseDto(int status) {
		this.status = status;
		this.time = new Date().getTime() / 1000;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
