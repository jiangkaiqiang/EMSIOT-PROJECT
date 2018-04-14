package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.ems.iot.manage.entity.Station;
import com.github.pagehelper.Page;

public interface StationMapper {
    int deleteByPrimaryKey(Integer station_id);

    int insert(Station record);

    int insertSelective(Station record);

    Station selectByPrimaryKey(Integer station_id);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);
    
    List<Station> findAllStations();
    
    Page<Station> findAllStationsByKey(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("stationPhyNum")Integer stationPhyNum, @Param("stationName")String stationName, @Param("stationStatus")Integer stationStatus);
}