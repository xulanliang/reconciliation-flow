package com.yiban.rec.emailbill.service;

import java.util.List;

import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;

/**
* @author swing
* @date 2018年6月25日 上午11:01:37
* 类说明  账单服务接口
*/
public interface ThirdBillService {
	/**
	 * 批量写入账单
	 * @param list
	 */
	void batchInsertThirdBill(List<ThirdBill> list);
	
	void batchInsertBill(List<ThirdBill> list)throws Exception;
	
	public void batchInsertHis(List<HisTransactionFlow> list)throws Exception;
	public void batchInsertPay(List<HisPayResult> list)throws Exception;
	/**
	 * 按条件删除账单
	 * @param startTime
	 * @param endTime
	 * @param orgCodes
	 * @param payType
	 * @return
	 */
	int delete(String startTime,String endTime,List<String> orgCodes,String payType);
	
	/**
	 * 按条件删除账单
	 * @param startTime
	 * @param endTime
	 * @param orgCodes
	 * @param payType
	 * @param billSource
	 * @return
	 * int
	 */
	int delete(String startTime,String endTime,List<String> orgCodes,String payType, String billSource);
	
	/**
	 * 查询账单
	 * @param orderNo
	 * @param orderType
	 * @return
	 */
	public List<ThirdBill>  queryThrdBill(String orderNo,String orderType);
	
	/**
	 * 查询账单
	 * @param orderNo
	 * @return
	 */
	public List<ThirdBill>  queryThrdBillByOrderNo(String orderNo);
	
	public int delete(String startTime, String endTime, String orgCode)throws Exception;
	
	List<ThirdBill> findByPayFlowNo(String paramString);
	
}
