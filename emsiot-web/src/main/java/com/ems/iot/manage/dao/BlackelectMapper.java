package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Blackelect;
import com.github.pagehelper.Page;

public interface BlackelectMapper {
    int deleteByPrimaryKey(Integer black_id);
    
    int deleteByPlateNum(String plate_num);

    int insert(Blackelect record);

    int insertSelective(Blackelect record);

    Blackelect selectByPrimaryKey(Integer black_id);

    int updateByPrimaryKeySelective(Blackelect record);

    int updateByPrimaryKey(Blackelect record);
    
    Page<Blackelect> findAllBlackelect();
    
    Page<Blackelect> findAllBlackelectByOptions(@Param("plateNum")String plateNum,@Param("ownerTele")String ownerTele,
    		@Param("DealStatus")Integer DealStatus,@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    List<Blackelect> findBlackelectsList(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    /*2018-12-17*/
    Integer findBlackelectsListCount(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    Integer findBlackelectsListCountForApp(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower,@Param("recorderId") Integer recorderId,@Param("dealStatus") Integer dealStatus);
    
    List<Blackelect> findBlackelectsByOwnerTele(@Param("ownerTele") String ownerTele);
    
    Integer findBlackelectsCountByOwnerTele(@Param("ownerTele") String ownerTele);
}