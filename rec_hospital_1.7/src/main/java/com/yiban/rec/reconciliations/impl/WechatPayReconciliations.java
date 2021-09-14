package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 微信对账
 * @author swing
 * @date 2018年7月19日 下午2:14:48 类说明
 */
public class WechatPayReconciliations extends AbstractThirdBillReconciliations {
    
    public WechatPayReconciliations(String orgCode, String date) {
        super(orgCode, date);
    }

    @Override
    public List<ThirdBill> sourceList() {
        return super.sourceList(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
    }

    @Override
    public List<HisTransactionFlow> targetList() {
        return super.targetList(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
    }
}
