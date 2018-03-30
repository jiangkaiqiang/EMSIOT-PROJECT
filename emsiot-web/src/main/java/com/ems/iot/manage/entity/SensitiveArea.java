package com.ems.iot.manage.entity;

import java.util.Date;

public class SensitiveArea {
    private Integer sensitive_area_id;

    private String sensitive_area_name;

    private String station_ids;

    private Date add_time;

    private String black_list_elects;

    private Date sens_start_time;

    private Date sens_end_time;

    private Integer enter_num;

    public Integer getSensitive_area_id() {
        return sensitive_area_id;
    }

    public void setSensitive_area_id(Integer sensitive_area_id) {
        this.sensitive_area_id = sensitive_area_id;
    }

    public String getSensitive_area_name() {
        return sensitive_area_name;
    }

    public void setSensitive_area_name(String sensitive_area_name) {
        this.sensitive_area_name = sensitive_area_name == null ? null : sensitive_area_name.trim();
    }

    public String getStation_ids() {
        return station_ids;
    }

    public void setStation_ids(String station_ids) {
        this.station_ids = station_ids == null ? null : station_ids.trim();
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public String getBlack_list_elects() {
        return black_list_elects;
    }

    public void setBlack_list_elects(String black_list_elects) {
        this.black_list_elects = black_list_elects == null ? null : black_list_elects.trim();
    }

    public Date getSens_start_time() {
        return sens_start_time;
    }

    public void setSens_start_time(Date sens_start_time) {
        this.sens_start_time = sens_start_time;
    }

    public Date getSens_end_time() {
        return sens_end_time;
    }

    public void setSens_end_time(Date sens_end_time) {
        this.sens_end_time = sens_end_time;
    }

    public Integer getEnter_num() {
        return enter_num;
    }

    public void setEnter_num(Integer enter_num) {
        this.enter_num = enter_num;
    }
}