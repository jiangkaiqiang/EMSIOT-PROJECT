package com.ems.iot.manage.controllerApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.ApplicationMapper;
import com.ems.iot.manage.dto.AppResultDto;
/**
 * @author Barry
 * @date 2018年3月20日下午3:32:09  
 * @version 1.0
 * Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/application")
public class ApplicationController {

    @Autowired
    private ApplicationMapper applicationMapper;
    
    @RequestMapping(value = "/getApplicationInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object getApplicationInfo(String app_name) {
        return new AppResultDto(applicationMapper.selectByPrimaryKey(app_name));
    }
    
}
