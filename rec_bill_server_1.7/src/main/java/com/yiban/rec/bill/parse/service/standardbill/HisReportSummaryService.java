package com.yiban.rec.bill.parse.service.standardbill;

public interface HisReportSummaryService {

	public void hisSummary(String orgCode, String date);

	/**
	 * 删除汇总t_follow_summary记录
	 * 
	 * @param orgCode
	 * @param date
	 */
	public void delete(String orgCode, String date);

	/**
	 * 当日汇总数据写入t_follow_summary表
	 * 
	 * @param orgCode
	 * @param date
	 */
	public void summary(String orgCode, String date);
}
