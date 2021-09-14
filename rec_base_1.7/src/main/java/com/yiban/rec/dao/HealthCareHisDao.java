package com.yiban.rec.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.HealthCareHis;

/**
 * His医保
 * @Author WY
 * @Date 2018年7月20日
 */
public interface HealthCareHisDao extends JpaRepository<HealthCareHis, Long>, 
    JpaSpecificationExecutor<HealthCareHis>{
	
    /**
     * 通过机构列表、交易时间区间查询医保数据
     * @param orgNos
     * @param beginDate
     * @param endDate
     * @return
     * List<HealthCareOfficial>
     */
    List<HealthCareHis> findByOrgNoInAndTradeDatatimeBetween(Set<String> orgNos, 
            Date beginDate, Date endDate);
    
    List<HealthCareHis> findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(String orgNo, String payFlowNo, String orderState,
            Date startTime, Date endTime);
    
    HealthCareHis findByPayFlowNo(String payFlowNo);
}
