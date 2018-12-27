package com.ems.iot.manage.entity;

import java.util.Date;

public class ElectrombileStation {
    private Integer ele_sta_id;

    private Integer ele_gua_card_num;

    private Integer station_phy_num;

    private String hard_read_time;

    private String update_time;
    
    ///////////////////////车辆信息
    private String owner_name;
    
    private String plate_num;
    
    private String station_name;
    private String longitude;
    private String latitude;
    private String station_address;

    public Integer getEle_sta_id() {
        return ele_sta_id;
    }

    public void setEle_sta_id(Integer ele_sta_id) {
        this.ele_sta_id = ele_sta_id;
    }

    public Integer getEle_gua_card_num() {
        return ele_gua_card_num;
    }

    public void setEle_gua_card_num(Integer ele_gua_card_num) {
        this.ele_gua_card_num = ele_gua_card_num;
    }

    public Integer getStation_phy_num() {
        return station_phy_num;
    }

    public void setStation_phy_num(Integer station_phy_num) {
        this.station_phy_num = station_phy_num;
    }

    public String getHard_read_time() {
        return hard_read_time;
    }

    public void setHard_read_time(String hard_read_time) {
        this.hard_read_time = hard_read_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
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
    
    
}