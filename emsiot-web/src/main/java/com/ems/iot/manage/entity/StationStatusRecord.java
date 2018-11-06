package com.ems.iot.manage.entity;

import java.util.Date;

public class StationStatusRecord {
    private Integer station_status_id;

    private Integer station_phy_num;

    private Integer station_status;
   
    private String update_time;
    
    private String station_name;
    
    private String station_address;

	public Integer getStation_status_id() {
		return station_status_id;
	}

	public void setStation_status_id(Integer station_status_id) {
		this.station_status_id = station_status_id;
	}

	public Integer getStation_phy_num() {
		return station_phy_num;
	}

	public void setStation_phy_num(Integer station_phy_num) {
		this.station_phy_num = station_phy_num;
	}

	public Integer getStation_status() {
		return station_status;
	}

	public void setStation_status(Integer station_status) {
		this.station_status = station_status;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getStation_name() {
		return station_name;
	}

	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}

	public String getStation_address() {
		return station_address;
	}

	public void setStation_address(String station_address) {
		this.station_address = station_address;
	}
    
   
}