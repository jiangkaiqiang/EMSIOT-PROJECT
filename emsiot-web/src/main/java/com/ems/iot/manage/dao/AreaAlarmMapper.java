package com.ems.iot.manage.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.AreaAlarm;
import com.github.pagehelper.Page;

public interface AreaAlarmMapper {
    int deleteByPrimaryKey(Integer alarm_id);

    int insert(AreaAlarm record);

    int insertSelective(AreaAlarm record);

    AreaAlarm selectByPrimaryKey(Integer alarm_id);

    int updateByPrimaryKeySelective(AreaAlarm record);

    int updateByPrimaryKey(AreaAlarm record);
    
    Page<AreaAlarm> findAllAreaAlarmByOptions(@Param("plateNum")String plateNum, @Param("areaName")String areaName, @Param("alarmDateStart")Date alarmDateStart,@Param("alarmDateEnd")Date alarmDateEnd,
    		@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    List<AreaAlarm> findAreaAlarmByStationNumAndTime(@Param("alarmStationPhyNum")String alarmStationPhyNum, @Param("alarmDateStart")Date alarmDateStart,@Param("alarmDateEnd")Date alarmDateEnd,
    		@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    Integer findAreaAlarmCountByStationNumAndTime(@Param("alarmStationPhyNum")String alarmStationPhyNum, @Param("areaName")String areaName, @Param("alarmDateStart")Date alarmDateStart,@Param("alarmDateEnd")Date alarmDateEnd,
    		@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
}