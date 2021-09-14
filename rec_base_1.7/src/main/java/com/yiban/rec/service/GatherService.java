package com.yiban.rec.service;

import java.util.Map;

import com.yiban.rec.domain.vo.AppRuntimeConfig;

public interface GatherService {
	
	
	/**
	* @date：2017年3月27日 
	* @Description：对账管理---汇总统计查询
	* @return: 返回结果描述
	* @return List<Map<String,Object>>: 返回值类型
	* @throws
	 */
	public Map<String,Object> getGatherData(String orgNo,String payDate,AppRuntimeConfig hConfig);
	
	public Map<String,Object> getOrgMap();
	
	public Map<String,Object> getOrgMapFromCode();
	
	public Map<String,Object> getOrgIdFromCode();
	
	
}
