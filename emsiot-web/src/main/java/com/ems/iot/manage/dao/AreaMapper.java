package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.Area;

public interface AreaMapper {
    int deleteByPrimaryKey(Integer _id);

    int insert(Area record);

    int insertSelective(Area record);

    Area selectByPrimaryKey(Integer _id);

    int updateByPrimaryKeySelective(Area record);

    int updateByPrimaryKey(Area record);
}