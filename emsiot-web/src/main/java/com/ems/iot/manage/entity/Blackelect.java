package com.ems.iot.manage.entity;

import java.util.Date;

public class Blackelect {
    private Integer black_id;

    private Integer gua_card_num;

    private Date case_occur_time;

    private String owner_tele;

    private String owner_name;

    private Integer pro_id;

    private Integer city_id;

    private Integer area_id;

    private String case_address;

    private String case_address_type;

    private String case_detail;

    private Integer deal_status;

    public Integer getBlack_id() {
        return black_id;
    }

    public void setBlack_id(Integer black_id) {
        this.black_id = black_id;
    }

    public Integer getGua_card_num() {
        return gua_card_num;
    }

    public void setGua_card_num(Integer gua_card_num) {
        this.gua_card_num = gua_card_num;
    }

    public Date getCase_occur_time() {
        return case_occur_time;
    }

    public void setCase_occur_time(Date case_occur_time) {
        this.case_occur_time = case_occur_time;
    }

    public String getOwner_tele() {
        return owner_tele;
    }

    public void setOwner_tele(String owner_tele) {
        this.owner_tele = owner_tele == null ? null : owner_tele.trim();
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name == null ? null : owner_name.trim();
    }

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

    public String getCase_address() {
        return case_address;
    }

    public void setCase_address(String case_address) {
        this.case_address = case_address == null ? null : case_address.trim();
    }

    public String getCase_address_type() {
        return case_address_type;
    }

    public void setCase_address_type(String case_address_type) {
        this.case_address_type = case_address_type == null ? null : case_address_type.trim();
    }

    public String getCase_detail() {
        return case_detail;
    }

    public void setCase_detail(String case_detail) {
        this.case_detail = case_detail == null ? null : case_detail.trim();
    }

    public Integer getDeal_status() {
        return deal_status;
    }

    public void setDeal_status(Integer deal_status) {
        this.deal_status = deal_status;
    }
}