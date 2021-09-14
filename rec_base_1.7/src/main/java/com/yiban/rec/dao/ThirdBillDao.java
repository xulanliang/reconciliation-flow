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

import com.yiban.rec.domain.ThirdBill;

/**
 * 第三方账单交易记录
 * @Author WY
 * @Date 2018年7月20日
 */
public interface ThirdBillDao extends JpaRepository<ThirdBill, Long>, JpaSpecificationExecutor<ThirdBill>{

	@Transactional
	@Modifying
	@Query("update ThirdBill h set h.isActived = 0 where h.orgNo = ?1 and h.tradeDatatime >= ?2 and h.tradeDatatime<=?3")
	void updateIsActivedByOrgNoAndPayDate(String orgNo,String startDate,String endDate);
	
	@Modifying
	@Query(value = "SELECT SUM(pay_amount) AS Pay_Amount FROM t_thrid_bill WHERE "
			+ "org_no=?1 AND Trade_datatime>=?2 AND  Trade_datatime<=?3", 
			nativeQuery = true)
	String findAllAmount(String orgNo, Date tradeDateStart, Date tradeDateEnd);
	
	List<ThirdBill> findByPayFlowNo(String paramString);
	
	@Modifying
	@Query(value = "SELECT SUM(pay_amount) AS Pay_Amount FROM t_thrid_bill WHERE "
			+ "org_no=?1 AND pay_type=?2 AND Trade_datatime>=?3 AND  Trade_datatime<=?4", 
			nativeQuery = true)
	String findAllPayTypeAmount(String orgNo, Integer payType,Date tradeDateStart, Date tradeDateEnd);
	

	@Query(value="select t from ThirdBill t where t.orgNo in(?1) and t.tradeDatatime >= ?2 and t.tradeDatatime <= ?3 GROUP BY payFlowNo,orderNo,outTradeNo,orderState ")
	List<ThirdBill> searchBill(Set<String> orgCodeList,Date startTime,Date endTime);
	
	@Query(value="select t from ThirdBill t where t.orgNo in(?1) and t.tradeDatatime = ?2")
	List<ThirdBill> searchBill(Set<String> orgCodeList,Date date);
	
	@Query(value="select t from ThirdBill t where t.tradeDatatime >= ?1 and t.tradeDatatime <= ?2")
	List<ThirdBill> searchBill(Date startTime,Date endTime);
	
	@Modifying
	@Query(value="select * from t_thrid_bill t where t.org_no=?1 and t.trade_datatime >= ?2 and t.trade_datatime <= ?3 and t.pay_type in (?4) and t.pay_amount > 0", 
			nativeQuery = true)
	List<ThirdBill> findByThirdBillAndPaytype(String orgNo,Date startTime,Date endTime,String[] payType);
	
	@Transactional
	@Modifying
	@Query(value="delete from t_thrid_bill  where org_no in (?1) and trade_datatime >= ?2 and trade_datatime <= ?3 and rec_pay_type in ('0249','0349') and bill_source = 'self'", 
			nativeQuery = true)
	int delete(String[] orgNos,Date startTime,Date endTime);
	
	
	@Transactional
	@Modifying
	@Query(value="delete from t_thrid_bill  where trade_datatime >= ?1 and trade_datatime <= ?2 and org_no in(?3) and rec_pay_type=?4  AND bill_source = 'self_td_jd' ",nativeQuery = true)
	int delete(String startTime,String endTime,List<String> orgCodes,String payType);
	
	@Transactional
	@Modifying
	@Query(value="delete from t_thrid_bill  where file_id=?1",nativeQuery = true)
	int deleteByFileId(String fileId);
	
	@Transactional
	@Modifying
	@Query(value="delete from t_thrid_bill  where shop_flow_no=?1 or Pay_Flow_No=?1",nativeQuery = true)
	int delete(String payFlowNo);
	//@Query(value="select t from ThirdBill t where (t.Pay_Flow_No = ?1 or t.shop_flow_no= ?1 or t.out_trade_no= ?1) and t.Order_State= ?2")
	@Query(value="select t from ThirdBill t where (t.payFlowNo = ?1 or t.shopFlowNo= ?1 or t.outTradeNo= ?1) and t.orderState= ?2")
	public List<ThirdBill> findByOrderNoAndOrderState(String orderNo,String orderType);
	@Query(value="select t from ThirdBill t where (t.payFlowNo = ?1 or t.shopFlowNo= ?1 or t.outTradeNo= ?1)  ")
	public List<ThirdBill> findByOrderNoAndOrderStateAndTradeDatetime(String orderNo);
	
	@Query(value="select t from ThirdBill t where t.payFlowNo = ?1 or t.shopFlowNo= ?1 or t.outTradeNo= ?1 or t.orderNo= ?1")
	public List<ThirdBill> findByOrderNoAndOrderState(String orderNo);
	
	@Query(value="select t from ThirdBill t where t.payFlowNo = ?1 or t.shopFlowNo= ?1 or t.outTradeNo= ?1 or t.orderNo= ?1")
	public List<ThirdBill> findByOrderNo(String orderNo);
	@Transactional
	@Modifying
	@Query(value="delete from t_thrid_bill  where trade_datatime >= ?1 and trade_datatime <= ?2 and org_no in(?3) ",nativeQuery = true)
	int delete(String startTime,String endTime,List<String> orgCodes);
	
	/**
	 * 通过机构编码集合、交易时间区间和支付类型查询交易记录
	 * @param orgNos
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 * List<ThirdBill>
	 */
	List<ThirdBill> findByOrgNoInAndRecPayTypeAndTradeDatatimeBetween(Set<String> orgNos, String payType, 
	        Date startTime, Date endTime);
	
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @param orgCodes
	 * @param payType
	 * @param billSource
	 * @return
	 * int
	 */
	@Transactional
    @Modifying
    @Query(value="delete from t_thrid_bill  where trade_datatime >= ?1 and trade_datatime <= ?2 and org_no in(?3) and rec_pay_type=?4 and bill_source =?5",nativeQuery = true)
    int delete(String startTime,String endTime,List<String> orgCodes,String payType, String billSource);
	
	List<ThirdBill> findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(String orgNo,String payFlowNo,String orderState,
            Date startTime, Date endTime);

	List<ThirdBill> findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(String orgNo, String payFlowNo, BigDecimal payAmount, Date startTime, Date endTime);

	@Query(value = "SELECT COUNT(1) FROM t_thrid_bill t WHERE t.`org_no`IN(:orgCodes) AND t.`pay_type`=:payType "
			+ " AND DATE(t.`Trade_datatime`)=:tradeDate AND t.`Pay_Flow_No`=:payFlowNo", nativeQuery = true)
	int countByOrgCodesAndPaytypeAndDateAndPayflowno(@Param("orgCodes") Set<String> orgCodes,
													 @Param("payType") String payType, @Param("tradeDate") String tradeDate,
													 @Param("payFlowNo") String payFlowNo);
}
