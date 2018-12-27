package com.ems.iot.manage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ems.iot.manage.dto.StationElectDto;
import com.ems.iot.manage.dto.Thermodynamic;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.PeopleStation;

public interface PeopleStationMapper {
    int deleteByPrimaryKey(Integer ele_sta_id);

    int insert(PeopleStation record);

    int insertSelective(PeopleStation record);

    PeopleStation selectByPrimaryKey(Integer ele_sta_id);

    int updateByPrimaryKeySelective(PeopleStation record);

    int updateByPrimaryKey(PeopleStation record);
    
    /**
     * 根据人员芯片编号，查询到人员最后一次出现的基站位置
     * @param ele_sta_id
     * @return
     */
    PeopleStation selectByGuaCardNumForLocation(@Param("guaCardNum") int guaCardNum);
    
    /**
     * 根据人员芯片编号和时间，查询人员轨迹
     * @param guaCardNum
     * @param startTimeForTrace
     * @param endTimeForTrace
     * @return
     */
    List<PeopleStation> selectByGuaCardNumForTrace(@Param("guaCardNum") int guaCardNum,
    		@Param("startTimeForTrace") String startTimeForTrace, @Param("endTimeForTrace") String endTimeForTrace);
    
    
    List<StationElectDto> selectElectsByGuaCardNumNumAndTimeDesc(@Param("guaCardNum") int guaCardNum,
    		@Param("startTime") String startTimeForTrace, @Param("endTime") String endTimeForTrace);
    /**
     * 根据基站的物理编号和时间，查询某个基站下的人员
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<PeopleStation> selectPeoplesByStationPhyNumAndTime(@Param("stationPhyNum") int stationPhyNum,
    		@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    /**
     * 根据基站的物理编号来获取该基站下当前一分钟之内存在的人员，如果需要修改时间的大小，如改成一秒，则直接改此方法对应的SQL即可
     * @param stationPhyNum
     * @return
     */
    List<Thermodynamic> selectPeoplesByStationPhyNumNow(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
    
    /**
     * 返回当前时间范围内在线人员的数量；如果需要修改时间大小则修改时间范围，需要根据车号进行分组过滤，
     * 即如果一辆车在该范围内经过了多个基站，则认为在线人员只有一个
     * @return
     */
    List<Integer> selectPeoplesByEleGuaCardNumNow(@Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);

    /**
     * 根据数据获得每个人最后一次出现的人员数据
     * @param station_phy_num
     * @param startTime
     * @param endTime
     * @return
     */
    List<PeopleStation> selectPeopleStationPeopleLocationByTime(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("proPower") Integer proPower,@Param("cityPower") Integer cityPower,@Param("areaPower") Integer areaPower);
}