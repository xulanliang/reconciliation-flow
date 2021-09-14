package com.yiban.rec.emailbill.service;



/**
* @author swing
* @date 2018年6月25日 下午2:57:24
* 类说明邮件账单解析
*/
public interface EmailBillService {
	
	/**
	 * 邮件账单解析
	 * @param beginDate
	 * @param endDate
	 */
	void parseBill(String beginDate, String endDate);
	
}
