package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Blackelect;

public class BlackelectDto {
	private Blackelect blackelect;
	private String plateNum;
	public Blackelect getBlackelect() {
		return blackelect;
	}
	public void setBlackelect(Blackelect blackelect) {
		this.blackelect = blackelect;
	}
	public String getPlateNum() {
		return plateNum;
	}
	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}
	
}
