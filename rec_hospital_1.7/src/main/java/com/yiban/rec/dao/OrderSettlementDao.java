package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yiban.rec.domain.OrderSettlement;

/**
 * @author swing
 * @date 2018年8月9日 下午2:59:36 类说明 订单结算dao
 */
public interface OrderSettlementDao extends JpaRepository<OrderSettlement, Long> {

}
