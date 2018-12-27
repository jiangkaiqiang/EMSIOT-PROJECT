package com.ems.iot.manage.entity;


public class PeopleStation {
    private Integer peo_sta_id;

    private Integer people_gua_card_num;

    private Integer station_phy_num;

    private String hard_read_time;

    private String update_time;

    //附加信息
    private String people_name;
    private String station_name;
    private String longitude;
    private String latitude;
    private String people_tele;

    public Integer getPeo_sta_id() {
		return peo_sta_id;
	}

	public void setPeo_sta_id(Integer peo_sta_id) {
		this.peo_sta_id = peo_sta_id;
	}

	public Integer getPeople_gua_card_num() {
		return people_gua_card_num;
	}

	public void setPeople_gua_card_num(Integer people_gua_card_num) {
		this.people_gua_card_num = people_gua_card_num;
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

	public String getPeople_name() {
		return people_name;
	}

	public void setPeople_name(String people_name) {
		this.people_name = people_name;
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

	public String getStation_name() {
		return station_name;
	}

	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}

	public String getPeople_tele() {
		return people_tele;
	}

	public void setPeople_tele(String people_tele) {
		this.people_tele = people_tele;
	}
    
}