package com.yiban.rec.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.TradeCheckFollow;

public interface TradeCheckFollowDao
		extends JpaRepository<TradeCheckFollow, Long>, JpaSpecificationExecutor<TradeCheckFollow> {

	@Modifying
	@Query("delete from  TradeCheckFollow t where t.tradeDate >= ?1 and t.tradeDate <= ?2 and t.orgNo in ?3")
	void deleteTradeCheckFollow(String startDate, String endDate, String[] orgNo);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4")
	Page<TradeCheckFollow> findByOrgNoAndTradeDate(String[] orgNo, String startDate, String endDate, Integer checkState,
			Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4")
	List<TradeCheckFollow> findByOrgNoAndTradeDateNoPage(String[] orgNo, String startDate, String endDate,
			Integer checkState);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4 and patType=?5")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPatType(String[] orgNo, String startDate, String endDate,
			Integer checkState, String patType, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4 and billSource=?5")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndBillSource(String[] orgNo, String startDate, String endDate,
			Integer checkState, String billSource, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4 and patType=?5 and billSource=?6")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPatTypeAndBillSource(String[] orgNo, String startDate,
			String endDate, Integer checkState, String patType, String billSource, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState!=?4 and t.payName in (?5)")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayName(String[] orgNo, String startDate, String endDate,
			Integer checkState, String[] payType, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeTime>=?2 and t.tradeTime<=?3 and t.checkState!=?4 and t.payName in (?5)")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayName(String[] orgNo, Date startDate, Date endDate,
			Integer checkState, String[] payType, Pageable pageable);

	@Modifying
	@Query(value = "delete from TradeCheckFollow t where t.businessNo=?1")
	void deleteByBusinessNo(String orderNo);

	List<TradeCheckFollow> findByTradeDateAndOrgNoIn(String tradeTime, Set<String> orgSets);

	// public List<TradeCheckFollow>
	// findByOrgNoAndBusinessNoAndPayNameAndTradeNameAndTradeDateNot(String
	// orgNo,String businessNo,String payName,String tradeName,String tradeDate);
	public List<TradeCheckFollow> findByBusinessNoAndTradeDateNotAndCheckState(String businessNo, String tradeDate,
			Integer checkState);

	List<TradeCheckFollow> findByTradeDateNot(String tradeDate);

	List<TradeCheckFollow> findByOrgNoInAndTradeDateBetween(String[] orgList, String startDate, String endDate);

	// 此为没有查询异常订单时,忽略调异常订单的时间
//	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.checkState in (?2) and t.payName in (?3) and billSource in (?4) and (t.businessNo = ?5 or t.shopFlowNo = ?5)")
//	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayNameAndStatusAndBusinessNo(String[] orgNos,  Integer[] checkStates, String[] payTypes,String[] billSource,String businessNo, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.checkState in (?2) and t.payName in (?3) and billSource in (?4) and (t.businessNo = ?5 or t.shopFlowNo = ?5) and t.tradeDate>=?6 and t.tradeDate<=?7")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayNameAndStatusAndBusinessNo(String[] orgNos,
			Integer[] checkStates, String[] payTypes, String[] billSource, String businessNo,String startDate, String endDate, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState in (?4) and t.payName in (?5) and billSource in (?6) and patType like %?7%")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayNameAndStatus(String[] orgNos, String startDate, String endDate,
			Integer[] checkStates, String[] payTypes, String[] billSource, String patType, Pageable pageable);

	@Query("select t from TradeCheckFollow t where (patType != ?1 or patType is null) and t.orgNo in (?2) and t.tradeDate>=?3 and t.tradeDate<=?4 and t.checkState in (?5) and t.payName in (?6) and billSource in (?7) ")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayNameAndStatus(String patType, String[] orgNos, String startDate,
			String endDate, Integer[] checkStates, String[] payTypes, String[] billSource, Pageable pageable);

	@Query("select t from TradeCheckFollow t where t.orgNo in (?1) and t.tradeDate>=?2 and t.tradeDate<=?3 and t.checkState in (?4) and t.payName in (?5) and billSource in (?6)")
	Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPayNameAndStatus(String[] orgNos, String startDate, String endDate,
			Integer[] checkStates, String[] payTypes, String[] billSource, Pageable pageable);

	TradeCheckFollow findByBusinessNoAndTradeNameAndTradeDateAndTradeAmountAndPayName(String payFlowNo,
			String orderState, Date tradeDate, BigDecimal payAmount, String payType);
}
