package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.entity.Station;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
/**
 * @author Barry
 * @date 2018年3月20日下午3:32:54
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/station")
public class StationController extends BaseController {
	@Autowired
	private StationMapper stationMapper;

	/**
	 * 查询所有基站
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAllStations")
	@ResponseBody
	public Object findAllStations(@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "stationID", required = false) Integer stationID,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum,
			@RequestParam(value = "stationName", required = false) String stationName,
			@RequestParam(value = "longitude", required = false) String longitude,
			@RequestParam(value = "latitude", required = false) String latitude,
			@RequestParam(value = "stationType", required = false) String stationType,
			@RequestParam(value = "stationStatus", required = false) Integer stationStatus,
			@RequestParam(value = "installDate", required = false) Date installDate,
			@RequestParam(value = "stickNum", required = false) String stickNum) throws UnsupportedEncodingException {
		pageNum = pageNum == null ? 1 : pageNum;
		pageSize = pageSize == null ? 12 : pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Station> staions= (Page<Station>) stationMapper.findAllStations();

		return stationMapper.findAllStations();
	}
	
	@RequestMapping(value = "/addElect")
	@ResponseBody
	public Object addElect(@RequestParam(required = false) Integer stationID,
			@RequestParam(required = false) Integer stationPhyNum,
			@RequestParam(required = false) String stationName,
			@RequestParam(required = false) String longitude,
			@RequestParam(required = false) String latitude,
			@RequestParam(required = false) String stationType,
			@RequestParam(required = false) Integer stationStatus,
			@RequestParam(required = false) Date installDate,
			@RequestParam(required = false) Date softVersion,
			@RequestParam(required = false) Date contactPerson,
			@RequestParam(required = false) Date contactTele,
			@RequestParam(required = false) String stickNum			
			)throws UnsupportedEncodingException{
		
		return null;
	}
}
