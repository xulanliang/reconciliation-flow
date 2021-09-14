package com.yiban.rec.dao;

import com.yiban.rec.domain.PayorderUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 支付订单上送Dao
 *
 * @Author xll
 * @Date 2020年07月16日
 */
public interface PayOrderUploadDao extends JpaRepository<PayorderUpload, Long>, JpaSpecificationExecutor<PayorderUpload> {

    List<PayorderUpload> findByOrgCodeAndPayId(String orgCode, String payId);

    PayorderUpload findByOutTradeNo(String outTradeNo);
}
