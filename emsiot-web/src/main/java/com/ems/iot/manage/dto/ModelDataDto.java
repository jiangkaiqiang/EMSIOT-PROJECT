package com.ems.iot.manage.dto;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:53  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class ModelDataDto {
	
	private String num;
	
	private String type;
	
	private Integer state;
	
	private String single;
	
	private String floor;
	
	private String project;
	
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSingle() {
		return single;
	}

	public void setSingle(String single) {
		this.single = single;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	
}
