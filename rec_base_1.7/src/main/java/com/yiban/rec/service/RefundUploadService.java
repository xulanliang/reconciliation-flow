package com.yiban.rec.service;

import com.yiban.rec.domain.RefundUpload;

/**
 * 退费结果上送
 * @Author WY
 * @Date 2018年10月26日
 */
public interface RefundUploadService {
    
    /**
     * 通过退款单号查询
     * @param refundOrderNo
     * @return
     * RefundUpload
     */
    RefundUpload findByRefundOrderNo(String refundOrderNo);
    
    /**
     * 保存
     * @param refundUpload
     * @return
     * RefundUpload
     */
    RefundUpload save(RefundUpload refundUpload);
}
