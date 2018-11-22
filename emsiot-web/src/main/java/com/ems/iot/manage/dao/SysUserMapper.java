package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.SysUser;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Barry
 * @date 2018年3月20日下午3:36:10  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface SysUserMapper {
	int deleteByPrimaryKey(@Param("user_id") int user_id);
	
	SysUser findUser(@Param("user_name") String username, @Param("password") String password);

	SysUser findUserByName(@Param("user_name") String user_name);
	
	SysUser findUserById(@Param("user_id") int user_id);
	
	int insert(SysUser record);
	
	void updateUser(SysUser userEntity);
	
	void updateUserSetting(@Param("userId")String userId, @Param("fixedLon")String fixedLon, @Param("fixedLat")String fixedLat,
			@Param("fixedZoom")Integer fixedZoom, @Param("fixedTimer")Integer fixedTimer, @Param("fixedQueryTime")Integer fixedQueryTime);
		
	Page<SysUser> findAllUser(@Param("keyword")String keyword,@Param("startTime")String startTime, @Param("endTime")String endTime,
			@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower,@Param("adminId")Integer adminId);
}
