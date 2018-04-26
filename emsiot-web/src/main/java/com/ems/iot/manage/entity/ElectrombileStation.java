package com.ems.iot.manage.entity;

import java.util.Date;

public class ElectrombileStation {
    private Integer ele_sta_id;

    private Integer ele_gua_card_num;

    private Integer station_phy_num;

    private String hard_read_time;

    private String update_time;

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
}