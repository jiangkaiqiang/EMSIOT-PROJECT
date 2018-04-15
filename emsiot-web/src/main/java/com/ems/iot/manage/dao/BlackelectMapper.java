package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.Blackelect;

public interface BlackelectMapper {
    int deleteByPrimaryKey(Integer black_id);

    int insert(Blackelect record);

    int insertSelective(Blackelect record);

    Blackelect selectByPrimaryKey(Integer black_id);

    int updateByPrimaryKeySelective(Blackelect record);

    int updateByPrimaryKey(Blackelect record);
}