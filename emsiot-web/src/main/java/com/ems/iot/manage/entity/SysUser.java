package com.ems.iot.manage.entity;

import java.util.Date;

public class SysUser {
    private Integer user_id;

    private String user_name;

    private String nickname;

    private String password;

    private Integer opt_power;

    private String menu_power;
    
    private String area_power;
    
    private String pro_power;

    private String city_power;

    private Date login_time;

    private String create_time;

    private String user_tel;
    
    private String company;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name == null ? null : user_name.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getOpt_power() {
        return opt_power;
    }

    public void setOpt_power(Integer opt_power) {
        this.opt_power = opt_power;
    }

    public String getMenu_power() {
        return menu_power;
    }

    public void setMenu_power(String menu_power) {
        this.menu_power = menu_power == null ? null : menu_power.trim();
    }

    public String getArea_power() {
        return area_power;
    }

    public void setArea_power(String area_power) {
        this.area_power = area_power;
    }

    public Date getLogin_time() {
        return login_time;
    }

    public void setLogin_time(Date login_time) {
        this.login_time = login_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel == null ? null : user_tel.trim();
    }

	public String getPro_power() {
		return pro_power;
	}

	public void setPro_power(String pro_power) {
		this.pro_power = pro_power;
	}

	public String getCity_power() {
		return city_power;
	}

	public void setCity_power(String city_power) {
		this.city_power = city_power;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
    
    
}