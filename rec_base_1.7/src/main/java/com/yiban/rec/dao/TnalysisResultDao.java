package com.yiban.rec.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.log.TnalysisResult;


public interface TnalysisResultDao extends JpaRepository<TnalysisResult, Integer>, JpaSpecificationExecutor<TnalysisResult>{

	TnalysisResult findByOrgCodeAndPayChannelAndOrderDate(String orgCode,String payChannel,Date orderDate);
	
	@Modifying
	@Query("delete  from  TnalysisResult where orgCode= ?1  and payChannel= ?2 and orderDate= ?3")  
	public void deleteResult(String orgCode,String payChannel,Date orderDate);
}
