package com.ems.iot.manage.dto;
/**
 * 未查询个基站下的车辆提供支持
 * @author barry
 *
 */
public class StationPeopleDto {
	private String people_name;
	private String people_id_cards;
	private String corssTime;
	//基站名
	private String station_name;
	
	public String getPeople_name() {
		return people_name;
	}
	public void setPeople_name(String people_name) {
		this.people_name = people_name;
	}
	public String getPeople_id_cards() {
		return people_id_cards;
	}
	public void setPeople_id_cards(String people_id_cards) {
		this.people_id_cards = people_id_cards;
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
