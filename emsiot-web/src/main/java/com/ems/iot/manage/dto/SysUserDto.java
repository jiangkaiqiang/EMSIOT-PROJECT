package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.SysUser;

/**
 * @author Barry
 * @date 2018年3月20日下午3:37:10  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class SysUserDto {
	
	private SysUser sysUser;
	
	private String area_name;
	
    private String pro_name;
	
	private String city_name;

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

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}
	
}
