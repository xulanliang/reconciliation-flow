package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.TradeCheck;


public interface TradeCheckDao extends JpaRepository<TradeCheck, Long>, JpaSpecificationExecutor<TradeCheck>{
	
	@Modifying
	@Query("delete from  TradeCheck t where t.tradeDate = ?1  and t.userId = ?2 ")
	void deleteTradeCheck(String tradeDate,Long userId);
	
	@Modifying
	@Query("update TradeCheck h set h.checkState = 1 where h.id = ?1 ")
	void updateZp(Long id);
	
}
