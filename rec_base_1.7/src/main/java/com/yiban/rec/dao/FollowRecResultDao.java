package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.FollowRecResult;


public interface FollowRecResultDao extends JpaRepository<FollowRecResult, Long>, JpaSpecificationExecutor<FollowRecResult>{
	
	@Modifying
	@Query("delete from  FollowRecResult t where t.tradeDate >= ?1  and t.tradeDate <= ?2 and t.orgNo = ?3")
	void deleteFollowRec(String startDate,String endDate,String orgNo);
	
	
	@Query("select t  from  FollowRecResult t where t.tradeDate >= ?1 and t.tradeDate <= ?2 and t.orgNo = ?3")
	List<FollowRecResult> findByOrgNoAndTradeDate(String startDate,String endDate,String orgNo);
	
}
