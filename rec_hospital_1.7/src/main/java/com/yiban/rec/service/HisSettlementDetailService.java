package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.vo.HisSettlementDetailVo;

public interface HisSettlementDetailService {

	public Page<RecHisSettlement> queryPage(HisSettlementDetailVo vo, List<Organization> orgList, Pageable pageable);

	public List<RecHisSettlement> queryNoPage(HisSettlementDetailVo vo, List<Organization> orgList, Sort sort);

	public Map<String, Object> querySum(HisSettlementDetailVo vo, List<Organization> orgList);

	public Map<String, List<Map<String, Object>>> queryTradeDateList(HisSettlementDetailVo vo);
	
	public List<Map<String, Object>> querySelect(String value);

	public Page<HisTransactionFlow> getOmissionAmountPage(HisSettlementDetailVo vo, List<Organization> orgList,
			PageRequest pageable);

	public Map<String, Object> getOmissionAmountCollect(HisSettlementDetailVo vo, List<Organization> orgList);

	public Page<RecHisSettlement> getBeforeSettlementPage(HisSettlementDetailVo vo, List<Organization> orgList,
			PageRequest pageable);

	public Map<String, Object> getBeforeSettlementCollect(HisSettlementDetailVo vo, List<Organization> orgList);

	public Map<String, List<Map<String, Object>>> queryBeforeTradeDateList(HisSettlementDetailVo vo);
}
