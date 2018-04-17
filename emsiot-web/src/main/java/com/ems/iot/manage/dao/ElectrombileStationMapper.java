package com.ems.iot.manage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.entity.ElectrombileStation;

public interface ElectrombileStationMapper {
    int deleteByPrimaryKey(Integer ele_sta_id);

    int insert(ElectrombileStation record);

    int insertSelective(ElectrombileStation record);

    ElectrombileStation selectByPrimaryKey(Integer ele_sta_id);

    int updateByPrimaryKeySelective(ElectrombileStation record);

    int updateByPrimaryKey(ElectrombileStation record);
    
    /**
     * 根据车辆芯片编号，查询到车辆最后一次出现的基站位置
     * @param ele_sta_id
     * @return
     */
    ElectrombileStation selectByGuaCardNumForLocation(@Param("guaCardNum") int guaCardNum);
    
    /**
     * 根据车辆芯片编号和时间，查询车辆轨迹
     * @param guaCardNum
     * @param startTimeForTrace
     * @param endTimeForTrace
     * @return
     */
    List<ElectrombileStation> selectByGuaCardNumForTrace(@Param("guaCardNum") int guaCardNum,
    		@Param("startTimeForTrace") String startTimeForTrace, @Param("endTimeForTrace") String endTimeForTrace);
}