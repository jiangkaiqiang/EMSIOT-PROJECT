package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Station;
import java.util.Date;
public class TraceStationDto {
	private Date crossTime;
	private Station station;
	public Date getCrossTime() {
		return crossTime;
	}
	public void setCrossTime(Date crossTime) {
		this.crossTime = crossTime;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	
}
