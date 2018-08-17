package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.SensitiveArea;

public interface SensitiveAreaMapper {
    int deleteByPrimaryKey(Integer sensitive_area_id);

    int insert(SensitiveArea record);

    int insertSelective(SensitiveArea record);

    SensitiveArea selectByPrimaryKey(Integer sensitive_area_id);

    int updateByPrimaryKeySelective(SensitiveArea record);

    int updateByPrimaryKey(SensitiveArea record);
    
    
    /**
     * 防止出现相同名字的限制区域
     * @param limitAreaName
     * @return
     */
    SensitiveArea findSensitiveAreaForFilter(@Param("sensitiveAreaName")String sensitiveAreaName);
}