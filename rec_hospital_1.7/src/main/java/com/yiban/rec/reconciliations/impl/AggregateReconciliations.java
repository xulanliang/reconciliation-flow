package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 聚合支付对账
 * @Author WY
 * @Date 2018年7月23日
 */
public class AggregateReconciliations extends AbstractThirdBillReconciliations {

    public AggregateReconciliations(String orgCode, String date) {
        super(orgCode, date);
    }

    @Override
    public List<ThirdBill> sourceList() {
        return super.sourceList(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
    }

    @Override
    public List<HisTransactionFlow> targetList() {
        return super.targetList(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
    }
}
