package com.ems.iot.manage.dto;

import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.Electrombile;

public class BlackElectAppDto {
	private Blackelect blackelect;
	private Electrombile electrombile;
	public Blackelect getBlackelect() {
		return blackelect;
	}
	public void setBlackelect(Blackelect blackelect) {
		this.blackelect = blackelect;
	}
	public Electrombile getElectrombile() {
		return electrombile;
	}
	public void setElectrombile(Electrombile electrombile) {
		this.electrombile = electrombile;
	}
	
}
