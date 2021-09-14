package com.yiban.rec.reconciliations.impl;

import java.util.List;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.reconciliations.AbstractThirdBillReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 武进一账通对账
 * @author admin
 *
 */
public class WJYZTReconciliations extends AbstractThirdBillReconciliations{
	
	public WJYZTReconciliations(String orgCode, String date) {
        super(orgCode, date);
    }
	@Override
	protected List<ThirdBill> sourceList() {
		return super.sourceList(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue());
	}

	@Override
	protected List<HisTransactionFlow> targetList() {
		return super.targetList(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue());
	}

}
