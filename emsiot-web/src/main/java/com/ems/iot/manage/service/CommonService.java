package com.ems.iot.manage.service;

import java.util.List;
import java.util.Map;
/**
 * @author Barry
 * @date 2018年3月20日下午3:44:15  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface CommonService {
	
	 List<Map<String, Object>> getCommData(String codetype);
	
	 List<Map<String, Object>> getCommDataByID(Integer id,String codetype);

	 List<Map<String, Object>> getBaseData(String table,String code,String value);
	 
	 List<Map<String, Object>> getBaseData(String table,String code,String value,Integer id);
	 
}
