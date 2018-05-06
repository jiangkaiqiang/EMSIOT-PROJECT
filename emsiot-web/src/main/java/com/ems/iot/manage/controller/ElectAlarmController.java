package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

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
	 */
	@RequestMapping(value = "/findAllElectAlarmByOptions",method = RequestMethod.POST)
	@ResponseBody
	public Object findAllBlackelectByOptions(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize",required=false) Integer pageSize,
			@RequestParam(value="alarmTime",required=false) Date alarmTime
			) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<ElectAlarm> electAlarms=electAlarmMapper.findAllElectalarmByOptions(alarmTime);
		Page<ElectAlarmDto> electAlarmDtos = new Page<ElectAlarmDto>();
		for(ElectAlarm electAlarm:electAlarms){
			ElectAlarmDto electAlarmDto = new ElectAlarmDto();
			electAlarmDto.setElectAlarm(electAlarm);
			electAlarmDto.setStatioAddress(stationMapper.findAllStationsByKey(null, null, electAlarm.getAlarm_station_phy_num(), null, null).get(0).getStation_address());
			Electrombile electrombile=electrombileMapper.findPlateNumByGuaCardNum(electAlarm.getAlarm_gua_card_num());
			electAlarmDto.setOwnerName(electrombile.getOwner_name());
			electAlarmDto.setOwnerTele(electrombile.getOwner_tele());
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
	@RequestMapping(value = "/deleteBlackelectByID")
	@ResponseBody
	public Object deleteElectByID(@RequestParam(value="blackID",required=false)Integer blackID) {
		 blackelectMapper.deleteByPrimaryKey(blackID);
		 return new BaseDto(0);
	}
	
	/**
	 * 根据多个ID删除报警信息
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteBlackelectByIDs")
	@ResponseBody
	public Object deleteElectByIDs(@RequestParam(value="BlackIDs",required=false)Integer[] blackIDs) {
		for(Integer blackID:blackIDs){
			blackelectMapper.deleteByPrimaryKey(blackID);
		}
		return new BaseDto(0);
	}
}
