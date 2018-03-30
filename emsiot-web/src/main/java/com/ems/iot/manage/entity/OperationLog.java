package com.ems.iot.manage.entity;

import java.util.Date;

/**
 * @author Barry
 * @date 2018年3月20日下午3:38:04  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class OperationLog {
    private Integer id;

    private String requestname;//请求名称

    private String adminname;//操作者

    private String requesturl;//请求路径

    private String content;//请求内容
    
    private Date addtime;//请求时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRequestname() {
		return requestname;
	}

	public void setRequestname(String requestname) {
		this.requestname = requestname;
	}

	public String getAdminname() {
		return adminname;
	}

	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}

	public String getRequesturl() {
		return requesturl;
	}

	public void setRequesturl(String requesturl) {
		this.requesturl = requesturl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

}