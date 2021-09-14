package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.ThirdTradeFlow;


public interface ThirdTradeFlowDao extends JpaRepository<ThirdTradeFlow, Long>, JpaSpecificationExecutor<ThirdTradeFlow>{
	
	
	@Modifying
	@Query("delete from  ThirdTradeFlow t where t.tradeDate = ?1 and t.payName = ?2 and t.userId = ?3 and t.tradeName = ?4")
	void deleteThird(String tradeDate,Integer payName,Long userId,String tradeName);
	
	List<ThirdTradeFlow> findByTradeDateAndUserId(String tradeDate,Long userId);
	
}
