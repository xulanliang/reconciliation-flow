package com.yiban.rec.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.HisTransactionFlow;

/**
 * His交易记录
 * @Author WY
 * @Date 2018年7月20日
 */
public interface HisTransactionFlowDao
		extends JpaRepository<HisTransactionFlow, Integer>, JpaSpecificationExecutor<HisTransactionFlow> {
	
	@Transactional
	@Modifying
	@Query("update HisTransactionFlow h set h.isActived = 0 where h.orgNo = ?1 and h.tradeDatatime >= ?2 and h.tradeDatatime<=?3")
	void updateIsActivedByOrgNoAndPayDate(String orgNo,String startDate,String endDate);
	
	/**
	 * 通过机构集合、交易时间和支付类型查询His交易记录
	 * @param orgNos
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 * List<HisTransactionFlow>
	 */
	List<HisTransactionFlow> findByOrgNoInAndPayTypeAndTradeDatatimeBetween(
	        Set<String> orgNos, String payType, Date startTime, Date endTime);
	
	@Query(value="select t from HisTransactionFlow t where t.id=?1")
	HisTransactionFlow findById(Long id);
	
	@Query(value="select t from HisTransactionFlow t where t.payFlowNo=?1 or t.hisFlowNo=?1 or t.businessFlowNo=?1")
	List<HisTransactionFlow> findByOrderNoAndOrderState(String orderNo);

	@Query(value = "SELECT COUNT(1) FROM t_rec_histransactionflow t WHERE t.`org_no`IN(:orgCodes) AND t.`pay_type`=:payType "
			+ " AND DATE(t.`Trade_datatime`)=:tradeDate AND t.`Pay_Flow_No`=:payFlowNo", nativeQuery = true)
	public int countByOrgCodesAndPaytypeAndDateAndPayflowno(@Param("orgCodes") Set<String> orgCodes,
			@Param("payType") String payType, @Param("tradeDate") String tradeDate,
			@Param("payFlowNo") String payFlowNo);
	
	
	List<HisTransactionFlow> findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(String orgNo, String payFlowNo, String orderState,
            Date startTime, Date endTime);

	List<HisTransactionFlow> findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(String orgNo, String payFlowNo, BigDecimal payAmount, Date startTime, Date endTime);
}
