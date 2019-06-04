package com.ems.iot.manage.controllerApp;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.ems.iot.manage.dao.AppUserMapper;
import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.VerifyCodeMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.dto.AppUserDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.dto.SysUserDto;
import com.ems.iot.manage.entity.AppUser;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.VerifyCode;
import com.ems.iot.manage.service.CookieService;
import com.ems.iot.manage.util.StringUtil;
import com.ems.iot.manage.util.TelephoneVerifyUtil;
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
	@Autowired
	private VerifyCodeMapper verifyCodeMapper;
	@Autowired
	private CityMapper cityMapper;
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
			return new AppResultDto(3001, "用户名或密码错误", false);
		}else{
			return new AppResultDto(3001, "用户名和密码不能为空", false);
		}		
	}
	
	
	@RequestMapping(value = "/loginWithLoginCode")
	@ResponseBody
	public Object loginWithLoginCode(String appUserName,HttpServletRequest request, String loginCode, String telephone) {
		if(StringUtil.isnotNull(appUserName)){
			//String sessyzm=""+request.getSession().getAttribute("loginCode");
			VerifyCode verifyCode = verifyCodeMapper.findVerifyCodeByTypeAndTele(2, telephone);
			if (verifyCode==null) {
				return new AppResultDto(3001,"手机号输入错误",false);
			}
			String sessyzm = verifyCode.getCode_num();
			if(loginCode==null||!(sessyzm).equalsIgnoreCase(loginCode)){
				return new AppResultDto(3001,"验证码输入错误",false);
			}
			AppUser appUser = appUserMapper.findUserByName(appUserName);
			if (appUser == null) {
				return new AppResultDto(3001, "该用户不存在请先注册", false);
				
			}else {
				appUser.setLogin_time(new Date());
				appUserMapper.updateByPrimaryKeySelective(appUser);
				String cookie = cookieService.insertCookie(appUserName);
	            return new AppResultDto(true, 1001, "登录成功", cookie);
			}
		}else{
			return new AppResultDto(3001, "用户名不能为空", false);
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
					if(appUser.getPro_id()!=null && appUser.getCity_id()!=null && appUser.getArea_id()!=null) {
						appUser.setHaveAddress(true);
						appUser.setPro_name(cityMapper.findProvinceById(appUser.getPro_id()).getName());
						appUser.setCity_name(cityMapper.findCityById(appUser.getCity_id()).getName());
						appUser.setArea_name(cityMapper.findAreaNameByAreaID(appUser.getArea_id()).getName());
					}
					
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
	 * 校验注册验证码是否正确
	 * @param request
	 * @param signUpCode
	 * @return
	 */
	@RequestMapping(value = "/verifySignUpCode")
	@ResponseBody
	public Object verifySignUpCode(HttpServletRequest request, String telephone, String signUpCode) {
		//String sessyzm=""+request.getSession().getAttribute("signUpCode");
		VerifyCode verifyCode = verifyCodeMapper.findVerifyCodeByTypeAndTele(1, telephone);
		if (verifyCode==null) {
			return new AppResultDto(3001,"手机号输入错误",false);
		}
		String sessyzm = verifyCode.getCode_num();
		if(signUpCode==null||!(sessyzm).equalsIgnoreCase(signUpCode))
		    return new AppResultDto(3001,"验证码输入错误",false);
		else 
		    return new AppResultDto(1001,"验证码验证成功"); 
	}
	
	/**
	 * 发送注册验证码
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
			//request.getSession().setAttribute("signUpCode", signUpCode);
            insertVerifyCode(1, telephone, signUpCode);
			return new AppResultDto(1001,"验证码已发送，请查收！"); 
		}
		return new AppResultDto(3001,"请输入手机号",false);
	}
	
	/**
	 * 将验证码入库
	 * @param code_type
	 * @param telephone
	 * @param signUpCode
	 */
	public void insertVerifyCode(Integer code_type, String telephone, String signUpCode){
		VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode_num(signUpCode);
        verifyCode.setCode_type(code_type);
        verifyCode.setUser_tele(telephone);
        VerifyCode verifyCodeOld = verifyCodeMapper.findVerifyCodeByTypeAndTele(verifyCode.getCode_type(), verifyCode.getUser_tele());
        if (verifyCodeOld!=null) {
        	verifyCode.setCode_id(verifyCodeOld.getCode_id());
			verifyCodeMapper.updateByPrimaryKeySelective(verifyCode);
		}else {
			verifyCodeMapper.insert(verifyCode);
		}
	}
	
	/**
	 * 发送登录验证码
	 * @param request
	 * @param telephone
	 * @return
	 * @throws ServerException
	 * @throws ClientException
	 */
	@RequestMapping(value = "/sendLoginCode")
	@ResponseBody
	public Object sendLoginCode(HttpServletRequest request,@RequestParam(value="telephone",required=true) String telephone) throws ServerException, ClientException {
		if(telephone!=null&&!telephone.equals("")){
			TelephoneVerifyUtil teleVerify = new TelephoneVerifyUtil();
			String loginCode = teleVerify.loginVerify(telephone);
            if (loginCode==null) {
            	return new AppResultDto(3001,"获取验证码失败",false);
			}
			//request.getSession().setAttribute("loginCode", loginCode);
            insertVerifyCode(2, telephone, loginCode);
			return new AppResultDto(1001,"验证码已发送，请查收！"); 
		}
		return new AppResultDto(3001,"请输入手机号",false);
	}
	
	/**
	 * 发送找回密码验证码
	 * @param request
	 * @param telephone
	 * @return
	 * @throws ServerException
	 * @throws ClientException
	 */
	@RequestMapping(value = "/sendFindPasswordCode")
	@ResponseBody
	public Object sendFindPasswordCode(HttpServletRequest request,@RequestParam(value="telephone",required=true) String telephone) throws ServerException, ClientException {
		if(telephone!=null&&!telephone.equals("")){
			TelephoneVerifyUtil teleVerify = new TelephoneVerifyUtil();
			String findPasswordCode = teleVerify.findPassWordVerify(telephone);
            if (findPasswordCode==null) {
            	return new AppResultDto(3001,"获取验证码失败",false);
			}
			//request.getSession().setAttribute("findPasswordCode", findPasswordCode);
            insertVerifyCode(3, telephone, findPasswordCode);
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
	 * 个人用户注册，包含注册码验证
	 * @param request
	 * @param password
	 * @param userTele
	 * @param nickname
	 * @param signUpCode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addAppUserWithSignUpCode")
	@ResponseBody
	public Object addAppUserWithSignUpCode(HttpServletRequest request,String password,String userTele,String nickname, String signUpCode,Integer source) throws UnsupportedEncodingException {
		//String sessyzm=""+request.getSession().getAttribute("signUpCode");
		VerifyCode verifyCode = verifyCodeMapper.findVerifyCodeByTypeAndTele(1, userTele);
		if (verifyCode==null) {
			return new AppResultDto(3001,"手机号输入错误",false);
		}
		String sessyzm = verifyCode.getCode_num();
		if(signUpCode==null||!(sessyzm).equalsIgnoreCase(signUpCode)){
			 return new AppResultDto(3001,"验证码输入错误",false);
		}
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
		/**
		 * 注册来源（1：IOS，2：Android，3、小程序）
		 */
		appUser.setSource(source);
		appUserMapper.insert(appUser);
		return new AppResultDto(1001,"注册成功");
	}
	
	/**
	 * 个人用户根据找回密码验证码找回密码
	 * @param request
	 * @param password
	 * @param userTele
	 * @param appUserID
	 * @param findPasswordCode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findPasswordWithfindPasswordCode")
	@ResponseBody
	public Object findPasswordWithfindPasswordCode(HttpServletRequest request,String password,String userTele, String findPasswordCode) throws UnsupportedEncodingException {
		//String sessyzm=""+request.getSession().getAttribute("findPasswordCode");
		VerifyCode verifyCode = verifyCodeMapper.findVerifyCodeByTypeAndTele(3, userTele);
		if (verifyCode==null) {
			return new AppResultDto(3001,"手机号输入错误",false);
		}
		String sessyzm = verifyCode.getCode_num();
		if(findPasswordCode==null||!(sessyzm).equalsIgnoreCase(findPasswordCode)){
			 return new AppResultDto(3001,"验证码输入错误",false);
		}
		if (password == null||userTele==null) {
			return new AppResultDto(3001, "手机号和密码不能为空",false);
		}
		AppUser appUserOld = appUserMapper.findUserByTele(userTele);
		if (appUserOld==null) {
			return new ResultDto(3001,"该手机号未绑定账号，请注册",false);
		}
		AppUser appUser = new AppUser();
		appUser.setPassword(password);
		appUser.setUser_id(appUserOld.getUser_id());
		appUser.setUser_tele(userTele);
		appUserMapper.updateByPrimaryKeySelective(appUser);
		return new AppResultDto(1001,"设置成功");
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
	
	/**
	 * 个人用户修改密码
	 * @param request
	 * @param password
	 * @param oldPassword
	 * @param appUserID
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/changePwd")
	@ResponseBody
	public Object changePwd(HttpServletRequest request,
			@RequestParam(value="password") String password,
			@RequestParam(value="oldPassword", required=false) String oldPassword,
			@RequestParam(value="appUserID", required=false) Integer appUserID, String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		AppUser oldAppUser = appUserMapper.findUserById(appUserID);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
//		if (oldPassword==null || oldPassword.equals("")) {
//			return new ResultDto(3001, "旧密码不能为空", false);
//		}
		if (password==null || password.equals("")) {
			return new ResultDto(3001, "新密码不能为空", false);
		}
		if (appUserID==null) {
			return new ResultDto(3001,"用户id不能为空",false);
		}
		if (!oldAppUser.getPassword().equals(oldPassword)) {
			return new ResultDto(3001, "旧密码输入错误", false);
		}
		AppUser appUser = new AppUser();
		appUser.setUser_id(Long.parseLong(appUserID+""));
		appUser.setPassword(password);
		appUserMapper.updateByPrimaryKeySelective(appUser);
		logout(token);
		return new ResultDto(1001, "修改成功");
	}
	
	@RequestMapping(value = "/changeArea")
	@ResponseBody
	public Object changeArea(HttpServletRequest request,
			@RequestParam(value="proId", required=false) Integer proId,
			@RequestParam(value="cityId", required=false) Integer cityId,
			@RequestParam(value="areaId", required=false) Integer areaId, String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token); 
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
		}
		AppUser appUser = null;
		if (proId==null) {
			return new ResultDto(3001, "所属省不能为空", false);
		}
		if (cityId==null || cityId==-1) {
			return new ResultDto(3001, "所属市不能为空", false);
		}
		if (areaId==null || areaId==-1) {
			return new ResultDto(3001, "所属区/县不能为空", false);
		}
		appUser = appUserMapper.findUserByName(effectiveCookie.getUsername());
		if(appUser==null){
			return new ResultDto(3001, "用户不存在", false);
		}
		appUser.setPro_id(proId);
		appUser.setCity_id(cityId);
		appUser.setArea_id(areaId);
		appUserMapper.updateByPrimaryKeySelective(appUser);
		return new ResultDto(1001, "修改成功",true);
	}
	
	@RequestMapping(value = "/findUserAppList", method = RequestMethod.POST)
	@ResponseBody
	public Object findUserAppList(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize") Integer pageSize, 
			@RequestParam(required = false) Integer proPower,
			@RequestParam(required = false) Integer cityPower,
			@RequestParam(required = false) Integer areaPower,
			@RequestParam(value="startTime", required=false) String startTime,
			@RequestParam(value="endTime", required=false) String endTime,
			@RequestParam(value="userTele", required=false) String userTele,
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
		Page<AppUser> appUsers = appUserMapper.findAllUserApp(keyword, userTele, startTime, endTime, proPower, cityPower, areaPower);
		Page<AppUserDto> appUserDtos = new Page<AppUserDto>();
		for (AppUser appUser : appUsers) {
			AppUserDto appUserDto = new AppUserDto();
			appUserDto.setAppUser(appUser);
			if (null==appUser.getPro_id() || appUser.getPro_id()==-1) {
				appUserDto.setPro_name("不限");
			}
			else {
				appUserDto.setPro_name(cityMapper.findProvinceById(appUser.getPro_id()).getName());
			}
			if (null==appUser.getCity_id() || appUser.getCity_id()==-1) {
				appUserDto.setCity_name("不限");
			}
			else {
				appUserDto.setCity_name(cityMapper.findCityById(appUser.getCity_id()).getName());
			}
			if (null==appUser.getArea_id() || appUser.getArea_id()==-1) {
				appUserDto.setArea_name("不限");
			}
			else {
				appUserDto.setArea_name(cityMapper.findAreaNameByAreaID(appUser.getArea_id()).getName());
			}
			appUserDtos.add(appUserDto);
		}
		appUserDtos.setPageSize(appUsers.getPageSize());
		appUserDtos.setPages(appUsers.getPages());
		appUserDtos.setTotal(appUsers.getTotal());
		return new PageInfo<AppUserDto>(appUserDtos);
		
	}
}
