package com.ems.iot.manage.dto;

import java.util.List;

import com.ems.iot.manage.entity.Station;

public class ElectLockTraceDto {

	private String plateNum;
	
	private String lockTime;
	
	private List<Station> station;

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getLockTime() {
		return lockTime;
	}

	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}

	public List<Station> getStation() {
		return station;
	}

	public void setStation(List<Station> station) {
		this.station = station;
	}
	
	
}
