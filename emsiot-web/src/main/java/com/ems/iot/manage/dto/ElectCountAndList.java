package com.ems.iot.manage.dto;
/**
 * 未查询个基站下的车辆提供支持
 * @author barry
 *
 */

import java.util.List;
import java.util.Map;


public class ElectCountAndList {
	private Map<String, List<StationElectDto>> stationElectList ;
	private Map<String, Integer> electCountByStation;
	
	public Map<String, List<StationElectDto>> getStationElectList() {
		return stationElectList;
	}
	public void setStationElectList(Map<String, List<StationElectDto>> stationElectList) {
		this.stationElectList = stationElectList;
	}
	public Map<String, Integer> getElectCountByStation() {
		return electCountByStation;
	}
	public void setElectCountByStation(Map<String, Integer> electCountByStation) {
		this.electCountByStation = electCountByStation;
	}
	
	
	
}
