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
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.NgRemoteValidateDTO;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.SysUserDto;
import com.ems.iot.manage.entity.Cookies;
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
@RequestMapping(value = "/user")
public class UserController extends BaseController {
	private static String baseDir = "picture";
	@Autowired
	private SysUserMapper userDao;
	@Autowired
	private CookieService cookieService;
	@Autowired
	private FtpService ftpService;
	
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
	     SysUser userEntity = userDao.findUserById(spaceUserID);
	     return userEntity;
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
			@RequestParam(value="startTime", required=false) String startTime,
			@RequestParam(value="endTime", required=false) String endTime,
			@RequestParam(value="keyword", required=false) String keyword) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		if(keyword.equals("undefined"))
			keyword = null;
		else{
		keyword = URLDecoder.decode(keyword, "UTF-8");
		}
		Page<SysUser> sysUsers = userDao.findAllUser(keyword,startTime, endTime);
		Page<SysUserDto> sysUserDtos = new Page<SysUserDto>();
		sysUserDtos.setPageSize(sysUsers.getPageSize());
		sysUserDtos.setPages(sysUsers.getPages());
		sysUserDtos.setTotal(sysUsers.getTotal());
		return new PageInfo<SysUserDto>(sysUserDtos);
		
	}
	
	@RequestMapping(value = "/findAllUsers")
	@ResponseBody
	public Object findAllUsers() throws UnsupportedEncodingException {
		return userDao.findAllUser(null, null, null);
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
