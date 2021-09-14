package com.yiban.rec.service.settlement;

import java.util.Date;

/**
 * 结算明细Service
 * @Author WY
 * @Date 2019年1月11日
 */
public interface RecHisSettlementService {

    /**
     * 获取his订单，并保存入库，入库之前删除结算日账单数据
     * @param hisBillDate
     */
    void getAndSaveHisOrders(String hisBillDate) throws Exception;
    
    /**
     * 删除结算日账单数据
     * @param settlementDate
     * @return
     * Long
     */
    Long deleteBySettlementDate(Date settlementDate);
}
