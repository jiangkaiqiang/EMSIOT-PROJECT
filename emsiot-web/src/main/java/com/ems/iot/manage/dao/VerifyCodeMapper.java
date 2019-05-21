package com.ems.iot.manage.dao;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.VerifyCode;

public interface VerifyCodeMapper {
    int deleteByPrimaryKey(Integer code_id);

    int insert(VerifyCode record);

    int insertSelective(VerifyCode record);

    VerifyCode selectByPrimaryKey(Integer code_id);

    int updateByPrimaryKeySelective(VerifyCode record);

    int updateByPrimaryKey(VerifyCode record);
    
    VerifyCode findVerifyCodeByTypeAndTele(@Param("code_type")Integer code_type, @Param("user_tele")String user_tele);
}