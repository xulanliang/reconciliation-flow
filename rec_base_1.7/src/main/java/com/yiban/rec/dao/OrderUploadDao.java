package com.yiban.rec.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.OrderUpload;

/**
 * 第三方业务系统账单上送结果
 * @Author WY
 * @Date 2018年7月25日
 */
public interface OrderUploadDao extends JpaRepository<OrderUpload, Long>, 
    JpaSpecificationExecutor<OrderUpload> {
	
    /**
     * 删除历史记录
     * @param orgCode
     * @param tradeDate
     * @return
     * Long
     */
    Long deleteByOrgCodeAndTradeDate(String orgCode, String tradeDate);
    
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
     * 通过状态统计
     * @param orderState
     * @return
     * Long
     */
    Long countByOrderState(String orderState);
    
    /**
     * 通过状态统计排除某些第三方订单
     * @param orderState
     * @param tsnOrderNo
     * @return
     * Long
     */
    Long countByOrderStateAndTsnOrderNoNotIn(String orderState, Set<String> tsnOrderNo);
    
    /**
     * 通过机构和状态统计
     * @param orgCode
     * @param orderState
     * @return
     * Long
     */
    Long countByOrgCodeAndOrderState(String orgCode, String orderState);
    
    /**
     * 通过机构和状态统计排除某些第三方订单
     * @param orgCode
     * @param orderState
     * @param tsnOrderNo
     * @return
     * Long
     */
    Long countByOrgCodeAndOrderStateAndTsnOrderNoNotIn(String orgCode, 
            String orderState, Set<String> tsnOrderNo);
    
    /**
     * 通过机构和状态统计
     * @param orgCode
     * @param orderState
     * @return
     * Long
     */
    Long countByOrgCodeInAndOrderStateAndRefundOrderStateIsNull(Set<String> orgCode, 
            String orderState);
}
