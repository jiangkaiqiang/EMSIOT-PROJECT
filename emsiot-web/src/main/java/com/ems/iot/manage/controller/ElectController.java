package com.ems.iot.manage.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.util.PublicSuffixList;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.PeopleMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectrombileDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.StationElectDto;
import com.ems.iot.manage.dto.Thermodynamic;
import com.ems.iot.manage.dto.TraceStationDto;
import com.ems.iot.manage.dto.ElectCountAndList;
import com.ems.iot.manage.entity.Area;
import com.ems.iot.manage.entity.City;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Province;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.OssService;
import com.ems.iot.manage.util.Constant;
import com.ems.iot.manage.util.ExcelImportUtil;
import com.ems.iot.manage.util.InfluxDBConnection;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sun.misc.BASE64Decoder;

/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/elect")
public class ElectController extends BaseController {
	private static String baseDir = "picture";
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private PeopleMapper peopleMapper;
	
	InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
	/**
	 * 根据电动车的ID寻找电动车
	 * 
	 * @param electID
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectByID")
	@ResponseBody
	public Object findElectByID(@RequestParam(value = "electID", required = false) Integer electID)
			throws UnsupportedEncodingException {
		Electrombile electrombile = electrombileMapper.selectByPrimaryKey(electID);
		return electrombile;
	}

	/**
	 * 根据基站的物理编号和时间，查询某个基站下的车辆，为页面点击基站显示基站下的车辆提供服务
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsCountByStationIdAndTime")
	@ResponseBody
	public Object findElectsCountByStationIdAndTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum)
					throws UnsupportedEncodingException {
		
//		Integer count = electrombileStationMapper.selectElectsCountByStationPhyNumAndTime(stationPhyNum, startTime, endTime);
//		return count;
		
		//查询influxdb 2019-01-29
		int count = 0;
		String strSql=" SELECT count(*) FROM " + Constant.electStationTable;
		String where = "";
		
		if( startTime != null && !"".equals(startTime)) {
			where += " time >= '" + startTime+"'";
		}
		if( endTime != null && !"".equals(endTime)) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date date=null;
			Calendar calendar = Calendar.getInstance();
			try {
				
				date=sdf.parse(endTime);
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!where.equals("")) {
				where += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				where += " time < '" + sdf.format(calendar.getTime())+"'";
			}
		}
		if( proPower != null) {
			if(!where.equals("")) {
				where += " and pro_id = "+proPower;
			}else {
		
				where += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!where.equals("")) {
				where += " and city_id = "+cityPower;
			}else {
				where += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!where.equals("")) {
				where += " and area_id = "+areaPower;
			}else {
				where += " area_id = "+areaPower;
			}
		}
		if( stationPhyNum != null) {
			if(!where.equals("")) {
				where += " and station_phy_num = '"+stationPhyNum+"'";
			}else {
				where += " station_phy_num = '"+stationPhyNum+"'";
			}
		}
		if(!where.equals("")) {
			strSql+=" where "+where;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			
			List<List<Object>> listVal = series.get(0).getValues();
			count = (int)Float.parseFloat(listVal.get(0).get(1).toString());
			
		}
		return count;
		
	}
	
	public List<Integer> areaCar(Integer proPower,Integer cityPower,Integer areaPower) {
		String strSqlCard=" SELECT DISTINCT(gua_card_num) FROM " + Constant.electStationTable;
		String whereCard = "";
		if( proPower != null) {
			if(!whereCard.equals("")) {
				whereCard += " and pro_id = "+proPower;
			}else {
		
				whereCard += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!whereCard.equals("")) {
				whereCard += " and city_id = "+cityPower;
			}else {
				whereCard += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!whereCard.equals("")) {
				whereCard += " and area_id = "+areaPower;
			}else {
				whereCard += " area_id = "+areaPower;
			}
		}
		if(!whereCard.equals("")) {
			strSqlCard+=" where "+ whereCard ;
		}
		QueryResult resultsCard = influxDBConnection
				.query(strSqlCard);
		Result oneResultCard = resultsCard.getResults().get(0);
		
		List<Integer> cardArray=new ArrayList<Integer>();
		if (oneResultCard.getSeries() != null) {
			List<Series> seriesCard = oneResultCard.getSeries();
			List<List<Object>> listVal = seriesCard.get(0).getValues();
			for (List<Object> lists : listVal) {
				cardArray.add((int)Float.parseFloat(lists.get(1).toString()));
			}
		}
		return cardArray;
	}
	/**
	 * 根据基站的物理编号和时间，查询某个基站下的车辆，为页面点击基站显示基站下的车辆提供服务
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsCountByStationsIdAndTime")
	@ResponseBody
	public Object findElectsCountByStationsIdAndTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "stationPhyNumStr", required = false) String stationPhyNumStr,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower)
					throws UnsupportedEncodingException {
		
//		Integer count = electrombileStationMapper.selectElectsCountByStationPhyNumAndTime(stationPhyNum, startTime, endTime);
//		return count;
		String[] stationPhyNums=null;
		if(stationPhyNumStr!=null) {
			stationPhyNums=stationPhyNumStr.split(",");
		}
		
		//查询该区域的车辆
		List<Integer> cardArray = areaCar(proPower, cityPower, areaPower);
		ElectCountAndList countAndList=new ElectCountAndList();
		
		
		
		
		//查询influxdb 2019-01-29
		int count = 0;
//		Date sysDate = new Date();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		startTime = sdf.format(sysDate);
//		endTime = sdf.format(sysDate);
		String strSql=" SELECT * FROM " + Constant.electStationTable;
		String where = "";
		String timeCond = "";
		if( startTime != null && !"".equals(startTime)) {
			timeCond += " time >= '" + startTime+"'";
		}
		if( endTime != null && !"".equals(endTime)) {
			//SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date date=null;
			Calendar calendar = Calendar.getInstance();
			try {
				
				date=sdf.parse(endTime);
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!timeCond.equals("")) {
				timeCond += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				timeCond += " time < '" + sdf.format(calendar.getTime())+"'";
			}
		}
		if( proPower != null) {
			if(!timeCond.equals("")) {
				timeCond += " and pro_id = "+proPower;
			}else {
		
				timeCond += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!timeCond.equals("")) {
				timeCond += " and city_id = "+cityPower;
			}else {
				timeCond += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!timeCond.equals("")) {
				timeCond += " and area_id = "+areaPower;
			}else {
				timeCond += " area_id = "+areaPower;
			}
		}
		
		Map<String, Integer> mapList=new HashMap<String, Integer>();
		Map<String, List<StationElectDto>> mapElectList=new HashMap<String, List<StationElectDto>>();
		String sql="";
		for (Integer str : cardArray) {
			
				if(!timeCond.equals("")) {
					where = " and gua_card_num = "+str;
				}else {
					where = " gua_card_num = "+str;
				}

				sql=strSql+" where "+timeCond+where+" order by time desc limit 1";
				
				QueryResult results = influxDBConnection
						.query(sql);
				Result oneResult = results.getResults().get(0);
				if (oneResult.getSeries() != null) {
					List<Series> series = oneResult.getSeries();
					List<String> listCol = series.get(0).getColumns();
					List<List<Object>> listVal = series.get(0).getValues();
					count = series.get(0).getValues().size();
					String stationNum = listVal.get(0).get(listCol.indexOf("station_phy_num")).toString();
					Integer cardNum = (int)Float.parseFloat(listVal.get(0).get(listCol.indexOf("gua_card_num")).toString());
					if(mapList.containsKey(stationNum)) {
						mapList.put(stationNum, mapList.get(stationNum)+count);
					}else {
						mapList.put(stationNum, count);
					}
					//基站下车辆的数据
					//List<StationElectDto> listElect = new ArrayList<StationElectDto>();
					StationElectDto elect = new StationElectDto();
					elect.setOwner_name(listVal.get(0).get(listCol.indexOf("owner_name")).toString());
					elect.setPlate_num(listVal.get(0).get(listCol.indexOf("plate_num")).toString());
					elect.setStation_name(listVal.get(0).get(listCol.indexOf("station_name")).toString());
					elect.setCorssTime(listVal.get(0).get(listCol.indexOf("hard_read_time")).toString());
					
					if(mapElectList.containsKey(stationNum)) {
						List<StationElectDto> listElect = mapElectList.get(stationNum);
						listElect.add(elect);
						mapElectList.put(stationNum, listElect);
					}else {
						//基站下车辆的数据
						List<StationElectDto> listElect = new ArrayList<StationElectDto>();
						listElect.add(elect);
						mapElectList.put(stationNum, listElect);
					}
				}
			
			
		}
		countAndList.setStationElectList(mapElectList);
		countAndList.setElectCountByStation(mapList);
		return countAndList;
		
	}
	/**
	 * 根据基站的物理编号和时间，查询某个基站下的车辆，为页面点击基站显示基站下的车辆提供服务
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsByStationIdAndTime")
	@ResponseBody
	public Object findElectsByStationIdAndTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum)
			throws UnsupportedEncodingException {
		/*List<ElectrombileStation> electrombileStations = electrombileStationMapper
				.selectElectsByStationPhyNumAndTime(stationPhyNum, startTime, endTime);
		List<StationElectDto> stationElectDtos = new ArrayList<StationElectDto>();
		for (ElectrombileStation electrombileStation : electrombileStations) {
			StationElectDto stationElectDto = new StationElectDto();
			stationElectDto.setCorssTime(electrombileStation.getUpdate_time());
			Electrombile electrombile = electrombileMapper
					.findPlateNumByGuaCardNum(electrombileStation.getEle_gua_card_num());
			stationElectDto.setOwner_name(electrombile.getOwner_name());
			stationElectDto.setPlate_num(electrombile.getPlate_num());
			stationElectDtos.add(stationElectDto);
		}*/
		//List<StationElectDto> stationElectDtos = electrombileStationMapper.selectElectsByStationPhyNumAndTime2(stationPhyNum, startTime, endTime);
		//return stationElectDtos;
		List<StationElectDto> listElect = new ArrayList<StationElectDto>();
		if("0".equals(limit)) {
			return listElect;
		}
		//查询influxdb 2019-01-29
		String strSql=" SELECT * FROM " + Constant.electStationTable;
		String where = "";
		
		if( startTime != null && !"".equals(startTime)) {
			where += " time >= '" + startTime+"'";
		}
		if( endTime != null && !"".equals(endTime)) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date date=null;
			Calendar calendar = Calendar.getInstance();
			try {
				
				date=sdf.parse(endTime);
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!where.equals("")) {
				where += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				where += " time < '" + sdf.format(calendar.getTime())+"'";
			}
		}
		if( proPower != null) {
			if(!where.equals("")) {
				where += " and pro_id = "+proPower;
			}else {
		
				where += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!where.equals("")) {
				where += " and city_id = "+cityPower;
			}else {
				where += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!where.equals("")) {
				where += " and area_id = "+areaPower;
			}else {
				where += " area_id = "+areaPower;
			}
		}
		if( stationPhyNum != null) {
			if(!where.equals("")) {
				where += " and station_phy_num = '"+stationPhyNum+"'";
			}else {
				where += " station_phy_num = '"+stationPhyNum+"'";
			}
		}
		if(!where.equals("")) {
			strSql+=" where "+where +" order by time desc limit "+limit;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> listCol = series.get(0).getColumns();
			List<List<Object>> listVal = series.get(0).getValues();
			for (List<Object> lists : listVal) {
				StationElectDto elect = new StationElectDto();
				elect.setOwner_name(lists.get(listCol.indexOf("owner_name")).toString());
				elect.setPlate_num(lists.get(listCol.indexOf("plate_num")).toString());
				elect.setStation_name(lists.get(listCol.indexOf("station_name")).toString());
				elect.setCorssTime(lists.get(listCol.indexOf("hard_read_time")).toString());
				listElect.add(elect);
			}
		}
		return listElect;
	}

	/**
	 * 返回所有基站下，当前所拥有的车辆数量，为车辆热力图提供支持
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsNumByStations")
	@ResponseBody
	public Object findElectsNumByStations(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower) throws UnsupportedEncodingException {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		List<Thermodynamic> thermodynamics = electrombileStationMapper.selectElectsByStationPhyNumNow(proPower,cityPower,areaPower);
		return thermodynamics;
	}

	/**
	 * 找到最近一段时间的活跃车辆，为在线车辆功能提供服务
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findInlineElectsNum")
	@ResponseBody
	public Object findInlineElectsNum(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower)
			throws UnsupportedEncodingException {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		/*List<Integer> inlineList = electrombileStationMapper.selectElectsByEleGuaCardNumNow(proPower, cityPower,
				areaPower);
		return inlineList.size();*/
		
		
		int count = 0;
		/*Date sysDate = new Date();
		String startTime = null;
		String endTime = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		startTime = sdf.format(sysDate);
		endTime = sdf.format(sysDate);*/
		String strSql=" SELECT count(DISTINCT(gua_card_num)) FROM " + Constant.electStationTable;
		String where = "";
		/*if( startTime != null && !"".equals(startTime)) {
			where += " time >= '" + startTime+"'";
		}
		if( endTime != null && !"".equals(endTime)) {
			
			Date date=null;
			Calendar calendar = Calendar.getInstance();
			try {
				
				date=sdf.parse(endTime);
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!where.equals("")) {
				where += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				where += " time < '" + sdf.format(calendar.getTime())+"'";
			}
		}*/
		if( proPower != null) {
			if(!where.equals("")) {
				where += " and pro_id = "+proPower;
			}else {
		
				where += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!where.equals("")) {
				where += " and city_id = "+cityPower;
			}else {
				where += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!where.equals("")) {
				where += " and area_id = "+areaPower;
			}else {
				where += " area_id = "+areaPower;
			}
		}
		
		if(!where.equals("")) {
			strSql+=" where "+ where ;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			
			List<List<Object>> listVal = series.get(0).getValues();
			count = (int)Float.parseFloat(listVal.get(0).get(1).toString());
			
		}
		return count;

	}

	/**
	 * 根据各种关键字查询车辆
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param recorderID
	 *            录入人ID
	 * @param electState
	 *            车辆状态
	 * @param insurDetail
	 *            是否投保
	 * @param proID
	 *            所属区域——省
	 * @param cityID
	 *            所属区域——市
	 * @param areaID
	 *            所属区域——县
	 * @param ownerTele
	 *            车主手机号
	 * @param ownerID
	 *            车主身份证号
	 * @param plateNum
	 *            车牌号
	 * @param guaCardNum
	 *            防盗芯片编号
	 * @param ownerName
	 *            车主姓名
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectList", method = RequestMethod.POST)
	@ResponseBody
	public Object findElectList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "recorderID", required = false) Integer recorderID,
			@RequestParam(value = "electState", required = false) Integer electState,
			@RequestParam(value = "insurDetail", required = false) Integer insurDetail,
			@RequestParam(value = "proID", required = false) Integer proID,
			@RequestParam(value = "cityID", required = false) Integer cityID,
			@RequestParam(value = "areaID", required = false) Integer areaID,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "ownerTele", required = false) String ownerTele,
			@RequestParam(value = "ownerID", required = false) String ownerID,
			@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "guaCardNum", required = false) String guaCardNum,
			@RequestParam(value = "ownerName", required = false) String ownerName) throws UnsupportedEncodingException {
		if (null == recorderID || recorderID == 0) {
			recorderID = null;
		}
		if (null == electState || electState == 8) {
			electState = null;
		}
		if (null == insurDetail || insurDetail == 8) {
			insurDetail = null;
		}
		if (null == proID || proID == -1) {
			proID = null;
		}
		if (null == cityID || cityID == -1) {
			cityID = null;
		}
		if (null == areaID || areaID == -1) {
			areaID = null;
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
		pageNum = pageNum == null ? 1 : pageNum;
		pageSize = pageSize == null ? 12 : pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Electrombile> electrombiles = electrombileMapper.findAllElectrombiles(startTime, endTime, recorderID,
				electState, insurDetail, proID, cityID, areaID, ownerTele, ownerID, plateNum, guaCardNum, ownerName,
				proPower, cityPower, areaPower);
		Page<ElectrombileDto> electrombileDtos = new Page<ElectrombileDto>();
		for (Electrombile electrombile : electrombiles) {
			ElectrombileDto electrombileDto = new ElectrombileDto();
			electrombileDto.setElectrombile(electrombile);
			Province province = cityMapper.findProvinceById(electrombile.getPro_id());
			if (province!=null) {
				electrombileDto.setProvinceName(province.getName());
			}
			City city = cityMapper.findCityById(electrombile.getCity_id());
			if (city!=null) {
				electrombileDto.setCityName(city.getName());
			}
			Area area = cityMapper.findAreaNameByAreaID(electrombile.getArea_id());
			if (area!=null) {
				electrombileDto.setAreaName(area.getName());
			}
			SysUser sysUser = sysUserMapper.findUserById(electrombile.getRecorder_id());
			if (sysUser!=null) {
				electrombileDto.setRecordName(sysUser.getUser_name());
			}
			electrombileDtos.add(electrombileDto);
		}
		electrombileDtos.setPageSize(electrombiles.getPageSize());
		electrombileDtos.setPages(electrombiles.getPages());
		electrombileDtos.setTotal(electrombiles.getTotal());
		return new PageInfo<ElectrombileDto>(electrombileDtos);
	}

	/**
	 * 添加车辆
	 * 
	 * @param electrombile
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@RequestMapping(value = "/addElect")
	@ResponseBody
	public Object addElect(@RequestParam(required = false) Integer gua_card_num,
			@RequestParam(required = false) String plate_num, @RequestParam(required = false) String ve_id_num,
			@RequestParam(required = false) String elect_brand, @RequestParam(required = false) String buy_date,
			@RequestParam(required = false) String elect_color, @RequestParam(required = false) String motor_num,
			@RequestParam(required = false) String note, @RequestParam(required = false) Integer pro_id,
			@RequestParam(required = false) Integer city_id, @RequestParam(required = false) Integer area_id,
			@RequestParam(required = false) Integer elect_type, @RequestParam(required = false) Integer insur_detail,
			@RequestParam(required = false) MultipartFile elect_pic,
			@RequestParam(required = false) String indentity_card_pic,
			@RequestParam(required = false) MultipartFile record_pic,
			@RequestParam(required = false) MultipartFile install_card_pic,
			@RequestParam(required = false) MultipartFile insur_pic,
			@RequestParam(required = false) MultipartFile tele_fee_pic,
			@RequestParam(required = false) String owner_tele, @RequestParam(required = false) String owner_name,
			@RequestParam(required = false) String owner_address, @RequestParam(required = false) String owner_id,
			
			@RequestParam(required = false) String owner_id_type, @RequestParam(required = false) String owner_sex,
			@RequestParam(required = false) String owner_nationality, @RequestParam(required = false) String owner_id_issuing_authority,
			@RequestParam(required = false) String owner_id_useful_life,
			@RequestParam(required = false) String owner_pic,
			@RequestParam(required = false) String owner_born,
			
			@RequestParam(required = false) Integer recorder_id, @RequestParam(required = false) Integer elect_state)
			throws ParseException, IOException {
		if (gua_card_num == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (plate_num == null) {
			return new ResultDto(-1, "车牌号不能为空！");
		}
		if (pro_id==null) {
			return new ResultDto(-1, "车牌号所属省不能为空");
		}
		if (city_id==null || city_id==-1) {
			return new ResultDto(-1, "车牌号所属市不能为空");
		}
		if (area_id==null || area_id==-1) {
			return new ResultDto(-1, "车牌号所属区/县不能为空");
		}
		if (owner_tele==null || "".equals(owner_tele)){
			return new ResultDto(-1, "车主手机号不能为空");
		}
		if (owner_name==null || "".equals(owner_name)){
			return new ResultDto(-1, "车主姓名不能为空");
		}
		if (owner_id==null || "".equals(owner_id)){
			return new ResultDto(-1, "车主身份证号不能为空");
		}
		if (electrombileMapper.findElectForFilter(gua_card_num, null)!=null) {
			return new ResultDto(-1, "防盗芯片编号已存在，不可重复添加！");
		}
		if (peopleMapper.findPlateNumByPeopleGuaCardNum(gua_card_num)!=null) {
			return new ResultDto(-1, "防盗芯片编号在人员备案中已存在！");
		}
		if (electrombileMapper.findElectForFilter(null, plate_num)!=null) {
			return new ResultDto(-1, "车牌号已存在，不可重复添加！");
		}
		Electrombile electrombile = new Electrombile();
		electrombile.setGua_card_num(gua_card_num);
		electrombile.setPlate_num(plate_num);
		electrombile.setVe_id_num(ve_id_num);
		electrombile.setElect_brand(elect_brand);
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// electrombile.setBuy_date(sdf.parse(buy_date));
		electrombile.setBuy_date(buy_date);
		electrombile.setElect_color(elect_color);
		electrombile.setMotor_num(motor_num);
		electrombile.setNote(note);
		electrombile.setPro_id(pro_id);
		electrombile.setCity_id(city_id);
		electrombile.setArea_id(area_id);
		electrombile.setElect_type(elect_type);
		electrombile.setInsur_detail(insur_detail);
		electrombile.setOwner_tele(owner_tele);
		electrombile.setOwner_name(owner_name);
		electrombile.setOwner_address(owner_address);
		electrombile.setOwner_id(owner_id);
		electrombile.setRecorder_id(recorder_id);
		electrombile.setElect_state(elect_state);
		
		electrombile.setOwner_id_type(owner_id_type);
		electrombile.setOwner_sex(owner_sex);
		electrombile.setOwner_nationality(owner_nationality);
		electrombile.setOwner_id_issuing_authority(owner_id_issuing_authority);
		electrombile.setOwner_id_useful_life(owner_id_useful_life);
		electrombile.setOwner_born(owner_born);
		
		// 创建OSSClient实例。
	    OSSClient ossClient = new OSSClient(OssService.endpoint, OssService.accessKeyId, OssService.accessKeySecret);
	    //本人照片
	    /*if (null != owner_pic && !"".equals(owner_pic)) {
	    	String dir = String.format("%s/ownerPic/", baseDir);
	    	String owner_pic_name = String.format("%s_%s.%s", electrombile.getOwner_id(), electrombile.getOwner_name(), "jpg");
	    	// 上传文件流。
	    	
	    	InputStream file = new FileInputStream(owner_pic);
	    	
	    	InputStream inputStream = owner_pic.getInputStream();
	    	ossClient.putObject("emsiot", dir+owner_pic_name, file);
	    	electrombile.setOwner_pic(OssService.readUrl + dir+owner_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
	    }*/
	    //本人身份证照片
	    if (null != indentity_card_pic && !"".equals(indentity_card_pic)) {
	    	String dir = String.format("%s/ownerPic/", baseDir);
	    	String indentity_card_pic_name = String.format("%s_%s.%s", electrombile.getOwner_id(), "h", "jpg");
	    	// 上传文件流。
	    	
	    	InputStream file = new FileInputStream(indentity_card_pic);
	    	
	    	/*InputStream inputStream = owner_pic.getInputStream();*/
	    	ossClient.putObject("emsiot", dir+indentity_card_pic_name, file);
	    	electrombile.setIndentity_card_pic(OssService.readUrl + dir+indentity_card_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
	    }
		if (null != elect_pic) {
			String dir = String.format("%s/electPic/", baseDir);
			String elect_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = elect_pic.getInputStream();
			ossClient.putObject("emsiot", dir+elect_pic_name, inputStream);
			electrombile.setElect_pic(OssService.readUrl + dir+elect_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
		/*if (null != indentity_card_pic) {
			String dir = String.format("%s/indCardPic/", baseDir);
			String indentity_card_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = indentity_card_pic.getInputStream();
			ossClient.putObject("emsiot", dir+indentity_card_pic_name, inputStream);
			electrombile.setIndentity_card_pic(OssService.readUrl + dir+indentity_card_pic_name);
		}*/
		if (null != record_pic) {
			String dir = String.format("%s/recordPic/", baseDir);
			String record_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = record_pic.getInputStream();
			ossClient.putObject("emsiot", dir+record_pic_name, inputStream);
			electrombile.setRecord_pic(OssService.readUrl + dir+record_pic_name);
		}
		if (null != install_card_pic) {
			String dir = String.format("%s/installCardPic/", baseDir);
			String install_card_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = install_card_pic.getInputStream();
			ossClient.putObject("emsiot", dir+install_card_pic_name, inputStream);
			electrombile.setInstall_card_pic(OssService.readUrl + dir+install_card_pic_name);
		}
		if (null!=insur_pic) {
			String dir = String.format("%s/insurPic/", baseDir);
			String insur_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = insur_pic.getInputStream();
			ossClient.putObject("emsiot", dir+insur_pic_name, inputStream);
			electrombile.setInsur_pic(OssService.readUrl + dir+insur_pic_name);
		}
		if (null!=tele_fee_pic) {
			String dir = String.format("%s/telefeePic/", baseDir);
			String tele_fee_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = tele_fee_pic.getInputStream();
			ossClient.putObject("emsiot", dir+tele_fee_pic_name, inputStream);
			electrombile.setTele_fee_pic(OssService.readUrl + dir+tele_fee_pic_name);
		}
		//electrombileMapper.insert(electrombile);
		electrombileMapper.insertSelective(electrombile);
		 // 关闭OSSClient。
	 	ossClient.shutdown();
		return new ResultDto(0, "添加成功");
	}

	/**
	 * 更新车辆
	 * 
	 * @param electrombile
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/updateElect")
	@ResponseBody
	public Object updateElect(@RequestParam(required = false) Integer elect_id,
			@RequestParam(required = false) Integer gua_card_num, @RequestParam(required = false) String plate_num,
			@RequestParam(required = false) String ve_id_num, @RequestParam(required = false) String elect_brand,
			@RequestParam(required = false) String buy_date, @RequestParam(required = false) String elect_color,
			@RequestParam(required = false) String motor_num, @RequestParam(required = false) String note,
			@RequestParam(required = false) Integer pro_id, @RequestParam(required = false) Integer city_id,
			@RequestParam(required = false) Integer area_id, @RequestParam(required = false) Integer elect_type,
			@RequestParam(required = false) Integer insur_detail,
			@RequestParam(required = false) MultipartFile elect_pic,
			@RequestParam(required = false) String indentity_card_pic,
			@RequestParam(required = false) MultipartFile record_pic,
			@RequestParam(required = false) MultipartFile install_card_pic,
			@RequestParam(required = false) MultipartFile insur_pic,
			@RequestParam(required = false) MultipartFile tele_fee_pic,
			@RequestParam(required = false) String owner_tele, @RequestParam(required = false) String owner_name,
			@RequestParam(required = false) String owner_address, @RequestParam(required = false) String owner_id,
			
			@RequestParam(required = false) String owner_id_type, @RequestParam(required = false) String owner_sex,
			@RequestParam(required = false) String owner_nationality, @RequestParam(required = false) String owner_id_issuing_authority,
			@RequestParam(required = false) String owner_id_useful_life,
			@RequestParam(required = false) String owner_pic,
			@RequestParam(required = false) String owner_born,
			
			@RequestParam(required = false) Integer recorder_id, @RequestParam(required = false) Integer elect_state)
			throws ParseException, IOException {
		if (gua_card_num == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (plate_num == null) {
			return new ResultDto(-1, "车牌号不能为空！");
		}
		if (pro_id==null) {
			return new ResultDto(-1, "车牌号所属省不能为空");
		}
		if (city_id==null) {
			return new ResultDto(-1, "车牌号所属市不能为空");
		}
		if (area_id==null) {
			return new ResultDto(-1, "车牌号所属区/县不能为空");
		}
		if (owner_tele==null || "".equals(owner_tele) ){
			return new ResultDto(-1, "车主手机号不能为空");
		}
		if (owner_name==null || "".equals(owner_name)){
			return new ResultDto(-1, "车主姓名不能为空");
		}
		if (owner_id==null || "".equals(owner_id)){
			return new ResultDto(-1, "车主身份证号不能为空");
		}
		if (peopleMapper.findPlateNumByPeopleGuaCardNum(gua_card_num)!=null) {
			return new ResultDto(-1, "防盗芯片编号在人员备案中已存在！");
		}
//		if (electrombileMapper.findElectForFilter(gua_card_num, null)!=null) {
//			return new ResultDto(-1, "防盗芯片编号已存在，不可重复添加！");
//		}
//		if (electrombileMapper.findElectForFilter(null, plate_num)!=null) {
//			return new ResultDto(-1, "车牌号已存在，不可重复添加！");
//		}
		Electrombile electrombile = new Electrombile();
		electrombile.setElect_id(elect_id);
		electrombile.setGua_card_num(gua_card_num);
		electrombile.setPlate_num(plate_num);
		electrombile.setVe_id_num(ve_id_num);
		electrombile.setElect_brand(elect_brand);
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// electrombile.setBuy_date(sdf.parse(buy_date));
		electrombile.setBuy_date(buy_date);
		electrombile.setElect_color(elect_color);
		electrombile.setMotor_num(motor_num);
		electrombile.setNote(note);
		electrombile.setPro_id(pro_id);
		electrombile.setCity_id(city_id);
		electrombile.setArea_id(area_id);
		electrombile.setElect_type(elect_type);
		electrombile.setInsur_detail(insur_detail);
		electrombile.setOwner_tele(owner_tele);
		electrombile.setOwner_name(owner_name);
		electrombile.setOwner_address(owner_address);
		electrombile.setOwner_id(owner_id);
		electrombile.setRecorder_id(recorder_id);
		electrombile.setElect_state(elect_state);
		
		electrombile.setOwner_id_type(owner_id_type);
		electrombile.setOwner_sex(owner_sex);
		electrombile.setOwner_nationality(owner_nationality);
		electrombile.setOwner_id_issuing_authority(owner_id_issuing_authority);
		electrombile.setOwner_id_useful_life(owner_id_useful_life);
		electrombile.setOwner_born(owner_born);
		
		// 创建OSSClient实例。
	    OSSClient ossClient = new OSSClient(OssService.endpoint, OssService.accessKeyId, OssService.accessKeySecret);
	   //修改本人照片
	   /* if (null != owner_pic) {
	    	String dir = String.format("%s/ownerPic/", baseDir);
	    	String owner_pic_name = String.format("%s_%s.%s", electrombile.getOwner_id(), electrombile.getOwner_name(), "jpg");
	    	// 上传文件流。
	    	
	    	InputStream file = new FileInputStream(owner_pic);

	    	ossClient.putObject("emsiot", dir+owner_pic_name, file);
	    	electrombile.setOwner_pic(OssService.readUrl + dir+owner_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
	    }*/
	  //修改本人身份证照片
	    if (null != indentity_card_pic && !"".equals(indentity_card_pic) && indentity_card_pic.indexOf("C:")>-1) {
	    	String dir = String.format("%s/ownerPic/", baseDir);
	    	String indentity_card_pic_name = String.format("%s_%s.%s", electrombile.getOwner_id(), "h", "jpg");
	    	// 上传文件流。
	    	
	    	InputStream file = new FileInputStream(indentity_card_pic);
	    	
	    	/*InputStream inputStream = owner_pic.getInputStream();*/
	    	ossClient.putObject("emsiot", dir+indentity_card_pic_name, file);
	    	electrombile.setIndentity_card_pic(OssService.readUrl + dir+indentity_card_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
	    }
		if (null != elect_pic) {
			String dir = String.format("%s/electPic/", baseDir);
			String elect_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = elect_pic.getInputStream();
			ossClient.putObject("emsiot", dir+elect_pic_name, inputStream);
			electrombile.setElect_pic(OssService.readUrl + dir+elect_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
		/*if (null != indentity_card_pic) {
			String dir = String.format("%s/indCardPic/", baseDir);
			String indentity_card_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = indentity_card_pic.getInputStream();
			ossClient.putObject("emsiot", dir+indentity_card_pic_name, inputStream);
			electrombile.setIndentity_card_pic(OssService.readUrl + dir+indentity_card_pic_name);
		}*/
		if (null != record_pic) {
			String dir = String.format("%s/recordPic/", baseDir);
			String record_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = record_pic.getInputStream();
			ossClient.putObject("emsiot", dir+record_pic_name, inputStream);
			electrombile.setRecord_pic(OssService.readUrl + dir+record_pic_name);
		}
		if (null != install_card_pic) {
			String dir = String.format("%s/installCardPic/", baseDir);
			String install_card_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = install_card_pic.getInputStream();
			ossClient.putObject("emsiot", dir+install_card_pic_name, inputStream);
			electrombile.setInstall_card_pic(OssService.readUrl + dir+install_card_pic_name);
		}
		if (null!=insur_pic) {
			String dir = String.format("%s/insurPic/", baseDir);
			String insur_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = insur_pic.getInputStream();
			ossClient.putObject("emsiot", dir+insur_pic_name, inputStream);
			electrombile.setInsur_pic(OssService.readUrl + dir+insur_pic_name);
		}
		if (null!=tele_fee_pic) {
			String dir = String.format("%s/telefeePic/", baseDir);
			String tele_fee_pic_name = String.format("%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = tele_fee_pic.getInputStream();
			ossClient.putObject("emsiot", dir+tele_fee_pic_name, inputStream);
			electrombile.setTele_fee_pic(OssService.readUrl + dir+tele_fee_pic_name);
		}
		electrombileMapper.updateByPrimaryKeySelective(electrombile);
		 // 关闭OSSClient。
	 	ossClient.shutdown();
		return new ResultDto(0, "更新成功");
	}

	/**
	 * 根据ID删除车辆
	 * 
	 * @param electID
	 * @return
	 */
	@RequestMapping(value = "/deleteElectByID")
	@ResponseBody
	public Object deleteElectByID(Integer electID) {
		electrombileMapper.deleteByPrimaryKey(electID);
		return new BaseDto(0);
	}

	/**
	 * 根据多个ID删除车辆
	 * 
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteElectByIDs")
	@ResponseBody
	public Object deleteElectByIDs(Integer[] electIDs) {
		for (Integer electID : electIDs) {
			electrombileMapper.deleteByPrimaryKey(electID);
		}
		return new BaseDto(0);
	}

	/**
	 * 导出车辆为excel
	 * 
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/exportElectByIDs")
	@ResponseBody
	public Object exportElectByIDs(Integer[] electIDs) {
		List<Electrombile> electrombiles = new ArrayList<Electrombile>();
		for (Integer electID : electIDs) {
			Electrombile electrombile = electrombileMapper.selectByPrimaryKey(electID);
			electrombiles.add(electrombile);
		}
		Map<String, Object> dataExel = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("车牌号");
		titles.add("防盗芯片编号");
		titles.add("所属地区");
		titles.add("联系电话");
		titles.add("车辆状态");
		titles.add("车主姓名");
		titles.add("身份证号");
		titles.add("添加时间");
		List<Map<String, Object>> varList = new ArrayList<Map<String, Object>>();
		for (Electrombile electrombile : electrombiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("var1", electrombile.getPlate_num());
			map.put("var2", electrombile.getGua_card_num().toString());
			String area = "";
			area = area + cityMapper.findProvinceById(electrombile.getPro_id()).getName();
			area = area + cityMapper.findCityById(electrombile.getCity_id()).getName();
			area = area + cityMapper.findAreaNameByAreaID(electrombile.getArea_id()).getName();
			map.put("var3", area);
			map.put("var4", electrombile.getOwner_tele());
			if (electrombile.getElect_state() == 1) {
				map.put("var5", "正常");
			} else if (electrombile.getElect_state() == 2) {
				map.put("var5", "黑名单");
			} else {
				map.put("var5", "未知");
			}
			map.put("var6", electrombile.getOwner_name());
			map.put("var7", electrombile.getOwner_id());
			map.put("var8", electrombile.getRecorder_time());
			varList.add(map);
		}
		dataExel.put("titles", titles);
		dataExel.put("varList", varList);
		return ExcelImportUtil.exportExcel(dataExel);
	}

	/**
	 * 根据关键字定位车辆
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectLocation")
	@ResponseBody
	public Object findElectLocation(@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "guaCardNum", required = false) Integer guaCardNum)
			throws UnsupportedEncodingException {
//		Electrombile electrombile = electrombileMapper.findElectrombileForLocation(guaCardNum, plateNum);
//		Station station = new Station();
//		if (null != electrombile) {
//			ElectrombileStation electrombileStation = electrombileStationMapper
//					.selectByGuaCardNumForLocation(electrombile.getGua_card_num());
//			station = stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num());
//		}
//		return station;
		
		Station station = new Station();
		String strSql=" SELECT * FROM  " + Constant.electStationTable;
		String where = "";
		if( plateNum != null && !"".equals(plateNum)) {
			where += " plate_num = '"+plateNum+"'";
		}
		if( proPower != null) {
			if(!where.equals("")) {
				where += " and pro_id = "+proPower;
			}else {
		
				where += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!where.equals("")) {
				where += " and city_id = "+cityPower;
			}else {
				where += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!where.equals("")) {
				where += " and area_id = "+areaPower;
			}else {
				where += " area_id = "+areaPower;
			}
		}
		if( guaCardNum != null ) {
			if(!where.equals("")) {
				where += " and gua_card_num = "+guaCardNum;
			}else {
				where += " gua_card_num = "+guaCardNum;
			}
		}
		if(plateNum != null && guaCardNum != null ) {
			return station;
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
		
		
		return station;
		
	}

	/**
	 * 查询车辆轨迹
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @param startTimeForTrace
	 * @param endTimeForTrace
	 * @return
	 * @throws UnsupportedEncodingException
	 */
//	@RequestMapping(value = "/findElectTrace")
//	@ResponseBody
//	public Object findElectTrace(@RequestParam(value = "plateNum", required = false) String plateNum,
//			@RequestParam(value = "guaCardNum", required = false) Integer guaCardNum,
//			@RequestParam(value = "startTimeForTrace", required = false) String startTimeForTrace,
//			@RequestParam(value = "endTimeForTrace", required = false) String endTimeForTrace)
//			throws UnsupportedEncodingException {
//		Electrombile electrombile = electrombileMapper.findElectrombileForLocation(guaCardNum, plateNum);
//		//List<TraceStationDto> traceStationDtos = new ArrayList<TraceStationDto>();
//		if (null != electrombile) {
//			List<ElectrombileStation> electrombileStations = electrombileStationMapper
//					.selectByGuaCardNumForTrace(electrombile.getGua_card_num(), startTimeForTrace, endTimeForTrace);
//			/*for (ElectrombileStation electrombileStation : electrombileStations) {
//				TraceStationDto traceStationDto = new TraceStationDto();
//				traceStationDto.setCrossTime(electrombileStation.getUpdate_time());
//				traceStationDto
//						.setStation(stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num()));
//				traceStationDtos.add(traceStationDto);
//			}*/
//			return electrombileStations;
//		}
//		return new ArrayList<ElectrombileStation>();
//	}
	
	/**
	 * 查询车辆轨迹   查询influxdb 2019-01-29  重写
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
	public Object findElectTrace(@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "guaCardNum", required = false) Integer guaCardNum,
			@RequestParam(value = "startTimeForTrace", required = false) String startTimeForTrace,
			@RequestParam(value = "endTimeForTrace", required = false) String endTimeForTrace,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower)
					throws UnsupportedEncodingException {
		String strSql=" SELECT * FROM " + Constant.electStationTable;
		String where = "";
		
		if( startTimeForTrace != null && !"".equals(startTimeForTrace)) {
			where += " time >= '" + startTimeForTrace+"'";
		}
		if( endTimeForTrace != null && !"".equals(endTimeForTrace)) {
			/*
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd ");
			Date date=null;
			Calendar calendar = Calendar.getInstance();
			try {
				
				date=sdf.parse(endTimeForTrace);
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!where.equals("")) {
				where += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				where += " time < '" + sdf.format(calendar.getTime())+"'";
			}
			*/
			if(!where.equals("")) {
				where += " and time <= '" + endTimeForTrace+"'";
			}else {
				where += " time <= '" + endTimeForTrace+"'";
			}
		}
		if( proPower != null) {
			if(!where.equals("")) {
				where += " and pro_id = "+proPower;
			}else {
		
				where += " pro_id = "+proPower;
			}
		}
		if( cityPower != null) {
			if(!where.equals("")) {
				where += " and city_id = "+cityPower;
			}else {
				where += " city_id = "+cityPower;
			}
		}
		if( areaPower != null) {
			if(!where.equals("")) {
				where += " and area_id = "+areaPower;
			}else {
				where += " area_id = "+areaPower;
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
		if(plateNum != null && guaCardNum != null ) {
			return new ArrayList<ElectrombileStation>();
		}
		
		if(!where.equals("")) {
			strSql+=" where "+ where;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		List<ElectrombileStation> listElect = new ArrayList<ElectrombileStation>();
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> listCol = series.get(0).getColumns();
			List<List<Object>> listVal = series.get(0).getValues();
			double card_num;
			for (List<Object> lists : listVal) {
				ElectrombileStation elect = new ElectrombileStation();
				card_num=Double.valueOf(lists.get(listCol.indexOf("gua_card_num")).toString());
				elect.setEle_gua_card_num((int)card_num);
				elect.setStation_phy_num(Integer.parseInt(lists.get(listCol.indexOf("station_phy_num")).toString()));
				elect.setHard_read_time(lists.get(listCol.indexOf("hard_read_time")).toString());
				elect.setOwner_name(lists.get(listCol.indexOf("owner_name")).toString());
				elect.setPlate_num(lists.get(listCol.indexOf("plate_num")).toString());
				elect.setStation_name(lists.get(listCol.indexOf("station_name")).toString());
				elect.setLongitude(lists.get(listCol.indexOf("longitude")).toString());
				elect.setLatitude(lists.get(listCol.indexOf("latitude")).toString());
				elect.setStation_address(lists.get(listCol.indexOf("station_address")).toString());
				elect.setUpdate_time(lists.get(listCol.indexOf("hard_read_time")).toString());
				listElect.add(elect);
			}
		}
		return listElect;
	}

	/**
	 * 获取所有备案登记车辆
	 * 
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsList")
	@ResponseBody
	public Object findElectsList(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower)
			throws UnsupportedEncodingException {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		List<Electrombile> electrombiles = electrombileMapper.findElectsList(proPower, cityPower, areaPower);
		return electrombiles;
	}
	
	/**
	 * 2018-12-17
	 * 获取所有备案登记车辆数量
	 * 
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsListCount")
	@ResponseBody
	public Object findElectsListCount(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower)
					throws UnsupportedEncodingException {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		Integer count = electrombileMapper.findElectsListCount(proPower, cityPower, areaPower);
		return count;
	}
	
	
	/**
	 * 获取某状态的备案登记车辆
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @param electState
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	 
	 
	@RequestMapping(value = "/findElectsListByElectState")
	@ResponseBody
	public Object findElectsListByElectState(@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "electState", required = false) Integer electState)
					throws UnsupportedEncodingException {
		if (null == proPower || proPower == -1) {
			proPower = null;
		}
		if (null == cityPower || cityPower == -1) {
			cityPower = null;
		}
		if (null == areaPower || areaPower == -1) {
			areaPower = null;
		}
		List<Electrombile> electrombiles = electrombileMapper.findElectsListByElectState(proPower, cityPower, areaPower, electState);
		return electrombiles;
	}

	// @RequestMapping(value = "/findElectTracePages")
	// @ResponseBody
	// public Object
	// findElectTracePages(@RequestParam(value="pageNum",required=false) Integer
	// pageNum,
	// @RequestParam(value="pageSize") Integer pageSize,
	// @RequestParam(value="guaCardNum", required=false) Integer guaCardNum,
	// @RequestParam(value="startTimeForTrace", required=false) String
	// startTimeForTrace,
	// @RequestParam(value="endTimeForTrace", required=false) String
	// endTimeForTrace) throws UnsupportedEncodingException {
	// pageNum = pageNum == null? 1:pageNum;
	// pageSize = pageSize==null? 12:pageSize;
	// PageHelper.startPage(pageNum, pageSize);
	// Page<TraceStationDto> traceStationDtos = new Page<TraceStationDto>();
	// Page<ElectrombileStation> electrombileStations =
	// (Page<ElectrombileStation>)electrombileStationMapper.
	// selectByGuaCardNumForTrace(guaCardNum, startTimeForTrace,
	// endTimeForTrace);
	// for (ElectrombileStation electrombileStation : electrombileStations) {
	// TraceStationDto traceStationDto = new TraceStationDto();
	// traceStationDto.setCrossTime(electrombileStation.getUpdate_time());
	// traceStationDto.setStation(stationMapper.selectByStationPhyNum(electrombileStation.getStation_phy_num()));
	// traceStationDtos.add(traceStationDto);
	// }
	// traceStationDtos.setPageSize(electrombileStations.getPageSize());
	// traceStationDtos.setPages(electrombileStations.getPages());
	// traceStationDtos.setTotal(electrombileStations.getTotal());
	// return new PageInfo<TraceStationDto>(traceStationDtos);
	// }
	
	//解码过程
	public InputStream JImgPic(String imgStr,String ownerPic){
		
		BASE64Decoder decoder = new BASE64Decoder();
		try{
			//Base64解码
			byte[] imageByte = decoder.decodeBuffer(ownerPic);
			for(int i=0; i < imageByte.length; ++i) {
				if(imageByte[i]<0){
				//调整异常数据
					imageByte[i]+=256;
				}
			}
			//生成jpg图片
			String imgFilePath="C:\\temp\\zp.jpg";//新生成的图片
			InputStream file = new FileInputStream (imgFilePath);
		
			return file;
		}catch(Exception e){
			return null;
		}
	}
	
}
