package com.yiban.rec.dao;

import com.yiban.rec.domain.RefundorderUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 退费订单上送Dao
 *
 * @Author xll
 * @Date 2020年07月16日
 */
public interface RefundOrderUploadDao extends JpaRepository<RefundorderUpload, Long>, JpaSpecificationExecutor<RefundorderUpload> {

    List<RefundorderUpload> findByOrgCodeAndMchOrderIdAndRefundId(String orgCode, String mchOrderId, String refundId);

}
