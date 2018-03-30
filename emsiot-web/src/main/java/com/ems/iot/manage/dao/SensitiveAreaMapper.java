package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.SensitiveArea;

public interface SensitiveAreaMapper {
    int deleteByPrimaryKey(Integer sensitive_area_id);

    int insert(SensitiveArea record);

    int insertSelective(SensitiveArea record);

    SensitiveArea selectByPrimaryKey(Integer sensitive_area_id);

    int updateByPrimaryKeySelective(SensitiveArea record);

    int updateByPrimaryKey(SensitiveArea record);
}