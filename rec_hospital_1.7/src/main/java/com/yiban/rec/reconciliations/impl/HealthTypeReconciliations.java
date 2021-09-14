package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

public class HealthTypeReconciliations extends AbstractThirdBillReconciliations {
	public HealthTypeReconciliations(String orgCode, String date) {
		super(orgCode, date);
	}

    @Override
    public List<ThirdBill> sourceList() {
        return super.sourceList(EnumTypeOfInt.PAY_TYPE_HEALTH.getValue());
    }

    @Override
    public List<HisTransactionFlow> targetList() {
        return super.targetList(EnumTypeOfInt.PAY_TYPE_HEALTH.getValue());
    }
}

