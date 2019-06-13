package com.ems.iot.manage.dto;

public class CountDto {

	/**
	 * 基站统计
	 */
	private Integer stationSum;//基站总数
	private Integer stationOnLineSum;//在线基站数
	private Integer stationOffLineSum;//离线基站数
	
	/**
	 * 车辆统计
	 */
	private Integer electRegisterSum; //登记车辆数
	private Integer electAlarmSum; //报警车辆数
	private Integer electBlacklistSum; //黑名单车辆数
	private Integer electOnLineSum; //在线车辆数
	
	/**
	 * 黑名单统计
	 */
	private Integer untreatedBlacklistSum;//未处理报警数
	private Integer processedBlacklistSum;//已处理报警数
	
	/**
	 * 日期和值
	 */
	private String dateKey;//已处理报警数
	private Integer dateValue;//已处理报警数
	
	public Integer getStationSum() {
		return stationSum;
	}
	public void setStationSum(Integer stationSum) {
		this.stationSum = stationSum;
	}
	public Integer getStationOnLineSum() {
		return stationOnLineSum;
	}
	public void setStationOnLineSum(Integer stationOnLineSum) {
		this.stationOnLineSum = stationOnLineSum;
	}
	public Integer getStationOffLineSum() {
		return stationOffLineSum;
	}
	public void setStationOffLineSum(Integer stationOffLineSum) {
		this.stationOffLineSum = stationOffLineSum;
	}
	public Integer getElectRegisterSum() {
		return electRegisterSum;
	}
	public void setElectRegisterSum(Integer electRegisterSum) {
		this.electRegisterSum = electRegisterSum;
	}
	public Integer getElectAlarmSum() {
		return electAlarmSum;
	}
	public void setElectAlarmSum(Integer electAlarmSum) {
		this.electAlarmSum = electAlarmSum;
	}
	public Integer getElectBlacklistSum() {
		return electBlacklistSum;
	}
	public void setElectBlacklistSum(Integer electBlacklistSum) {
		this.electBlacklistSum = electBlacklistSum;
	}
	public Integer getElectOnLineSum() {
		return electOnLineSum;
	}
	public void setElectOnLineSum(Integer electOnLineSum) {
		this.electOnLineSum = electOnLineSum;
	}
	public Integer getUntreatedBlacklistSum() {
		return untreatedBlacklistSum;
	}
	public void setUntreatedBlacklistSum(Integer untreatedBlacklistSum) {
		this.untreatedBlacklistSum = untreatedBlacklistSum;
	}
	public Integer getProcessedBlacklistSum() {
		return processedBlacklistSum;
	}
	public void setProcessedBlacklistSum(Integer processedBlacklistSum) {
		this.processedBlacklistSum = processedBlacklistSum;
	}
	public String getDateKey() {
		return dateKey;
	}
	public void setDateKey(String dateKey) {
		this.dateKey = dateKey;
	}
	public Integer getDateValue() {
		return dateValue;
	}
	public void setDateValue(Integer dateValue) {
		this.dateValue = dateValue;
	}
	
	
}
