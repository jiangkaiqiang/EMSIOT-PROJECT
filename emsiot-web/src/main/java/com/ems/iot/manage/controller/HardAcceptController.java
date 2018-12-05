package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.ems.iot.manage.dao.PeopleMapper;
import com.ems.iot.manage.dao.PeopleStationMapper;
import com.ems.iot.manage.dao.SensitiveAreaMapper;
import com.ems.iot.manage.dao.StationMapper;
import com.ems.iot.manage.dao.StationStatusRecordMapper;
import com.ems.iot.manage.entity.AreaAlarm;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.LimitArea;
import com.ems.iot.manage.entity.MessageEntity;
import com.ems.iot.manage.entity.People;
import com.ems.iot.manage.entity.PeopleStation;
import com.ems.iot.manage.entity.SensitiveArea;
import com.ems.iot.manage.entity.Station;
import com.ems.iot.manage.entity.StationStatusRecord;
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
	private SensitiveAreaMapper sensitiveAreaMapper;
	@Autowired
	private StationMapper stationMapper;
	@Autowired
	private StationStatusRecordMapper stationStatusRecordMapper;
	@Autowired
	private AreaAlarmMapper areaAlarmMapper;
	@Autowired
	private PeopleMapper peopleMapper;
	@Autowired
	private PeopleStationMapper peopleStationMapper;

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
		if (electrombile!=null && electrombile.getElect_state() == 2) {
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
		
//			//处理限制区域报警-移除
		/*List<LimitArea> limitAreas = limitAreaMapper.findAll();
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
				areaAlarm.setAlarm_station_phy_num(electrombileStation.getStation_phy_num());
				areaAlarm.setEnter_plate_num(electrombile.getPlate_num());
				areaAlarm.setProcess_state(0);//0未处理，没有默认值sql报错Column 'process_state' cannot be null;
				areaAlarmMapper.insert(areaAlarm);
			}
		}*/
		
	if(electrombile!=null) {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date sysDate=new Date();
//		//处理敏感区域报警
		List<SensitiveArea> sensitiveAreas = sensitiveAreaMapper.findAll();
		for (SensitiveArea sensitiveArea : sensitiveAreas) {
			String[] sensitiveStationIDs = sensitiveArea.getStation_ids().split(";");
	//	    	String[] limitElects = limitArea.getBlack_list_elects().split(";");
			List<Integer> sensitiveStationPhyNumsList = new ArrayList<Integer>();
			
			//是否有开启0未开启1开启
			if(sensitiveArea.getStatus()==0) {
				continue;
			}
			//判断是否有时间，没有时间就始终进行，有时间就按不同时间进行
			Date startTime = null;
			Date endTime = null;
			try {
				Long sysDateL=sdf.parse(sdf.format(sysDate)).getTime();
				
				if(sensitiveArea.getSens_start_time()!=null && sensitiveArea.getSens_end_time()!=null) {
					startTime = sdf.parse(sensitiveArea.getSens_start_time());
					endTime = sdf.parse(sensitiveArea.getSens_end_time());
					
					if(!(sysDateL>=startTime.getTime() && sysDateL<=endTime.getTime())) {
						continue;
					}
				
				}else if(sensitiveArea.getSens_start_time()!=null && sensitiveArea.getSens_end_time()==null){
					startTime = sdf.parse(sensitiveArea.getSens_start_time());
					if(!(sysDateL>=startTime.getTime())) {
						continue;
					}
					
				}else if(sensitiveArea.getSens_start_time()==null && sensitiveArea.getSens_end_time()!=null){
					endTime = sdf.parse(sensitiveArea.getSens_end_time());
					if(!(sysDateL<=endTime.getTime())) {
						continue;
					}
				}
			
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			
			//是否是限制车辆
			String elects = sensitiveArea.getBlack_list_elects();
			if(elects!=null) {
				System.err.println(elects.indexOf(electrombile.getElect_id()+""));
				if(elects.indexOf(electrombile.getElect_id()+"") == -1) {
					continue;
				}
			}else {
				continue;
			}
			
			
			
			for (String sensitiveStationID : sensitiveStationIDs) {
				Station station = stationMapper.selectByPrimaryKey(Integer.valueOf(sensitiveStationID));
				if (station!=null) {
					sensitiveStationPhyNumsList.add(station.getStation_phy_num());
				}
			}
			
			
			if (sensitiveStationPhyNumsList.contains(stationPhyNum)) {
				Integer areaAlarmCount = areaAlarmMapper.findAreaAlarmCountByStationNumAndTime(electrombile.getPlate_num(), sensitiveArea.getSensitive_area_name(), startTime, endTime, null, null, null);
				//此车辆超出出现次数进行提示
				if(sensitiveArea.getEnter_num() <= areaAlarmCount) {
					MessageEntity messageEntityLimit = new MessageEntity();
					messageEntityLimit.setContent(electrombile.getPro_id()+";"+electrombile.getCity_id()+";"+electrombile.getArea_id()+";"
							+"经过限制区域 '"+sensitiveArea.getSensitive_area_name()+"' "+(areaAlarmCount+1)+"次。 报警基站" + electrombileStation.getStation_phy_num() + "发现可疑车辆"
							+ electrombileStation.getEle_gua_card_num() + "!");
					CometUtil cometUtilLimit = new CometUtil();
					cometUtilLimit.pushToLimit(messageEntityLimit);
				}
				
				AreaAlarm areaAlarm = new AreaAlarm();
				areaAlarm.setArea_name(sensitiveArea.getSensitive_area_name());
				areaAlarm.setArea_type(1);//1表示敏感区域
				areaAlarm.setAlarm_station_phy_num(electrombileStation.getStation_phy_num());
				areaAlarm.setEnter_plate_num(electrombile.getPlate_num());
				areaAlarm.setProcess_state(0);//0未处理，没有默认值sql报错Column 'process_state' cannot be null;
				areaAlarmMapper.insert(areaAlarm);
			}
		}
	}
		
		
		if(electrombile==null) {
			//插入人员基站关系表
			People people = peopleMapper.findPlateNumByPeopleGuaCardNum(eleGuaCardNum);
			if(people != null) {
				PeopleStation peopleStation = new PeopleStation();
				peopleStation.setPeople_gua_card_num(eleGuaCardNum);
				peopleStation.setStation_phy_num(stationPhyNum);;
				peopleStation.setHard_read_time(hardReadTime);
				peopleStationMapper.insert(peopleStation);
			}
		}else {
		
		
		// 插入车辆基站关系表中
		electrombileStationMapper.insert(electrombileStation);
		}
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
			StationStatusRecord record = stationStatusRecordMapper.selectByStationPhyNumLimitOne(stationPhyNum);
			if(record == null ) {
				StationStatusRecord stationRecord = new StationStatusRecord();
				stationRecord.setStation_phy_num(stationPhyNum);
				stationRecord.setStation_status(stationStatus);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stationRecord.setUpdate_time(sdf.format(new Date()));
				stationStatusRecordMapper.insert(stationRecord);
			}else {
				if(record.getStation_status() != stationStatus) {
					StationStatusRecord stationRecord = new StationStatusRecord();
					stationRecord.setStation_phy_num(stationPhyNum);
					stationRecord.setStation_status(stationStatus);
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					stationRecord.setUpdate_time(sdf.format(new Date()));
					stationStatusRecordMapper.insert(stationRecord);
				}
			}
			Station sta = new Station();
			sta.setStation_phy_num(stationPhyNum);
			sta.setStation_status(stationStatus);
			stationMapper.updateStatusByStationNum(sta);
			
		}
		return ResponseData.newSuccess("接受成功");
		
	}
	
	@RequestMapping(value = "/test")
	@ResponseBody
	public Object test() {
		MessageEntity messageEntityLimit = new MessageEntity();
		messageEntityLimit.setContent("测试;测试;测试;测试!");
		CometUtil cometUtilLimit = new CometUtil();
		cometUtilLimit.pushToLimit(messageEntityLimit);
		return ResponseData.newSuccess("接受成功");
	}
	
}
