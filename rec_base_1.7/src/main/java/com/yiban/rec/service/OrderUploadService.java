package com.yiban.rec.service;

import com.yiban.rec.domain.OrderUpload;

/**
 * 第三方支付结果上送
 * @Author WY
 * @Date 2018年7月25日
 */
public interface OrderUploadService {
    
    /**
     * 新增、修改
     * @param orderUpload
     * @return
     * OrderUpload
     */
    OrderUpload save(OrderUpload orderUpload);
    
    /**
     * 通过业务单号查询
     * @param outTradeNo
     * @return
     * OrderUpload
     */
    OrderUpload findByOutTradeNo(String outTradeNo);
    
    /**
     * 通过第三方（微信支付宝）订单号查询
     * @param tsnOrderNo
     * @return
     * OrderUpload
     */
    OrderUpload findByTsnOrderNo(String tsnOrderNo);
    
    /**
     * 通过第三方（微信支付宝）订单号更新退费状态
     */
    void updateOrder(String tsnOrderNo,String state)throws Exception;
}
