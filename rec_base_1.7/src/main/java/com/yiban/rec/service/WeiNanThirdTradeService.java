package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import com.yiban.framework.account.domain.Organization;

public interface WeiNanThirdTradeService {
	
	Map<String,Object> getTradeCollect(String orgNo,String startDate,String endDate,String payType,String payFlowNo,String billSource, List<Organization> orgList, String shopFlowNo,String orderState);

	Map<String,Object> summary(String orgNo,String startDate,String endDate,String payType,String payFlowNo,String billSource, List<Organization> orgList, String shopFlowNo,String orderState);
}
