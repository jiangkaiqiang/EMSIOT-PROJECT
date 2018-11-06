package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.StationStatusRecord;

public class StationStatusRecordDto {
	private StationStatusRecord stationRecord ;
	private String station_name;
	
	public StationStatusRecord getStationRecord() {
		return stationRecord;
	}
	public void setStationRecord(StationStatusRecord stationRecord) {
		this.stationRecord = stationRecord;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	
	
	
}
