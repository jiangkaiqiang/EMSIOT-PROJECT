package com.ems.iot.manage.util;

import java.io.File;

/**
 * @author Barry
 * @date 2018年3月20日下午3:45:21  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class Constant {
    public static long EXPIRE_AFTER_ONE_HOUR = 30; //cache过期时间
    public static String CHANNEL_MSGCOUNT= "msgCount";
    public static String CHANNEL_MSG_DATA= "msgData";
    public static String CHANNEL_LIMIT_DATA= "limitData";
    public static String separator = File.separator;
    public static String url = separator+"emsiot";
    public static String influxDbUrl = "http://47.100.242.28:8086";
    public static String influxDbUserName = "admin";
    public static String influxDbPassword = "admin";
    public static String emsiotDbName = "emsiot";
    public static String electStationTable = "electStationTest3";
    public static String peopleStationTable = "peopleStationTest";
}