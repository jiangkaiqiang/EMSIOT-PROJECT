package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.People;
import com.ems.iot.manage.entity.Station;

public class PeopleStationDto {
	private People people;
	private String crossTime;
	private Station station;
	public String getCrossTime() {
		return crossTime;
	}
	public void setCrossTime(String crossTime) {
		this.crossTime = crossTime;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public People getPeople() {
		return people;
	}
	public void setPeople(People people) {
		this.people = people;
	}
	
}
