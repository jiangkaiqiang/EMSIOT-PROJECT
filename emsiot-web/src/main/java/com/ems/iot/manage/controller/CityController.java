package com.ems.iot.manage.controller;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ProvinceMapper;
import com.ems.iot.manage.entity.Area;
import com.ems.iot.manage.entity.City;
import com.ems.iot.manage.entity.Province;
import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * @author Barry
 * @date 2018年3月20日下午3:32:09  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/city")
public class CityController {

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;
    
    
    @RequestMapping(value = "/findProvinceList", method = RequestMethod.GET)
    @ResponseBody
    public Object findProvinceList() {
        return provinceMapper.findProvinceList();
    }
    
    @RequestMapping(value = "/findCityList", method = RequestMethod.GET)
    @ResponseBody
    public Object findCityList() {
        return cityMapper.findCityList();
    }
    
    
    @RequestMapping(value = "/findCitysByProvinceId", method = RequestMethod.GET)
    @ResponseBody
    public Object findCitysByProvinceId(@RequestParam Integer provinceID) {
        return cityMapper.findCitysByProvinceId(provinceID);
    }
    
    @RequestMapping(value = "/findAreasByCityId", method = RequestMethod.GET)
    @ResponseBody
    public Object findAreasByCityId(@RequestParam Integer cityID) {
        return cityMapper.findAreasByCityId(cityID);
    }
    
    @RequestMapping(value = "/findCityById", method = RequestMethod.GET)
    @ResponseBody
    public Object findCityById(@RequestParam Integer CityID) {
        return cityMapper.findCityById(CityID);
    }
    
    @RequestMapping(value = "/findProvinceById", method = RequestMethod.GET)
    @ResponseBody
    public Object findProvinceById(@RequestParam Integer provinceID) {
        return cityMapper.findProvinceById(provinceID);
    }
    
    
    @RequestMapping(value = "/findProvinceByName", method = RequestMethod.GET)
    @ResponseBody
    public Object findProvinceByName(@RequestParam String provinceName) {
    	if (provinceName!=null) {
    		 return cityMapper.findProvinceByName(provinceName);
		}
    	else{
    		return null;
    	}
    }
    
    @RequestMapping(value = "/findCityByNameAndProvinceId", method = RequestMethod.GET)
    @ResponseBody
    public Object findCityByNameAndProvinceId(@RequestParam Integer provinceID,@RequestParam String cityName) {
    	if (cityName!=null) {
    		 return cityMapper.findCityByNameAndProvinceId(cityName,provinceID);
		}
    	else{
    		return null;
    	}
    }
    
    @RequestMapping(value = "/findCityNameByUserPower")
    @ResponseBody
    public Object findCityNameByUserPower(@RequestParam Integer areaID,@RequestParam Integer cityID,@RequestParam Integer proID) {
    	if (proID==-1 || proID==null) {
    		Province province = new Province();
    		province.setName("上海");
			return province;
		}
    	else {
			if (cityID==-1||cityID==null) {
				return cityMapper.findProvinceById(proID);
			}
			else {
				if (areaID==-1||areaID==null) {
					return cityMapper.findCityById(cityID);
				}
				else {
					return cityMapper.findAreaNameByAreaID(areaID);
				}
			}
		}
    }
    
}
