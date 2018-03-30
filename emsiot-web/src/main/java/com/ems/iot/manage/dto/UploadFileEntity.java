package com.ems.iot.manage.dto;

import org.springframework.web.multipart.MultipartFile;
/**
 * @author Barry
 * @date 2018年3月20日下午3:37:19  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class UploadFileEntity {
	private String name;// file name-->FTP client
	private MultipartFile multipartFile;// multipartFile-->user upload file
	//相对地址，不加 /
	private String remoteNewDir;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public String getRemoteNewDir() {
		return remoteNewDir;
	}

	public void setRemoteNewDir(String remoteNewDir) {
		this.remoteNewDir = remoteNewDir;
	}

	public UploadFileEntity(String name, MultipartFile multipartFile, String remoteNewDir) {
		super();
		this.name = name;
		this.multipartFile = multipartFile;
		this.remoteNewDir = remoteNewDir;
	}

}
