package com.ems.iot.manage.entity;

import java.util.Date;

public class Station {
    private Integer station_id;

    private Integer station_phy_num;

    private String station_name;

    private String longitude;

    private String latitude;

    private String station_type;

    private Integer station_status;

    private String install_date;

    private String soft_version;

    private String contact_person;

    private String contact_tele;

    private String stick_num;

    private String install_pic;

    private String station_address;
    
    private Integer pro_id;
    
    private Integer city_id;
    
    private Integer area_id;
    
    public Integer getPro_id() {
        return pro_id;
    }

    public void setPro_id(Integer pro_id) {
        this.pro_id = pro_id;
    }
    
    public Integer getCity_id() {
        return city_id;
    }

    public void setCity_id(Integer city_id) {
        this.city_id = city_id;
    }
    
    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }
    
    public Integer getStation_id() {
        return station_id;
    }

    public void setStation_id(Integer station_id) {
        this.station_id = station_id;
    }

    public Integer getStation_phy_num() {
        return station_phy_num;
    }

    public void setStation_phy_num(Integer station_phy_num) {
        this.station_phy_num = station_phy_num;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name == null ? null : station_name.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type == null ? null : station_type.trim();
    }

    public Integer getStation_status() {
        return station_status;
    }

    public void setStation_status(Integer station_status) {
        this.station_status = station_status;
    }

    public String getInstall_date() {
        return install_date;
    }

    public void setInstall_date(String install_date) {
        this.install_date = install_date;
    }

    public String getSoft_version() {
        return soft_version;
    }

    public void setSoft_version(String soft_version) {
        this.soft_version = soft_version == null ? null : soft_version.trim();
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person == null ? null : contact_person.trim();
    }

    public String getContact_tele() {
        return contact_tele;
    }

    public void setContact_tele(String contact_tele) {
        this.contact_tele = contact_tele == null ? null : contact_tele.trim();
    }

    public String getStick_num() {
        return stick_num;
    }

    public void setStick_num(String stick_num) {
        this.stick_num = stick_num == null ? null : stick_num.trim();
    }

    public String getInstall_pic() {
        return install_pic;
    }

    public void setInstall_pic(String install_pic) {
        this.install_pic = install_pic == null ? null : install_pic.trim();
    }

    public String getStation_address() {
        return station_address;
    }

    public void setStation_address(String station_address) {
        this.station_address = station_address == null ? null : station_address.trim();
    }
}