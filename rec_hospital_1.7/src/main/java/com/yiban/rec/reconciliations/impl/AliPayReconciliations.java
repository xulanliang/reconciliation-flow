package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 支付宝对账
 * @author swing
 * @date 2018年7月19日 上午11:21:28 类说明
 * 支付宝两方对账实现
 */
public class AliPayReconciliations extends AbstractThirdBillReconciliations {

    public AliPayReconciliations(String orgCode, String date) {
        super(orgCode, date);
    }

    @Override
    public List<ThirdBill> sourceList() {
       return super.sourceList(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
    }

    @Override
    public List<HisTransactionFlow> targetList() {
        return super.targetList(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
    }
}
