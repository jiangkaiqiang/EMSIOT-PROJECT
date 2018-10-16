package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.AreaAlarmMapper;
import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.dao.LimitAreaMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.entity.AreaAlarm;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.MessageEntity;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.util.ResponseData;
import com.ems.iot.manage.util.CometUtil;

/**
 * @author Barry
 * @date 2018年3月20日下午3:32:54
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/hard")
public class HardAcceptController extends BaseController {
	@Autowired
	private ElectrombileStationMapper electrombileStationMapper;
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private ElectAlarmMapper electAlarmMapper;
	@Autowired
	private LimitAreaMapper limitAreaMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private AreaAlarmMapper areaAlarmMapper;

	/**
	 * 接收硬件数据，并判断是否报警
	 * 
	 * @param eleGuaCardNum
	 * @param stationPhyNum
	 * @param hardReadTime
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/accepter")
	@ResponseBody
	public Object accepter(HttpServletRequest request,
			@RequestParam(value = "eleGuaCardNum", required = false) Integer eleGuaCardNum,
			@RequestParam(value = "stationPhyNum") Integer stationPhyNum,
			@RequestParam(value = "hardReadTime", required = false) String hardReadTime)
			throws UnsupportedEncodingException {
		//SysUser user = (SysUser) request.getSession().getAttribute("user");
		ElectrombileStation electrombileStation = new ElectrombileStation();
		electrombileStation.setEle_gua_card_num(eleGuaCardNum);
		electrombileStation.setStation_phy_num(stationPhyNum);
		electrombileStation.setHard_read_time(hardReadTime);
		Electrombile electrombile = electrombileMapper.findPlateNumByGuaCardNum(eleGuaCardNum);
		// 如果是一辆黑名单车辆，则推送一条报警信息，并将其插入报警表之中
		if (electrombile.getElect_state() == 2) {
			MessageEntity messageEntity = new MessageEntity();
			messageEntity.setContent(electrombile.getPro_id()+";"+electrombile.getCity_id()+";"+electrombile.getArea_id()+";"
			+"警报：基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
			+ electrombileStation.getEle_gua_card_num() + "!");
			CometUtil cometUtil = new CometUtil();
			cometUtil.pushToAll(messageEntity);
//			if (user != null) {
//				if (user.getPro_power().equals("-1")) {
//					MessageEntity messageEntity = new MessageEntity();
//					messageEntity.setContent("警报：基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
//							+ electrombileStation.getEle_gua_card_num() + "!");
//					CometUtil cometUtil = new CometUtil();
//					cometUtil.pushToAll(messageEntity);
//				} else if (user.getPro_power().equals(electrombile.getPro_id() + "")) {
//					if (user.getCity_power().equals("-1")) {
//						MessageEntity messageEntity = new MessageEntity();
//						messageEntity.setContent("警报：基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
//								+ electrombileStation.getEle_gua_card_num() + "!");
//						CometUtil cometUtil = new CometUtil();
//						cometUtil.pushToAll(messageEntity);
//					} else if (user.getCity_power().equals(electrombile.getCity_id() + "")) {
//						if (user.getArea_power().equals("-1")||user.getArea_power().equals(electrombile.getArea_id()+"")) {
//							MessageEntity messageEntity = new MessageEntity();
//							messageEntity.setContent("警报：基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
//									+ electrombileStation.getEle_gua_card_num() + "!");
//							CometUtil cometUtil = new CometUtil();
//							cometUtil.pushToAll(messageEntity);
//						}
//					}
//				} 
//			} 
			// 插入报警表中
			ElectAlarm electAlarm = new ElectAlarm();
			electAlarm.setAlarm_gua_card_num(electrombileStation.getEle_gua_card_num());
			electAlarm.setAlarm_station_phy_num(electrombileStation.getStation_phy_num());
			electAlarmMapper.insert(electAlarm);
		}
		
//			//处理限制区域报警
		List<LimitArea> limitAreas = limitAreaMapper.findAll();
		for (LimitArea limitArea : limitAreas) {
			String[] limitStationIDs = limitArea.getStation_ids().split(";");
//		    	String[] limitElects = limitArea.getBlack_list_elects().split(";");
			List<Integer> limitStationPhyNumsList = new ArrayList<Integer>();
			for (String limitStationID : limitStationIDs) {
				Station station = stationMapper.selectByPrimaryKey(Integer.valueOf(limitStationID));
				if (station!=null) {
					limitStationPhyNumsList.add(station.getStation_phy_num());
				}
			}
			if (limitStationPhyNumsList.contains(stationPhyNum)) {
				
				MessageEntity messageEntityLimit = new MessageEntity();
				messageEntityLimit.setContent(electrombile.getPro_id()+";"+electrombile.getCity_id()+";"+electrombile.getArea_id()+";"
						+"限制区域报警：基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
						+ electrombileStation.getEle_gua_card_num() + "!");
				CometUtil cometUtilLimit = new CometUtil();
				cometUtilLimit.pushToLimit(messageEntityLimit);
				
				AreaAlarm areaAlarm = new AreaAlarm();
				areaAlarm.setArea_name(limitArea.getLimit_area_name());
				areaAlarm.setArea_type(2);//2表示限制区域
				areaAlarm.setEnter_plate_num(electrombile.getPlate_num());
				areaAlarm.setProcess_state(0);//0未处理，没有默认值sql报错Column 'process_state' cannot be null;
				areaAlarmMapper.insert(areaAlarm);
			}
		}
		// 插入车辆基站关系表中
		electrombileStationMapper.insert(electrombileStation);
		return ResponseData.newSuccess("接受成功");
	}
	
	
	/**
	 * 接收硬件数据，并判断基站是否正常
	 * 
	 * @param eleGuaCardNum
	 * @param stationPhyNum
	 * @param hardReadTime
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/accepterStatis")
	@ResponseBody
	public Object accepterStatis(HttpServletRequest request,
			@RequestParam(value = "stationPhyNum", required = false) Integer stationPhyNum,
			@RequestParam(value = "stationStatus", required = false) Integer stationStatus)
			throws UnsupportedEncodingException {
		
		Station station = stationMapper.selectByStationPhyNum(stationPhyNum);

		if(station.getStation_status() == 0 || station.getStation_status() == 1) {
			Station sta = new Station();
			sta.setStation_phy_num(stationPhyNum);
			sta.setStation_status(stationStatus);
			stationMapper.updateStatusByStationNum(sta);
			
		}
		return ResponseData.newSuccess("接受成功");
		
	}
	
}
