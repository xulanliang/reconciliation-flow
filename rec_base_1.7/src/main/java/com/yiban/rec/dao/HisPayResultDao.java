package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.HisPayResult;


public interface HisPayResultDao extends JpaRepository<HisPayResult, Integer>, JpaSpecificationExecutor<HisPayResult>{
	
	@Transactional
	@Modifying
	@Query("update HisPayResult h set h.isActived = 0 where h.orgNo = ?1 and h.tradeDatatime >= ?2 and h.tradeDatatime<=?3 ")
	void updateIsActivedByOrgNoAndPayDate(String orgNo,String startPayDate,String endPayDate);
	
	List<HisPayResult> findByFlowNo(@Param("flowNo") String flowNo);
	
	HisPayResult findByPayFlowNo(@Param("payFlowNo") String payFlowNo);
}
