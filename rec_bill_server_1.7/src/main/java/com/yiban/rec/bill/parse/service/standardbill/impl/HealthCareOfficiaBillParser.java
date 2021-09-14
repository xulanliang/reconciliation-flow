package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.util.List;

import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.domain.HealthCareOfficial;

/**
 * @author swing
 * @date 2018年7月25日 上午10:02:36 类说明 医保账单解析器
 */
public  class HealthCareOfficiaBillParser extends AbstractBillParser<HealthCareOfficial> {

	/**
	 * 医保账单解析
	 */
	@Override
	protected List<HealthCareOfficial> doParse(String orgCode,String date)throws BillParseException  {
		logger.info("医保账单账单解析");
		throw new BillParseException("医保账单解析异常");
		//return null;
	}
	
}
