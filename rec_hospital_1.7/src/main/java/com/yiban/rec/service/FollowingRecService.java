package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.User;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;

public interface FollowingRecService {
	
	List<FollowRecResult> getFollowRecMap(String startDate,String endDate,AppRuntimeConfig hConfig);
	List<Map<String, Object>> getFollowRecMapDetail(String startDate,String endDate,AppRuntimeConfig hConfig);
	public Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo,Pageable pageable);
	public List<TradeCheckFollow> findByOrgNoAndTradeDateNoPage(String orgNo, String startDate,String endDate,String correction);
	String checkRefund(Long id,User user) throws Exception;
	
	public List<Map<String, Object>> getFollowCount(String startDate, String endDate,String payTypeSql,String orgNo);
	public List<Map<String, Object>> getFollowCountOfHis(String startDate, String endDate,String payTypeSql,String orgNo);
	public List<Map<String, Object>> getFollowCountOfExp(String startDate, String endDate,String orgCodeSql);
//	public List<Map<String, Object>> getFollowCountOfRealMoney(String startDate, String endDate,String orgNo);
	public List<Map<String, Object>> getFollowRecMapDetailOf7Day(String startDate, String endDate, AppRuntimeConfig hConfig);
}
