package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.NgRemoteValidateDTO;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.SysUserDto;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.CookieService;
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
@RequestMapping(value = "/user")
public class UserController extends BaseController {
	@Autowired
	private SysUserMapper userDao;
	@Autowired
	private CookieService cookieService;
	@Autowired
	private CityMapper cityMapper;
	
	@RequestMapping(value = "/login")
	@ResponseBody
	public Object login(HttpServletRequest request, String userName, String password) {
		if(StringUtil.isnotNull(userName)&&StringUtil.isnotNull(password)){
			SysUser user = userDao.findUser(userName, password);
			if (user != null) {
				user.setLogin_time(new Date());
				userDao.updateUser(user);
				String cookie = cookieService.insertCookie(userName);
				user.setPassword("********");
				request.getSession().setAttribute("user", user);
	            return  ResponseData.newSuccess(String.format("token=%s", cookie));
			}
			return ResponseData.newFailure("用户名或者密码不正确~");
		}else{
			return ResponseData.newFailure("用户名和密码不能为空~");
		}
		
	}
	
	@RequestMapping(value = "/findUserByID")
	@ResponseBody
	public Object findUserByID(
			@RequestParam(value="spaceUserID", required=false) Integer spaceUserID
			) throws UnsupportedEncodingException {
	     SysUser sysUser = userDao.findUserById(spaceUserID);
	     SysUserDto sysUserDto = new SysUserDto();
	     sysUserDto.setSysUser(sysUser);
	     String menuPowers = sysUser.getMenu_power();
	     String[] menuPowerArray = menuPowers.split(";");
	     List<String> menuPowerList = new ArrayList<String>();
	     for (String menuPower : menuPowerArray) {
	    	 menuPowerList.add(menuPower);
		 }
	     if (menuPowerList.contains("console")) {
			sysUserDto.setConsole("1");
		 }
	     if (menuPowerList.contains("traceSearch")) {
			sysUserDto.setTraceSearch("1");
		 }
	     if (menuPowerList.contains("location")) {
			sysUserDto.setLocation("1");
		 }
	     if (menuPowerList.contains("electManage")) {
			sysUserDto.setElectManage("1");
	     }
	     if (menuPowerList.contains("electRecord")) {
			sysUserDto.setElectRecord("1");
		 }
	     if (menuPowerList.contains("blackManage")) {
			sysUserDto.setBlackManage("1");
		 }
	     if (menuPowerList.contains("alarmTrack")) {
			sysUserDto.setAlarmTrack("1");
		 }
	     if (menuPowerList.contains("electAdd")) {
			sysUserDto.setElectAdd("1");
		 }
	     if (menuPowerList.contains("electDelete")) {
	    	 sysUserDto.setElectDelete("1");
		 }
	     if (menuPowerList.contains("electExport")) {
			sysUserDto.setElectExport("1");
		 }
	     if (menuPowerList.contains("electEdit")) {
	    	 sysUserDto.setElectEdit("1");
		 }
	     if (menuPowerList.contains("blackAdd")) {
			sysUserDto.setBlackAdd("1");
		 }
	     if (menuPowerList.contains("blackDelete")) {
			sysUserDto.setBlackDelete("1");
		 }
	     if (menuPowerList.contains("blackEdit")) {
			sysUserDto.setBlackEdit("1");
		 }
	     if (menuPowerList.contains("alarmDelete")) {
			sysUserDto.setAlarmDelete("1");
		 }
	     if (menuPowerList.contains("stationManageWhole")) {
			sysUserDto.setStationManageWhole("1");
		 }
	     if (menuPowerList.contains("stationManage")) {
	    	 sysUserDto.setStationManage("1");
		 }
	     if (menuPowerList.contains("stationDeviceManage")) {
	    	 sysUserDto.setStationDeviceManage("1");
		 }
	     if (menuPowerList.contains("stationAdd")) {
	    	 sysUserDto.setStationAdd("1");
		 }
	     if (menuPowerList.contains("stationDelete")) {
	    	 sysUserDto.setStationDelete("1");
		 }
	     if (menuPowerList.contains("stationDeviceDelete")) {
			sysUserDto.setStationDeviceDelete("1");
		 }
	     if (menuPowerList.contains("stationDeviceUpdate")) {
			sysUserDto.setStationDeviceUpdate("1");
		 }
	     if (menuPowerList.contains("specialAreaManage")) {
	    	 sysUserDto.setSpecialAreaManage("1");
		 }
	     if (menuPowerList.contains("limitAreaManage")) {
	    	 sysUserDto.setLimitAreaManage("1");
		 }
	     if (menuPowerList.contains("sensitiveAreaManage")) {
	    	 sysUserDto.setSensitiveAreaManage("1");
		 }
	     if (menuPowerList.contains("AreaAlarmManage")) {
	    	 sysUserDto.setAreaAlarmManage("1");
		 }
	     if (menuPowerList.contains("limitAlarmManage")) {
	    	 sysUserDto.setLimitAlarmManage("1");
		 }
	     if (menuPowerList.contains("sensitiveAlarmManage")) {
			sysUserDto.setSensitiveAlarmManage("1");
		 }
	     if (menuPowerList.contains("dataAnalysis")) {
	    	 sysUserDto.setDataAnalysis("1");
		 }
	     if (menuPowerList.contains("userManage")) {
	    	 sysUserDto.setUserManage("1");
	     }
	     if (menuPowerList.contains("userAdd")) {
	    	 sysUserDto.setUserAdd("1");
		 }
	     if (menuPowerList.contains("userDelete")) {
	    	 sysUserDto.setUserDelete("1");
		 }
	     if (menuPowerList.contains("userEdit")) {
	    	 sysUserDto.setUserEdit("1");
		 }
	     if (menuPowerList.contains("privatePower")) {
	    	 sysUserDto.setPrivatePower("1");
		 }
	     if (sysUser.getPro_power().equals("-1")) {
				sysUserDto.setPro_name("不限");
			}
			else {
				sysUserDto.setPro_name(cityMapper.findProvinceById(Integer.parseInt(sysUser.getPro_power())).getName());
			}
			if (sysUser.getCity_power().equals("-1")) {
				sysUserDto.setCity_name("不限");
			}
			else {
				sysUserDto.setCity_name(cityMapper.findCityById(Integer.parseInt(sysUser.getCity_power())).getName());
			}
			if (sysUser.getArea_power().equals("-1")) {
				sysUserDto.setArea_name("不限");
			}
			else {
				sysUserDto.setArea_name(cityMapper.findAreaNameByAreaID(Integer.parseInt(sysUser.getArea_power())).getName());
			}
	     return sysUserDto;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Object logout(HttpServletRequest request) {
		request.getSession().removeAttribute("user");
		Cookie[] cookies = request.getCookies();
		if(cookies!=null&&cookies.length>0){
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("token")) {
					cookieService.deleteCookie(cookie.getValue());
				}
			}
		}
		return true;
	}

	@RequestMapping(value = "/findUser")
	@ResponseBody
	public Object findUser(HttpServletRequest request,String token) {
		SysUser user = (SysUser)request.getSession().getAttribute("user");
		if(user!=null){return user;}
		if(StringUtil.isNull(token)){
			Cookie[] cookies = request.getCookies();
			if(cookies!=null&&cookies.length>0){
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("token")) {token=	cookie.getValue();break;}
				}
			}
		}
		if(StringUtil.isnotNull(token)){
			Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
			if (effectiveCookie != null) {
				user = userDao.findUserByName(effectiveCookie.getUsername());
				if(user!=null){
					user.setPassword("********");
					request.getSession().setAttribute("user", user);
					return user;
				}
			}
		}
		user = new SysUser();
		return user;
	}
		
	@RequestMapping(value = "/findUserList", method = RequestMethod.POST)
	@ResponseBody
	public Object findUserList(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize") Integer pageSize, 
			@RequestParam(required = false) Integer adminId,
			@RequestParam(required = false) Integer proPower,
			@RequestParam(required = false) Integer cityPower,
			@RequestParam(required = false) Integer areaPower,
			@RequestParam(value="startTime", required=false) String startTime,
			@RequestParam(value="endTime", required=false) String endTime,
			@RequestParam(value="keyword", required=false) String keyword) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		if(keyword.equals("undefined"))
			keyword = null;
		else{
		keyword = URLDecoder.decode(keyword, "UTF-8");
		}
		Page<SysUser> sysUsers = userDao.findAllUser(keyword,startTime, endTime, proPower, cityPower, areaPower,adminId);
		Page<SysUserDto> sysUserDtos = new Page<SysUserDto>();
		for (SysUser sysUser : sysUsers) {
			SysUserDto sysUserDto = new SysUserDto();
			sysUserDto.setSysUser(sysUser);
			if (sysUser.getPro_power().equals("-1")) {
				sysUserDto.setPro_name("不限");
			}
			else {
				sysUserDto.setPro_name(cityMapper.findProvinceById(Integer.parseInt(sysUser.getPro_power())).getName());
			}
			if (sysUser.getCity_power().equals("-1")) {
				sysUserDto.setCity_name("不限");
			}
			else {
				sysUserDto.setCity_name(cityMapper.findCityById(Integer.parseInt(sysUser.getCity_power())).getName());
			}
			if (sysUser.getArea_power().equals("-1")) {
				sysUserDto.setArea_name("不限");
			}
			else {
				sysUserDto.setArea_name(cityMapper.findAreaNameByAreaID(Integer.parseInt(sysUser.getArea_power())).getName());
			}
			sysUserDtos.add(sysUserDto);
		}
		sysUserDtos.setPageSize(sysUsers.getPageSize());
		sysUserDtos.setPages(sysUsers.getPages());
		sysUserDtos.setTotal(sysUsers.getTotal());
		return new PageInfo<SysUserDto>(sysUserDtos);
		
	}
	
	@RequestMapping(value = "/findAllUsers")
	@ResponseBody
	public Object findAllUsers(@RequestParam(required = false) Integer proPower,
			@RequestParam(required = false) Integer cityPower,
			@RequestParam(required = false) Integer areaPower) throws UnsupportedEncodingException {
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		return userDao.findAllUser(null, null, null,proPower,cityPower,areaPower,null);
	}
	
	@RequestMapping(value = "/addUser", method = RequestMethod.GET)
	@ResponseBody
	public Object addUser(SysUser user) throws UnsupportedEncodingException {
		if (user.getUser_name() == null || user.getPassword() == null) {
			return new ResultDto(-1, "用户名和密码不能为空");
		}
		if (userDao.findUserByName(user.getUser_name())!=null) {
			return new ResultDto(-1, "用户名已存在");
		}
		userDao.insert(user);
		return new ResultDto(0,"添加成功");
	}
	
	@RequestMapping(value = "/updateUser", method = RequestMethod.GET)
	@ResponseBody
	public Object updateUser(SysUser user) throws UnsupportedEncodingException {
		/*if (user.getUser_name() == null || user.getPassword() == null) {
			return new ResultDto(-1, "用户名和密码不能为空");
		}*/
		if (user.getUser_name() == null) {
			return new ResultDto(-1, "用户名不能为空");
		}
//		if (userDao.findUserByName(user.getUser_name())!=null) {
//			return new ResultDto(-1, "用户名已存在");
//		}
		userDao.updateUser(user);
		return new ResultDto(0,"更新成功");
	}
	
	@RequestMapping(value = "/changePwd")
	@ResponseBody
	public Object changePwd(HttpServletRequest request,@RequestParam(value="password") String password,
			@RequestParam(value="userID", required=false) Integer userID) {
		SysUser sysUser = new SysUser();
		sysUser.setUser_id(userID);
		sysUser.setPassword(password);
		userDao.updateUser(sysUser);
		logout(request);
		return new BaseDto(0);
	}
	
	@ResponseBody
	@RequestMapping(value = "/checkUserName", method = RequestMethod.GET)
	public Object checkUserName(@RequestParam("value") String username) {
		username = StringUtils.trimAllWhitespace(username);
		NgRemoteValidateDTO ngRemoteValidateDTO = new NgRemoteValidateDTO();
		ngRemoteValidateDTO.setValid(userDao.findUserByName(username)==null? true:false);
		return ngRemoteValidateDTO;
	}
	
	
	@RequestMapping(value = "/deleteUserByID")
	@ResponseBody
	public Object deleteUserByID(Integer userID) {
		 userDao.deleteByPrimaryKey(userID);
		 return new BaseDto(0);
	}
	
	@RequestMapping(value = "/deleteUserByIDs")
	@ResponseBody
	public Object deleteUserByIDs(Integer[] userIDs) {
		for(Integer userID:userIDs){
			userDao.deleteByPrimaryKey(userID);
		}
		return new BaseDto(0);
	}
}
