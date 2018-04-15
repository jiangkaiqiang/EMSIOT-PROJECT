package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dto.BlackelectDto;
import com.ems.iot.manage.entity.Blackelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @author Barry
 * @date 2018年4月15日下午3:32:54
 * @version 1.0 Copyright: Copyright (c) EMSIOT 2018
 */
@Controller
@RequestMapping(value = "/blackelect")
public class BlackelectController extends BaseController {
	@Autowired
	private ElectrombileMapper electrombileMapper;
	@Autowired
	private BlackelectMapper blackelectMapper;
	
	/**
	 * 查询所有黑名单车辆
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAllBlackelectForMap",method = RequestMethod.POST)
	@ResponseBody
	public Object findAllBlackelectForMap(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize") Integer pageSize) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Blackelect> blackelets=blackelectMapper.findAllBlackelect();
		Page<BlackelectDto> blackelectDtos = new Page<BlackelectDto>();
		for(Blackelect blackelect:blackelets){
			BlackelectDto blackelectDto = new BlackelectDto();
			blackelectDto.setBlackelect(blackelect);
			blackelectDto.setPlateNum(electrombileMapper.findPlateNumByGuaCardNum(blackelect.getGua_card_num()).getPlate_num());
			blackelectDtos.add(blackelectDto);
		}		
		blackelectDtos.setPageSize(blackelets.getPageSize());
		blackelectDtos.setPages(blackelets.getPages());
		blackelectDtos.setTotal(blackelets.getTotal());
		return new PageInfo<BlackelectDto>(blackelectDtos);
	}
}
