package com.ems.iot.manage.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ems.iot.manage.dao.PeopleMapper;
import com.ems.iot.manage.dao.PeopleStationMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectAlarmDto;
import com.ems.iot.manage.dto.PeopleDto;
import com.ems.iot.manage.dto.PeopleStationDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.StationElectDto;
import com.ems.iot.manage.entity.Area;
import com.ems.iot.manage.entity.City;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.People;
import com.ems.iot.manage.entity.PeopleStation;
import com.ems.iot.manage.entity.Province;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.OssService;
import com.ems.iot.manage.util.Constant;
import com.ems.iot.manage.util.InfluxDBConnection;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/people")
public class PeopleController extends BaseController {
	private static String baseDir = "picture";
	@Autowired
	private PeopleMapper peopleMapper;
	@Autowired
	private PeopleStationMapper peopleStationMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private ElectrombileMapper electrombileMapper;

	InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
	/**
	 * 根据电动车的ID寻找电动车
	 * 
	 * @param electID
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeopleByID")
	@ResponseBody
	public Object findPeopleByID(@RequestParam(value = "electID", required = false) Integer electID)
			throws UnsupportedEncodingException {
		People electrombile = peopleMapper.selectByPrimaryKey(electID);
		return electrombile;
	}

	

	

	/**
	 * 根据各种关键字查询人员
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param recorderID
	 *            录入人ID
	 * @param peopleType
	 *            人员类型
	 * @param proID
	 *            所属区域——省
	 * @param cityID
	 *            所属区域——市
	 * @param areaID
	 *            所属区域——县
	 * @param peopleTele
	 *            人员手机号
	 * @param peopleIdCards
	 *            人员身份证号
	 * @param peopleName
	 *            人员名称
	 * @param peopleGuaCardNum
	 *            防盗芯片编号
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeopleList", method = RequestMethod.POST)
	@ResponseBody
	public Object findPeopleList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "recorderID", required = false) Integer recorderID,
			@RequestParam(value = "peopleType", required = false) Integer peopleType,
			@RequestParam(value = "proID", required = false) Integer proID,
			@RequestParam(value = "cityID", required = false) Integer cityID,
			@RequestParam(value = "areaID", required = false) Integer areaID,
			@RequestParam(value = "proPower", required = false) Integer proPower,
			@RequestParam(value = "cityPower", required = false) Integer cityPower,
			@RequestParam(value = "areaPower", required = false) Integer areaPower,
			@RequestParam(value = "peopleTele", required = false) String peopleTele,
			@RequestParam(value = "peopleIdCards", required = false) String peopleIdCards,
			@RequestParam(value = "peopleName", required = false) String peopleName,
			@RequestParam(value = "peopleGuaCardNum", required = false) String peopleGuaCardNum,
			@RequestParam(value = "guardianName", required = false) String guardianName,
			@RequestParam(value = "guardianTele", required = false) String guardianTele) throws UnsupportedEncodingException {
		if (null == recorderID || recorderID == 0) {
			recorderID = null;
		}
		if (null == peopleType || peopleType == 8) {
			peopleType = null;
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
		Page<People> peoples = peopleMapper.findAllPeoples(startTime, endTime, recorderID,
				peopleType, proID, cityID, areaID, peopleTele, peopleIdCards, peopleName, peopleGuaCardNum, guardianName, guardianTele, proPower, cityPower, areaPower);
		Page<PeopleDto> peopleDtos = new Page<PeopleDto>();
		for (People people : peoples) {
			PeopleDto peopleDto = new PeopleDto();
			peopleDto.setPeople(people);
			Province province = cityMapper.findProvinceById(people.getPro_id());
			if (province!=null) {
				peopleDto.setProvinceName(province.getName());
			}
			City city = cityMapper.findCityById(people.getCity_id());
			if (city!=null) {
				peopleDto.setCityName(city.getName());
			}
			Area area = cityMapper.findAreaNameByAreaID(people.getArea_id());
			if (area!=null) {
				peopleDto.setAreaName(area.getName());
			}
			SysUser sysUser = sysUserMapper.findUserById(people.getRecorder_id());
			if (sysUser!=null) {
				peopleDto.setRecordName(sysUser.getUser_name());
			}
			peopleDtos.add(peopleDto);
		}
		peopleDtos.setPageSize(peoples.getPageSize());
		peopleDtos.setPages(peoples.getPages());
		peopleDtos.setTotal(peoples.getTotal());
		return new PageInfo<PeopleDto>(peopleDtos);
	}

	/**
	 * 添加人员
	 * 
	 * @param electrombile
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@RequestMapping(value = "/addPeople")
	@ResponseBody
	public Object addPeople(@RequestParam(required = false) Integer people_gua_card_num,
			@RequestParam(required = false) String people_id_cards, @RequestParam(required = false) String people_tele,
			@RequestParam(required = false) String people_name, @RequestParam(required = false) Integer people_sex,
			@RequestParam(required = false) Integer people_age, @RequestParam(required = false) Integer pro_id,
			@RequestParam(required = false) Integer city_id, @RequestParam(required = false) Integer area_id,
			@RequestParam(required = false) Integer people_type, @RequestParam(required = false) String guardian_name,
			@RequestParam(required = false) MultipartFile people_pic,
			@RequestParam(required = false) String guardian_tele, @RequestParam(required = false) String guardian_relation,
			@RequestParam(required = false) String contact_address, @RequestParam(required = false) Integer recorder_id)
			throws ParseException, IOException {
		if (people_gua_card_num == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (pro_id==null) {
			return new ResultDto(-1, "人员所属省不能为空");
		}
		if (city_id==null || city_id==-1) {
			return new ResultDto(-1, "人员所属市不能为空");
		}
		if (area_id==null || area_id==-1) {
			return new ResultDto(-1, "人员所属区/县不能为空");
		}
		/*if (people_tele==null){
			return new ResultDto(-1, "人员手机号不能为空");
		}*/
		if (people_name==null){
			return new ResultDto(-1, "人员姓名不能为空");
		}
		if (people_id_cards==null){
			return new ResultDto(-1, "人员身份证号不能为空");
		}
		if (peopleMapper.findPlateNumByPeopleGuaCardNum(people_gua_card_num)!=null) {
			return new ResultDto(-1, "防盗芯片编号已存在，不可重复添加！");
		}
		if (electrombileMapper.findPlateNumByGuaCardNum(people_gua_card_num)!=null) {
			return new ResultDto(-1, "防盗芯片编号在车辆备案中已存在！");
		}
		People people = new People();
		people.setPeople_gua_card_num(people_gua_card_num);

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// electrombile.setBuy_date(sdf.parse(buy_date));
		people.setPeople_tele(people_tele);
		people.setPeople_id_cards(people_id_cards);
		people.setPeople_name(people_name);
		people.setPeople_sex(people_sex);
		people.setPeople_age(people_age);
		people.setPro_id(pro_id);
		people.setCity_id(city_id);
		people.setArea_id(area_id);
		people.setPeople_type(people_type);
		people.setGuardian_name(guardian_name);
		people.setGuardian_tele(guardian_tele);
		people.setGuardian_relation(guardian_relation);
		people.setContact_address(contact_address);
		people.setRecorder_id(recorder_id);
		
		// 创建OSSClient实例。
	    OSSClient ossClient = new OSSClient(OssService.endpoint, OssService.accessKeyId, OssService.accessKeySecret);
		if (null != people_pic) {
			String dir = String.format("%s/electPic/", baseDir);
			String elect_pic_name = String.format("%s_%s.%s", people.getPeople_gua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = people_pic.getInputStream();
			ossClient.putObject("emsiot", dir+elect_pic_name, inputStream);
			people.setPeople_pic(OssService.readUrl + dir+elect_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
		
		peopleMapper.insert(people);
		 // 关闭OSSClient。
	 	ossClient.shutdown();
		return new ResultDto(0, "添加成功");
	}

	/**
	 * 更新人员
	 * 
	 * @param electrombile
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/updatePeople")
	@ResponseBody
	public Object updatePeople(
	@RequestParam(required = false) Integer people_id,@RequestParam(required = false) Integer people_gua_card_num,
	@RequestParam(required = false) String people_id_cards, @RequestParam(required = false) String people_tele,
	@RequestParam(required = false) String people_name, @RequestParam(required = false) Integer people_sex,
	@RequestParam(required = false) Integer people_age, @RequestParam(required = false) Integer pro_id,
	@RequestParam(required = false) Integer city_id, @RequestParam(required = false) Integer area_id,
	@RequestParam(required = false) Integer people_type, @RequestParam(required = false) String guardian_name,
	@RequestParam(required = false) MultipartFile people_pic,@RequestParam(required = false) Integer wornCardNum,
	@RequestParam(required = false) String guardian_tele, @RequestParam(required = false) String guardian_relation,
	@RequestParam(required = false) String contact_address,	@RequestParam(required = false) Integer update_id)
			throws ParseException, IOException {
		if (people_gua_card_num == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (pro_id==null) {
			return new ResultDto(-1, "人员所属省不能为空");
		}
		if (city_id==null || city_id==-1) {
			return new ResultDto(-1, "人员所属市不能为空");
		}
		if (area_id==null || area_id==-1) {
			return new ResultDto(-1, "人员所属区/县不能为空");
		}
		/*if (people_tele==null){
			return new ResultDto(-1, "人员手机号不能为空");
		}*/
		if (people_name==null){
			return new ResultDto(-1, "人员姓名不能为空");
		}
		if (people_id_cards==null){
			return new ResultDto(-1, "人员身份证号不能为空");
		}
		People peopleNum = peopleMapper.findPlateNumByPeopleGuaCardNum(people_gua_card_num);
		if (peopleNum!=null) {
			if(peopleNum.getPeople_gua_card_num().intValue() != wornCardNum ) {
				return new ResultDto(-1, "防盗芯片编号已存在，不可重复添加！");
			}
		}
		
		Electrombile electrombile = electrombileMapper.findPlateNumByGuaCardNum(people_gua_card_num);
		if (electrombile!=null) {
			return new ResultDto(-1, "防盗芯片编号在车辆备案中已存在！");
		}

		People people = new People();
		people.setPeople_id(people_id);
		people.setPeople_gua_card_num(people_gua_card_num);

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// electrombile.setBuy_date(sdf.parse(buy_date));
		people.setPeople_tele(people_tele);
		people.setPeople_id_cards(people_id_cards);
		people.setPeople_name(people_name);
		people.setPeople_sex(people_sex);
		people.setPeople_age(people_age);
		people.setPro_id(pro_id);
		people.setCity_id(city_id);
		people.setArea_id(area_id);
		people.setPeople_type(people_type);
		people.setGuardian_name(guardian_name);
		people.setGuardian_tele(guardian_tele);
		people.setGuardian_relation(guardian_relation);
		people.setContact_address(contact_address);
		people.setUpdate_id(update_id);
		
		// 创建OSSClient实例。
	    OSSClient ossClient = new OSSClient(OssService.endpoint, OssService.accessKeyId, OssService.accessKeySecret);
		if (null != people_pic) {
			String dir = String.format("%s/peoplePic/", baseDir);
			String elect_pic_name = String.format("%s_%s.%s", people.getPeople_gua_card_num(), new Date().getTime(), "jpg");
			// 上传文件流。
			InputStream inputStream = people_pic.getInputStream();
			ossClient.putObject("emsiot", dir+elect_pic_name, inputStream);
			people.setPeople_pic(OssService.readUrl + dir+elect_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
		
		peopleMapper.updateByPrimaryKeySelective(people);
		 // 关闭OSSClient。
	 	ossClient.shutdown();
		return new ResultDto(0, "更新成功");
	}

	/**
	 * 根据ID删除人员
	 * 
	 * @param electID
	 * @return
	 */
	@RequestMapping(value = "/deletePeopleByID")
	@ResponseBody
	public Object deletePeopleByID(Integer peopleID) {
		peopleMapper.deleteByPrimaryKey(peopleID);
		return new BaseDto(0);
	}

	/**
	 * 根据多个ID删除人员
	 * 
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deletePeopleByIDs")
	@ResponseBody
	public Object deletePeopleByIDs(Integer[] peopleIDs) {
		for (Integer peopleID : peopleIDs) {
			peopleMapper.deleteByPrimaryKey(peopleID);
		}
		return new BaseDto(0);
	}

	

	/**
	 * 根据关键字定位人员
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	/*@RequestMapping(value = "/findPeopleLocation")
	@ResponseBody
	public Object findPeopleLocation(@RequestParam(value = "peopleIdCards", required = false) String peopleIdCards,
			@RequestParam(value = "peopleGuaCardNum", required = false) Integer peopleGuaCardNum)
			throws UnsupportedEncodingException {
		People people = peopleMapper.findPeopleForLocation(peopleGuaCardNum, peopleIdCards);
		Station station = new Station();
		if (null != people) {
			PeopleStation peopleStation = peopleStationMapper
					.selectByGuaCardNumForLocation(people.getPeople_gua_card_num());
			station = stationMapper.selectByStationPhyNum(peopleStation.getStation_phy_num());
		}
		return station;
	}*/

	/**
	 * 查询人员轨迹
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @param startTimeForTrace
	 * @param endTimeForTrace
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeopleTrace1")
	@ResponseBody
	public Object findPeopleTrace1(@RequestParam(value = "peopleIdCards", required = false) String peopleIdCards,
			@RequestParam(value = "peopleGuaCardNum", required = false) Integer peopleGuaCardNum,
			@RequestParam(value = "startTimeForTrace", required = false) String startTimeForTrace,
			@RequestParam(value = "endTimeForTrace", required = false) String endTimeForTrace)
			throws UnsupportedEncodingException {
		People people = peopleMapper.findPeopleForLocation(peopleGuaCardNum, peopleIdCards);
		//List<PeopleStationDto> peopleStationDtos = new ArrayList<PeopleStationDto>();
		if (null != people) {
			List<PeopleStation> peopleStations = peopleStationMapper
					.selectByGuaCardNumForTrace(people.getPeople_gua_card_num(), startTimeForTrace, endTimeForTrace);
			/*for (PeopleStation peopleStation : peopleStations) {
				PeopleStationDto peopleStationDto = new PeopleStationDto();
				peopleStationDto.setCrossTime(peopleStation.getUpdate_time());
				peopleStationDto
						.setStation(stationMapper.selectByStationPhyNum(peopleStation.getStation_phy_num()));
				peopleStationDto.setPeople(people);
				peopleStationDtos.add(peopleStationDto);
			}*/
			return peopleStations;
		}
		return new ArrayList<PeopleStation>();
	}

	/**
	 * 获取所有备案登记人员
	 * 
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeoplesList")
	@ResponseBody
	public Object findPeoplesList(@RequestParam(value = "proPower", required = false) Integer proPower,
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
		List<People> peoples = peopleMapper.findPeoplesList(proPower, cityPower, areaPower);
		return peoples;
	}
	
	
	/**
	 * 找到最近一段时间的报警车辆，为报警车辆功能提供服务
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findInlinePeoplesNum")
	@ResponseBody
	public Object findInlinePeoplesNum(@RequestParam(value = "proPower", required = false) Integer proPower,
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
		List<Integer> inlineList = peopleStationMapper.selectPeoplesByEleGuaCardNumNow(proPower, cityPower,
				areaPower);
		return inlineList.size();
	}
	
	
	/**
	 * 根据基站的物理编号和时间，查询某个基站下的人员，为页面点击基站显示基站下的人员提供服务
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeoplesByStationIdAndTime")
	@ResponseBody
	public Object findPeoplesByStationIdAndTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "peopleGuaCardNum", required = false) Integer peopleGuaCardNum)
			throws UnsupportedEncodingException {
		List<StationElectDto> peopleStations = peopleStationMapper.selectElectsByGuaCardNumNumAndTimeDesc(peopleGuaCardNum, startTime, endTime);
		/*List<StationElectDto> stationElectDtos = new ArrayList<StationElectDto>();
		for (PeopleStation peopleStation : peopleStations) {
			StationElectDto stationElectDto = new StationElectDto();
			stationElectDto.setCorssTime(peopleStation.getUpdate_time());
			Station station = stationMapper
					.selectByStationPhyNum(peopleStation.getStation_phy_num());
			stationElectDto.setStation_name(station.getStation_name());
			stationElectDtos.add(stationElectDto);
		}*/
		return peopleStations;
	}
	
	
	
	/**
	 * 根据查询时间，查询时间范围内的人员
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/selectPeopleStationPeopleLocationByTime")
	@ResponseBody
	public Object selectPeopleStationPeopleLocationByTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower)
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
		List<PeopleStation> peopleStations = peopleStationMapper
				.selectPeopleStationPeopleLocationByTime(startTime, endTime, proPower, cityPower, areaPower);
		/*List<PeopleStationDto> peopleStationDtos = new ArrayList<PeopleStationDto>();
		for (PeopleStation peopleStation : peopleStations) {
			PeopleStationDto peopleStationDto = new PeopleStationDto();
			Station station = stationMapper.selectByStationPhyNum(peopleStation.getStation_phy_num());
			peopleStationDto.setStation(station);
			peopleStationDto.setCrossTime(peopleStation.getUpdate_time());
			//electAlarmDto.setPeopleStation(peopleStation);
			People people = peopleMapper.findPlateNumByPeopleGuaCardNum(peopleStation.getPeople_gua_card_num());
			peopleStationDto.setPeople(people);
			
			peopleStationDtos.add(peopleStationDto);
		}*/
		
		return peopleStations;
	}
	
	
	
	
	
	
	
	
	// influxdb查询接口
	
	
	/**
	 * 根据基站的物理编号和时间，查询某个基站下的车辆数量。
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeoplesCountByStationsIdAndTime")
	@ResponseBody
	public Object findPeoplesCountByStationsIdAndTime(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "stationPhyNumStr", required = false) String stationPhyNumStr)
					throws UnsupportedEncodingException {
		
//		Integer count = electrombileStationMapper.selectElectsCountByStationPhyNumAndTime(stationPhyNum, startTime, endTime);
//		return count;
		String[] stationPhyNums=null;
		if(stationPhyNumStr!=null) {
			stationPhyNums=stationPhyNumStr.split(",");
		}
		//查询influxdb 2019-01-29
		int count = 0;
		String strSql=" SELECT count(*) FROM " + Constant.peopleStationTable;
		String where = "";
		String timeCond = "";
		if( startTime != null && !"".equals(startTime)) {
			timeCond += " time >= '" + startTime+"'";
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
			if(!timeCond.equals("")) {
				timeCond += " and time < '" + sdf.format(calendar.getTime())+"'";
			}else {
				timeCond += " time < '" + sdf.format(calendar.getTime())+"'";
			}
		}
		Map<String, Object> mapList=new HashMap<String, Object>();
		String sql="";
		for (String str : stationPhyNums) {
			
				if(!timeCond.equals("")) {
					where = " and station_phy_num = '"+str+"'";
				}else {
					where = " station_phy_num = '"+str+"'";
				}

				sql=strSql+" where "+timeCond+where;
				
				QueryResult results = influxDBConnection
						.query(sql);
				Result oneResult = results.getResults().get(0);
				if (oneResult.getSeries() != null) {
					List<Series> series = oneResult.getSeries();
					
					List<List<Object>> listVal = series.get(0).getValues();
					count = (int)Float.parseFloat(listVal.get(0).get(1).toString());
					mapList.put(str, count);
				}else {
					mapList.put(str, 0);
				}
			
			
		}
		return mapList;
		
	}
	
	/**
	 * 根据基站的物理编号和时间，查询某个基站下的人员，为页面点击基站显示基站下的车辆提供服务
	 * 
	 * @param startTime
	 * @param endTime
	 * @param station_phy_num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeoplesByStationIdAndTime1")
	@ResponseBody
	public Object findPeoplesByStationIdAndTime1(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum)
			throws UnsupportedEncodingException {
		
		
		
		//查询influxdb 2019-01-29
		String strSql=" SELECT * FROM " + Constant.peopleStationTable;
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
		List<StationElectDto> listElect = new ArrayList<StationElectDto>();
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
	
	
	
	//人员定位
	@RequestMapping(value = "/findPeopleLocation")
	@ResponseBody
	public Object findPeopleLocation(@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "guaCardNum", required = false) Integer guaCardNum)
			throws UnsupportedEncodingException {
		Station station = new Station();
		String strSql=" SELECT * FROM  " + Constant.peopleStationTable;
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
	 * 查询车辆轨迹   查询influxdb 
	 * 
	 * @param plateNum
	 * @param guaCardNum
	 * @param startTimeForTrace
	 * @param endTimeForTrace
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPeopleTrace")
	@ResponseBody
	public Object findPeopleTrace(@RequestParam(value = "plateNum", required = false) String plateNum,
			@RequestParam(value = "guaCardNum", required = false) Integer guaCardNum,
			@RequestParam(value = "startTimeForTrace", required = false) String startTimeForTrace,
			@RequestParam(value = "endTimeForTrace", required = false) String endTimeForTrace)
					throws UnsupportedEncodingException {
		String strSql=" SELECT * FROM " + Constant.electStationTable;
		String where = "";
		
		if( startTimeForTrace != null && !"".equals(startTimeForTrace)) {
			where += " time >= '" + startTimeForTrace+"'";
		}
		if( endTimeForTrace != null && !"".equals(endTimeForTrace)) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
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
			return new ArrayList<PeopleStation>();
		}
		
		if(!where.equals("")) {
			strSql+=" where "+where;
		}
		QueryResult results = influxDBConnection
				.query(strSql);
		Result oneResult = results.getResults().get(0);
		List<PeopleStation> listPeople = new ArrayList<PeopleStation>();
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> listCol = series.get(0).getColumns();
			List<List<Object>> listVal = series.get(0).getValues();
			double card_num;
			for (List<Object> lists : listVal) {
				PeopleStation people = new PeopleStation();
				card_num=Double.valueOf(lists.get(listCol.indexOf("gua_card_num")).toString());
				people.setPeople_gua_card_num((int)card_num);
				people.setStation_phy_num(Integer.parseInt(lists.get(listCol.indexOf("station_phy_num")).toString()));
				people.setHard_read_time(lists.get(listCol.indexOf("hard_read_time")).toString());
				
				people.setStation_name(lists.get(listCol.indexOf("station_name")).toString());
				people.setLongitude(lists.get(listCol.indexOf("longitude")).toString());
				people.setLatitude(lists.get(listCol.indexOf("latitude")).toString());
				people.setUpdate_time(lists.get(listCol.indexOf("time")).toString());
				listPeople.add(people);
			}
		}
		return listPeople;
	}
	
}
