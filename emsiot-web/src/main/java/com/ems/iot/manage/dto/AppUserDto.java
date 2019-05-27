package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.AppUser;

/**
 * @author Barry
 * @date 2018年3月20日下午3:37:10  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class AppUserDto {
	
	private AppUser appUser;
	
	private String area_name;
	
    private String pro_name;
	
	private String city_name;

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	
	
}
