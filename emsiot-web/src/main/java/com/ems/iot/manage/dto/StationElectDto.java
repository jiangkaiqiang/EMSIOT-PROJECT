package com.ems.iot.manage.dto;
/**
 * 未查询个基站下的车辆提供支持
 * @author barry
 *
 */
public class StationElectDto {
	private String plate_num;
	private String owner_name;
	private String corssTime;
	public String getPlate_num() {
		return plate_num;
	}
	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}
	public String getCorssTime() {
		return corssTime;
	}
	public void setCorssTime(String corssTime) {
		this.corssTime = corssTime;
	}
	
}
