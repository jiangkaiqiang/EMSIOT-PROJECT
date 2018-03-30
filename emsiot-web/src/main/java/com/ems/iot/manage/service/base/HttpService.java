package com.ems.iot.manage.service.base;

/**
 * @author Barry
 * @date 2018年3月20日下午3:44:31  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface HttpService {
	public String sendGet(String url);

	public String sendGet(String url, int timeout);

	public String sendPost(String url, String params);

	public String sendPost(String url, String params, int secTimeout);
}
