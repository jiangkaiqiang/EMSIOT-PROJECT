package com.ems.iot.manage.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.AreaAlarm;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.SensitiveArea;
import com.github.pagehelper.Page;

public interface SensitiveAreaMapper {
    int deleteByPrimaryKey(Integer sensitive_area_id);

    int insert(SensitiveArea record);

    int insertSelective(SensitiveArea record);

    SensitiveArea selectByPrimaryKey(Integer sensitive_area_id);

    int updateByPrimaryKeySelective(SensitiveArea record);

    int updateByPrimaryKey(SensitiveArea record);

    Page<SensitiveArea> findAllSensitiveAreas(@Param("sensitiveAreaID")Integer sensitiveAreaID,@Param("sensitiveAreaName")String sensitiveAreaName
    		,@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    List<SensitiveArea> findAll();
    
    /**
     * 防止出现相同名字的限制区域
     * @param limitAreaName
     * @return
     */
    SensitiveArea findSensitiveAreaForFilter(@Param("sensitiveAreaName")String sensitiveAreaName);
    
    Page<AreaAlarm> findAllSensitiveAreaAlarmByOptions(@Param("plateNum")String plateNum, @Param("areaName")String areaName, @Param("alarmDateStart")Date alarmDateStart,@Param("alarmDateEnd")Date alarmDateEnd,
    		@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
}