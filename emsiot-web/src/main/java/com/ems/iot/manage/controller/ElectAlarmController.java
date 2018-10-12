package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectAlarmDto;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.Station;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @author Barry
 * @date 2018年4月15日下午3:32:54
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/electalarm")
public class ElectAlarmController extends BaseController {
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private BlackelectMapper blackelectMapper;
	@Autowired
	private ElectAlarmMapper electAlarmMapper;
	@Autowired
	private StationMapper stationMapper;
	
	/**
	 * 根据条件查询所有报警信息
	 * 
	 * @returnx
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/findAllElectAlarmByOptions",method = RequestMethod.POST)
	@ResponseBody
	public Object findAllBlackelectByOptions(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize",required=false) Integer pageSize,
			@RequestParam(value="plateNum",required=false) String plateNum,
			@RequestParam(value="alarmDateStart",required=false) String alarmDateStartStr,
			@RequestParam(value="alarmDateEnd",required=false) String alarmDateEndStr,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower
			) throws UnsupportedEncodingException, ParseException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
		Date alarmDateStart=null;
		Date alarmDateEnd=null;
		if(alarmDateStartStr!="" && alarmDateStartStr!=null){
			alarmDateStart=sdf.parse(alarmDateStartStr);
		}
		if(alarmDateEndStr!="" && alarmDateEndStr!=null){
			alarmDateEnd=sdf.parse(alarmDateEndStr);
		}
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		Page<ElectAlarm> electAlarms=electAlarmMapper.findAllElectalarmByOptions(plateNum,alarmDateStart,alarmDateEnd,proPower, cityPower, areaPower);
		Page<ElectAlarmDto> electAlarmDtos = new Page<ElectAlarmDto>();
		for(ElectAlarm electAlarm:electAlarms){
			ElectAlarmDto electAlarmDto = new ElectAlarmDto();
			electAlarmDto.setElectAlarm(electAlarm);
			Page<Station> station = stationMapper.findAllStationsByKey(null, null, electAlarm.getAlarm_station_phy_num(), null, null,null,null,null);
			if(station.size()>0) {
				electAlarmDto.setStatioAddress(station.get(0).getStation_address());
			}
			Electrombile elect = electrombileMapper.findPlateNumByGuaCardNum(electAlarm.getAlarm_gua_card_num());
			Electrombile electrombile = new Electrombile();
			if(elect!=null) {
				electrombile = elect;
				electAlarmDto.setOwnerName(electrombile.getOwner_name());
				electAlarmDto.setOwnerTele(electrombile.getOwner_tele());
				electAlarmDto.setOwnerPlateNum(electrombile.getPlate_num());
			}
			electAlarmDtos.add(electAlarmDto);
		}
		electAlarmDtos.setPageSize(electAlarms.getPageSize());
		electAlarmDtos.setPages(electAlarms.getPages());
		electAlarmDtos.setTotal(electAlarms.getTotal());
		return new PageInfo<ElectAlarmDto>(electAlarmDtos);
	}
	
	 /**
     * 根据ID删除报警
     * @param electID
     * @return
     */
	@RequestMapping(value = "/deleteElectAlarmByID")
	@ResponseBody
	public Object deleteElectByID(@RequestParam(value="elect_alarm_id",required=false)Integer elect_alarm_id) {
		electAlarmMapper.deleteByPrimaryKey(elect_alarm_id);
		return new BaseDto(0);
	}
	
	/**
	 * 根据多个ID删除报警信息
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteElectAlarmByIDs")
	@ResponseBody
	public Object deleteElectByIDs(@RequestParam(value="ElectAlarmIDs",required=false)Integer[] ElectAlarmIDs) {
		for(Integer ElectAlarmID:ElectAlarmIDs){
			electAlarmMapper.deleteByPrimaryKey(ElectAlarmID);
		}
		return new BaseDto(0);
	}
	/**
	 * 返回所有报警，不分页
	 * @return
	 */
	@RequestMapping(value = "/findElectAlarmsList")
	@ResponseBody
	public Object findElectAlarmsList(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower) {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		List<ElectAlarm> electAlarms = electAlarmMapper.findElectalarmsList(proPower, cityPower,
				areaPower);
		return electAlarms;
	}
}
