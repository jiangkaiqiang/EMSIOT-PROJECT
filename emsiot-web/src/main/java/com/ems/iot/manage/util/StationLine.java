package com.ems.iot.manage.util;

public class StationLine {
	private double k;
	private double b;
	private double maxX;
	public double getMaxX() {
		return maxX;
	}
	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}
	public double getMinX() {
		return minX;
	}
	public void setMinX(double minX) {
		this.minX = minX;
	}
	private double minX;
	public double getK() {
		return k;
	}
	public void setK(double k) {
		this.k = k;
	}
	public StationLine(double k, double b, double maxX, double minX) {
		super();
		this.k = k;
		this.b = b;
		this.maxX = maxX;
		this.minX = minX;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	
}
