package com.ems.iot.manage.dao;

import java.util.List;

import com.ems.iot.manage.entity.Station;

public interface StationMapper {
    int deleteByPrimaryKey(Integer station_id);

    int insert(Station record);

    int insertSelective(Station record);

    Station selectByPrimaryKey(Integer station_id);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);
    
    List<Station> findAllStations();
}