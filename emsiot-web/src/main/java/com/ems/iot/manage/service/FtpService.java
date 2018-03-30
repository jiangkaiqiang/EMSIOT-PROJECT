package com.ems.iot.manage.service;

import java.util.List;

import com.ems.iot.manage.dto.UploadFileEntity;

/**
 * @author Barry
 * @date 2018年3月20日下午3:44:24  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public interface FtpService {

	//public final static String HOST = "10.25.192.102";

	//public final static String PUB_HOST = "42.121.130.177";
	public final static String PUB_HOST = "139.196.139.164";

	//public final static String USER_NAME = "pwftp";
	public final static String USER_NAME = "ftpuser";

	//public final static String PASSWORD = "!@QWaszx0o";
	public final static String PASSWORD = "@!ftpPassword";

	public final static int PORT = 21;

	public final static String BASEDIR = "/shfb";

	public final static int READPORT = 8089;
	
	//public final static String READ_URL = "ftp://pwftp:!@QWaszx0o@filestorage-weilanshu.xyz/";
	
	public final static String READ_URL = "http://42.121.130.177:8080/";
	public final static String FILE_Url = "http://139.196.139.164:65531/shfb/";
	
	//public final static String READ_URL = "http://139.196.189.93:8089/";
	
	//ftp://42.121.130.177/data/ueditor/20161217/1481958350959078009.png

	boolean uploadFile(UploadFileEntity uploadFile);

	boolean uploadFileList(List<UploadFileEntity> uploadFileList);
	
	boolean deleteFile(String url);

	boolean deleteByLocation(String location);
}
