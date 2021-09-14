package com.yiban.rec.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.RecCash;

/**
 * 现金交易记录
 * @Author WY
 * @Date 2018年7月20日
 */
public interface RecCashDao extends JpaRepository<RecCash, Long>, JpaSpecificationExecutor<RecCash> {

	@Transactional
	@Modifying
	@Query("update RecCash h set h.isActived = 0 where h.orgNo = ?1 and h.tradeDatatime >= ?2 and h.tradeDatatime<=?3 ")
	void updateIsActivedByOrgNoAndPayDate(Long orgNo,String startPayDate,String endPayDate);
	
	List<RecCash> findByFlowNo(@Param("flowNo") String flowNo);
	
	RecCash findByPayFlowNo(@Param("payFlowNo") String payFlowNo);
	
	/**
	 * 通过机构集合、交易日期区间查询交易记录
	 * @param orgNos
	 * @param beginDate
	 * @param endDate
	 * @return
	 * List<RecCash>
	 */
	List<RecCash> findByOrgNoInAndTradeDatatimeBetween(Set<String> orgNos, 
	        Date beginDate, Date endDate);
}
