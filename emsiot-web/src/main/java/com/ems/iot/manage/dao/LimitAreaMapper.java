package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.LimitArea;

public interface LimitAreaMapper {
    int deleteByPrimaryKey(Integer limit_area_id);

    int insert(LimitArea record);

    int insertSelective(LimitArea record);

    LimitArea selectByPrimaryKey(Integer limit_area_id);

    int updateByPrimaryKeySelective(LimitArea record);

    int updateByPrimaryKey(LimitArea record);
}