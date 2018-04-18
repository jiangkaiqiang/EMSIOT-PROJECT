package com.ems.iot.manage.entity;

import java.util.Date;

public class ElectAlarm {
    private Integer elect_alarm_id;

    private Integer alarm_gua_card_num;

    private Integer alarm_station_phy_num;

    private Date alarm_time;

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

    public Date getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(Date alarm_time) {
        this.alarm_time = alarm_time;
    }
}