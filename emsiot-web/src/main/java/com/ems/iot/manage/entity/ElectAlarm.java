package com.ems.iot.manage.entity;

import java.util.Date;

public class ElectAlarm {
    private Integer elect_alarm_id;

    private Integer alarm_gua_card_num;

    private Integer alarm_station_phy_num;

    private String alarm_time;
    
    
    //附加信息
    private String plate_num;
    private String station_name;
    private String longitude;
    private String latitude;
    
    private String station_address;
	private String owner_tele;
	private String owner_name;
	public Integer getElect_alarm_id() {
		return elect_alarm_id;
	}
	public void setElect_alarm_id(Integer elect_alarm_id) {
		this.elect_alarm_id = elect_alarm_id;
	}
	public Integer getAlarm_gua_card_num() {
		return alarm_gua_card_num;
	}
	public void setAlarm_gua_card_num(Integer alarm_gua_card_num) {
		this.alarm_gua_card_num = alarm_gua_card_num;
	}
	public Integer getAlarm_station_phy_num() {
		return alarm_station_phy_num;
	}
	public void setAlarm_station_phy_num(Integer alarm_station_phy_num) {
		this.alarm_station_phy_num = alarm_station_phy_num;
	}
	public String getAlarm_time() {
		return alarm_time;
	}
	public void setAlarm_time(String alarm_time) {
		this.alarm_time = alarm_time;
	}
	public String getPlate_num() {
		return plate_num;
	}
	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
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
	
	public String getStation_address() {
		return station_address;
	}
	public void setStation_address(String station_address) {
		this.station_address = station_address;
	}
	public String getOwner_tele() {
		return owner_tele;
	}
	public void setOwner_tele(String owner_tele) {
		this.owner_tele = owner_tele;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}


    
}