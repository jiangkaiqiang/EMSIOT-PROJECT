package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.SysUser;

/**
 * @author Barry
 * @date 2018年3月20日下午3:37:10  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class SysUserDto {
	
	private SysUser sysUser;
	
	private String area_name;
	
    private String pro_name;
	
	private String city_name;
	
	//menu power setting
	private String console = "0";//控制台权限
	
	private String traceSearch  = "0";//控制台-轨迹查询
	
	private String location  = "0";//控制台-定位
	
	private String electManage  = "0"; //车辆管理
	
	private String electRecord  = "0";//车辆管理-车辆备案
	
	private String blackManage  = "0";//车辆管理-黑名单管理
	
	private String alarmTrack = "0";//车辆管理-报警追踪
	
	private String electAdd = "0";//车辆管理-车辆备案-车辆添加
	
	private String electDelete = "0";//车辆管理-车辆备案-车辆删除
	
	private String electExport = "0";//车辆管理-车辆备案-车辆导出
	
	private String electEdit = "0";//车辆管理-车辆备案-车辆编辑
	
	private String blackAdd = "0";//车辆管理-黑名单管理-黑名单添加
	
	private String blackDelete = "0";//车辆管理-黑名单管理-黑名单删除
	
	private String blackEdit = "0";//车辆管理-黑名单管理-黑名单编辑
	
	private String alarmDelete = "0";//车辆管理-轨迹追踪-报警删除
	
	private String stationManageWhole = "0";//基站管理-总菜单
	
	private String stationManage = "0";//基站管理-基站管理分菜单
	
	private String stationDeviceManage = "0";//基站管理-基站设备管理

	private String stationAdd = "0";//基站管理-基站管理分菜单-基站添加
	
	private String stationDelete = "0";//基站管理-基站管理分菜单-基站删除
	
	private String stationDeviceDelete = "0";//基站管理-基站设备管理-基站删除
	
	private String stationDeviceUpdate = "0";//基站管理-基站设备管理-基站更新
	
	private String specialAreaManage = "0";//特殊区域管理
	
	private String limitAreaManage = "0";//特殊区域管理-限制区域管理
	
	private String sensitiveAreaManage = "0";//特殊区域管理-敏感区域管理
	
	private String AreaAlarmManage = "0";//区域报警管理
	
	private String limitAlarmManage = "0";//区域报警管理-限制区域报警管理
	
	private String sensitiveAlarmManage = "0";//区域报警管理-敏感区域报警管理
	
	private String dataAnalysis = "0";//图表统计
	
	private String userManage = "0";//用户管理
	
	private String userAdd = "0";//用户管理-用户添加
	
	private String userDelete = "0";//用户管理-用户删除
	
	private String userEdit = "0";//用户管理-用户编辑
	
	private String privatePower = "0";//隐私权限
	
	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public String getConsole() {
		return console;
	}

	public void setConsole(String console) {
		this.console = console;
	}

	public String getTraceSearch() {
		return traceSearch;
	}

	public void setTraceSearch(String traceSearch) {
		this.traceSearch = traceSearch;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getElectManage() {
		return electManage;
	}

	public void setElectManage(String electManage) {
		this.electManage = electManage;
	}

	public String getElectRecord() {
		return electRecord;
	}

	public void setElectRecord(String electRecord) {
		this.electRecord = electRecord;
	}

	public String getBlackManage() {
		return blackManage;
	}

	public void setBlackManage(String blackManage) {
		this.blackManage = blackManage;
	}

	public String getAlarmTrack() {
		return alarmTrack;
	}

	public void setAlarmTrack(String alarmTrack) {
		this.alarmTrack = alarmTrack;
	}

	public String getElectAdd() {
		return electAdd;
	}

	public void setElectAdd(String electAdd) {
		this.electAdd = electAdd;
	}

	public String getElectDelete() {
		return electDelete;
	}

	public void setElectDelete(String electDelete) {
		this.electDelete = electDelete;
	}

	public String getElectExport() {
		return electExport;
	}

	public void setElectExport(String electExport) {
		this.electExport = electExport;
	}

	public String getElectEdit() {
		return electEdit;
	}

	public void setElectEdit(String electEdit) {
		this.electEdit = electEdit;
	}

	public String getBlackAdd() {
		return blackAdd;
	}

	public void setBlackAdd(String blackAdd) {
		this.blackAdd = blackAdd;
	}

	public String getBlackDelete() {
		return blackDelete;
	}

	public void setBlackDelete(String blackDelete) {
		this.blackDelete = blackDelete;
	}

	public String getBlackEdit() {
		return blackEdit;
	}

	public void setBlackEdit(String blackEdit) {
		this.blackEdit = blackEdit;
	}

	public String getAlarmDelete() {
		return alarmDelete;
	}

	public void setAlarmDelete(String alarmDelete) {
		this.alarmDelete = alarmDelete;
	}

	public String getStationManageWhole() {
		return stationManageWhole;
	}

	public void setStationManageWhole(String stationManageWhole) {
		this.stationManageWhole = stationManageWhole;
	}

	public String getStationManage() {
		return stationManage;
	}

	public void setStationManage(String stationManage) {
		this.stationManage = stationManage;
	}

	public String getStationDeviceManage() {
		return stationDeviceManage;
	}

	public void setStationDeviceManage(String stationDeviceManage) {
		this.stationDeviceManage = stationDeviceManage;
	}

	public String getStationAdd() {
		return stationAdd;
	}

	public void setStationAdd(String stationAdd) {
		this.stationAdd = stationAdd;
	}

	public String getStationDelete() {
		return stationDelete;
	}

	public void setStationDelete(String stationDelete) {
		this.stationDelete = stationDelete;
	}

	public String getStationDeviceDelete() {
		return stationDeviceDelete;
	}

	public void setStationDeviceDelete(String stationDeviceDelete) {
		this.stationDeviceDelete = stationDeviceDelete;
	}

	public String getStationDeviceUpdate() {
		return stationDeviceUpdate;
	}

	public void setStationDeviceUpdate(String stationDeviceUpdate) {
		this.stationDeviceUpdate = stationDeviceUpdate;
	}

	public String getSpecialAreaManage() {
		return specialAreaManage;
	}

	public void setSpecialAreaManage(String specialAreaManage) {
		this.specialAreaManage = specialAreaManage;
	}

	public String getLimitAreaManage() {
		return limitAreaManage;
	}

	public void setLimitAreaManage(String limitAreaManage) {
		this.limitAreaManage = limitAreaManage;
	}

	public String getSensitiveAreaManage() {
		return sensitiveAreaManage;
	}

	public void setSensitiveAreaManage(String sensitiveAreaManage) {
		this.sensitiveAreaManage = sensitiveAreaManage;
	}

	public String getAreaAlarmManage() {
		return AreaAlarmManage;
	}

	public void setAreaAlarmManage(String areaAlarmManage) {
		AreaAlarmManage = areaAlarmManage;
	}

	public String getLimitAlarmManage() {
		return limitAlarmManage;
	}

	public void setLimitAlarmManage(String limitAlarmManage) {
		this.limitAlarmManage = limitAlarmManage;
	}

	public String getSensitiveAlarmManage() {
		return sensitiveAlarmManage;
	}

	public void setSensitiveAlarmManage(String sensitiveAlarmManage) {
		this.sensitiveAlarmManage = sensitiveAlarmManage;
	}

	public String getDataAnalysis() {
		return dataAnalysis;
	}

	public void setDataAnalysis(String dataAnalysis) {
		this.dataAnalysis = dataAnalysis;
	}

	public String getUserManage() {
		return userManage;
	}

	public void setUserManage(String userManage) {
		this.userManage = userManage;
	}

	public String getUserAdd() {
		return userAdd;
	}

	public void setUserAdd(String userAdd) {
		this.userAdd = userAdd;
	}

	public String getUserDelete() {
		return userDelete;
	}

	public void setUserDelete(String userDelete) {
		this.userDelete = userDelete;
	}

	public String getUserEdit() {
		return userEdit;
	}

	public void setUserEdit(String userEdit) {
		this.userEdit = userEdit;
	}

	public String getPrivatePower() {
		return privatePower;
	}

	public void setPrivatePower(String privatePower) {
		this.privatePower = privatePower;
	}
	
}
