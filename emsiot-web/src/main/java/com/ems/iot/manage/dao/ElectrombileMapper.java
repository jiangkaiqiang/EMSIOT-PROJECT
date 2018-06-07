package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.Electrombile;
import com.github.pagehelper.Page;

public interface ElectrombileMapper {
    int deleteByPrimaryKey(Integer elect_id);

    int insert(Electrombile record);

    int insertSelective(Electrombile record);

    Electrombile selectByPrimaryKey(Integer elect_id);
    
    Electrombile findPlateNumByGuaCardNum(@Param("guaCardNum") int guaCardNum);
    
    Electrombile findGuaCardNumByPlateNum(@Param("plateNum") String plateNum);
    
    List<Electrombile> findElectrombiles(@Param("num") int num);

    int updateByPrimaryKeySelective(Electrombile record);
    
    int updateByGuaCardNumSelective(Electrombile record);
    
    int updateByPlateNumSelective(Electrombile record);

    int updateByPrimaryKey(Electrombile record);
    
    Page<Electrombile> findAllElectrombiles(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("recorderID")Integer recorderID, @Param("electState")Integer electState, @Param("insurDetail")Integer insurDetail,
    		@Param("proID")Integer proID, @Param("cityID")Integer cityID, @Param("areaID")Integer areaID, @Param("ownerTele")String ownerTele, @Param("ownerID")String ownerID, 
    		@Param("plateNum")String plateNum, @Param("guaCardNum")String guaCardNum, @Param("ownerName")String ownerName,
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    Page<Electrombile> findAllElectrombilesForApp(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("recorderID")Integer recorderID, @Param("electState")Integer electState, @Param("insurDetail")Integer insurDetail,
    		@Param("proID")Integer proID, @Param("cityID")Integer cityID, @Param("areaID")Integer areaID, @Param("ownerTele")String ownerTele, @Param("ownerID")String ownerID, 
    		@Param("plateNum")String plateNum, @Param("guaCardNum")String guaCardNum, @Param("ownerName")String ownerName,
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    /**
     * 为车辆定位提供查询操作
     * @param guaCardNum
     * @return
     */
    Electrombile findElectrombileForLocation(@Param("guaCardNum") Integer guaCardNum,@Param("plateNum") String plateNum);
    /**
     * 获取某个管理员权限之内的所有车辆，不分页
     * @param proPower
     * @param cityPower
     * @param areaPower
     * @return
     */
    List<Electrombile> findElectsList(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
     /**
      * 根据车主的手机号获取车主所拥有的电动车（可能有多辆），为个人车主app提供服务
      * @param tele
      * @return
      */
    List<Electrombile> findElectsByTele(@Param("tele") String tele);
}