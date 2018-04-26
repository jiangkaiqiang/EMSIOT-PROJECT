package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Station;
import java.util.Date;
public class TraceStationDto {
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
	
}
