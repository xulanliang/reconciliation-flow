package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.util.List;

import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;

/**
 * @author swing
 * @date 2018年7月25日 下午2:09:08 类说明
 */
public class JuheBillParser<T> extends AbstractBillParser<T> {

	/**
	 * 聚合支付账单解析
	 */
	@Override
	protected List<T> doParse(String orgCode,String date) throws BillParseException {
		logger.info("聚合支付账单解析");
		return doParse(orgCode,date);
	}
}
