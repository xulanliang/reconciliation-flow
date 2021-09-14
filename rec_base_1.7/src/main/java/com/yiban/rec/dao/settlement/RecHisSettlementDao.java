package com.yiban.rec.dao.settlement;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.settlement.RecHisSettlement;

/**
 * His结算明细
 * @Author WY
 * @Date 2019年1月10日
 */
public interface RecHisSettlementDao extends JpaRepository<RecHisSettlement, Long>, 
    JpaSpecificationExecutor<RecHisSettlement> {
	
    Long deleteBySettlementDate(Date settlementDate);
}
