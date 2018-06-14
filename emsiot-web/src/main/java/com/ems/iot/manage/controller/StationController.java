package com.ems.iot.manage.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ResultDto;
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
	@Autowired
	private CityMapper cityMapper;
	private static String baseDir = "picture";
//	@Autowired
//	private FtpService ftpService;
	// Endpoint以杭州为例，其它Region请按实际情况填写。
	String endpoint = "http://oss-cn-hangzhou.aliyuncs.com/";
	// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
	String accessKeyId = "LTAIoPdymMIPeien";
	String accessKeySecret = "1T0RpATFgnWvuOJBvBV3TD2kaDZfyT";
	String readUrl = "https://emsiot.oss-cn-hangzhou.aliyuncs.com/";
	/**
	 * 根据关键字查询所有基站
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAllStations")
	@ResponseBody
	public Object findAllStations(@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value="startTime", required=false) String startTime,
			@RequestParam(value="endTime", required=false) String endTime,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum,
			@RequestParam(value = "stationName", required = false) String stationName,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower,
			@RequestParam(value = "stationStatus", required = false) Integer stationStatus
			) throws UnsupportedEncodingException {
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		pageNum = pageNum == null ? 1 : pageNum;
		pageSize = pageSize == null ? 12 : pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Station> staions= stationMapper.findAllStationsByKey(startTime, endTime, stationPhyNum, stationName, stationStatus, proPower, cityPower, areaPower);
		return new PageInfo<Station>(staions);
	}
	
	/**
	 * 查询所有基站
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAllStationsForMap")
	@ResponseBody
	public Object findAllStationsForMap(@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower) throws UnsupportedEncodingException {
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		return stationMapper.findAllStations(proPower, cityPower, areaPower);
	}
	
	/**
	 * 删除基站
	 * @param stationID
	 * @return
	 */
	@RequestMapping(value = "/deleteStationByID")
	@ResponseBody
	public Object deleteStationByID(Integer stationID) {
		 stationMapper.deleteByPrimaryKey(stationID);
		 return new BaseDto(0);
	}
	
	@RequestMapping(value = "/findStationByID")
	@ResponseBody
	public Object findStationByID(Integer stationID) { 
		 return stationMapper.selectByPrimaryKey(stationID);
	}
	
	/**
	 * 批量删除基站
	 * @param stationIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteStationByIDs")
	@ResponseBody
	public Object deleteStationByIDs(Integer[] stationIDs) {
		for(Integer stationID:stationIDs){
			stationMapper.deleteByPrimaryKey(stationID);
		}
		return new BaseDto(0);
	}
	
	/**
	 * 更新基站信息
	 * @param station_id
	 * @param station_phy_num
	 * @param station_name
	 * @param longitude
	 * @param latitude
	 * @param station_type
	 * @param station_status
	 * @param install_date
	 * @param soft_version
	 * @param contact_person
	 * @param contact_tele
	 * @param install_pic
	 * @param stick_num
	 * @param station_address
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@RequestMapping(value = "/updateStation")
	@ResponseBody
	public Object updateStation(
			@RequestParam(required = false) Integer station_id,
			@RequestParam(required = false) Integer station_phy_num,
			@RequestParam(required = false) String station_name,
			@RequestParam(required = false) String longitude,
			@RequestParam(required = false) String latitude,
			@RequestParam(required = false) String station_type,
			@RequestParam(required = false) Integer station_status,
			@RequestParam(required = false) String install_date,
			@RequestParam(required = false) String soft_version,
			@RequestParam(required = false) String contact_person,
			@RequestParam(required = false) String contact_tele,
			@RequestParam(required = false) MultipartFile install_pic,
			@RequestParam(required = false) String stick_num,
			@RequestParam(required = false) String station_address		
			)throws ParseException, IOException{
		if (null == station_phy_num) {
			return new ResultDto(-1, "基站物理编号不能为空！");
		}
		if (null == station_name) {
			return new ResultDto(-1, "基站名称不能为空！");
		}
	    Station station = new Station();
	    station.setStation_id(station_id);
	    station.setContact_person(contact_person);
	    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    //station.setInstall_date(sdf.parse(install_date));
	    station.setInstall_date(install_date);
	    station.setContact_tele(contact_tele);
	    station.setLatitude(latitude);
	    station.setLongitude(longitude);
	    station.setSoft_version(soft_version);
	    station.setStation_name(station_name);
	    station.setStation_phy_num(station_phy_num);
	    station.setStation_status(station_status);
	    station.setStation_type(station_type);
	    station.setStick_num(stick_num);
	    station.setStation_address(station_address);
	    if (station_address!=null) {
	    	 String[] stationAddress = station_address.split(",");
	    	 station.setPro_id(Integer.valueOf(cityMapper.findProvinceByName(stationAddress[0]).getProvince_id()));
	    	 station.setCity_id(Integer.valueOf(cityMapper.findCityByNameAndProId(stationAddress[1], station.getPro_id()).getCity_id()));
	    	 station.setArea_id(Integer.valueOf(cityMapper.findAreaByNameAndCityId(stationAddress[2], station.getCity_id()).getArea_id()));
		}
	    if (null!=install_pic) {
			String dir = String.format("%s/stationPic/", baseDir);
			String station_pic_name = String.format("%s_%s.%s", station.getStation_phy_num(), new Date().getTime(), "jpg");
			//UploadFileEntity uploadFileEntity = new UploadFileEntity(station_pic_name, install_pic, dir);
			//ftpService.uploadFile(uploadFileEntity);
			// 创建OSSClient实例。
			OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
			// 上传文件流。
			InputStream inputStream = install_pic.getInputStream();
			ossClient.putObject("emsiot", dir+station_pic_name, inputStream);
			// 关闭OSSClient。
			ossClient.shutdown();
			station.setInstall_pic(readUrl+dir + "/" + station_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
	    stationMapper.updateByPrimaryKeySelective(station);
	    return new ResultDto(0,"修改成功");
	}
	
	/**
	 * 添加基站
	 * @param station_phy_num
	 * @param station_name
	 * @param longitude
	 * @param latitude
	 * @param station_type
	 * @param station_status
	 * @param install_date
	 * @param soft_version
	 * @param contact_person
	 * @param contact_tele
	 * @param install_pic
	 * @param stick_num
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@RequestMapping(value = "/addStation")
	@ResponseBody
	public Object addStation(
			@RequestParam(required = false) Integer station_phy_num,
			@RequestParam(required = false) String station_name,
			@RequestParam(required = false) String longitude,
			@RequestParam(required = false) String latitude,
			@RequestParam(required = false) String station_type,
			@RequestParam(required = false) Integer station_status,
			@RequestParam(required = false) String install_date,
			@RequestParam(required = false) String soft_version,
			@RequestParam(required = false) String contact_person,
			@RequestParam(required = false) String contact_tele,
			@RequestParam(required = false) MultipartFile install_pic,
			@RequestParam(required = false) String stick_num,
			@RequestParam(required = false) String station_address		
			)throws ParseException, IOException{
		if (null == station_phy_num) {
			return new ResultDto(-1, "基站物理编号不能为空！");
		}
		if (null == station_name) {
			return new ResultDto(-1, "基站名称不能为空！");
		}
		// 创建OSSClient实例。
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
	    Station station = new Station();
	    station.setContact_person(contact_person);
	    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    //station.setInstall_date(sdf.parse(install_date));
	    station.setInstall_date(install_date);
	    station.setContact_tele(contact_tele);
	    station.setLatitude(latitude);
	    station.setLongitude(longitude);
	    station.setSoft_version(soft_version);
	    station.setStation_name(station_name);
	    station.setStation_phy_num(station_phy_num);
	    station.setStation_status(station_status);
	    station.setStation_type(station_type);
	    station.setStick_num(stick_num);
	    station.setStation_address(station_address);
	    if (station_address!=null) {
	    	 String[] stationAddress = station_address.split(",");
	    	 station.setPro_id(Integer.valueOf(cityMapper.findProvinceByName(stationAddress[0]).getProvince_id()));
	    	 station.setCity_id(Integer.valueOf(cityMapper.findCityByNameAndProId(stationAddress[1], station.getPro_id()).getCity_id()));
	    	 station.setArea_id(Integer.valueOf(cityMapper.findAreaByNameAndCityId(stationAddress[2], station.getCity_id()).getArea_id()));
		}
	    if (null!=install_pic) {
	    	String dir = String.format("%s/stationPic/", baseDir);
			String station_pic_name = String.format("%s_%s.%s", station.getStation_phy_num(), new Date().getTime(), "jpg");
			//UploadFileEntity uploadFileEntity = new UploadFileEntity(station_pic_name, install_pic, dir);
			//ftpService.uploadFile(uploadFileEntity);
			// 上传文件流。
			InputStream inputStream = install_pic.getInputStream();
			ossClient.putObject("emsiot", dir+station_pic_name, inputStream);
			station.setInstall_pic(readUrl + dir+station_pic_name);//https://emsiot.oss-cn-hangzhou.aliyuncs.com/picture/stationPic/geek.png
		}
	    stationMapper.insert(station);
	    // 关闭OSSClient。
	 	ossClient.shutdown();
	    return new ResultDto(0,"添加成功");
	    
	}
	
	/**
	 * 显示所有异常基站，为基站管理里面的显示异常基站按钮提供服务
	 * @param stationStatus
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findStationsByStatus")
	@ResponseBody
	public Object findStationsByStatus(
			@RequestParam(value = "stationStatus", required = false) Integer stationStatus,
			@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower
			) throws UnsupportedEncodingException {	
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		List<Station> staions= stationMapper.findStationsByStatus(stationStatus, proPower, cityPower, areaPower);
		return staions;
	}
}
