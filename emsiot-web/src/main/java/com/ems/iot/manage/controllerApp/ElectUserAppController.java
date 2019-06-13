package com.ems.iot.manage.controllerApp;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.AppUserMapper;
import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.ProvinceMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectLockTraceDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.TraceStationDto;
import com.ems.iot.manage.entity.AppUser;
import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.Constant;
import com.ems.iot.manage.util.InfluxDBConnection;
import com.ems.iot.manage.util.TimeUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
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
	@Autowired
	private BlackelectMapper blackelectMapper;
	@Autowired
	private ProvinceMapper provinceMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
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
	/*@RequestMapping(value = "/findElectLocation")
	@ResponseBody
	public Object findElectLocation1(
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
			if (electrombileStation != null) {
				station = stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num());
			}		
		}
		if (station==null||station.getLongitude()==null||station.getLatitude()==null) {
			return new AppResultDto(0, "未查询到车辆的位置信息");
		}
		return new AppResultDto(station);
	}*/
	
	/**
	 * 
	 * 查询车辆定位   查询influxdb 2019-05-05  重写
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @param token
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
		
		Station station = new Station();
		String strSql=" SELECT * FROM  " + Constant.electStationTable;
		String where = "";
		if( plateNum != null && !"".equals(plateNum)) {
			where += " plate_num = '"+plateNum+"'";
		}
		if( guaCardNum != null ) {
			if(!where.equals("")) {
				where += " and gua_card_num = "+guaCardNum;
			}else {
				where += " gua_card_num = "+guaCardNum;
			}
		}
		
		if(!where.equals("")) {
			strSql+=" where "+ where +" order by time desc limit 1 ";
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> listCol = series.get(0).getColumns();
			List<List<Object>> listVal = series.get(0).getValues();
			List<Object> values = listVal.get(0);
			station.setLatitude(values.get(listCol.indexOf("latitude")).toString());
			station.setLongitude(values.get(listCol.indexOf("longitude")).toString());
			station.setStation_phy_num(Integer.parseInt(values.get(listCol.indexOf("station_phy_num")).toString()));
			station.setStation_name(values.get(listCol.indexOf("station_name")).toString());
			station.setStation_address(values.get(listCol.indexOf("station_address")).toString());
			station.setStation_type(values.get(listCol.indexOf("station_type")).toString());
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
	/*@RequestMapping(value = "/findElectTrace")
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
	}*/
	
	/**
	 * 查询车辆轨迹   查询influxdb 2019-05-05  重写
	 * 
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
		
		String strSql=" SELECT * FROM " + Constant.electStationTable;
		String where = "";
		
		if( startTimeForTrace != null && !"".equals(startTimeForTrace)) {
			where += " time >= '" + startTimeForTrace+"'";
		}
		if( endTimeForTrace != null && !"".equals(endTimeForTrace)) {
			if(!where.equals("")) {
				where += " and time <= '" + endTimeForTrace+"'";
			}else {
				where += " time <= '" + endTimeForTrace+"'";
			}
		}
		
		if( plateNum != null && !"".equals(plateNum)) {
			if(!where.equals("")) {
				where += " and plate_num = '"+plateNum+"'";
			}else {
				
				where += " plate_num = '"+plateNum+"'";
			}
		}
		if( guaCardNum != null ) {
			if(!where.equals("")) {
				where += " and gua_card_num = "+guaCardNum;
			}else {
				where += " gua_card_num = "+guaCardNum;
			}
		}
		
		if(!where.equals("")) {
			strSql+=" where "+ where;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		List<TraceStationDto> traceStationDtos = new ArrayList<TraceStationDto>();
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> listCol = series.get(0).getColumns();
			List<List<Object>> listVal = series.get(0).getValues();
			for (List<Object> lists : listVal) {
				TraceStationDto traceStationDto = new TraceStationDto();
				Station station = new Station();
				station.setStation_phy_num(Integer.parseInt(lists.get(listCol.indexOf("station_phy_num")).toString()));
				station.setStation_name(lists.get(listCol.indexOf("station_name")).toString());
				station.setLongitude(lists.get(listCol.indexOf("longitude")).toString());
				station.setLatitude(lists.get(listCol.indexOf("latitude")).toString());
				station.setStation_address(lists.get(listCol.indexOf("station_address")).toString());
				traceStationDto.setStation(station);
				traceStationDto.setCrossTime(TimeUtil.getInfluxTime(lists.get(listCol.indexOf("time")).toString()));
				traceStationDtos.add(traceStationDto);
			}
		}
		if (traceStationDtos==null||traceStationDtos.size()==0) {
			return new AppResultDto(2001, plateNum + "：未查询到该车辆的轨迹");
		}
		return new AppResultDto(traceStationDtos);
	}
	
	/**
	 * 查询该用户已备案的车辆
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findMyElect")
	@ResponseBody
	public Object findMyElect(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		 Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		 if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	     }
		 AppUser appUser =  appUserMapper.findUserByName(effectiveCookie.getUsername());
		 if (appUser.getUser_tele()==null) {
			return new AppResultDto(3001,"手机号不能为空",false);
		 }
		 List<Electrombile> electrombiles = electrombileMapper.findElectsByTele(appUser.getUser_tele());
		 if (electrombiles==null||electrombiles.size()==0) {
			return new AppResultDto(2001, "未查询到该手机号绑定的车辆");
		 }
	     return new AppResultDto(electrombiles);
	}
	
	/**
	 * 添加报警信息
	 * @param plate_num
	 * @param case_occur_time
	 * @param case_address_type
	 * @param case_detail
	 * @param detail_address
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/addElectAlarm")
	@ResponseBody
	public Object addElectAlarm(@RequestParam(value = "plate_num", required = false) String plate_num,
			@RequestParam(value = "case_occur_time", required = false) String case_occur_time,
			@RequestParam(value = "case_address_type", required = false) String case_address_type,
			@RequestParam(value = "case_detail", required = false) String case_detail,
			@RequestParam(value = "detail_address", required = false) String detail_address,
			@RequestParam(value="token", required=false) String token)
			throws UnsupportedEncodingException, ParseException {
		Blackelect blackelect = new Blackelect();
		if (plate_num == null) {
			return new AppResultDto(3001, "车牌号不能为空！", false);
		}
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		 if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	     }
		List<Blackelect> blackelectExist = blackelectMapper.findAllBlackelectByOptions(plate_num, null, null, null, null, null);
		if (blackelectExist.size()>0) {
			return new AppResultDto(3001, "该车辆已经报警成功！", false);
		}
		Electrombile electrombile=electrombileMapper.findGuaCardNumByPlateNum(plate_num);
		if(electrombile==null){
			return new AppResultDto(3001, "该车牌号不存在！", false);
		}
		blackelect.setPlate_num(electrombile.getPlate_num());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		blackelect.setCase_occur_time(case_occur_time);
		blackelect.setOwner_tele(electrombile.getOwner_tele());
		blackelect.setOwner_name(electrombile.getOwner_name());
		blackelect.setPro_id(electrombile.getPro_id());
		blackelect.setCity_id(electrombile.getCity_id());
		blackelect.setArea_id(electrombile.getArea_id());
		blackelect.setCase_address_type(case_address_type);
		String caseAddress = provinceMapper.selectByProID(electrombile.getPro_id()).getName();
		if (electrombile.getCity_id() != null)
			caseAddress += cityMapper.findCityById(electrombile.getCity_id()).getName();
		if (electrombile.getArea_id() != null)
			caseAddress += cityMapper.findAreaNameByAreaID(electrombile.getArea_id()).getName();
		blackelect.setCase_address(caseAddress);
		blackelect.setCase_detail(case_detail);
		blackelect.setDeal_status(8);
		blackelect.setDetail_address(detail_address);
		blackelectMapper.insert(blackelect);
		Electrombile electRecord = new Electrombile();
		electRecord.setPlate_num(electrombile.getPlate_num());
		electRecord.setElect_state(2);
		electrombileMapper.updateByPlateNumSelective(electRecord);
		return new AppResultDto(1001, "添加成功");
	}
	
	/**
	 * 查询该用户的报警列表
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findMyAlarmElectsList")
	@ResponseBody
	public Object findMyAlarmElectsList(@RequestParam(value="token", required=false) String token)
			throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		 if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	     }
		AppUser appUser =  appUserMapper.findUserByName(effectiveCookie.getUsername());
		List<Blackelect> blackelects = blackelectMapper.findBlackelectsByOwnerTele(appUser.getUser_tele());
		return new AppResultDto(blackelects);
	}
	
	/**
	 * 根据ID删除黑名单
	 * 
	 * @param electID
	 * @return
	 */
	@RequestMapping(value = "/deleteMyAlarmElect")
	@ResponseBody
	public Object deleteMyAlarmElect(
			@RequestParam(value = "blackID", required = false) Integer blackID,
			@RequestParam(value="token", required=false) String token
			) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (blackID==null) {
			 return new AppResultDto(4001, "要删除的id不能为空", false);
		}
		Blackelect blackelect = blackelectMapper.selectByPrimaryKey(blackID);
		if(blackelect!=null) {
			//应该在判断下该报警车的状态是否有改动。
			Electrombile electrombile = new Electrombile();
			electrombile.setPlate_num(blackelect.getPlate_num());
			electrombile.setElect_state(1);
			electrombileMapper.updateByPlateNumSelective(electrombile);
		}
		blackelectMapper.deleteByPrimaryKey(blackID);
		return new AppResultDto(1001, "删除成功");
	}
	
	/**
	 * 设置车辆布防
	 * @param plate_num		车牌号
	 * @param lock_status	锁定状态
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/lockMyElect")
	@ResponseBody
	public Object lockMyElect(
			@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "lockStatus", required = false) Integer lockStatus,
			@RequestParam(value="token", required=false) String token
			) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		}
		Electrombile electrombile=electrombileMapper.findGuaCardNumByPlateNum(plateNum);
		if(electrombile==null){
			return new AppResultDto(3001, "该车牌号不存在！", false);
		}
		if(electrombile!=null) {
			List<Electrombile> openLockElect = electrombileMapper.findAllOpenLockElectByTele(electrombile.getOwner_tele());
			if(electrombile.getLock_status() == 0) {
				if(openLockElect.size()>0) {
					return new AppResultDto(5001, "该用户已有布防车辆，（仅限布防单个车辆）！", false);
				}
			}
			String influxSql=" SELECT * FROM " + Constant.electStationTable 
							+ " where plate_num = '"+electrombile.getPlate_num()+"'"
							+" order by time desc limit 1 ";
			QueryResult results = influxDBConnection
					.query(influxSql);
			Result oneResult = results.getResults().get(0);
			if (oneResult.getSeries() != null) {
				List<Series> series = oneResult.getSeries();
				List<String> listCol = series.get(0).getColumns();
				List<List<Object>> listVal = series.get(0).getValues();
				List<Object> values = listVal.get(0);
				electrombile.setPlate_num(electrombile.getPlate_num());
				electrombile.setLock_status(lockStatus);
				if(lockStatus == 1) {
					electrombile.setAlarm_sms(0);
				}
				electrombile.setLock_station(values.get(listCol.indexOf("station_name")).toString());
				electrombile.setLock_address(values.get(listCol.indexOf("station_address")).toString());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				electrombile.setLock_time(sdf.format(System.currentTimeMillis()));
				electrombileMapper.updateByPlateNumSelective(electrombile);
			} else {
				return new AppResultDto(2001, "该车牌号未经过任何基站,无法锁定！", false);
			}
		}
		return new AppResultDto(1001, "修改成功");
	}
	
	@RequestMapping(value = "/lockMyElectTrack")
	@ResponseBody
	public Object lockMyElectTrack(
			@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "lockTime", required = false) String lockTime,
			@RequestParam(value="token", required=false) String token
			) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		}
		Electrombile electrombile=electrombileMapper.findGuaCardNumByPlateNum(plateNum);
		
		if(electrombile!=null) {
			String where = "";
			if( lockTime != null && !"".equals(lockTime)) {
				where += " and lock_time = '" + lockTime+"'";
			}
			String influxSql=" SELECT * FROM " + Constant.electStationTable 
							+ " where plate_num = '"+electrombile.getPlate_num()+"'"
							+ where
							+" group by lock_time ";
			QueryResult results = influxDBConnection
					.query(influxSql);
			Result oneResult = results.getResults().get(0);
			List<ElectLockTraceDto> electLockTrackDtos = new ArrayList<ElectLockTraceDto>();
			if (oneResult.getSeries() != null) {
				List<Series> seriesList = oneResult.getSeries();
				for (Series series : seriesList) {
					List<String> listCol = series.getColumns();
					List<List<Object>> listVal = series.getValues();
					String lock_time = series.getTags().get("lock_time");
					ElectLockTraceDto electLock = new ElectLockTraceDto();
					electLock.setLockTime(lock_time);
					electLock.setPlateNum(plateNum);
					List<TraceStationDto> traceStations = new ArrayList<TraceStationDto>();
					for (List<Object> lists : listVal) {
						TraceStationDto traceStationDto = new TraceStationDto();
						Station station = new Station();
						station.setStation_phy_num(Integer.parseInt(lists.get(listCol.indexOf("station_phy_num")).toString()));
						station.setStation_name(lists.get(listCol.indexOf("station_name")).toString());
						station.setLongitude(lists.get(listCol.indexOf("longitude")).toString());
						station.setLatitude(lists.get(listCol.indexOf("latitude")).toString());
						station.setStation_address(lists.get(listCol.indexOf("station_address")).toString());
						traceStationDto.setStation(station);
						traceStationDto.setCrossTime(TimeUtil.getInfluxTime(lists.get(listCol.indexOf("time")).toString()));
						traceStations.add(traceStationDto);
					}
					electLock.setTraceStation(traceStations);
					electLockTrackDtos.add(electLock);
				}
				return new AppResultDto(electLockTrackDtos);
			} else {
				return new AppResultDto(3001, "该车牌号未经过任何基站！", false);
			}
		}else {
			return new AppResultDto(3001, "该车牌号不存在！", false);
		}
		
	}
	
	
	
}
