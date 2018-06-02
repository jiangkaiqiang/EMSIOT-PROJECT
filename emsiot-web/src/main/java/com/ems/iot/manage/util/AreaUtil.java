package com.ems.iot.manage.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ems.iot.manage.dto.StationLine;
import com.ems.iot.manage.dto.StationPoint;

public class AreaUtil {
	public static void CalLines(List<StationPoint> ls,List<StationLine> lines ){
		int size = ls.size();
		for(int i=0;i<size;i++){
			StationPoint p1= ls.get(i);
			StationPoint p2= ls.get((i+1)%size);
			CalLine(p1,p2,lines);
		}
	}
	public static void CalLine(StationPoint p1,StationPoint p2,List<StationLine> lines){
		if(p1.getX()==p2.getX())
			return;
		double k = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
		double b = p1.getY()-k*p1.getX();
		double minX = Math.min(p1.getX(), p2.getX());
		double maxX = Math.max(p1.getX(), p2.getX());
		lines.add(new StationLine(k,b,maxX,minX));
	}
	
	public static boolean isUpper(StationPoint p,List<StationLine> lines){
		Set<Double> set = new HashSet<Double>();
		for(int i =0 ;i < lines.size();i++){
			if(p.getX()>=lines.get(i).getMinX() && p.getX() <= lines.get(i).getMaxX()){
				double res = lines.get(i).getK() * p.getX() + lines.get(i).getB();
				if(res>p.getY()){set.add(res);}
			}
		}
		if(set.size()%2==1)
			return true;
		else
			return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			StationPoint p1 = new StationPoint(0.0, 0.0);
			StationPoint p2 = new StationPoint(1.0, 1.0);
			StationPoint p3 = new StationPoint(2.0, 0.0);
			List<StationPoint> points = new ArrayList<StationPoint>();
			points.add(p1);
			points.add(p2);
			points.add(p3);
			List<StationLine> lines = new ArrayList<StationLine>();
			//遍历所有点，看取出的点是否是已经有的点
			CalLines(points,lines);
			System.out.println(isUpper(new StationPoint(1.0, 0.5),lines));
			System.out.println(isUpper(new StationPoint(-1.0, 0.5),lines));
			System.out.println(isUpper(new StationPoint(1.1, 0.2),lines));
			System.out.println(isUpper(new StationPoint(1.1, 2),lines));
	}

}
