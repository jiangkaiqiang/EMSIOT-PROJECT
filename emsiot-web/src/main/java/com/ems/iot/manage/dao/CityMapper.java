package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Area;
import com.ems.iot.manage.entity.City;
import com.ems.iot.manage.entity.Province;

import java.util.List;

/**
 * @author Barry
 * @date 2018年3月20日下午3:33:37  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface CityMapper {
    List<City> findCitysByProvinceId(@Param("provinceID") int provinceID);
    
    List<City> findAreasByCityId(@Param("cityID") int cityID);
    
    List<City> findCityList();
    
    City findCityById(@Param("CityID") int CityID);

	Province findProvinceById(@Param("provinceID") int provinceID);

	List<City> findCityByNameAndProvinceId(@Param("cityName") String cityName, @Param("provinceID") Integer provinceID);

	Area findAreaNameByAreaID(@Param("AreaID") int AreaID);
	
	Province findProvinceByName(@Param("provinceName") String provinceName);
	
	City findCityByNameAndProId(@Param("cityName") String cityName, @Param("proID") int proID);
	
	Area findAreaByNameAndCityId(@Param("areaName") String areaName, @Param("cityID") int cityID);
}
