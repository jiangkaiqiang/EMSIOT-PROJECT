package com.ems.iot.manage.dto;

/**
 * @author Barry
 * @date 2018年3月20日下午3:37:02  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class ResultDto {

	private int status;

	private String message;
	
	private boolean success;
	
	private Object data;

	public ResultDto() {

	}

	public ResultDto(int status, String message) {
		this.status = status;
		this.message = message;
		this.success = true;
	}
	
	

	public ResultDto(int status, String message, boolean success) {
		super();
		this.status = status;
		this.message = message;
		this.success = success;
	}
	

	public ResultDto(Object data) {
		super();
		this.status = 1;
		this.message = "查询成功";
		this.success = true;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
