package com.yiban.rec.service;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.log.RecLogDetails;

public interface RecLogDetailsService {
	
	
	
	/**
	* @date：2017年3月27日 
	* @Description：对账管理---对账日志查询
	* @return: 返回结果描述
	* @return Page<HistoryState>: 返回值类型
	* @throws
	 */
	Page<RecLogDetails> getRecLogData(Long orgNo,Date date,List<Organization> orgListTemp,Pageable pageable);
	
	/**
	 * 保存
	 * @param recLogDetails
	 * @return
	 * RecLogDetails
	 */
	RecLogDetails save(RecLogDetails recLogDetails);
	
	/**
	 * 查询日志明细
	 * @param orderDate
	 * @param orgCode
	 * @return
	 * RecLogDetails
	 */
	List<RecLogDetails> findByOrderDateAndOrgCode(String orderDate, String orgCode);
	
	/**
	 * 更具机构和日志删除日志
	 * @param orderDate
	 * @param orgCode
	 * @return Long
	 */
	Long deleteByOrderDateAndOrgCode(String orderDate, String orgCode);
}
