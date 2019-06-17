package com.ems.iot.manage.controllerApp;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.ElectrombileDto;
import com.ems.iot.manage.dto.CountDto;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.TraceStationDto;
import com.ems.iot.manage.dto.UploadFileEntity;
import com.ems.iot.manage.entity.Area;
import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.City;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Province;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.service.FtpService;
import com.ems.iot.manage.util.Constant;
import com.ems.iot.manage.util.InfluxDBConnection;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14  app接口-车辆相关
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/electSysUserApp")
public class ElectSysUserAppController extends AppBaseController {
	private static String baseDir = "picture";
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	@Autowired
	private FtpService ftpService;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private ElectAlarmMapper electAlarmMapper;
	@Autowired
	private BlackelectMapper blackelectMapper;
	@Autowired
	private CookieService cookieService;
	
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
		 ElectrombileDto electrombileDto = new ElectrombileDto();
	     electrombileDto.setElectrombile(electrombile);
		 electrombileDto.setProvinceName(cityMapper.findProvinceById(electrombile.getPro_id()).getName());
		 electrombileDto.setCityName(cityMapper.findCityById(electrombile.getCity_id()).getName());
		 electrombileDto.setAreaName(cityMapper.findAreaNameByAreaID(electrombile.getArea_id()).getName());
		 electrombileDto.setRecordName(sysUserMapper.findUserById(electrombile.getRecorder_id()).getUser_name());
	     return new AppResultDto(electrombileDto);
	}
	
	/**
	 * 根据各种关键字查询车辆
	 * @param pageNum 
	 * @param pageSize
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param recorderID 录入人ID
	 * @param electState 车辆状态
	 * @param insurDetail 是否投保
	 * @param proID 所属区域——省
	 * @param cityID 所属区域——市
	 * @param areaID 所属区域——县
	 * @param ownerTele 车主手机号
	 * @param ownerID 车主身份证号
	 * @param plateNum 车牌号
	 * @param guaCardNum 防盗芯片编号
	 * @param ownerName 车主姓名
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectList")
	@ResponseBody
	public Object findElectList(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize") Integer pageSize, 
			@RequestParam(value="startTime", required=false) String startTime,
			@RequestParam(value="endTime", required=false) String endTime,
			@RequestParam(value="recorderID", required=false) Integer recorderID,
			@RequestParam(value="electState", required=false) Integer electState,
			@RequestParam(value="insurDetail", required=false) Integer insurDetail,
			@RequestParam(value="proID", required=false) Integer proID,
			@RequestParam(value="cityID", required=false) Integer cityID,
			@RequestParam(value="areaID", required=false) Integer areaID,
			@RequestParam(value="ownerID", required=false) String ownerID,
			@RequestParam(value="guaCardNum", required=false) String guaCardNum,
			@RequestParam(value="keyword", required=false) String keyword,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		if (null==recorderID||recorderID==0) {
			recorderID = null;
		}
		if (null==electState||electState==8) {
			electState = null;
		}
		if (null==insurDetail||insurDetail==8) {
			insurDetail = null;
		}
		if (null==proID||proID==-1) {
			proID = null;
		}
		if (null==cityID||cityID==-1) {
			cityID = null;
		}
		if (null==areaID||areaID==-1) {
			areaID = null;
		}
//		Integer proPower = null;
//		Integer cityPower = null;
//		Integer areaPower = null;
//		if (!sysUser.getPro_power().equals("-1")) {
//			proPower = Integer.valueOf(sysUser.getPro_power());
//		}
//		if (!sysUser.getCity_power().equals("-1")) {
//			cityPower = Integer.valueOf(sysUser.getCity_power());
//		}
//		if (!sysUser.getArea_power().equals("-1")) {
//			areaPower = Integer.valueOf(sysUser.getArea_power());
//		}
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Electrombile> electrombiles = electrombileMapper.findAllElectrombilesForApp(startTime, endTime, sysUser.getUser_id(), electState, insurDetail, proID, 
				cityID, areaID, keyword, ownerID, keyword, guaCardNum, keyword,null,null,null);
        if (electrombiles==null||electrombiles.size()==0) {
        	return new AppResultDto(2001, "在该管理员所具有的权限区域内未查询到车辆信息");
		}
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
			SysUser sysRecorder = sysUserMapper.findUserById(electrombile.getRecorder_id());
			if (sysRecorder!=null) {
				electrombileDto.setRecordName(sysRecorder.getUser_name());
			}
			electrombileDtos.add(electrombileDto);
		}
		electrombileDtos.setPageSize(electrombiles.getPageSize());
		electrombileDtos.setPages(electrombiles.getPages());
		electrombileDtos.setTotal(electrombiles.getTotal());
		
		PageInfo<ElectrombileDto> elePageInfo = new PageInfo<ElectrombileDto>(electrombileDtos);
		return new AppResultDto(elePageInfo);
	}
	
	/**
	 * 添加车辆
	 * @param electrombile
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/addElect")
	@ResponseBody
	public Object addElect(@RequestParam(required = false) Integer gua_card_num,
			@RequestParam(required = false) String plate_num,
			@RequestParam(required = false) String ve_id_num,
			@RequestParam(required = false) String elect_brand,
			@RequestParam(required = false) String buy_date,
			@RequestParam(required = false) String elect_color,
			@RequestParam(required = false) String motor_num, 
			@RequestParam(required = false) String note,
			@RequestParam(required = false) Integer pro_id,
			@RequestParam(required = false) Integer city_id,
			@RequestParam(required = false) Integer area_id,
			@RequestParam(required = false) Integer elect_type,
			@RequestParam(required = false) Integer insur_detail,
			@RequestParam(required = false) String elect_pic,
			@RequestParam(required = false) String indentity_card_pic,
			@RequestParam(required = false) String record_pic,
			@RequestParam(required = false) String install_card_pic,
			@RequestParam(required = false) String insur_pic,
			@RequestParam(required = false) String tele_fee_pic,
			@RequestParam(required = false) String owner_tele,
			@RequestParam(required = false) String owner_name,
			@RequestParam(required = false) String owner_address,
			@RequestParam(required = false) String owner_id,
			@RequestParam(required = false) Integer recorder_id,
			@RequestParam(required = false) Integer elect_state,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException, ParseException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (gua_card_num == null) {
			return new AppResultDto(3001, "防盗芯片编号不能为空",false);
		}
		if (gua_card_num.toString().length()>20) {
			return new AppResultDto(3001, "防盗芯片编号最多为20位",false);
		}
		if (plate_num == null) {
			return new AppResultDto(3001, "车牌号不能为空", false);
		}
		if (plate_num.length()>20) {
			return new AppResultDto(3001, "车牌号最多为20位",false);
		}
		if (pro_id==null) {
			return new AppResultDto(3001, "车牌号所属省不能为空", false);
		}
		if (city_id==null) {
			return new AppResultDto(3001, "车牌号所属市不能为空", false);
		}
		if (area_id==null) {
			return new AppResultDto(3001, "车牌号所属区/县不能为空", false);
		}
		if (owner_tele==null){
			return new AppResultDto(3001, "车主手机号不能为空", false);
		}
		if (owner_name==null){
			return new AppResultDto(3001, "车主姓名不能为空", false);
		}
		if (owner_id==null){
			return new AppResultDto(3001, "车主身份证号不能为空", false);
		}
		if (electrombileMapper.findElectForFilter(gua_card_num, null)!=null) {
			return new AppResultDto(3001, "防盗芯片编号已存在，不可重复添加！", false);
		}
		if (electrombileMapper.findElectForFilter(null, plate_num)!=null) {
			return new AppResultDto(3001, "车牌号已存在，不可重复添加！", false);
		}
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		recorder_id = sysUser.getUser_id();
		Electrombile electrombile = new Electrombile();
		electrombile.setGua_card_num(gua_card_num);
		electrombile.setPlate_num(plate_num);
		electrombile.setVe_id_num(ve_id_num);
		electrombile.setElect_brand(elect_brand);
		if (buy_date!=null&&!buy_date.equals("")) {
			electrombile.setBuy_date(buy_date);
		}
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
		if(elect_state==null) {
			electrombile.setElect_state(1);
		}else {
			electrombile.setElect_state(elect_state);
		}
		electrombile.setElect_pic(elect_pic);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		electrombile.setIndentity_card_pic(indentity_card_pic);
		electrombile.setRecord_pic(record_pic);
		electrombile.setInstall_card_pic(install_card_pic);
		electrombile.setInsur_pic(insur_pic);
		electrombile.setTele_fee_pic(tele_fee_pic);
		electrombileMapper.insert(electrombile);
		return new AppResultDto(1001,"添加成功");
	}
	
	/**
	 * 更新车辆
	 * @param electrombile
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/updateElect")
	@ResponseBody
	public Object updateElect(@RequestParam(required = false) Integer elect_id,
			@RequestParam(required = false) Integer gua_card_num,
			@RequestParam(required = false) String plate_num,
			@RequestParam(required = false) String ve_id_num,
			@RequestParam(required = false) String elect_brand,
			@RequestParam(required = false) String buy_date,
			@RequestParam(required = false) String elect_color,
			@RequestParam(required = false) String motor_num, 
			@RequestParam(required = false) String note,
			@RequestParam(required = false) Integer pro_id,
			@RequestParam(required = false) Integer city_id,
			@RequestParam(required = false) Integer area_id,
			@RequestParam(required = false) Integer elect_type,
			@RequestParam(required = false) Integer insur_detail,
			@RequestParam(required = false) String elect_pic,
			@RequestParam(required = false) String indentity_card_pic,
			@RequestParam(required = false) String record_pic,
			@RequestParam(required = false) String install_card_pic,
			@RequestParam(required = false) String insur_pic,
			@RequestParam(required = false) String tele_fee_pic,
			@RequestParam(required = false) String owner_tele,
			@RequestParam(required = false) String owner_name,
			@RequestParam(required = false) String owner_address,
			@RequestParam(required = false) String owner_id,
			@RequestParam(required = false) Integer recorder_id,
			@RequestParam(required = false) Integer elect_state,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException, ParseException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (elect_id == null) {
			return new AppResultDto(3001, "要更新的车辆id不能为空",false);
		}
		if (gua_card_num == null) {
			return new AppResultDto(3001, "防盗芯片编号不能为空",false);
		}
		if (plate_num == null) {
			return new AppResultDto(3001, "车牌号不能为空", false);
		}
		if (pro_id==null) {
			return new AppResultDto(3001, "车牌号所属省不能为空", false);
		}
		if (city_id==null) {
			return new AppResultDto(3001, "车牌号所属市不能为空", false);
		}
		if (area_id==null) {
			return new AppResultDto(3001, "车牌号所属区/县不能为空", false);
		}
		if (owner_tele==null){
			return new AppResultDto(3001, "车主手机号不能为空", false);
		}
		if (owner_name==null){
			return new AppResultDto(3001, "车主姓名不能为空", false);
		}
		if (owner_id==null){
			return new AppResultDto(3001, "车主身份证号不能为空", false);
		}
//		if (electrombileMapper.findElectForFilter(gua_card_num, null)!=null) {
//			return new AppResultDto(3001, "防盗芯片编号已存在，不可重复添加！", false);
//		}
//		if (electrombileMapper.findElectForFilter(null, plate_num)!=null) {
//			return new AppResultDto(3001, "车牌号已存在，不可重复添加！", false);
//		}
		Electrombile electrombile = new Electrombile();
		electrombile.setElect_id(elect_id);
		electrombile.setGua_card_num(gua_card_num);
		electrombile.setPlate_num(plate_num);
		electrombile.setVe_id_num(ve_id_num);
		electrombile.setElect_brand(elect_brand);
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
		electrombile.setElect_pic(elect_pic);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		electrombile.setIndentity_card_pic(indentity_card_pic);
		electrombile.setRecord_pic(record_pic);
		electrombile.setInstall_card_pic(install_card_pic);
		electrombile.setInsur_pic(insur_pic);
		electrombile.setTele_fee_pic(tele_fee_pic);
		electrombileMapper.updateByPrimaryKeySelective(electrombile);
		return new AppResultDto(1001, "更新成功");
	}
	
    /**
     * 根据ID删除车辆
     * @param electID
     * @return
     */
	@RequestMapping(value = "/deleteElectByID")
	@ResponseBody
	public Object deleteElectByID(Integer electID,
			@RequestParam(value="token", required=false) String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		 if (electID==null) {
			return new AppResultDto(3001, "要删除的车辆id不能为空", false);
		 }
		 electrombileMapper.deleteByPrimaryKey(electID);
		 return new AppResultDto(1001, "删除成功");
	}
	
	/**
	 * 根据多个ID删除车辆
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteElectByIDs")
	@ResponseBody
	public Object deleteElectByIDs(Integer[] electIDs,
		@RequestParam(value="token", required=false) String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (electIDs==null||electIDs.length==0) {
			return new AppResultDto(3001, "要删除的车辆id不能为空", false);
		}
		for(Integer electID:electIDs){
			electrombileMapper.deleteByPrimaryKey(electID);
		}
		return new AppResultDto(1001,"删除成功");
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
	
	/**
	 * 查询已备案车辆的数量
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsNum")
	@ResponseBody
	public Object findElectsNum(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
//		Integer proPower = null;
//		Integer cityPower = null;
//		Integer areaPower = null;
//		if (!sysUser.getPro_power().equals("-1")) {
//			proPower = Integer.valueOf(sysUser.getPro_power());
//		}
//		if (!sysUser.getCity_power().equals("-1")) {
//			cityPower = Integer.valueOf(sysUser.getCity_power());
//		}
//		if (!sysUser.getArea_power().equals("-1")) {
//			areaPower = Integer.valueOf(sysUser.getArea_power());
//		}
//		List<Electrombile> electrombiles =  electrombileMapper.findElectsList(proPower, cityPower, areaPower);
		List<Electrombile> electrombiles =  electrombileMapper.findElectsByRecorderId(sysUser.getUser_id().toString());
		if (electrombiles==null||electrombiles.size()==0) {
			return new AppResultDto(2001, "在该管理员所具有的权限区域内未查询到车辆信息");
		}
		return new AppResultDto(electrombiles.size());
	}
	
	/**
	 * 根据车辆的防盗芯片编号返回车辆的车牌号，为手持器App提供车牌号查询服务
	 * @param token
	 * @param guaCardNum
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPlateNumByGuaCardNum")
	@ResponseBody
	public Object findPlateNumByGuaCardNum(
			@RequestParam(value="token", required=false) String token,
			@RequestParam(value="guaCardNum", required=false) Integer guaCardNum
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		Electrombile electrombile = electrombileMapper.findPlateNumByGuaCardNum(guaCardNum);
		return new AppResultDto(electrombile.getPlate_num());
	}
	/**
	 * 跟用户的token获取用户所在的区域，并获取到该区域内所有的黑名单车辆，为手持器App提供黑名单车辆查询服务
	 * @param token
	 * @param guaCardNum
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findBlackElectsByToken")
	@ResponseBody
	public Object findBlackElectsByToken(
			@RequestParam(value="token", required=false) String token,
			@RequestParam(value="guaCardNum", required=false) Integer guaCardNum
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		List<Electrombile> electrombiles = null;
		if (sysUser!=null) {
			electrombiles = electrombileMapper.findElectsListByElectState(Integer.parseInt(sysUser.getPro_power()), Integer.parseInt(sysUser.getCity_power()), Integer.parseInt(sysUser.getArea_power()), 2);
		}
		return new AppResultDto(electrombiles);
	}
	
	
	/**
	 * 登记车辆统计
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectDateCount")
	@ResponseBody
	public Object findElectDateCount(
			@RequestParam(value="token", required=false) String token,
			@RequestParam(value="type", required=false) Integer type
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		}
		
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proPower = null;
		Integer cityPower = null;
		Integer areaPower = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proPower = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityPower = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaPower = Integer.valueOf(sysUser.getArea_power());
		}
		List<CountDto> countDtos = null;
		if(type == null || (type != 1 && type != 2 && type != 3)) {
			//按当天时间段查询数量（默认）
			countDtos =  electrombileMapper.findElectDateHourCount(proPower, cityPower, areaPower);
		}else if(type == 2 || type == 1) {
			//type=1 统计本周的数量 ，type=2 统计本月的数量
			countDtos =  electrombileMapper.findElectDateDayCount(proPower, cityPower, areaPower, dateList(type));
		}else if(type == 3) {
			//所有时间段的统计
			countDtos =  electrombileMapper.findElectDateCount(proPower, cityPower, areaPower);
		}
		
		return new AppResultDto(countDtos);
	}
	
	public List<Integer> dateList(Integer type) {
		List<Integer> day = new ArrayList<Integer>();
		Calendar calendar = Calendar.getInstance();
		switch (type) {
		case 1:
			boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
			//获取周几
			int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
			//若一周第一天为星期天，则-1
			if(isFirstSunday){
				 weekDay = weekDay - 1;
				 if(weekDay == 0){
				     weekDay = 7;
				 }
			}
			
			for (int i = 0; i < weekDay-1; i++) {
				day.add(i+1);
			}
			break;
			
		case 2:

			//获取月
			int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
			
			for (int i = 0; i < monthDay-1; i++) {
				day.add(i+1);
			}
			break;
		case 3:
			
			break;
		default:
			break;
		}
			

		return day;
		
	}

	
	/**
	 * 丢失车辆报警统计（近7天）
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findMissingVehicleAlarmCount")
	@ResponseBody
	public Object findMissingVehicleAlarmCount(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		}
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proPower = null;
		Integer cityPower = null;
		Integer areaPower = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proPower = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityPower = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaPower = Integer.valueOf(sysUser.getArea_power());
		}
		List<CountDto> countDtos =  electAlarmMapper.findElectAlarmsInRecent7DaysCount(proPower, cityPower, areaPower);
		return new AppResultDto(countDtos);
	}
	
	
	/**
	 * 查询基站数和在线离线的数量统计
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findStationCount")
	@ResponseBody
	public Object findStationCount(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proPower = null;
		Integer cityPower = null;
		Integer areaPower = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proPower = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityPower = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaPower = Integer.valueOf(sysUser.getArea_power());
		}
		List<Station> station =  stationMapper.findStationsByStatus(null, proPower, cityPower, areaPower);
		List<Station> stationOnLine =  stationMapper.findStationsByStatus(0, proPower, cityPower, areaPower);
		List<Station> stationOffLine =  stationMapper.findStationsByStatus(1, proPower, cityPower, areaPower);
		
		CountDto countDto = new CountDto();
		countDto.setStationSum(station.size());
		countDto.setStationOnLineSum(stationOnLine.size());
		countDto.setStationOffLineSum(stationOffLine.size());
		return new AppResultDto(countDto);
	}
	
	/**
	 * 查询黑名单数和未处理与已处理的数量统计
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAlarmCount")
	@ResponseBody
	public Object findAlarmCount(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proPower = null;
		Integer cityPower = null;
		Integer areaPower = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proPower = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityPower = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaPower = Integer.valueOf(sysUser.getArea_power());
		}
		Integer blacklistCount = blackelectMapper.findBlackelectsListCount(proPower, cityPower, areaPower,null);
		Integer untreatedBlacklistCount =  blackelectMapper.findBlackelectsListCount(proPower, cityPower, areaPower,1);
		Integer processedBlacklistCount =  blackelectMapper.findBlackelectsListCount(proPower, cityPower, areaPower,2);
		
		CountDto countDto = new CountDto();
		countDto.setElectBlacklistSum(blacklistCount);
		countDto.setUntreatedBlacklistSum(untreatedBlacklistCount);
		countDto.setProcessedBlacklistSum(processedBlacklistCount);
		return new AppResultDto(countDto);
	}
	
	/**
	 * 查询已备案车辆的数量
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsCount")
	@ResponseBody
	public Object findElectsCount(
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录移动失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proPower = null;
		Integer cityPower = null;
		Integer areaPower = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proPower = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityPower = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaPower = Integer.valueOf(sysUser.getArea_power());
		}
		List<Electrombile> electrombiles =  electrombileMapper.findElectsByRecorderId(sysUser.getUser_id().toString());
		List<Electrombile> electBlacklist =  electrombileMapper.findAllElectrombilesForApp(null, null, sysUser.getUser_id(), 2, null, null, null, null, null, null, null, null, null, null, null, null);
		Integer electAlarmSum = electAlarmMapper.findElectalarmsListCount(proPower, cityPower, areaPower);
		CountDto countDto = new CountDto();
		countDto.setElectRegisterSum(electrombiles.size());
		countDto.setElectBlacklistSum(electBlacklist.size());
		countDto.setElectAlarmSum(electAlarmSum);
		countDto.setElectOnLineSum(electOnLineSum(proPower, cityPower, areaPower));
		return new AppResultDto(countDto);
	}
	/**
	 * 当天在线移动车辆
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 */
	public Integer electOnLineSum(Integer proPower, Integer cityPower, Integer areaPower) {
		String startTime = null;
		String endTime = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		startTime = sdf.format(System.currentTimeMillis());
		endTime = sdf.format(System.currentTimeMillis());
		String where = "";
		if( startTime != null && !"".equals(startTime)) {
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
		
		
		String influxSql=" SELECT * FROM " + Constant.electStationTable; 
				
		if(!where.equals("")) {
			influxSql+=" where "+ where + " group by plate_num " ;
		}
		QueryResult results = influxDBConnection
				.query(influxSql);
		Result oneResult = results.getResults().get(0);
		if (oneResult.getSeries() != null) {
			List<Series> seriesList = oneResult.getSeries();
			return seriesList.size();
		}
		return 0;
	}
	
}
