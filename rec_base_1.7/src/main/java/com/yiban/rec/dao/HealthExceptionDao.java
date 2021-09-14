package com.yiban.rec.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.HealthException;
import org.springframework.transaction.annotation.Transactional;

public interface HealthExceptionDao extends JpaRepository<HealthException, Long>, JpaSpecificationExecutor<HealthException> {

	@Modifying
	@Query("delete from  HealthException t where t.tradeDataTime >= ?1 and t.tradeDataTime <= ?2 and t.orgNo in ?3")
	void deleteException(Date startDate,Date endDate,String[] orgNo);

	@Transactional
	@Modifying
	@Query("update HealthException t set t.crossDayRec = 'true' where t.payFlowNo in ?1")
	void updateExceptionByPayFlowNoIn(List<String> payFlowNoList);
}