package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.util.GenDataUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @author Barry
 * @date 2018年3月20日下午3:32:54  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
import com.github.pagehelper.ReturnPageInfo;
@Controller
@RequestMapping(value = "/genDataController")
public class genDataController extends BaseController {
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	/**
	 * 查询所有基站
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/genData")
	@ResponseBody
	public Object genData() throws UnsupportedEncodingException {
		for (int i = 0; i < 1000; i++) {
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
}
