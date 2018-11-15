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
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.LimitAreaMapper;
import com.ems.iot.manage.dao.SensitiveAreaMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.AreaAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.SensitiveArea;
import com.ems.iot.manage.entity.Station;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("/sensitiveArea")
public class SensitiveAreaController {
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private SensitiveAreaMapper sensitiveAreaMapper;
	@Autowired
	private AreaAlarmMapper areaAlarmMapper;
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
			@RequestParam(value = "enterNum", required = false) Integer enterNum,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "sensStartTime", required = false) String sensStartTime,
			@RequestParam(value = "sensEndTime", required = false) String sensEndTime,
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
		sensitiveArea.setList_elects(addElectPlatenum);
		sensitiveArea.setSens_start_time(sensStartTime);
		sensitiveArea.setSens_end_time(sensEndTime);
		sensitiveArea.setStatus(status);
		sensitiveArea.setEnter_num(enterNum);
		sensitiveArea.setPro_id(proPower);
		sensitiveArea.setCity_id(cityPower);
		sensitiveArea.setArea_id(areaPower);
		sensitiveAreaMapper.insert(sensitiveArea);
//		
		return new ResultDto(0, "添加成功");
	}
	
	/**
	 * 修改敏感区域
	 * @return
	 */	
	@RequestMapping(value = "/updateSensitiveArea", method = RequestMethod.POST)
	@ResponseBody
	public Object updateSensitiveArea(@RequestParam(required = false) Integer sensitive_area_id,
			@RequestParam(value = "updateSensitiveAreaName", required = false) String updateSensitiveAreaName,
			@RequestParam(value = "updateStationNames", required = false) String updateStationNames,
			@RequestParam(value = "updateBlackelectPlatenum", required = false) String updateBlackelectPlatenum,
			@RequestParam(value = "updateElectPlatenum", required = false) String updateElectPlatenum,
			@RequestParam(value = "enterNum", required = false) Integer enterNum,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "sensStartTime", required = false) String sensStartTime,
			@RequestParam(value = "sensEndTime", required = false) String sensEndTime,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower){
		
		if (updateSensitiveAreaName==null||updateSensitiveAreaName.equals("")) {
			return new ResultDto(-1, "限制区域的名称不能为空");
		}
		SensitiveArea sensitive = sensitiveAreaMapper.findSensitiveAreaForFilter(updateSensitiveAreaName);
		if (sensitive != null) {
			if(sensitive.getSensitive_area_id()!=sensitive_area_id) {
				return new ResultDto(-1, "该限制区域名称已存在，不能重复添加");
			}
		}
//		
		SensitiveArea sensitiveArea = new SensitiveArea();
		sensitiveArea.setAdd_time(new Date());
		sensitiveArea.setBlack_list_elects(updateBlackelectPlatenum);
		sensitiveArea.setSensitive_area_name(updateSensitiveAreaName);
//
		
		String[] stationNames = updateStationNames.split(";");
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
		sensitiveArea.setSensitive_area_id(sensitive_area_id);
		sensitiveArea.setStation_ids(stationIDs);
		sensitiveArea.setBlack_list_elects(updateBlackelectPlatenum);
		sensitiveArea.setList_elects(updateElectPlatenum);
		sensitiveArea.setSens_start_time(sensStartTime);
		sensitiveArea.setSens_end_time(sensEndTime);
		sensitiveArea.setStatus(status);
		sensitiveArea.setEnter_num(enterNum);
		sensitiveArea.setPro_id(proPower);
		sensitiveArea.setCity_id(cityPower);
		sensitiveArea.setArea_id(areaPower);
		sensitiveAreaMapper.updateByPrimaryKeySelective(sensitiveArea);
//		
		return new ResultDto(0, "更新成功");
	}
	
	
	
	/**
	 * 查找在限制区域
	 * @return
	 */	
	@RequestMapping(value = "/findSensitiveByOptions",method=RequestMethod.POST)
	@ResponseBody
	public Object showAllSensitiveArea(@RequestParam(value="sensitiveAreaID",required = false) Integer sensitiveAreaID,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower,
			@RequestParam(value="alarmDateStart", required=false) Integer alarmDateStart,
			@RequestParam(value="alarmDateEnd", required=false) Integer alarmDateEnd,
			@RequestParam(value="sensitiveAreaName", required = false) String sensitiveAreaName){
		pageNum = pageNum == null ? 1 : pageNum;
		pageSize = pageSize == null ? 12 : pageSize;
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		PageHelper.startPage(pageNum, pageSize);
		Page<SensitiveArea> sensitiveAreas= sensitiveAreaMapper.findAllSensitiveAreas(sensitiveAreaID, sensitiveAreaName, proPower, cityPower, areaPower);
		//增加基站和车辆的名称（不用id显示）
		String stationStr = "";
		String electStr = "";
		for (SensitiveArea sensitiveArea : sensitiveAreas) {
			if(sensitiveArea.getStation_ids()!=null) {
				String[] stationIds = sensitiveArea.getStation_ids().split(";");
				for (int i = 0; i < stationIds.length; i++) {
					Station station = stationMapper.selectByPrimaryKey(Integer.valueOf(stationIds[i]));
					if(station!=null) {
						if(stationStr == "") {
							stationStr = station.getStation_name().toString();
						}else {
							stationStr += ";" + station.getStation_name();
						}
					}
				}
			}
			if(sensitiveArea.getBlack_list_elects()!=null) {
				String[] electIds = sensitiveArea.getBlack_list_elects().split(";");
				for (int i = 0; i < electIds.length; i++) {
					Electrombile elect = electrombileMapper.selectByPrimaryKey(Integer.valueOf(electIds[i]));
					if(elect!=null) {
						if(electStr == "") {
							electStr = elect.getPlate_num().toString();
						}else {
							electStr += ";" + elect.getPlate_num();
						}
					}
				}
			}
			sensitiveArea.setStation_names(stationStr);
			sensitiveArea.setBlack_list_elects_names(electStr);
			stationStr = "";
			electStr = "";
		}
		

		return new PageInfo<SensitiveArea>(sensitiveAreas);
	}
	
	
	/**
	 * 根据条件查询所有报警信息
	 * 
	 * @returnx
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/findAllSensitiveAreaAlarmByOptions")
	@ResponseBody
	public Object findAllSensitiveAreaAlarmByOptions(@RequestParam(value="pageNum",required=false) Integer pageNum,
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
		Page<AreaAlarm> sensitiveArea = sensitiveAreaMapper.findAllSensitiveAreaAlarmByOptions(plateNum, areaName, alarmDateStart, alarmDateEnd, proPower, cityPower, areaPower);
		PageInfo<AreaAlarm> areaAlarmPageInfo =  new PageInfo<AreaAlarm>(sensitiveArea);
		return new ResultDto(areaAlarmPageInfo);
	}
	
	
	
	/**
	 * 删除限制区域
	 * @return
	 */
	@RequestMapping(value="/deleteSensitiveAreaByID")
	@ResponseBody
	public Object deleteSensitiveAreaByID(@RequestParam("sensitiveAreaID")Integer sensitiveAreaID){
		sensitiveAreaMapper.deleteByPrimaryKey(sensitiveAreaID);
		return new BaseDto(0);
	}
	
	/**
	 * 删除多个限制区域
	 * @return
	 */
	@RequestMapping(value="/deleteSensitiveAreaByIDs")
	@ResponseBody
	public Object deleteSensitiveAreaByIDs(@RequestParam("sensitiveAreaIDs")Integer[] sensitiveAreaIDs){
		for (int i = 0; i < sensitiveAreaIDs.length; i++) {
			sensitiveAreaMapper.deleteByPrimaryKey(sensitiveAreaIDs[i]);
		}		
		return new BaseDto(0);
	}
	
	/**
	 *  查看详情
	 * @param sensitiveAreaID
	 * @return
	 */
	@RequestMapping(value="/findSensitiveAreaByID")
	@ResponseBody
	public Object findSensitiveAreaByID(@RequestParam("sensitiveAreaID")Integer sensitiveAreaID){
		SensitiveArea sensitiveArea = sensitiveAreaMapper.selectByPrimaryKey(sensitiveAreaID);
		
		String stationStr = "";
		String electStr = "";
		
		if(sensitiveArea.getStation_ids()!=null) {
			String[] stationIds = sensitiveArea.getStation_ids().split(";");
			for (int i = 0; i < stationIds.length; i++) {
				Station station = stationMapper.selectByPrimaryKey(Integer.valueOf(stationIds[i]));
				if(station!=null) {
					if(stationStr == "") {
						stationStr = station.getStation_name().toString();
					}else {
						stationStr += ";" + station.getStation_name();
					}
				}
			}
		}
		
		if(sensitiveArea.getBlack_list_elects()!=null) {
			String[] electIds = sensitiveArea.getBlack_list_elects().split(";");
			for (int i = 0; i < electIds.length; i++) {
				Electrombile elect = electrombileMapper.selectByPrimaryKey(Integer.valueOf(electIds[i]));
				if(elect!=null) {
					if(electStr == "") {
						electStr = elect.getPlate_num().toString();
					}else {
						electStr += ";" + elect.getPlate_num();
					}
				}
			}
		}
		sensitiveArea.setStation_names(stationStr);
		sensitiveArea.setBlack_list_elects_names(electStr);
		
		return sensitiveArea;
	}
	
	
	 /**
     * 根据ID删除报警
     * @param electID
     * @return
     */
	@RequestMapping(value = "/deleteSensitiveAreaAlarmByID")
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
	@RequestMapping(value = "/deleteSensitiveAreaAlarmByIDs")
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
	
	
	/**
	 * 根据条件查询限制区域报警信息
	 * 
	 * @returnx
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/findSensitiveAreaAlarmByStationNumAndTime")
	@ResponseBody
	public Object findSensitiveAreaAlarmByStationNumAndTime(@RequestParam(value="alarmStationPhyNum",required=false) String alarmStationPhyNum,
			@RequestParam(value="alarmDateStart",required=false) String alarmDateStartStr,
			@RequestParam(value="alarmDateEnd",required=false) String alarmDateEndStr,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower
			) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		List<AreaAlarm> areaAlarms = areaAlarmMapper.findAreaAlarmByStationNumAndTime(alarmStationPhyNum, alarmDateStart, alarmDateEnd, proPower, cityPower, areaPower);
		return areaAlarms;
	}
	
	
}
