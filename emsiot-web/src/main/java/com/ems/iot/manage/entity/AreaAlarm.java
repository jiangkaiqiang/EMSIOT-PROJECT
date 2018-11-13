package com.ems.iot.manage.entity;

import java.util.Date;

public class AreaAlarm {
    private Integer alarm_id;

    private Integer area_type;

    private String area_name;

    private String enter_plate_num;

    private String enter_time;

    private Integer process_state;
    
    private Integer alarm_station_phy_num;
    
    
    //基站名称
    private String station_name;
    //车主
    private String owner_name;
    //车主手机号
    private String owner_tele;

    public Integer getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(Integer alarm_id) {
        this.alarm_id = alarm_id;
    }

    public Integer getArea_type() {
        return area_type;
    }

    public void setArea_type(Integer area_type) {
        this.area_type = area_type;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name == null ? null : area_name.trim();
    }

    public String getEnter_plate_num() {
        return enter_plate_num;
    }

    public void setEnter_plate_num(String enter_plate_num) {
        this.enter_plate_num = enter_plate_num == null ? null : enter_plate_num.trim();
    }

    public String getEnter_time() {
        return enter_time;
    }

    public void setEnter_time(String enter_time) {
        this.enter_time = enter_time;
    }

    public Integer getProcess_state() {
        return process_state;
    }

    public void setProcess_state(Integer process_state) {
        this.process_state = process_state;
    }

	public Integer getAlarm_station_phy_num() {
		return alarm_station_phy_num;
	}

	public void setAlarm_station_phy_num(Integer alarm_station_phy_num) {
		this.alarm_station_phy_num = alarm_station_phy_num;
	}

	

	public String getStation_name() {
		return station_name;
	}

	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getOwner_tele() {
		return owner_tele;
	}

	public void setOwner_tele(String owner_tele) {
		this.owner_tele = owner_tele;
	}
    
}