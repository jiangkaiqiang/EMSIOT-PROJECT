package com.ems.iot.manage.dto;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:57  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class NgRemoteValidateDTO {
	private boolean isValid;
	private String value;
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
