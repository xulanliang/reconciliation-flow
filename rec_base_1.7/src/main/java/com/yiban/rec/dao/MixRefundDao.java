package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.MixRefund;

public interface MixRefundDao extends JpaRepository<MixRefund, Long>, JpaSpecificationExecutor<MixRefund> {

	public MixRefund findByRefundOrderNo(String refundOrderNo);
	
}
