package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.People;
import com.github.pagehelper.Page;

public interface PeopleMapper {
    int deleteByPrimaryKey(Integer people_id);

    int insert(People record);

    int insertSelective(People record);

    People selectByPrimaryKey(Integer people_id);
    
    People findPlateNumByPeopleGuaCardNum(@Param("peopleGuaCardNum") int peopleGuaCardNum);

    int updateByPrimaryKeySelective(People record);
    
    int updateByPeopleGuaCardNumSelective(People record);

    int updateByPrimaryKey(People record);
    
    Page<People> findAllPeoples(@Param("startTime")String startTime, @Param("endTime")String endTime, 
    		@Param("recorderID")Integer recorderID, @Param("peopleType")Integer peopleType, 
    		@Param("proID")Integer proID, @Param("cityID")Integer cityID, @Param("areaID")Integer areaID, @Param("peopleTele")String peopleTele, @Param("peopleIdCards")String peopleIdCards, 
    		@Param("peopleName")String peopleName, @Param("peopleGuaCardNum")String peopleGuaCardNum, @Param("guardianName")String guardianName, @Param("guardianTele")String guardianTele, 
    		@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    /**
     * 获取某个管理员权限之内的所有车辆，不分页
     * @param proPower
     * @param cityPower
     * @param areaPower
     * @return
     */
    List<People> findPeoplesList(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
 
    People findPeopleForLocation(@Param("peopleGuaCardNum") Integer peopleGuaCardNum,@Param("peopleIdCards") String peopleIdCards);
   
}