package com.ems.iot.manage.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.Electrombile;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.entity.Station;

public class GenDataUtil {
	private static double interval = 0.001;
	private static int before_interval = 1000;
	// 经度范围[75.9343,76.053308]
	// 维度范围[39.434591,39.543735]
	private static String[] firstName = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈" };
	private static String[] lastName = { "小贤", "志伟", "海涛", "凯强", "利栋", "宗超", "超", "伟", "亮", "栋" };
	private static String[] color = { "红", "橙", "黄", "绿", "青", "蓝", "紫", "黑", "白", "银" };
	private static String[] occurType= {"社区","地铁站","商场","超市","沿街商铺","办公楼","医院","车站","学校","其他"};

	public static void gendata(Electrombile electrombile, Station station, ElectrombileStation electrombileStation,int index){
	Random rand= new Random();
//	Electrombile 随机数据
	int gua_card_num=index; 
	electrombile.setGua_card_num(Integer.valueOf("10101"+gua_card_num));
	electrombile.setPlate_num(String.valueOf(gua_card_num));
	electrombile.setVe_id_num(String.valueOf(gua_card_num));
	
	Date currentTime = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	electrombile.setBuy_date(formatter.format(currentTime));
	electrombile.setElect_color(color[rand.nextInt(10)]);
	electrombile.setMotor_num(String.valueOf(gua_card_num));
	electrombile.setNote("XXX");
	electrombile.setPro_id(650000);
	electrombile.setCity_id(653100);
	electrombile.setArea_id(653130);
	electrombile.setOwner_name(firstName[rand.nextInt(10)]+lastName[rand.nextInt(10)]);
	electrombile.setOwner_tele("1330304"+String.valueOf(1000+rand.nextInt(8999)));
	electrombile.setOwner_address("新疆喀什"+String.valueOf(gua_card_num));
	electrombile.setOwner_id("65310119601111"+String.valueOf(1000+rand.nextInt(8999)));
	electrombile.setRecorder_id(123456);
	
	electrombile.setRecorder_time(formatter.format(currentTime));
	electrombile.setElect_state(1);
	electrombile.setElect_brand("小鸟"+rand.nextInt(10));
	
//	Station随机数据
	int station_phy_num=gua_card_num; 
	station.setStation_phy_num(Integer.valueOf("20202"+station_phy_num));
	station.setStation_name("基站"+String.valueOf(station_phy_num));

	station.setLongitude(String.valueOf(rand.nextInt(100)*interval+75.9343));
	station.setLatitude(String.valueOf(rand.nextInt(100)*interval+39.4345));
	
	station.setStation_status(0);
	station.setStation_type("A");	
//	Calendar install_calendar = Calendar.getInstance();
//	install_calendar.add(Calendar.DATE, rand.nextInt(before_interval));
//	Date install_date=install_calendar.getTime();
	station.setInstall_date(formatter.format(currentTime));
	
	station.setSoft_version("1.0");
	station.setContact_person(firstName[rand.nextInt(10)]+lastName[rand.nextInt(10)]);
	station.setContact_tele("1520102"+String.valueOf(1000+rand.nextInt(8999)));
	station.setStick_num("杆"+String.valueOf(station_phy_num));	
	station.setPro_id(370000);
	station.setCity_id(370100);
	station.setArea_id(370102);
	
//	Electrombile_Station数据生成
	electrombileStation.setEle_gua_card_num(electrombile.getGua_card_num());
	electrombileStation.setStation_phy_num(station.getStation_phy_num());
	electrombileStation.setHard_read_time(formatter.format(currentTime));
	electrombileStation.setUpdate_time(formatter.format(currentTime));
	}
	
	public static void genBlackelect(Electrombile electrombile,Blackelect blackelect){
		Calendar occur_calendar = Calendar.getInstance();
		Random rand= new Random();
		occur_calendar.add(Calendar.DATE, -rand.nextInt(before_interval));
		Date occur_date = occur_calendar.getTime();
		
		blackelect.setPlate_num(electrombile.getPlate_num());
		blackelect.setCase_occur_time(occur_date);
		blackelect.setOwner_tele(electrombile.getOwner_tele());
		blackelect.setOwner_name(electrombile.getOwner_name());
		blackelect.setCase_address("新疆喀什XXX");
		blackelect.setCase_address_type(occurType[rand.nextInt(10)]);
		blackelect.setDeal_status(rand.nextInt(2));
	}
}
