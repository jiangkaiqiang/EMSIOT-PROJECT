package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Blackelect;
import com.github.pagehelper.Page;

public interface BlackelectMapper {
    int deleteByPrimaryKey(Integer black_id);

    int insert(Blackelect record);

    int insertSelective(Blackelect record);

    Blackelect selectByPrimaryKey(Integer black_id);

    int updateByPrimaryKeySelective(Blackelect record);

    int updateByPrimaryKey(Blackelect record);
    
    Page<Blackelect> findAllBlackelect();
    
    Page<Blackelect> findAllBlackelectByOptions(@Param("plateNum")String plateNum,@Param("ownerTele")String ownerTele,
    		@Param("DealStatus")Integer DealStatus,@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    List<Blackelect> findBlackelectsList(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    List<Blackelect> findBlackelectsByOwnerTele(@Param("ownerTele") String ownerTele);
}