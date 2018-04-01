package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Electrombile;
import com.github.pagehelper.Page;

public interface ElectrombileMapper {
    int deleteByPrimaryKey(Integer elect_id);

    int insert(Electrombile record);

    int insertSelective(Electrombile record);

    Electrombile selectByPrimaryKey(Integer elect_id);

    int updateByPrimaryKeySelective(Electrombile record);

    int updateByPrimaryKey(Electrombile record);
    
    Page<Electrombile> findAllElectrombiles(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("recorderID")Integer recorderID, @Param("electState")Integer electState, @Param("insurDetail")Integer insurDetail,
    		@Param("proID")Integer proID, @Param("cityID")Integer cityID, @Param("areaID")Integer areaID, @Param("ownerTele")String ownerTele, @Param("ownerID")String ownerID, 
    		@Param("plateNum")String plateNum, @Param("guaCardNum")String guaCardNum, @Param("ownerName")String ownerName);
}