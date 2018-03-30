package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.AreaAlarm;

public interface AreaAlarmMapper {
    int deleteByPrimaryKey(Integer alarm_id);

    int insert(AreaAlarm record);

    int insertSelective(AreaAlarm record);

    AreaAlarm selectByPrimaryKey(Integer alarm_id);

    int updateByPrimaryKeySelective(AreaAlarm record);

    int updateByPrimaryKey(AreaAlarm record);
}