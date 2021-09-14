package com.yiban.rec.service;


import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.domain.vo.FinanceVo;


public interface FinanceService {
	
	
	/**
	* @date：2017年3月27日 
	* @Description：对账管理---财务汇总统计
	* @return: 返回结果描述
	* @return Page<Map<String,Object>>: 返回值类型
	* @throws
	 */
	public Page<Map<String,Object>> getFinanceData(OprPageRequest pagerequest,FinanceVo fvo,List<String> orgList);
	
	/**
	* @date：2017年3月27日 
	* @Description：对账管理---导出财务汇总统计
	* @return: 返回结果描述
	* @return Page<Map<String,Object>>: 返回值类型
	* @throws
	 */
	public List<Map<String,Object>> exportFinanceData(FinanceVo fvo,List<String> orgList);
	
}
