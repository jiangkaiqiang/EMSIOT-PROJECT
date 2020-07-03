package com.ems.iot.manage.service;

public interface OssService {
	// Endpoint以杭州为例，其它Region请按实际情况填写。
	public final static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com/";
	// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
	public final static String accessKeyId = "***";
	public final static String accessKeySecret = "***";
	public final static String readUrl = "https://emsiot.oss-cn-hangzhou.aliyuncs.com/";
}
