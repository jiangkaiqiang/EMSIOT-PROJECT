package com.ems.iot.manage.controllerApp;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.StringUtil;
/**
 * @author Barry
 * @date 2018年3月20日下午3:33:14  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/SysUserApp")
public class SysUserAppController extends AppBaseController {
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private CookieService cookieService;
	
	/**
	 * 系统用户（管理员登录）
	 * @param request
	 * @param sysUserName
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	public Object login(String sysUserName, String password) {
		if(StringUtil.isnotNull(sysUserName)&&StringUtil.isnotNull(password)){
			SysUser sysUser = sysUserMapper.findUser(sysUserName, password);
			if (sysUser != null) {
				sysUser.setLogin_time(new Date());
				sysUserMapper.updateUser(sysUser);
				String cookie = cookieService.insertCookie(sysUserName);
	            return new AppResultDto(true, 1001, "登录成功", cookie);
			}
			return new AppResultDto(3001, "用户名密码错误", false);
		}else{
			return new AppResultDto(3001, "用户名和密码不能为空", false);
		}		
	}
	
	/**
	 * 系统用户（管理员）注销
	 * @param request
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
	 * 查询用户是否登录成功
	 * @param request
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/findSysUser")
	@ResponseBody
	public Object findSysUser(String token) {
		SysUser sysUser = null;
		if(StringUtil.isnotNull(token)){
			Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
			if (effectiveCookie != null) {
				sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
				if(sysUser!=null){
					sysUser.setPassword("********");
					return new AppResultDto(true, 1, "用户已登录", sysUser);
				}
			}
		}
		else {
			return new AppResultDto(3001, "请输入token", false);
		}
		return new AppResultDto(4001,"登录已过期，请重新登录",false);
	}
	
	/**
	 * 系统用户注册
	 * @param sysUser
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addSysUser")
	@ResponseBody
	public Object addSysUser(SysUser sysUser) throws UnsupportedEncodingException {
		if (sysUser.getUser_name() == null || sysUser.getPassword() == null) {
			return new AppResultDto(3001, "用户名和密码不能为空",false);
		}
		if (sysUserMapper.findUserByName(sysUser.getUser_name())!=null) {
			return new AppResultDto(3001, "用户名已存在", false);
		}
		sysUserMapper.insert(sysUser);
		return new AppResultDto(1001,"添加成功");
	}
	
	/**
	 * 修改系统用户信息
	 * @param sysUser
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/updateSysUser")
	@ResponseBody
	public Object updateSysUser(SysUser sysUser,String token) throws UnsupportedEncodingException {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		if (effectiveCookie==null) {
				return new AppResultDto(4001, "登录失效，请先登录", false);
			 }
		if (sysUser.getUser_name() == null) {
			return new ResultDto(3001, "用户名不能为空");
		}
		sysUserMapper.updateUser(sysUser);
		return new ResultDto(1001,"更新成功");
	}
	
	/**
	 * 修改系统用户（管理员）密码
	 * 
	 * @param request
	 * @param password
	 * @param sysUserID
	 * @return
	 */
	@RequestMapping(value = "/changePwd")
	@ResponseBody
	public Object changePwd(HttpServletRequest request,@RequestParam(value="password") String password,
			@RequestParam(value="sysUserID", required=false) Integer sysUserID,String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		if (password==null) {
			return new ResultDto(3001, "密码不能为空", false);
		}
		if (sysUserID==null) {
			return new ResultDto(3001,"用户id不能为空",false);
		}
		SysUser sysUser = new SysUser();
		sysUser.setUser_id(sysUserID);
		sysUser.setPassword(password);
		sysUserMapper.updateUser(sysUser);
		logout(token);
		return new ResultDto(1001, "修改成功");
	}
}
