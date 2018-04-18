package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.ElectAlarm;

public interface ElectAlarmMapper {
    int deleteByPrimaryKey(Integer elect_alarm_id);

    int insert(ElectAlarm record);

    int insertSelective(ElectAlarm record);

    ElectAlarm selectByPrimaryKey(Integer elect_alarm_id);

    int updateByPrimaryKeySelective(ElectAlarm record);

    int updateByPrimaryKey(ElectAlarm record);
}