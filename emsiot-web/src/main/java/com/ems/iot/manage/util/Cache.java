package com.ems.iot.manage.util;

/**
 * @author Barry
 * @date 2018年3月20日下午3:45:18  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class Cache {
    private String key; 
    private Object value; 
    private long timeOut; 
    private boolean expired; 
    public Cache() { 
            super(); 
    } 
             
    public Cache(String key, String value, long timeOut, boolean expired) { 
            this.key = key; 
            this.value = value; 
            this.timeOut = timeOut; 
            this.expired = expired; 
    } 

    public String getKey() { 
            return key; 
    } 

    public long getTimeOut() { 
            return timeOut; 
    } 

    public Object getValue() { 
            return value; 
    } 

    public void setKey(String string) { 
            key = string; 
    } 

    public void setTimeOut(long l) { 
            timeOut = l; 
    } 

    public void setValue(Object object) { 
            value = object; 
    } 

    public boolean isExpired() { 
            return expired; 
    } 

    public void setExpired(boolean b) { 
            expired = b; 
    }
}