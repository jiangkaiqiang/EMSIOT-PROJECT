package com.ems.iot.manage.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author Barry
 * @date 2018年3月20日下午3:33:48  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface CommonMapper {
	
	List<Map<String, Object>> getCommData(@Param("codetype")String codetype);
	
	List<Map<String, Object>> getCommDataByID(@Param("type_code")Integer type_code,@Param("codetype")String codetype);
	
	List<Map<String, Object>> getBaseData(@Param("table")String table,@Param("code")String code,@Param("value")String value);
	
    List<Map<String, Object>> getBaseDataByID(@Param("table")String table,@Param("code")String code,@Param("value")String value,@Param("id")Integer id);
    
    
    
    
}
