package com.ems.iot.manage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.LimitAreaMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.StationLine;
import com.ems.iot.manage.dto.StationPoint;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.util.AreaUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/specialArea")
public class SpecialAreaController {
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private LimitAreaMapper limitAreaMapper;
	/**
	 * 查找在限制区域
	 * @return
	 */	
	@RequestMapping(value = "/findLimitAreaByOptions",method=RequestMethod.POST)
	@ResponseBody
	public Object showAllLimitArea(@RequestParam(value="limitAreaID",required = false) Integer limitAreaID,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value="limitAreaName", required = false) String limitAreaName){
		pageNum = pageNum == null ? 1 : pageNum;
		pageSize = pageSize == null ? 12 : pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<LimitArea> limitAreas= limitAreaMapper.findAllLimitAreas(limitAreaID, limitAreaName);
		return new PageInfo<LimitArea>(limitAreas);
	}
	/**
	 * 删除限制区域
	 * @return
	 */
	@RequestMapping(value="/deleteLimitAreaByID")
	@ResponseBody
	public Object deleteLimitAreaByID(@RequestParam("limitAreaID")Integer limitAreaID){
		limitAreaMapper.deleteByPrimaryKey(limitAreaID);
		return new BaseDto(0);
	}
	/**
	 * 删除多个限制区域
	 * @return
	 */
	@RequestMapping(value="/deleteLimitAreaByIDs")
	@ResponseBody
	public Object deleteLimitAreaByIDs(@RequestParam("limitAreaIDs")Integer[] limitAreaIDs){
		for (int i = 0; i < limitAreaIDs.length; i++) {
			limitAreaMapper.deleteByPrimaryKey(limitAreaIDs[i]);
		}		
		return new BaseDto(0);
	}
	/**
	 * 添加限制区域
	 * @return
	 */	
	@RequestMapping(value = "/addSpecialArea", method = RequestMethod.POST)
	@ResponseBody
	public Object addBlackelect(@RequestParam(value = "addLimitAreaName", required = false) String addLimitAreaName,
			@RequestParam(value = "addStationNames", required = false) String addStationNames,
			@RequestParam(value = "addBlackelectPlatenum", required = false) String addBlackelectPlatenum){
		LimitArea limitArea = new LimitArea();
		limitArea.setAdd_time(new Date());
		limitArea.setBlack_list_elects(addBlackelectPlatenum);
		limitArea.setLimit_area_name(addLimitAreaName);
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
		limitArea.setStation_ids(stationIDs);
		limitArea.setBlack_list_elects(addBlackelectPlatenum);
		limitAreaMapper.insert(limitArea);
		return new ResultDto(0, "添加成功");
	}
	/**
	 * 查找在区域内的基站
	 * @return
	 */	
	@RequestMapping(value = "/findStations", method = RequestMethod.POST)
	@ResponseBody
	public Object findStations(
			@RequestParam(value = "area_power",required=false) Integer areaPower,
			@RequestParam(value = "city_power",required=false) Integer cityPower,
			@RequestParam(value = "pro_power",required=false) Integer proPower,
			@RequestParam(value = "borderPoints",required=false) String borderPoints
			){
		areaPower=areaPower==-1?null:areaPower;
		cityPower=cityPower==-1?null:cityPower;
		proPower=proPower==-1?null:proPower;
		borderPoints = "["+borderPoints +"]";
        JSONArray borderArr = JSONArray.fromObject(borderPoints);
        List<StationPoint> points = new ArrayList<StationPoint>();
        List<StationLine> lines = new ArrayList<StationLine>();
        List<String> stationNames = new ArrayList<String>();
        for(int i = 0;i<borderArr.size();i++){
            JSONObject borderPoint=(JSONObject)borderArr.get(i);
            points.add(new StationPoint(Double.parseDouble(borderPoint.getString("lat")), 
            		Double.parseDouble(borderPoint.getString("lng"))));
        }
        AreaUtil.CalLines(points,lines);
        List<Station> stations=stationMapper.findAllStations(proPower, cityPower, areaPower);
        for(int i = 0 ;i < stations.size();i++){
        	boolean flag=AreaUtil.isUpper(new StationPoint(Double.parseDouble(stations.get(i).getLatitude()), 
        			Double.parseDouble(stations.get(i).getLongitude())),lines);
        	if(flag){
        		stationNames.add(stations.get(i).getStation_name());
        	}
        }
		return stationNames;
	}
}
