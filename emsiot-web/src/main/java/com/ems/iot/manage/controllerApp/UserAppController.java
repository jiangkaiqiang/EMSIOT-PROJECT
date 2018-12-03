package com.ems.iot.manage.controllerApp;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.ems.iot.manage.dao.AppUserMapper;
import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.AppUser;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.StringUtil;
import com.ems.iot.manage.util.TelephoneVerifyUtil;
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
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private BlackelectMapper blackelectMapper;
	
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
					appUser.setElectCount(electrombileMapper.findElectsCountByTele(appUser.getUser_tele()));
					appUser.setAlarnCount(blackelectMapper.findBlackelectsCountByOwnerTele(appUser.getUser_tele()));
					appUser.setPassword("********");
					return new AppResultDto(true, 1001, "用户已登录", appUser);
				}
			}
		}
		else {
			return new AppResultDto(3001, "请输入token", false);
		}
		return new AppResultDto(4001,"登录已过期，请重新登录",false);
	}
	
	/**
	 * 校验验证码是否正确
	 * @param request
	 * @param signUpCode
	 * @return
	 */
	@RequestMapping(value = "/verifySignUpCode")
	@ResponseBody
	public Object verifySignUpCode(HttpServletRequest request, String signUpCode) {
		String sessyzm=""+request.getSession().getAttribute("signUpCode");
		if(signUpCode==null||!(sessyzm).equalsIgnoreCase(signUpCode))
		    return new AppResultDto(3001,"验证码输入错误",false);
		else 
		    return new AppResultDto(1001,"验证码验证成功"); 
	}
	
	/**
	 * 发送验证码
	 * @param request
	 * @param telephone
	 * @return
	 * @throws ServerException
	 * @throws ClientException
	 */
	@RequestMapping(value = "/sendSignUpCode")
	@ResponseBody
	public Object sendSignUpCode(HttpServletRequest request,@RequestParam(value="telephone",required=true) String telephone) throws ServerException, ClientException {
		if(telephone!=null&&!telephone.equals("")){
			TelephoneVerifyUtil teleVerify = new TelephoneVerifyUtil();
			String signUpCode = teleVerify.signUpVerify(telephone);
            if (signUpCode==null) {
            	return new AppResultDto(3001,"获取验证码失败",false);
			}
			request.getSession().setAttribute("signUpCode", signUpCode);
			return new AppResultDto(1001,"验证码已发送，请查收！"); 
		}
		return new AppResultDto(3001,"请输入手机号",false);
	}
	
	/**
	 * 个人用户注册
	 * @param sysUser
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addAppUser")
	@ResponseBody
	public Object addAppUser(String password,String userTele,String nickname) throws UnsupportedEncodingException {
		if (password == null||userTele==null) {
			return new AppResultDto(3001, "手机号和密码不能为空",false);
		}
		AppUser appUser = new AppUser();
		appUser.setNickname(nickname);
		appUser.setPassword(password);
		/**
		 * 在此将用户名和手机号都设置为手机号，用户注册只需要输入手机号即可
		 */
		appUser.setUser_name(userTele);
		appUser.setUser_tele(userTele);
		if (appUserMapper.findUserByName(appUser.getUser_name())!=null) {
			return new AppResultDto(3001, "用户手机号已存在", false);
		}
		appUserMapper.insert(appUser);
		return new AppResultDto(1001,"注册成功");
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
