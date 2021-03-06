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
    
    List<Station> selectByPrimaryKeyIn(@Param("listIds")List<Integer> listIds);
    
    Station selectByStationName(@Param("stationName")String stationName);

    int updateByPrimaryKeySelective(Station record);
    
    int updateStatusByStationNum(Station record);

    int updateByPrimaryKey(Station record);
    
    List<Station> findAllStations(@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    Page<Station> findAllStationsByKey(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("stationPhyNum")Integer stationPhyNum, @Param("stationName")String stationName, @Param("stationStatus")Integer stationStatus,
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    Station selectByStationPhyNum(Integer stationPhyNum);
    
    List<Station> findStationsByStatus(@Param("stationStatus")Integer stationStatus,
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    /**
     * 避免添加重复字段
     * @param stationName
     * @param stationPhyNum
     * @return
     */
    Station findStationsForFilter(@Param("stationName")String stationName,@Param("stationPhyNum")Integer stationPhyNum);
}