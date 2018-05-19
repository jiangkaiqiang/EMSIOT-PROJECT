package com.ems.iot.manage.dto;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:37  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class AppResultDto {
	private boolean success;
	private int code;//状态码：（1001：成功并返回数据；2001：成功但返回数据为空；3001：请求失败；4001：token失效）
	private String message;
	private Object data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public AppResultDto(boolean success, int code, String message, Object data) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}
	public AppResultDto(int code,String message,boolean success) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
	}
	public AppResultDto(int code, String message) {
		super();
		this.code = code;
		this.message = message;
		this.success = true;
	}
	public AppResultDto(Object data) {
		super();
		this.data = data;
		this.success = true;
		this.message = "查询成功";
		this.code = 1001;
	}
	
}
