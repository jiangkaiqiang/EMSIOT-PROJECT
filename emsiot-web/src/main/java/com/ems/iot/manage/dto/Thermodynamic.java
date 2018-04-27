package com.ems.iot.manage.dto;

public class Thermodynamic {
	private String station_phy_num; // 基站经度
	private String longitude; // 基站经度
	private String latitude; // 基站纬度
	private Integer num; // 基站下当前车辆的数量

	public String getStation_phy_num() {
		return station_phy_num;
	}

	public void setStation_phy_num(String station_phy_num) {
		this.station_phy_num = station_phy_num;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
