package com.ems.iot.manage.dao;

import com.ems.iot.manage.entity.ElectrombileStation;

public interface ElectrombileStationMapper {
    int deleteByPrimaryKey(Integer ele_sta_id);

    int insert(ElectrombileStation record);

    int insertSelective(ElectrombileStation record);

    ElectrombileStation selectByPrimaryKey(Integer ele_sta_id);

    int updateByPrimaryKeySelective(ElectrombileStation record);

    int updateByPrimaryKey(ElectrombileStation record);
}