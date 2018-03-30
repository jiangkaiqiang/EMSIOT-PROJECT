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
	
	private String projectName;
	
	private String userRoleName;
	
	private String compFactoryName;

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUserRoleName() {
		return userRoleName;
	}

	public void setUserRoleName(String userRoleName) {
		this.userRoleName = userRoleName;
	}

	public String getCompFactoryName() {
		return compFactoryName;
	}

	public void setCompFactoryName(String compFactoryName) {
		this.compFactoryName = compFactoryName;
	}
	
	
}
