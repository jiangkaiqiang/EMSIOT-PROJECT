package com.ems.iot.manage.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.dto.ElectAlarmDto;
import com.ems.iot.manage.dto.StationElectDto;
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
    
    Integer findElectalarmsListCount(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    /**
     * 根据车辆芯片编号和时间，查询报警车辆轨迹
     * @param guaCardNum
     * @param startTimeForTrace
     * @param endTimeForTrace
     * @return
     */
    List<ElectAlarm> selectByGuaCardNumForTrace(@Param("guaCardNum") int guaCardNum,
    		@Param("startTimeForTrace") String startTimeForTrace, @Param("endTimeForTrace") String endTimeForTrace);
    
    /**
     * 返回当前时间范围内报警车辆的数量；如果需要修改时间大小则修改时间范围，需要根据车号进行分组过滤，
     * 即如果一辆车在该范围内经过了多个基站，则认为报警车辆只有一辆
     * @return
     */
    List<Integer> selectElectsByEleGuaCardNumNow(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    
    /**
     * 根据基站的物理编号和时间，查询某个基站下的车辆
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<ElectAlarm> selectElectsByStationPhyNumAndTime(@Param("stationPhyNum") int stationPhyNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 根据车辆的物理编号和时间，查询某个车辆下的基站
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<StationElectDto> selectElectsByGuaCardNumNumAndTime(@Param("guaCardNum") int guaCardNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    
    /**
     * 根据报警数据获得最后一次出现的车辆数据
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<ElectAlarm> selectElectAlarmVehicleByTime(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
}