package com.ems.iot.manage.entity;

import java.util.Date;

public class AreaAlarm {
    private Integer alarm_id;

    private Integer area_type;

    private String area_name;

    private String enter_plate_num;

    private Date enter_time;

    private Integer process_state;

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

    public Date getEnter_time() {
        return enter_time;
    }

    public void setEnter_time(Date enter_time) {
        this.enter_time = enter_time;
    }

    public Integer getProcess_state() {
        return process_state;
    }

    public void setProcess_state(Integer process_state) {
        this.process_state = process_state;
    }
}