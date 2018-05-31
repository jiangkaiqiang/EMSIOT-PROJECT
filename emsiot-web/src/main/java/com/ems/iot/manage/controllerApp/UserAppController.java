package com.ems.iot.manage.controllerApp;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.AppUserMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.AppUser;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.StringUtil;
/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/UserApp")
public class UserAppController extends AppBaseController {
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private CookieService cookieService;
	
	/**
	 * app 个人用户登录
	 * @param appUserName
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	public Object login(String appUserName, String password) {
		if(StringUtil.isnotNull(appUserName)&&StringUtil.isnotNull(password)){
			AppUser appUser = appUserMapper.findUser(appUserName, password);
			if (appUser != null) {
				appUser.setLogin_time(new Date());
				appUserMapper.updateByPrimaryKeySelective(appUser);
				String cookie = cookieService.insertCookie(appUserName);
	            return new AppResultDto(true, 1001, "登录成功", cookie);
			}
			return new AppResultDto(3001, "用户名密码错误", false);
		}else{
			return new AppResultDto(3001, "用户名和密码不能为空", false);
		}		
	}
	
	/**
	 * app个人用户注销
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/logout")
	@ResponseBody
	public Object logout(String token) {
		if (token==null) {
			return new AppResultDto(3001, "请输入token", false);
		}
		cookieService.deleteCookie(token);
		return new AppResultDto(1001, "注销成功");
	}

	/**
	 * 查询个人用户是否登录成功
	 * @param request
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/findAppUser")
	@ResponseBody
	public Object findAppUser(String token) {
		AppUser appUser = null;
		if(StringUtil.isnotNull(token)){
			Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
			if (effectiveCookie != null) {
				appUser = appUserMapper.findUserByName(effectiveCookie.getUsername());
				if(appUser!=null){
					appUser.setPassword("********");
					return new AppResultDto(true, 1, "用户已登录", appUser);
				}
			}
		}
		else {
			return new AppResultDto(3001, "请输入token", false);
		}
		return new AppResultDto(4001,"登录已过期，请重新登录",false);
	}
	
	/**
	 * 个人用户注册
	 * @param sysUser
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addAppUser")
	@ResponseBody
	public Object addAppUser(AppUser appUser) throws UnsupportedEncodingException {
		if (appUser.getUser_name() == null || appUser.getPassword() == null) {
			return new AppResultDto(3001, "用户名和密码不能为空",false);
		}
		appUser.setUser_name(appUser.getUser_tele());
		if (appUserMapper.findUserByName(appUser.getUser_name())!=null) {
			return new AppResultDto(3001, "用户名已存在", false);
		}
		appUserMapper.insert(appUser);
		return new AppResultDto(1001,"添加成功");
	}
	
	/**
	 * 修改app个人用户信息
	 * @param appUser
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/updateAppUser")
	@ResponseBody
	public Object updateAppUser(AppUser appUser,String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		if (effectiveCookie==null) {
				return new AppResultDto(4001, "登录失效，请先登录", false);
			 }
		if (appUser.getUser_name() == null) {
			return new ResultDto(3001, "用户名不能为空");
		}
		appUserMapper.updateByPrimaryKey(appUser);
		return new ResultDto(1001,"更新成功");
	}
	
	
	@RequestMapping(value = "/changePwd")
	@ResponseBody
	public Object changePwd(HttpServletRequest request,@RequestParam(value="password") String password,
			@RequestParam(value="appUserID", required=false) Integer appUserID, String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (password==null) {
			return new ResultDto(3001, "密码不能为空", false);
		}
		if (appUserID==null) {
			return new ResultDto(3001,"用户id不能为空",false);
		}
		AppUser appUser = new AppUser();
		appUser.setUser_id(Long.parseLong(appUserID+""));
		appUser.setPassword(password);
		appUserMapper.updateByPrimaryKeySelective(appUser);
		logout(token);
		return new ResultDto(1001, "修改成功");
	}
}
