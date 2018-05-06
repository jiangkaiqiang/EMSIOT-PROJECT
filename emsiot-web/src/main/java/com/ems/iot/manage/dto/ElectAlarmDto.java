package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.ElectAlarm;

public class ElectAlarmDto {
	private ElectAlarm electAlarm;
	private String statioAddress;
	private String ownerTele;
	private String ownerName;
	public ElectAlarm getElectAlarm() {
		return electAlarm;
	}
	public void setElectAlarm(ElectAlarm electAlarm) {
		this.electAlarm = electAlarm;
	}
	public String getStatioAddress() {
		return statioAddress;
	}
	public void setStatioAddress(String statioAddress) {
		this.statioAddress = statioAddress;
	}
	public String getOwnerTele() {
		return ownerTele;
	}
	public void setOwnerTele(String ownerTele) {
		this.ownerTele = ownerTele;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}
