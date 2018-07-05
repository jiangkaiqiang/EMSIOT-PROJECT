package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.util.GenDataUtil;
@Controller
@RequestMapping(value = "/genDataController")
public class genDataController extends BaseController {
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	@Autowired
	private BlackelectMapper blackelectMapper;
	@Autowired
	private ElectAlarmMapper electAlarmMapper;
	/**
	 * 查询所有基站
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/genData")
	@ResponseBody
	public Object genData() throws UnsupportedEncodingException {
		for (int i = 0; i < 50; i++) {
			Station station = new Station();
			Electrombile electrombile = new Electrombile();
			ElectrombileStation electrombileStation = new ElectrombileStation();
		    GenDataUtil.gendata(electrombile, station, electrombileStation, i);
		    stationMapper.insert(station);
		    electrombileMapper.insert(electrombile);
		    electrombileStationMapper.insert(electrombileStation);
		}
		return new ResultDto(1, "gen Success");
	}
	
	@RequestMapping(value = "/genBlackelect")
	@ResponseBody
	public Object genBlackelect() throws UnsupportedEncodingException {
		int genNum=100;
		List<Electrombile> electrombiles=electrombileMapper.findElectrombiles(genNum);
		for (Electrombile electrombile:electrombiles) {
			Blackelect blackelect=new Blackelect();
			GenDataUtil.genBlackelect(electrombile,blackelect);
			blackelectMapper.insert(blackelect);
		}
		return new ResultDto(1, "gen Success");
	}
	@RequestMapping(value = "/genAlarm")
	@ResponseBody
	public Object genAlarm() throws UnsupportedEncodingException {
		int genNum=12;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(int i=1;i<=genNum;i++){
			ElectAlarm electAlarm = new ElectAlarm();
			electAlarm.setAlarm_gua_card_num(101010);
			electAlarm.setAlarm_station_phy_num(202020);
			date.setMonth(i);
			electAlarm.setAlarm_time(sdf.format(date));
			electAlarmMapper.insert(electAlarm);
		}				
		return new ResultDto(1, "gen Success");
	}
}
