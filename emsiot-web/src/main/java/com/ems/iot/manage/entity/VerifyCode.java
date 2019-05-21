package com.ems.iot.manage.entity;

public class VerifyCode {
    private Integer code_id;

    private String code_num;

    private Integer code_type;

    private String user_tele;

    public Integer getCode_id() {
        return code_id;
    }

    public void setCode_id(Integer code_id) {
        this.code_id = code_id;
    }

    public String getCode_num() {
        return code_num;
    }

    public void setCode_num(String code_num) {
        this.code_num = code_num == null ? null : code_num.trim();
    }

    public Integer getCode_type() {
        return code_type;
    }

    public void setCode_type(Integer code_type) {
        this.code_type = code_type;
    }

    public String getUser_tele() {
        return user_tele;
    }

    public void setUser_tele(String user_tele) {
        this.user_tele = user_tele == null ? null : user_tele.trim();
    }
}