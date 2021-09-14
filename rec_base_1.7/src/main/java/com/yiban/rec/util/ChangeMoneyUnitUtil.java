package com.yiban.rec.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ChangeMoneyUnitUtil {

	/**
	 * 分转元
	 * @param feeAmt
	 * @return
	 */
	public static BigDecimal fenToYuan(String feeAmt) {
		BigDecimal feeNo = new BigDecimal(feeAmt);
    	feeNo = feeNo.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		return feeNo;
	}
}
