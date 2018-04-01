package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Electrombile;

public class ElectrombileDto {
	private Electrombile electrombile;
	private String provinceName;
	private String cityName;
	private String areaName;
	private String recordName;
	public Electrombile getElectrombile() {
		return electrombile;
	}
	public void setElectrombile(Electrombile electrombile) {
		this.electrombile = electrombile;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getRecordName() {
		return recordName;
	}
	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}
	
}
