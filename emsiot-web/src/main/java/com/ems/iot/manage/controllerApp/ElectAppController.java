package com.ems.iot.manage.controllerApp;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.ElectrombileDto;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.TraceStationDto;
import com.ems.iot.manage.dto.UploadFileEntity;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.service.FtpService;
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
@RequestMapping(value = "/electApp")
public class ElectAppController extends AppBaseController {
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
			@RequestParam(value="ownerTele", required=false) String ownerTele,
			@RequestParam(value="ownerID", required=false) String ownerID,
			@RequestParam(value="plateNum", required=false) String plateNum,
			@RequestParam(value="guaCardNum", required=false) String guaCardNum,
			@RequestParam(value="ownerName", required=false) String ownerName,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower,
			@RequestParam(value="token", required=false) String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (proPower==null) {
			return new AppResultDto(3001, "管理员的省权限不能为空", false);
		}
		if (cityPower==null) {
			return new AppResultDto(3001,"管理员的市权限不能为空", false);
		}
		if (areaPower==null) {
			return new AppResultDto(3001, "管理员的区/县权限不能为空", false);
		}
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
		if (proPower==-1) {
			proPower = null;
		}
		if (cityPower==-1) {
			cityPower = null;
		}
		if (areaPower==-1) {
			areaPower = null;
		}
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Electrombile> electrombiles = electrombileMapper.findAllElectrombiles(startTime, endTime, recorderID, electState, insurDetail, proID, 
				cityID, areaID, ownerTele, ownerID, plateNum, guaCardNum, ownerName,proPower,cityPower,areaPower);
        if (electrombiles==null||electrombiles.size()==0) {
        	return new AppResultDto(2001, "在该管理员所具有的权限区域内未查询到车辆信息");
		}
		Page<ElectrombileDto> electrombileDtos = new Page<ElectrombileDto>();
		for (Electrombile electrombile : electrombiles) {
			ElectrombileDto electrombileDto = new ElectrombileDto();
			electrombileDto.setElectrombile(electrombile);
			electrombileDto.setProvinceName(cityMapper.findProvinceById(electrombile.getPro_id()).getName());
			electrombileDto.setCityName(cityMapper.findCityById(electrombile.getCity_id()).getName());
			electrombileDto.setAreaName(cityMapper.findAreaNameByAreaID(electrombile.getArea_id()).getName());
			electrombileDto.setRecordName(sysUserMapper.findUserById(electrombile.getRecorder_id()).getUser_name());
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
			@RequestParam(required = false) MultipartFile elect_pic,
			@RequestParam(required = false) MultipartFile indentity_card_pic,
			@RequestParam(required = false) MultipartFile record_pic,
			@RequestParam(required = false) MultipartFile install_card_pic,
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
		Electrombile electrombile = new Electrombile();
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
		if (null!=elect_pic) {
			String dir = String.format("%s/elect/electPic", baseDir);
			String elect_pic_name = String.format("electPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(elect_pic_name, elect_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setElect_pic(FtpService.READ_URL+"data/"+dir + "/" + elect_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=indentity_card_pic) {
			String dir = String.format("%s/elect/indCardPic", baseDir);
			String indentity_card_pic_name = String.format("indCardPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(indentity_card_pic_name, indentity_card_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setIndentity_card_pic(FtpService.READ_URL+"data/"+dir + "/" + indentity_card_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=record_pic) {
			String dir = String.format("%s/elect/recordPic", baseDir);
			String record_pic_name = String.format("recordPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(record_pic_name, record_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setRecord_pic(FtpService.READ_URL+"data/"+dir + "/" + record_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=install_card_pic) {
			String dir = String.format("%s/elect/installCardPic", baseDir);
			String install_card_pic_name = String.format("installCardPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(install_card_pic_name, elect_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setInstall_card_pic(FtpService.READ_URL+"data/"+dir + "/" + install_card_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
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
			@RequestParam(required = false) MultipartFile elect_pic,
			@RequestParam(required = false) MultipartFile indentity_card_pic,
			@RequestParam(required = false) MultipartFile record_pic,
			@RequestParam(required = false) MultipartFile install_card_pic,
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
		if (null!=elect_pic) {
			String dir = String.format("%s/elect/electPic", baseDir);
			String elect_pic_name = String.format("electPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(elect_pic_name, elect_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setElect_pic(FtpService.READ_URL+"data/"+dir + "/" + elect_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=indentity_card_pic) {
			String dir = String.format("%s/elect/indCardPic", baseDir);
			String indentity_card_pic_name = String.format("indCardPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(indentity_card_pic_name, indentity_card_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setIndentity_card_pic(FtpService.READ_URL+"data/"+dir + "/" + indentity_card_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=record_pic) {
			String dir = String.format("%s/elect/recordPic", baseDir);
			String record_pic_name = String.format("recordPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(record_pic_name, record_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setRecord_pic(FtpService.READ_URL+"data/"+dir + "/" + record_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
		if (null!=install_card_pic) {
			String dir = String.format("%s/elect/installCardPic", baseDir);
			String install_card_pic_name = String.format("installCardPic%s_%s.%s", electrombile.getGua_card_num(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(install_card_pic_name, elect_pic, dir);
			ftpService.uploadFile(uploadFileEntity);
			electrombile.setInstall_card_pic(FtpService.READ_URL+"data/"+dir + "/" + install_card_pic_name);//http://42.121.130.177:8089/picture/user/1124/3456789.png
		}
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
	 * 获取所有备案登记车辆
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectsList")
	@ResponseBody
	public Object findElectsList(
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower,
			@RequestParam(value="token", required=false) String token
			) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (proPower==null) {
			return new AppResultDto(3001, "管理员的省权限不能为空", false);
		}
		if (cityPower==null) {
			return new AppResultDto(3001,"管理员的市权限不能为空", false);
		}
		if (areaPower==null) {
			return new AppResultDto(3001, "管理员的区/县权限不能为空", false);
		}
		if (proPower==-1) {
			proPower = null;
		}
		if (cityPower==-1) {
			cityPower = null;
		}
		if (areaPower==-1) {
			areaPower = null;
		}
		List<Electrombile> electrombiles =  electrombileMapper.findElectsList(proPower, cityPower, areaPower);
		if (electrombiles==null||electrombiles.size()==0) {
			return new AppResultDto(2001, "在该管理员所具有的权限区域内未查询到车辆信息");
		}
		return new AppResultDto(electrombiles);
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
