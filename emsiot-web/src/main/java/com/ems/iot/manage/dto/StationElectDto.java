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
	//基站名
	private String station_name;
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
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	
}
