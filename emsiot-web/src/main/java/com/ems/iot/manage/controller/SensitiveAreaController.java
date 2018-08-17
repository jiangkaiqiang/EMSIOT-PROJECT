package com.ems.iot.manage.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.LimitAreaMapper;
import com.ems.iot.manage.dao.SensitiveAreaMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.SensitiveArea;
import com.ems.iot.manage.entity.Station;

@Controller
@RequestMapping("/SensitiveArea")
public class SensitiveAreaController {
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private SensitiveAreaMapper sensitiveAreaMapper;
	/**
	 * 添加敏感区域
	 * @return
	 */	
	@RequestMapping(value = "/addSensitiveArea", method = RequestMethod.POST)
	@ResponseBody
	public Object addSensitiveArea(@RequestParam(value = "addSensitiveAreaName", required = false) String addSensitiveAreaName,
			@RequestParam(value = "addStationNames", required = false) String addStationNames,
			@RequestParam(value = "addBlackelectPlatenum", required = false) String addBlackelectPlatenum,
			@RequestParam(value = "addElectPlatenum", required = false) String addElectPlatenum,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower){
		
		if (addSensitiveAreaName==null||addSensitiveAreaName.equals("")) {
			return new ResultDto(-1, "限制区域的名称不能为空");
		}
		if (sensitiveAreaMapper.findSensitiveAreaForFilter(addSensitiveAreaName)!=null) {
			return new ResultDto(-1, "该限制区域名称已存在，不能重复添加");
		}
//		
		SensitiveArea sensitiveArea = new SensitiveArea();
		sensitiveArea.setAdd_time(new Date());
		sensitiveArea.setBlack_list_elects(addBlackelectPlatenum);
		sensitiveArea.setSensitive_area_name(addSensitiveAreaName);
//
		
		String[] stationNames = addStationNames.split(",");
		String stationIDs = "";
		for(int index = 0; index < stationNames.length;index++){
			Station station = stationMapper.selectByStationName(stationNames[index]);
			if(index == 0){
				stationIDs = station.getStation_id()+"";
			} else {
				stationIDs += ";"+station.getStation_id();
			}
		}
		
//		
		sensitiveArea.setStation_ids(stationIDs);
		sensitiveArea.setBlack_list_elects(addBlackelectPlatenum);
		sensitiveArea.setPro_id(proPower);
		sensitiveArea.setCity_id(cityPower);
		sensitiveArea.setArea_id(areaPower);
		sensitiveAreaMapper.insert(sensitiveArea);
//		
		return new ResultDto(0, "添加成功");
	}
	
}
