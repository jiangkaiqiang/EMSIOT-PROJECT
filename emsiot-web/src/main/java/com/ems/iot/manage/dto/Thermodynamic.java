package com.ems.iot.manage.dto;

public class Thermodynamic {
	private String lng; // 基站经度
	private String lat; // 基站纬度
	private Integer count; // 基站下当前车辆的数量
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
