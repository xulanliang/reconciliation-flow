package com.yiban.rec.reconciliations;

/**
 * @author swing
 * @date 2018年7月20日 上午10:58:24
 * 应用层使用的对账接口(提供给 Controller使用)
 */
public interface ReconciliationsService {
	/**
	 * 对账接口
	 * @param orgCode
	 * @param date
	 * @throws Exception
	 */
	void compareBill(String orgCode, String date) throws Exception;
	
	
	void compareHealthBill(String orgCode, String date)throws Exception;
}
