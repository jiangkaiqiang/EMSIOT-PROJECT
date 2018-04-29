package com.ems.iot.manage.entity;

import java.math.BigInteger;
import java.util.Date;
/**
 * 消息实体类
 * @author jiangkaiqiang
 * @version 创建时间：2016-10-21 下午1:53:16 
 *
 */
public class MessageEntity {
	private BigInteger id;
	
	private String title;
	
	private String msgcategory;
	
	private BigInteger senderid;//发送者id
	
	private String content;
	
	private BigInteger receiverid;//接收者id
	
    private Date informtime;//通知时间

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsgcategory() {
		return msgcategory;
	}

	public void setMsgcategory(String msgcategory) {
		this.msgcategory = msgcategory;
	}

	public BigInteger getSenderid() {
		return senderid;
	}

	public void setSenderid(BigInteger senderid) {
		this.senderid = senderid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BigInteger getReceiverid() {
		return receiverid;
	}

	public void setReceiverid(BigInteger receiverid) {
		this.receiverid = receiverid;
	}

	public Date getInformtime() {
		return informtime;
	}

	public void setInformtime(Date informtime) {
		this.informtime = informtime;
	}
 
    
}
