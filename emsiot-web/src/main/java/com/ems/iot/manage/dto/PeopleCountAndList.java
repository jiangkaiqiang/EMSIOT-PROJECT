package com.ems.iot.manage.dto;
/**
 * 未查询个基站下的车辆提供支持
 * @author barry
 *
 */

import java.util.List;
import java.util.Map;


public class PeopleCountAndList {
	private Map<String, List<StationPeopleDto>> stationPeopleList ;
	private Map<String, Integer> peopleCountByStation;
	public Map<String, List<StationPeopleDto>> getStationPeopleList() {
		return stationPeopleList;
	}
	public void setStationPeopleList(Map<String, List<StationPeopleDto>> stationPeopleList) {
		this.stationPeopleList = stationPeopleList;
	}
	public Map<String, Integer> getPeopleCountByStation() {
		return peopleCountByStation;
	}
	public void setPeopleCountByStation(Map<String, Integer> peopleCountByStation) {
		this.peopleCountByStation = peopleCountByStation;
	}
	
	
	
	
	
}
