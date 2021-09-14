package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
/**
 * 社保卡银行卡对账
 * @author jxl
 * @Time 2020年11月3日下午3:52:02
 */
public class HealthCareBankReconciliations extends AbstractThirdBillReconciliations{

	public HealthCareBankReconciliations(String orgCode, String date) {
        super(orgCode, date);
    }
	
	@Override
	protected List<ThirdBill> sourceList() {
		return super.sourceList(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
	}

	@Override
	protected List<HisTransactionFlow> targetList() {
		return super.targetList(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
	}

}
