package com.ems.iot.manage.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.github.pagehelper.Page;

public interface ElectAlarmMapper {
    int deleteByPrimaryKey(Integer elect_alarm_id);

    int insert(ElectAlarm record);

    int insertSelective(ElectAlarm record);

    ElectAlarm selectByPrimaryKey(Integer elect_alarm_id);

    int updateByPrimaryKeySelective(ElectAlarm record);

    int updateByPrimaryKey(ElectAlarm record);
    
    Page<ElectAlarm> findAllElectalarmByOptions(@Param("plateNum")String plateNum,@Param("alarmDateStart")Date alarmDateStart,@Param("alarmDateEnd")Date alarmDateEnd,
    		@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    List<ElectAlarm> findElectalarmsList(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    /**
     * 根据车辆芯片编号和时间，查询报警车辆轨迹
     * @param guaCardNum
     * @param startTimeForTrace
     * @param endTimeForTrace
     * @return
     */
    Page<ElectAlarm> selectByGuaCardNumForTrace(@Param("guaCardNum") int guaCardNum,
    		@Param("startTimeForTrace") String startTimeForTrace, @Param("endTimeForTrace") String endTimeForTrace);
    
}