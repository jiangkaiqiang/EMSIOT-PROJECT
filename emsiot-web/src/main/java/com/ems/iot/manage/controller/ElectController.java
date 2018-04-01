package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.ElectrombileDto;
import com.ems.iot.manage.dto.NgRemoteValidateDTO;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.SysUserDto;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.service.FtpService;
import com.ems.iot.manage.util.ResponseData;
import com.ems.iot.manage.util.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/elect")
public class ElectController extends BaseController {
	private static String baseDir = "picture";
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private FtpService ftpService;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	/**
	 * 根据电动车的ID寻找电动车
	 * @param electID
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findElectByID")
	@ResponseBody
	public Object findElectByID(
			@RequestParam(value="electID", required=false) Integer electID
			) throws UnsupportedEncodingException {
		 Electrombile electrombile = electrombileMapper.selectByPrimaryKey(electID);
	     return electrombile;
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
	@RequestMapping(value = "/findElectList", method = RequestMethod.POST)
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
			@RequestParam(value="ownerName", required=false) String ownerName) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Electrombile> electrombiles = electrombileMapper.findAllElectrombiles(startTime, endTime, recorderID, electState, insurDetail, proID, 
				cityID, areaID, ownerTele, ownerID, plateNum, guaCardNum, ownerName);
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
		return new PageInfo<ElectrombileDto>(electrombileDtos);
	}
	
	/**
	 * 添加车辆
	 * @param electrombile
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addElect", method = RequestMethod.POST)
	@ResponseBody
	public Object addElect(Electrombile electrombile) throws UnsupportedEncodingException {
		if (electrombile.getGua_card_num() == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (electrombile.getPlate_num() == null) {
			return new ResultDto(-1, "车牌号不能为空！");
		}
		electrombileMapper.insert(electrombile);
		return new ResultDto(0,"添加成功");
	}
	
	/**
	 * 更新车辆
	 * @param electrombile
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/updateElect", method = RequestMethod.GET)
	@ResponseBody
	public Object updateElect(Electrombile electrombile) throws UnsupportedEncodingException {
		if (electrombile.getGua_card_num() == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if (electrombile.getPlate_num() == null) {
			return new ResultDto(-1, "车牌号不能为空！");
		}
		electrombileMapper.updateByPrimaryKeySelective(electrombile);
		return new ResultDto(0,"更新成功");
	}
	
    /**
     * 根据ID删除车辆
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
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteElectByIDs")
	@ResponseBody
	public Object deleteElectByIDs(Integer[] electIDs) {
		for(Integer electID:electIDs){
			electrombileMapper.deleteByPrimaryKey(electID);
		}
		return new BaseDto(0);
	}
	/*@RequestMapping(value = "/updatePhoto")
	@ResponseBody
	public Object updatePhoto(HttpServletRequest request, @RequestParam(required = false) MultipartFile userphoto) {
		SysUserEntity user = new SysUserEntity();
		SysUserEntity old_user = (SysUserEntity)request.getSession().getAttribute("user");
		user.setUser_id(old_user.getUser_id());
		if(userphoto!=null){
			String dir = String.format("%s/user/photo/%s", baseDir, user.getUser_id());
			String fileName = String.format("user%s_%s.%s", user.getUser_id(), new Date().getTime(), "jpg");
			UploadFileEntity uploadFileEntity = new UploadFileEntity(fileName, userphoto, dir);
			ftpService.uploadFile(uploadFileEntity);
			user.setPhoto(FtpService.READ_URL+"data/"+dir + "/" + fileName);//http://42.121.130.177:8089/picture/user/1124/3456789.png
			this.userDao.updateUser(user);
			SysUserEntity ol_user = this.userDao.findUserById(user.getUser_id());
			ol_user.setPassword("********");
			request.getSession().setAttribute("user",ol_user);
			return ResponseData.newSuccess(ol_user);
		}
		return ResponseData.newFailure();
	}*/
}
