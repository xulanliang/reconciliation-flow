package com.yiban.rec.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.ExcepHandingRecord;

public interface ExcepHandingRecordDao extends JpaRepository<ExcepHandingRecord, Long>, 
    JpaSpecificationExecutor<ExcepHandingRecord>{
	
    @Modifying
	@Query("delete from  ExcepHandingRecord  where id = ?1  or fatherId = ?1")
	void deleteRecord(Long id);
	@Query("select t from ExcepHandingRecord t where t.id= ?1 or t.fatherId =?1")
	List<ExcepHandingRecord> details(Long id);
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from ExcepHandingRecord t where t.paymentRequestFlow in(?1) and (t.fatherId=0 or t.fatherId is null) ORDER BY t.handleDateTime ASC")
	List<ExcepHandingRecord> findByPaymentRequestFlow(List<String> payNo);
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from ExcepHandingRecord t where t.paymentRequestFlow in(?1) and t.billSource=?2 and (t.fatherId=0 or t.fatherId is null) ORDER BY t.handleDateTime ASC")
	List<ExcepHandingRecord> findByPaymentRequestFlowAndbillSource(List<String> payNo,String billSource);
	@Query("select t from ExcepHandingRecord t where t.fatherId=0 or t.fatherId is null")
	List<ExcepHandingRecord> findByFatherId();
	
	
	/**
	 * 通过机构、状态和登录名统计
	 * @param orgNo
	 * @param state
	 * @param userName
	 * @return
	 * Long
	 */
	Long countByOrgNoAndStateAndUserNameAndFatherId(String orgNo, String state, String userName, Long fatherId);
	
	/**
	 * 通过机构和状态统计
	 * @param orgNo
	 * @param state
	 * @return
	 * Long
	 */
	Long countByOrgNoAndStateAndFatherId(String orgNo, String state, Long fatherId);
	
	/**
	 * 通过状态统计
	 * @param state
	 * @return
	 * Long
	 */
	Long countByStateAndFatherId(String state, Long fatherId);
	
	/**
	 * 异常处理表
	 * @param orgNo
	 * @param state
	 * @return
	 * List<ExcepHandingRecord>
	 */
	List<ExcepHandingRecord> findByOrgNoAndState(String orgNo, String state);
	
	/**
	 * 通过机构查询所有异常记录
	 * @param orgNos
	 * @return
	 * List<ExcepHandingRecord>
	 */
	List<ExcepHandingRecord> findByOrgNoIn(Set<String> orgNos);

	/**
	 * 通过机构编码，订单状态 查询订单
	 * @param orgNo 机构编码
	 * @param paymentRequestFlow  支付流水号
	 * @return
	 * List<ExcepHandingRecord>
	 */
	List<ExcepHandingRecord> findByOrgNoAndPaymentRequestFlowAndTradeAmount(String orgNo, String paymentRequestFlow, BigDecimal tradAmount);
}
