package com.ems.iot.manage.intercepter;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ems.iot.manage.dao.OperationLogMapper;
import com.ems.iot.manage.entity.OperationLog;
import com.ems.iot.manage.entity.SysUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Barry
 * @date 2018年3月20日下午3:43:54  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class UserRoleLogInterceptor implements HandlerInterceptor{

	@Autowired
	private OperationLogMapper operationLogDao;
	
	private SysUser sysUser;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		sysUser = (SysUser) request.getSession().getAttribute("user");
		return sysUser != null;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		String methodName = handlerMethod.getMethod().getName();
		OperationLog operationLog = new OperationLog();
		if (sysUser!=null) {
			operationLog.setAdminname(sysUser.getUser_name());
		}
		operationLog.setAddtime(new Date());
		if (methodName.equals("addUserRole")) {
			operationLog.setRequestname("添加用户角色");
			operationLog.setContent(String.format("用户角色名称：%s", request.getParameter("user_role_name")));
			operationLog.setRequesturl(request.getRequestURL().toString());
		}else if (methodName.equals("updateUserRole")) {
			operationLog.setRequestname("更新用户角色");
			operationLog.setContent(String.format("用户角色名称：%s", request.getParameter("user_role_name")));
			operationLog.setRequesturl(request.getRequestURL().toString());
		}
		else if (methodName.equals("deleteUserRoleByID")) {
			operationLog.setRequestname("删除用户角色");
			operationLog.setContent(String.format("用户角色id：%s", request.getParameter("userRoleID")));
			operationLog.setRequesturl(request.getRequestURL().toString());
		}
		else if (methodName.equals("deleteUserRoleByIDs")) {
			String[] userRoleIDs = request.getParameterValues("userRoleIDs");
			operationLog.setRequestname("批量删除用户角色");
			operationLog.setContent(String.format("删除的用户角色id：%s",Arrays.toString(userRoleIDs)));
			operationLog.setRequesturl(request.getRequestURL().toString());
		}
		if (operationLog.getRequestname() != null) {
			operationLogDao.insertOperationLog(operationLog);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
