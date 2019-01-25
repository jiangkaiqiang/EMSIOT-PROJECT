package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.Station;

public class ElectStationInfluxDto {
	private Electrombile electrombile;
	
	private Station station;
	
	private String hard_read_time;

	public Electrombile getElectrombile() {
		return electrombile;
	}

	public void setElectrombile(Electrombile electrombile) {
		this.electrombile = electrombile;
	}

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

}
