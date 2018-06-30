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

import com.ems.iot.manage.dao.AreaAlarmMapper;
import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectAlarmDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.AreaAlarm;
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
@RequestMapping(value = "/limitareaalarm")
public class LimitAreaAlarmController extends BaseController {
	@Autowired
	private AreaAlarmMapper areaAlarmMapper;
	
	/**
	 * 根据条件查询所有报警信息
	 * 
	 * @returnx
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/findAllLimitAreaAlarmByOptions")
	@ResponseBody
	public Object findAllLimitAreaAlarmByOptions(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize",required=false) Integer pageSize,
			@RequestParam(value="plateNum",required=false) String plateNum,
			@RequestParam(value="areaName",required=false) String areaName,
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
		Page<AreaAlarm> areaAlarms = areaAlarmMapper.findAllAreaAlarmByOptions(plateNum, areaName, alarmDateStart, alarmDateEnd, proPower, cityPower, areaPower);
		PageInfo<AreaAlarm> areaAlarmPageInfo =  new PageInfo<AreaAlarm>(areaAlarms);
		return new ResultDto(areaAlarmPageInfo);
	}
	
	 /**
     * 根据ID删除报警
     * @param electID
     * @return
     */
	@RequestMapping(value = "/deletelimitAreaAlarmByID")
	@ResponseBody
	public Object deletelimitAreaAlarmByID(@RequestParam(value="area_alarm_id",required=false)Integer area_alarm_id) {
		if (area_alarm_id==null) {
			return new ResultDto(-1,"请输入要删除的id");
		}
		areaAlarmMapper.deleteByPrimaryKey(area_alarm_id);
		return new ResultDto(0,"删除成功");
	}
	
	/**
	 * 根据多个ID删除报警信息
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteLimitAreaAlarmByIDs")
	@ResponseBody
	public Object deleteLimitAreaAlarmByIDs(@RequestParam(value="area_alarm_ids",required=false)Integer[] area_alarm_ids) {
		if (area_alarm_ids==null||area_alarm_ids.length==0) {
			return new ResultDto(-1,"请输入要删除的ids");
		}
		for(Integer area_alarm_id:area_alarm_ids){
			areaAlarmMapper.deleteByPrimaryKey(area_alarm_id);
		}
		return new ResultDto(0,"删除成功");
	}
}
