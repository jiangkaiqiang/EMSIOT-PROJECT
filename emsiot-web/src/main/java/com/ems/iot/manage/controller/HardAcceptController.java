package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.ElectAlarmMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.entity.ElectAlarm;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.MessageEntity;
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
	/**
	 * 接收硬件数据，并判断是否报警
	 * @param eleGuaCardNum
	 * @param stationPhyNum
	 * @param hardReadTime
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/accepter")
	@ResponseBody
	public Object accepter(@RequestParam(value = "eleGuaCardNum", required = false) Integer eleGuaCardNum,
			@RequestParam(value = "stationPhyNum") Integer stationPhyNum,
			@RequestParam(value="hardReadTime", required=false) String hardReadTime
			) throws UnsupportedEncodingException {
		ElectrombileStation electrombileStation = new ElectrombileStation();
		electrombileStation.setEle_gua_card_num(eleGuaCardNum);
		electrombileStation.setStation_phy_num(stationPhyNum);
		electrombileStation.setHard_read_time(hardReadTime);
		Electrombile electrombile = electrombileMapper.findPlateNumByGuaCardNum(eleGuaCardNum);
		//如果是一辆黑名单车辆，则推送一条报警信息，并将其插入报警表之中
		if (electrombile.getElect_state()==2) {
			MessageEntity messageEntity = new MessageEntity();
			messageEntity.setContent("警报：基站"+electrombileStation.getStation_phy_num()+"发现可疑车辆"+electrombileStation.getEle_gua_card_num()+"!");
			CometUtil cometUtil = new CometUtil();
			cometUtil.pushToAll(messageEntity);
			//插入报警表中
			ElectAlarm electAlarm = new ElectAlarm();
			electAlarm.setAlarm_gua_card_num(electrombileStation.getEle_gua_card_num());
			electAlarm.setAlarm_station_phy_num(electrombileStation.getStation_phy_num());
			electAlarmMapper.insert(electAlarm);
		}
		//插入车辆基站关系表中
		electrombileStationMapper.insert(electrombileStation);
		return ResponseData.newSuccess("接受成功");
	}
}
