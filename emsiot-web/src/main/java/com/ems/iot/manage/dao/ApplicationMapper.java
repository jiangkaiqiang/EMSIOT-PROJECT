package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.Application;

public interface ApplicationMapper {
    int deleteByPrimaryKey(String app_name);

    int insert(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(String app_name);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);
}