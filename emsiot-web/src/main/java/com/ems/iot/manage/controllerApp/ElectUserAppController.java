package com.ems.iot.manage.controllerApp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.TraceStationDto;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.service.CookieService;
/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14  app接口-车辆相关
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/electUserApp")
public class ElectUserAppController extends AppBaseController {
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private CookieService cookieService;
	/**
	 * 根据电动车的ID寻找电动车
	 * @param electID
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectByID")
	@ResponseBody
	public Object findElectByID(
			@RequestParam(value="electID", required=false) Integer electID, 
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException {
		 Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		 if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		 }
		 if (electID==null) {
			return new AppResultDto(3001, "要查找的车辆id不能为空",false);
		 }
		 Electrombile electrombile = electrombileMapper.selectByPrimaryKey(electID);
		 if (electrombile==null) {
			return new AppResultDto(2001, "未查询到给车辆id对应的车辆信息");
		 }
	     return new AppResultDto(electrombile);
	}
	
	
	/**
	 * 根据关键字定位车辆
	 * @param plateNum
	 * @param guaCardNum
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectLocation")
	@ResponseBody
	public Object findElectLocation(
			@RequestParam(value="plateNum", required=false) String plateNum,
			@RequestParam(value="guaCardNum", required=false) Integer guaCardNum,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (plateNum==null) {
			return new AppResultDto(3001, "车牌号不能为空", false);
		}
		Electrombile electrombile = electrombileMapper.findElectrombileForLocation(guaCardNum, plateNum);
		Station station = new Station();
		if (null!=electrombile) {
			ElectrombileStation electrombileStation =  electrombileStationMapper.selectByGuaCardNumForLocation(electrombile.getGua_card_num());
			station = stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num());
		}
		if (station==null||station.getLongitude()==null||station.getLatitude()==null) {
			return new AppResultDto(0, "未查询到车辆的位置信息");
		}
		return new AppResultDto(station);
	}
	
	/**
	 * 查询车辆轨迹
	 * @param plateNum
	 * @param guaCardNum
	 * @param startTimeForTrace
	 * @param endTimeForTrace
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectTrace")
	@ResponseBody
	public Object findElectTrace(
			@RequestParam(value="plateNum", required=false) String plateNum,
			@RequestParam(value="guaCardNum", required=false) Integer guaCardNum,
			@RequestParam(value="startTimeForTrace", required=false) String startTimeForTrace,
			@RequestParam(value="endTimeForTrace", required=false) String endTimeForTrace,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (plateNum==null) {
			return new AppResultDto(3001, "车牌号不能为空", false);
		}
		Electrombile electrombile = electrombileMapper.findElectrombileForLocation(guaCardNum, plateNum);
		List<TraceStationDto> traceStationDtos = new ArrayList<TraceStationDto>();
		if (null!=electrombile) {
			List<ElectrombileStation> electrombileStations = electrombileStationMapper.
					selectByGuaCardNumForTrace(electrombile.getGua_card_num(), startTimeForTrace, endTimeForTrace);
			for (ElectrombileStation electrombileStation : electrombileStations) {
				TraceStationDto traceStationDto = new TraceStationDto();
				traceStationDto.setCrossTime(electrombileStation.getUpdate_time());
				traceStationDto.setStation(stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num()));
				traceStationDtos.add(traceStationDto);
			}
		}
		if (traceStationDtos==null||traceStationDtos.size()==0) {
			return new AppResultDto(2001, "未查询到该车辆的轨迹");
		}
		return new AppResultDto(traceStationDtos);
	}
	
	@RequestMapping(value = "/findElectByTele")
	@ResponseBody
	public Object findElectByTele(
			@RequestParam(value="tele", required=false) Integer tele,
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		 Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		 if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	     }
		 if (tele==null) {
			return new AppResultDto(3001,"手机号不能为空",false);
		 }
		 List<Electrombile> electrombiles = electrombileMapper.findElectsByTele(tele);
		 if (electrombiles==null||electrombiles.size()==0) {
			return new AppResultDto(2001, "未查询到该手机号绑定的车辆");
		 }
	     return new AppResultDto(electrombiles);
	}
}
