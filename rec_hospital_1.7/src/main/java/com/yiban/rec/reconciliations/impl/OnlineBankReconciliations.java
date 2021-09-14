package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
/**
 * 网银对账
 * @author jxl
 * @Time 2020年11月16日上午10:11:50
 */
public class OnlineBankReconciliations extends AbstractThirdBillReconciliations {

	public OnlineBankReconciliations(String orgCode, String date) {
		super(orgCode, date);
	}

	@Override
	protected List<ThirdBill> sourceList() {
		return super.sourceList(EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue());
	}

	@Override
	protected List<HisTransactionFlow> targetList() {
		return super.targetList(EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue());
	}

}
