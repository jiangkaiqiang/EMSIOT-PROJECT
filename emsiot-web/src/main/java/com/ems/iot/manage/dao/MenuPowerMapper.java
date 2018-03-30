package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.MenuPower;

public interface MenuPowerMapper {
    int deleteByPrimaryKey(Integer menu_id);

    int insert(MenuPower record);

    int insertSelective(MenuPower record);

    MenuPower selectByPrimaryKey(Integer menu_id);

    int updateByPrimaryKeySelective(MenuPower record);

    int updateByPrimaryKey(MenuPower record);
}