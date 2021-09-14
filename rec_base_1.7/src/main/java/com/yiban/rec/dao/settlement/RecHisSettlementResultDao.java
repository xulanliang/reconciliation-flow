package com.yiban.rec.dao.settlement;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import com.yiban.rec.domain.settlement.RecHisSettlementResult;

/**
 * His结算汇总
 * @Author WY
 * @Date 2019年1月10日
 */
public interface RecHisSettlementResultDao extends JpaRepository<RecHisSettlementResult, Long>, 
    JpaSpecificationExecutor<RecHisSettlementResult> {
	
	
	RecHisSettlementResult findBySettleDateAndBillSource(Date date,String billSource);
	
	List<RecHisSettlementResult> findBySettleDateLessThanEqualAndBillSourceAndOrgCodeOrderBySettleDateDesc(Date date,String billSource,String orgCode);
	
	RecHisSettlementResult findBySettleDateAndBillSourceAndOrgCode(Date date,String billSource,String orgCode);
	
	@Modifying
	void deleteBySettleDate(Date settleDate);
}
