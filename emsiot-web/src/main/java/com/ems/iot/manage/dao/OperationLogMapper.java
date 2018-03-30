package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;
import com.ems.iot.manage.entity.OperationLog;
import com.github.pagehelper.Page;
/**
 * @author Barry
 * @date 2018年3月20日下午3:34:40  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface OperationLogMapper {
    
	void insertOperationLog(OperationLog operationLog);
	
	Page<OperationLog> findOperationLogList(@Param("adminName")String adminName);
}