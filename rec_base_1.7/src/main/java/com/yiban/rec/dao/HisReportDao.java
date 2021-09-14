package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yiban.rec.domain.HisReport;

/**
 * his报表汇总dao
 * 
 * @author clearofchina
 *
 */
public interface HisReportDao extends JpaRepository<HisReport, Long> {
	
	int deleteByOrgCodeAndTradeDate(String orgCode, String tradeDate);
}
