package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.Electrombile;

public interface ElectrombileMapper {
    int deleteByPrimaryKey(Integer elect_id);

    int insert(Electrombile record);

    int insertSelective(Electrombile record);

    Electrombile selectByPrimaryKey(Integer elect_id);

    int updateByPrimaryKeySelective(Electrombile record);

    int updateByPrimaryKey(Electrombile record);
}