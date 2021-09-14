package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.basicInfo.RetryOfHisToCenterTaskFailInfo;

public interface RetryOfHisToCenterTaskFailInfoDao
		extends JpaRepository<RetryOfHisToCenterTaskFailInfo, Long>, JpaSpecificationExecutor<RetryOfHisToCenterTaskFailInfo> {

	@Transactional
	@Modifying
	@Query("update RetryOfHisToCenterTaskFailInfo r set r.status = ?1 where r.orgNo = ?2 AND r.billType = ?3 AND r.billDate= ?4")
	void updateStatus(Integer status, String orgNo,Integer billType,String billDate);
	
	RetryOfHisToCenterTaskFailInfo findByOrgNoAndBillTypeAndStartPositionAndEndPositionAndBillDate(String orgNo,Integer billType,Integer startPosition,Integer endPosition,String billDate);
	
	RetryOfHisToCenterTaskFailInfo findByOrgNoAndBillTypeAndBillDate(String orgNo,Integer billType,String billDate);
	
	List<RetryOfHisToCenterTaskFailInfo> findByStatusOrderByIdAsc(Integer status);
	
	List<RetryOfHisToCenterTaskFailInfo> findByStatusInAndSendAmountLessThanEqualOrderByIdAsc(List<Integer> status,Integer sendAmount);
	
	List<RetryOfHisToCenterTaskFailInfo> findByStatusInAndSendAmountLessThanEqualOrderByIdAsc(Integer status,Integer sendAmount);
	
	@Transactional
	@Modifying
	@Query("update RetryOfHisToCenterTaskFailInfo r set r.sendAmount=r.sendAmount+1 where r.id in ?1")
	void updateSendTimes(List<Long> ids);
	
	@Transactional
	@Modifying
	@Query("update RetryOfHisToCenterTaskFailInfo r set r.sendAmount=r.sendAmount+1 where r.id = ?1")
	void updateSendTimes(Long id);
	
	@Transactional
	@Modifying
	@Query("update RetryOfHisToCenterTaskFailInfo r set r.status = ?1 where r.id= ?2 ")
	void updateStatus(Integer status, Long id);
}
