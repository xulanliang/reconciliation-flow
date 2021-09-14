package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.HisTradeFlow;


public interface HisTradeFlowDao extends JpaRepository<HisTradeFlow, Long>, JpaSpecificationExecutor<HisTradeFlow>{
	
	@Transactional
	@Modifying
	@Query("delete from  HisTradeFlow t where t.tradeDate = ?1  and t.userId = ?2 ")
	void deleteTradeFlow(String tradeDate,Long userId);
	
}
