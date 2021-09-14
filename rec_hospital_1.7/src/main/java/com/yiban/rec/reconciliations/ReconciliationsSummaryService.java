package com.yiban.rec.reconciliations;
/**
* @author swing
* @date 2018年7月19日 下午5:25:33
* 类说明
* 对账汇总接口
*/
public interface ReconciliationsSummaryService {

	/**
	 * 删除汇总t_follow_summary记录
	 * @param orgCode
	 * @param date
	 */
	public void delete(String orgCode,String date);
	/**
	 * 当日汇总数据写入t_follow_summary表
	 * @param orgCode
	 * @param date
	 */
	public void summary(String orgCode,String date);
}
