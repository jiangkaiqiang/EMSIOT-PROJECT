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
    /**
     * 为APP端的管理员查询电动车列表，及筛选提供服务，注意是根据RecorderID先过滤，跟PC端不同
     * @param startTime
     * @param endTime
     * @param recorderID
     * @param electState
     * @param insurDetail
     * @param proID
     * @param cityID
     * @param areaID
     * @param ownerTele
     * @param ownerID
     * @param plateNum
     * @param guaCardNum
     * @param ownerName
     * @param proPower
     * @param cityPower
     * @param areaPower
     * @return
     */
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
    /**
     * 根据车主的手机号获取车主所拥有的电动车数目，为个人车主app提供服务
     * @param tele
     * @return
     */
    Integer findElectsCountByTele(@Param("tele") String tele);
   
    /**
     * 根据管理员的ID查询该管理员已经备案登记的车辆数量，此处是为App端的管理员提供服务
     * @param recorderId
     * @return
     */
    List<Electrombile> findElectsByRecorderId(@Param("recorderId") String recorderId);
    
    /**
     * 为防止车牌号或防盗芯片编号这种唯一字段重复添加而设计的判断编号是否存在方法
     * @param guaCardNum
     * @param plateNum
     * @return
     */
    Electrombile findElectForFilter(@Param("guaCardNum") Integer guaCardNum, @Param("plateNum") String plateNum);

    /**
     * 查询某状态的车辆，用于多选分类下拉框。（特殊需要限制车辆的选择列表）
     * @param proPower
     * @param cityPower
     * @param areaPower
     * @param electState
     * @return
     */
    List<Electrombile> findElectsListByElectState(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower,@Param("electState") Integer electState);
    
}