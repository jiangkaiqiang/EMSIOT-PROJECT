package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.People;
import com.ems.iot.manage.entity.Station;

public class PeopleStationInfluxDto {
	private Station station;
	
	private String hard_read_time;
	
	private People people;

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getHard_read_time() {
		return hard_read_time;
	}

	public void setHard_read_time(String hard_read_time) {
		this.hard_read_time = hard_read_time;
	}

	public People getPeople() {
		return people;
	}

	public void setPeople(People people) {
		this.people = people;
	}

}
