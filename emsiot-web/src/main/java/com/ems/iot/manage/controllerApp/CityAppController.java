package com.ems.iot.manage.controllerApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ProvinceMapper;
import com.ems.iot.manage.dao.SysUserMapper;
import com.ems.iot.manage.dto.AppResultDto;
import com.ems.iot.manage.entity.Cookies;
import com.ems.iot.manage.entity.Province;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.service.CookieService;
/**
 * @author Barry
 * @date 2018年3月20日下午3:32:09  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/cityApp")
public class CityAppController {

	@Autowired
	private SysUserMapper sysUserMapper;
	
    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;
    
    @Autowired
	private CookieService cookieService;
 
    @RequestMapping(value = "/findProvinceList", method = RequestMethod.GET)
    @ResponseBody
    public Object findProvinceList() {
        return new AppResultDto(provinceMapper.findProvinceList());
    }
    
    @RequestMapping(value = "/findCitysByProvinceId", method = RequestMethod.GET)
    @ResponseBody
    public Object findCitysByProvinceId(@RequestParam Integer provinceID) {
        return new AppResultDto(cityMapper.findCitysByProvinceId(provinceID));
    }
    
    @RequestMapping(value = "/findAreasByCityId", method = RequestMethod.GET)
    @ResponseBody
    public Object findAreasByCityId(@RequestParam Integer cityID) {
        return new AppResultDto(cityMapper.findAreasByCityId(cityID));
    }
   
    /**
     * 定位管理员的当前城市
     * @param token
     * @return
     */
    @RequestMapping(value = "/findCityNameByUser")
    @ResponseBody
    public Object findCityNameByUserPower(String token) {
		Cookies effectiveCookie = cookieService.findEffectiveCookie(token);
		if (effectiveCookie==null) {
			return new AppResultDto(4001, "登录失效，请先登录", false);
	    }
		SysUser sysUser = sysUserMapper.findUserByName(effectiveCookie.getUsername());
		Integer proID = null;
		Integer cityID = null;
		Integer areaID = null;
		if (!sysUser.getPro_power().equals("-1")) {
			proID = Integer.valueOf(sysUser.getPro_power());
		}
		if (!sysUser.getCity_power().equals("-1")) {
			cityID = Integer.valueOf(sysUser.getCity_power());
		}
		if (!sysUser.getArea_power().equals("-1")) {
			areaID = Integer.valueOf(sysUser.getArea_power());
		}
    	if (proID==-1 || proID==null) {
    		Province province = new Province();
    		province.setName("上海");
			return new AppResultDto(province);
		}
    	else {
			if (cityID==-1||cityID==null) {
				return new AppResultDto(cityMapper.findProvinceById(proID));
			}
			else {
				if (areaID==-1||areaID==null) {
					return new AppResultDto(cityMapper.findCityById(cityID));
				}
				else {
					return new AppResultDto(cityMapper.findAreaNameByAreaID(areaID));
				}
			}
		}
    }
    
}
