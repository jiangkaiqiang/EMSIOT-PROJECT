package com.ems.iot.manage.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
/**
 * @author Barry
 * @date 2018年3月20日下午3:41:22  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
public class CrossInterceptor extends OncePerRequestFilter {
	
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//	        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            response.setHeader("P3P", "CP=CAO PSA OUR");//解决IE下SessionID丢失的问题
            response.setHeader("Access-Control-Max-Age", "1800");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));//*request.getHeader("Origin")
            response.setHeader("Access-Control-Allow-Credentials","true"); //是否支持cookie跨域
            response.setHeader("Access-Control-Allow-Methods", "POST, GET,PUT, OPTIONS, DELETE");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Transfer-Encoding");//,Content-Type,Content-Transfer-Encoding
            if (request.getServletPath().contains("/n/")) {
            	System.out.println(request.getServletPath());
            	request.getRequestDispatcher("/componentMobile.html").forward(request,response);
            	return;
            }
            filterChain.doFilter(request, response);
	    }
}
