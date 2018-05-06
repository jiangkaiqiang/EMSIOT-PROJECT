package com.ems.iot.manage.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.ElectAlarm;
import com.github.pagehelper.Page;

public interface ElectAlarmMapper {
    int deleteByPrimaryKey(Integer elect_alarm_id);

    int insert(ElectAlarm record);

    int insertSelective(ElectAlarm record);

    ElectAlarm selectByPrimaryKey(Integer elect_alarm_id);

    int updateByPrimaryKeySelective(ElectAlarm record);

    int updateByPrimaryKey(ElectAlarm record);
    
    Page<ElectAlarm> findAllElectalarmByOptions(@Param("alarmTime")Date alarmTime);
}