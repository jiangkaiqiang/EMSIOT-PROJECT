package com.ems.iot.manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ems.iot.manage.dao.AreaMapper;
import com.ems.iot.manage.dao.BlackelectMapper;
import com.ems.iot.manage.dao.CityMapper;
import com.ems.iot.manage.dao.ElectrombileMapper;
import com.ems.iot.manage.dao.ProvinceMapper;
import com.ems.iot.manage.dto.BaseDto;
import com.ems.iot.manage.dto.BlackelectDto;
import com.ems.iot.manage.dto.ResultDto;
import com.ems.iot.manage.entity.Blackelect;
import com.ems.iot.manage.entity.Electrombile;
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
	@Autowired
	private ProvinceMapper provinceMapper;
	@Autowired
	private CityMapper cityMapper;
	
	/**
	 * 根据条件查询所有黑名单车辆
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findAllBlackelectByOptions",method = RequestMethod.POST)
	@ResponseBody
	public Object findAllBlackelectByOptions(@RequestParam(value="pageNum",required=false) Integer pageNum,
			@RequestParam(value="pageSize",required=false) Integer pageSize,
			@RequestParam(value="blackID",required=false) Integer blackID,
			@RequestParam(value="ownerTele",required=false) String ownerTele,
			@RequestParam(value="plateNum",required=false) String plateNum,
			@RequestParam(value="DealStatus",required=false) Integer DealStatus) throws UnsupportedEncodingException {
		pageNum = pageNum == null? 1:pageNum;
		pageSize = pageSize==null? 12:pageSize;
		PageHelper.startPage(pageNum, pageSize);
		Page<Blackelect> blackelets=blackelectMapper.findAllBlackelectByOptions(blackID,ownerTele,DealStatus);
		Page<BlackelectDto> blackelectDtos = new Page<BlackelectDto>();
		for(Blackelect blackelect:blackelets){
			BlackelectDto blackelectDto = new BlackelectDto();
			blackelectDto.setBlackelect(blackelect);
			blackelectDto.setPlateNum(electrombileMapper.findPlateNumByGuaCardNum(blackelect.getGua_card_num()).getPlate_num());
			if(plateNum!=null){
				if(blackelectDto.getPlateNum().equals(plateNum)){
					blackelectDtos.add(blackelectDto);
					break;
				}
			} else {
				blackelectDtos.add(blackelectDto);
			}
		}		
		blackelectDtos.setPageSize(blackelets.getPageSize());
		blackelectDtos.setPages(blackelets.getPages());
		blackelectDtos.setTotal(blackelets.getTotal());
		return new PageInfo<BlackelectDto>(blackelectDtos);
	}
	
	 /**
     * 根据ID删除黑名单
     * @param electID
     * @return
     */
	@RequestMapping(value = "/deleteBlackelectByID")
	@ResponseBody
	public Object deleteElectByID(@RequestParam(value="blackID",required=false)Integer blackID) {
		 blackelectMapper.deleteByPrimaryKey(blackID);
		 return new BaseDto(0);
	}
	
	/**
	 * 根据多个ID删除黑名单
	 * @param electIDs
	 * @return
	 */
	@RequestMapping(value = "/deleteBlackelectByIDs")
	@ResponseBody
	public Object deleteElectByIDs(@RequestParam(value="BlackIDs",required=false)Integer[] blackIDs) {
		for(Integer blackID:blackIDs){
			blackelectMapper.deleteByPrimaryKey(blackID);
		}
		return new BaseDto(0);
	}
	
	/**
	 * 添加黑名单  (业务逻辑存在问题，当防盗芯片不存在时，无法显示车牌号)
	 * @param blackelect
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/addBlackelect",method = RequestMethod.POST)
	@ResponseBody
	public Object addBlackelect(@RequestParam(value="gua_card_num",required = false) Integer gua_card_num,
			@RequestParam(value="case_occur_time",required = false) String case_occur_time,
			@RequestParam(value="owner_tele",required = false) String owner_tele,
			@RequestParam(value="owner_name",required = false) String owner_name,
			@RequestParam(value="pro_id",required = false) Integer pro_id,
			@RequestParam(value="city_id",required = false) Integer city_id,
			@RequestParam(value="area_id",required = false) Integer area_id,
			@RequestParam(value="case_address_type",required = false) String case_address_type,
			@RequestParam(value="case_detail",required = false) String case_detail,
			@RequestParam(value="deal_status",required = false) Integer deal_status) throws UnsupportedEncodingException, ParseException{
		Blackelect blackelect = new Blackelect();
		blackelect.setGua_card_num(gua_card_num);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		blackelect.setCase_occur_time(sdf.parse(case_occur_time));
		blackelect.setOwner_tele(owner_tele);
		blackelect.setOwner_name(owner_name);
		blackelect.setPro_id(pro_id);
		blackelect.setCity_id(city_id);
		blackelect.setArea_id(area_id);
		blackelect.setCase_address_type(case_address_type);
		String caseAddress=provinceMapper.selectByProID(pro_id).getName();
		if(city_id!=null)
			caseAddress += cityMapper.findCityById(city_id).getName();
		if(area_id!=null)
			caseAddress += cityMapper.findAreaNameByAreaID(area_id).getName();
		blackelect.setCase_address(caseAddress);
		blackelect.setCase_detail(case_detail);
		blackelect.setDeal_status(deal_status);
		if (blackelect.getGua_card_num() == null) {
			return new ResultDto(-1, "防盗芯片编号不能为空！");
		}
		if(electrombileMapper.findElectrombileForLocation(gua_card_num, null)==null){
			return new ResultDto(-1,"防盗芯片编号不存在！");
		}
		//更新elect表中车辆的状态为黑名单状态
		Electrombile electrombile = new Electrombile();
		electrombile.setGua_card_num(blackelect.getGua_card_num());
		electrombile.setElect_state(2);
		electrombileMapper.updateByGuaCardNumSelective(electrombile);
		blackelectMapper.insert(blackelect);
		return new ResultDto(0,"添加成功");
	}
	
	 /**
     * 根据ID删除黑名单
     * @param electID
     * @return
     */
	@RequestMapping(value = "/findBlackelectByID")
	@ResponseBody
	public Object findBlackElectByID(@RequestParam(value="BlakcID")Integer blackID) {
		Page<Blackelect> blackelets=blackelectMapper.findAllBlackelectByOptions(blackID,null,null);
		BlackelectDto blackelectDto = new BlackelectDto();
		blackelectDto.setBlackelect(blackelets.get(0));
		blackelectDto.setPlateNum(electrombileMapper.findPlateNumByGuaCardNum(blackelets.get(0).getGua_card_num()).getPlate_num());
		return blackelectDto;
	}
	 /**
     * 更新黑名单
     * @param electID
     * @return
     */
	

	@RequestMapping(value = "/updateBlackelect")
	@ResponseBody
	public Object updateBlackElect(
			@RequestParam(value="black_id",required = false)Integer  black_id,
			@RequestParam(value="gua_card_num",required = false)Integer  gua_card_num,
			@RequestParam(value="case_occur_time",required = false)Date  case_occur_time,
			@RequestParam(value="owner_tele",required = false)String  owner_tele,
			@RequestParam(value="owner_name",required = false)String  owner_name,
			@RequestParam(value="pro_id",required = false)Integer  pro_id,
			@RequestParam(value="city_id",required = false)Integer  city_id,
			@RequestParam(value="area_id",required = false)Integer  area_id,
			@RequestParam(value="case_address_type",required = false)String  case_address_type,
			@RequestParam(value="case_detail",required = false)String  case_detail,
			@RequestParam(value="deal_status",required = false)Integer  deal_status
			){
				Blackelect blackelect = new Blackelect();
				blackelect.setBlack_id(black_id);
				blackelect.setGua_card_num(gua_card_num);
				blackelect.setCase_occur_time(case_occur_time);
				blackelect.setOwner_tele(owner_tele);
				blackelect.setOwner_name(owner_name);
				blackelect.setPro_id(pro_id);
				blackelect.setCity_id(city_id);
				blackelect.setArea_id(area_id);
				blackelect.setCase_address_type(case_address_type);
				blackelect.setCase_detail(case_detail);
				blackelect.setDeal_status(deal_status);
				blackelectMapper.updateByPrimaryKeySelective(blackelect);		
		return  new ResultDto(0,"修改成功");
	}
	
	/**
	 * 获取该管理员权限下的所有车辆，不分页
	 * @param proPower
	 * @param cityPower
	 * @param areaPower
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/findBlackelectsList")
	@ResponseBody
	public Object findBlackelectsList(@RequestParam(value="proPower", required=false) Integer proPower,
			@RequestParam(value="cityPower", required=false) Integer cityPower,
			@RequestParam(value="areaPower", required=false) Integer areaPower) throws UnsupportedEncodingException {
		if (null==proPower||proPower==-1) {
			proPower = null;
		}
		if (null==cityPower||cityPower==-1) {
			cityPower = null;
		}
		if (null==areaPower||areaPower==-1) {
			areaPower = null;
		}
		List<Blackelect> blackelects = blackelectMapper.findBlackelectsList(proPower, cityPower, areaPower);
		return blackelects;
	}
}
