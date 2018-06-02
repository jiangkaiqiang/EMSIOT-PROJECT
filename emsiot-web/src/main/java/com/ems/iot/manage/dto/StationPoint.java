package com.ems.iot.manage.dto;

public class StationPoint {
	private double x;
	private double y;
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public StationPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
}
