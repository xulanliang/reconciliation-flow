package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.RefundUpload;

/**
 * 退费结果上送
 * @Author WY
 * @Date 2018年10月26日
 */
public interface RefundUploadDao extends JpaRepository<RefundUpload, Long>, 
    JpaSpecificationExecutor<RefundUpload> {
	
    /**
     * 通过退款单号查询
     * @param refundOrderNo
     * @return
     * RefundUpload
     */
    RefundUpload findByRefundOrderNo(String refundOrderNo);
}
