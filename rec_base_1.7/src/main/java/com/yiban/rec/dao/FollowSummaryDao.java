package com.yiban.rec.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.FollowSummary;

/**
 * 财务汇总
 * @Author WY
 * @Date 2018年11月14日
 */
public interface FollowSummaryDao extends JpaRepository<FollowSummary, Long>, 
    JpaSpecificationExecutor<FollowSummary> {
	
    /**
     * 通过机构编码和账单日期
     * @param orgNo
     * @param tradeDate
     * @return
     * List<FollowSummary>
     */
    List<FollowSummary> findByOrgNoAndTradeDate(String orgNo, String tradeDate);
    
    /**
     * 通过机构编码包含子机构信息和账单日期查询汇总信息
     * @param orgNos
     * @param tradeDate
     * @return
     * List<FollowSummary>
     */
    List<FollowSummary> findByOrgNoInAndTradeDate(Set<String> orgNos, String tradeDate);
}
