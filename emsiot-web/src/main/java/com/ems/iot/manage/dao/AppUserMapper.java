package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.AppUser;
import com.github.pagehelper.Page;

public interface AppUserMapper {
    int deleteByPrimaryKey(Long user_id);

    int insert(AppUser record);

    int insertSelective(AppUser record);

    AppUser selectByPrimaryKey(Long user_id);

    int updateByPrimaryKeySelective(AppUser record);

    int updateByPrimaryKey(AppUser record);
    
    AppUser findUser(@Param("user_name") String username, @Param("password") String password);

    AppUser findUserByName(@Param("user_name") String user_name);
    
    AppUser findUserByTele(@Param("user_tele") String user_tele);
	
    AppUser findUserById(@Param("user_id") int user_id);
    
    Page<AppUser> findAllUserApp(@Param("keyword")String keyword,@Param("userTele")String userTele,@Param("startTime")String startTime, @Param("endTime")String endTime,
			@Param("proPower")Integer proPower, @Param("cityPower")Integer cityPower, @Param("areaPower")Integer areaPower);
}