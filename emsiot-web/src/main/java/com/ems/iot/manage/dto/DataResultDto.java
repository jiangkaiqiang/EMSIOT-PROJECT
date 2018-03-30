package com.ems.iot.manage.dto;

import java.util.Date;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:46  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class DataResultDto {

	private String status;

	private String time;

	public DataResultDto(int status) {
		this.status = Integer.toString(status);
		this.time = Long.toString(new Date().getTime() / 1000);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
