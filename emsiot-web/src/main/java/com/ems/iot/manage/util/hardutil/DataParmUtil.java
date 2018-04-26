package com.ems.iot.manage.util.hardutil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import redis.clients.jedis.Jedis;

public class DataParmUtil {
	public static final  int portXP=13129;
	public static final  int portDKS=13128;
	
	//public static final  int portXPNew=9001;
	public static final  int portXPNew=13125;
	
	//3号卡口
	public static final int portXPPd = 13125;
	//2号卡口
	public static final int portXPTwo = 13124;
	
	//1号卡口
	public static final int portXPOne = 13123;
	
	//工地读卡器接口
	public static final int portArea = 13135;
	
	public static Jedis jedis = new Jedis("127.0.0.1",6379);
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat sdfym = new SimpleDateFormat("yyyyMM");
	
	public static int personStartNum = 0;
	
	
	public static String triggerNumS = "200";
	public static String triggerNumF = "201";
	public static String triggerNum = triggerNumS + "," +triggerNumF;
	
	public static String areaEqNums = "22,44,66,88";
	/**
	 * 从C盘读取配置文件，获取人员数量的初始值
	 */
	public static void getPersonStartNum() {
		Properties prop = new Properties(); 
		
        //读取属性文件a.properties
        InputStream in = null;
		try {
			in = new BufferedInputStream (new FileInputStream("c:/personNum.properties"));
			prop.load(in);
			   ///加载属性列表
	        personStartNum =Integer.valueOf(prop.getProperty("personStartNum"));	        
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}
