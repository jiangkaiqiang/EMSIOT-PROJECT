package com.ems.iot.manage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.dto.StationElectDto;
import com.ems.iot.manage.dto.Thermodynamic;
import com.ems.iot.manage.entity.ElectrombileStation;

public interface ElectrombileStationMapper {
    int deleteByPrimaryKey(Integer ele_sta_id);

    int insert(ElectrombileStation record);
    
    int insertBatch(@Param("recordList")List<ElectrombileStation> recordList);

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
    /**
     * 根据基站的物理编号和时间，查询某个基站下的车辆
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<ElectrombileStation> selectElectsByStationPhyNumAndTime(@Param("stationPhyNum") int stationPhyNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 2018-12-17
     * 根据基站的物理编号和时间，查询某个基站下的车辆总数
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    Integer selectElectsCountByStationPhyNumAndTime(@Param("stationPhyNum") int stationPhyNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    /**
     * 重写	2018-12-17
     * 根据基站的物理编号和时间，查询某个基站下的车辆
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<StationElectDto> selectElectsByStationPhyNumAndTime2(@Param("stationPhyNum") int stationPhyNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 根据基站的物理编号来获取该基站下当前一分钟之内存在的车辆，如果需要修改时间的大小，如改成一秒，则直接改此方法对应的SQL即可
     * @param stationPhyNum
     * @return
     */
    List<Thermodynamic> selectElectsByStationPhyNumNow(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    /**
     * 返回当前时间范围内在线车辆的数量；如果需要修改时间大小则修改时间范围，需要根据车号进行分组过滤，
     * 即如果一辆车在该范围内经过了多个基站，则认为在线车辆只有一辆
     * @return
     */
    List<Integer> selectElectsByEleGuaCardNumNow(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
}