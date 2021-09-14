package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.vo.HisPayQueryVo;

public interface HisTransactionFlowService {
	/**
	 * his交易明细查询
	 * @return
	 */
	public Page<HisTransactionFlow> getHisPayPage(HisPayQueryVo vo,List<Organization> orgListTemp,Pageable pageable);
	
	public List<HisTransactionFlow> getHisPayNoPage(HisPayQueryVo vo,List<Organization> orgListTemp,Sort sort);
	
	public Long getHisBillCount(HisPayQueryVo vo,List<Organization> orgListTemp);

	public Page<HisTransactionFlow> getHisPayPage(HisPayQueryVo vo, List<Organization> orgListTemp, List<String> payTypes, List<String> payBusinessTypes, PageRequest requestPageabledWithInitSort);

	public List<HisTransactionFlow> getHisPayList(HisPayQueryVo vo, List<Organization> orgListTemp, List<String> payTypes, List<String> payBusinessTypes, Sort sort);
	
	Map<String,Object> getTradeCollect(HisPayQueryVo vo,List<Organization> orgList);
	
	public List<Map<String, Object>> getFollowCountOfHisBusiness(String startDate, String endDate,String payTypeSql,String orgCodeSql);
	
	public List<Map<String, Object>> getFollowCountOfHisBusinessOf7Day(String startDate, String endDate,String payTypeSql,String orgCodeSql);
	
	public List<Map<String, Object>> getFollowCountOfPayType(String startDate, String endDate,String payTypeSql,String orgCodeSql);
	
	public List<Map<String, Object>> getFollowCountOfPayTypeOf7Day(String startDate, String endDate,String payTypeSql,String orgCodeSql);

	public Map<String, Object> searchSumary(HisPayQueryVo vo, List<Organization> orgList);
}
