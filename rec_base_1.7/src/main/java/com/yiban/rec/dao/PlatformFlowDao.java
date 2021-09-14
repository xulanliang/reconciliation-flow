package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.Platformflow;


public interface PlatformFlowDao extends JpaRepository<Platformflow, Integer>, JpaSpecificationExecutor<Platformflow>{
	
	@Transactional
	@Modifying
	@Query("update Platformflow h set h.isActived = 0 where h.orgNo = ?1 and h.payDate = ?2")
	void updateIsActivedByOrgNoAndPayDate(Long orgNo,String payDate);

}
