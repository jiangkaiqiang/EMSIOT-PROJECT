package com.ems.iot.manage.dao;

import java.util.List;

import com.ems.iot.manage.entity.Province;
/**
 * @author Barry
 * @date 2018年3月20日下午3:34:48  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface ProvinceMapper {

    List<Province> findProvinceList();

}
