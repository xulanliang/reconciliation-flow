package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.MixRefundDetails;

public interface MixRefundDetailsDao extends JpaRepository<MixRefundDetails, Long>, JpaSpecificationExecutor<MixRefundDetails> {

	@Query("select count(t) from MixRefundDetails t where t.tsnOrderNo = ?1 and t.refundState= ?2")
	public int refundCount(String tsnOrderNo,Integer refundState);
	
	public List<MixRefundDetails> findByrefundState(Integer refundState);
	
	@Query("select t from MixRefundDetails t where t.refundOrderNo = ?1 and t.refundState!=?2")
	public List<MixRefundDetails> findByrefundOrderNoAndRefundState(String refundOrderNo,Integer refundState);
	
	public List<MixRefundDetails> findByrefundOrderNo(String refundOrderNo);
}
