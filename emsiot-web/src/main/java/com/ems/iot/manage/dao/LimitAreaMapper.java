package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.LimitArea;
import com.github.pagehelper.Page;

public interface LimitAreaMapper {
    int deleteByPrimaryKey(Integer limit_area_id);

    int insert(LimitArea record);

    int insertSelective(LimitArea record);

    LimitArea selectByPrimaryKey(Integer limit_area_id);

    int updateByPrimaryKeySelective(LimitArea record);

    int updateByPrimaryKey(LimitArea record);
    
    Page<LimitArea> findAllLimitAreas(@Param("limitAreaID")Integer limitAreaID,@Param("limitAreaName")String limitAreaName
    		,@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
    
    List<LimitArea> findAll();
    /**
     * 防止出现相同名字的限制区域
     * @param limitAreaName
     * @return
     */
    LimitArea findLimitAreaForFilter(@Param("limitAreaName")String limitAreaName);
}