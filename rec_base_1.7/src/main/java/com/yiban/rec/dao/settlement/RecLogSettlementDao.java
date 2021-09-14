package com.yiban.rec.dao.settlement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.settlement.RecLogSettlement;

/**
 * His结算汇总日志
 * @Author WY
 * @Date 2019年1月10日
 */
public interface RecLogSettlementDao extends JpaRepository<RecLogSettlement, Long>, 
    JpaSpecificationExecutor<RecLogSettlement> {
	
    /**
     * 通过机构编码和日期查询
     * @param orgCode
     * @param orderDate
     * @return
     * Long
     */
    RecLogSettlement findByOrgCodeAndOrderDate(String orgCode, String orderDate);
}
