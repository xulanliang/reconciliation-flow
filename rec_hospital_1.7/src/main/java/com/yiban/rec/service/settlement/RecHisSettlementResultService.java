package com.yiban.rec.service.settlement;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.vo.RecHisSettlementResultVo;

/**
 * 结算汇总Service
 * @Author WY
 * @Date 2019年1月11日
 */
public interface RecHisSettlementResultService {

	
	public void summary(List<RecHisSettlement> list,String date) throws Exception;
	
	public List<Map<String, Object>> getSettlementPage(RecHisSettlementResultVo vo,List<Organization> orgList,PageRequest pageRequest) throws ParseException;
}
