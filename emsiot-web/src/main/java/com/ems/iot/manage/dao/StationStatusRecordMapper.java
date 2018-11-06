package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.StationStatusRecord;
import com.github.pagehelper.Page;

public interface StationStatusRecordMapper {
    int deleteByPrimaryKey(Integer station_status_id);

    int insert(StationStatusRecord record);
    
    List<StationStatusRecord> findStationsRecordByStationNumAndLimit(@Param("stationPhyNum")Integer stationPhyNum, @Param("limit")Integer limit );
    
    Page<StationStatusRecord> findAllStationsRecordByKey(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("stationPhyNum")Integer stationPhyNum, @Param("stationStatus")Integer stationStatus,
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    StationStatusRecord selectByStationPhyNumLimitOne(Integer stationPhyNum);
    
}