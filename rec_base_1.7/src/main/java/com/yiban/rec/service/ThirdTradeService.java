package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.yiban.framework.account.domain.Organization;

@Component
public interface ThirdTradeService {
	
	Map<String,Object> getTradeCollect(String orgNo,String startDate,String endDate,String payType,String payFlowNo,String billSource, List<Organization> orgList, String shopFlowNo,String orderState);

	Map<String,Object> summary(String orgNo,String startDate,String endDate,String payType,String payFlowNo,String billSource, List<Organization> orgList, String shopFlowNo,String orderState);
}
