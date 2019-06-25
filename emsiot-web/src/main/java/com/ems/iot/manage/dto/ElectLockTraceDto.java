package com.ems.iot.manage.dto;

import java.util.List;

import com.ems.iot.manage.entity.Station;

public class ElectLockTraceDto {

	private String plateNum;
	
	private String lockTime;
	
	private List<TraceStationDto> traceStation;

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

	public List<TraceStationDto> getTraceStation() {
		return traceStation;
	}

	public void setTraceStation(List<TraceStationDto> traceStation) {
		this.traceStation = traceStation;
	}

	
	
	
	
}
