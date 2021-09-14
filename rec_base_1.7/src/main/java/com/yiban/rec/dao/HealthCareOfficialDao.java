package com.yiban.rec.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.HealthCareOfficial;

/**
 * 医保
 * @Author WY
 * @Date 2018年7月20日
 */
public interface HealthCareOfficialDao extends JpaRepository<HealthCareOfficial, Long>, 
    JpaSpecificationExecutor<HealthCareOfficial>{
	
    /**
	 * 通过机构列表、交易时间区间查询医保数据
	 * @param orgNos
	 * @param beginDate
	 * @param endDate
	 * @return
	 * List<HealthCareOfficial>
	 */
    List<HealthCareOfficial> findByOrgNoInAndTradeDatatimeBetween(Set<String> orgNos, 
            Date beginDate, Date endDate);
    
	@Query("select t from HealthCareOfficial t")
	List<HealthCareOfficial> findCost(String cost);
	
	List<HealthCareOfficial> findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(String orgNo, 
            String payFlowNo, String orderState,
            Date startTime, Date endTime);
	
    HealthCareOfficial findByPayFlowNo(String payFlowNo);
}
