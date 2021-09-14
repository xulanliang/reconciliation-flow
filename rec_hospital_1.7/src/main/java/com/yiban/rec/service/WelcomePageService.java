package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import com.yiban.framework.account.domain.User;

public interface WelcomePageService {
	
	/**
	 * 电子对账
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> electronInfo(String orgCode);
	/**
	 * 渠道折线图
	 * @return
	 */
	List<Map<String, Object>> billSourceLine(String orgCode, String billSource);
	/**
	 * 获取对账日期
	 * @return
	 */
	String getRecDate();
	/**
	 * 近七天业务收入汇总
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> businessIncomeSummary(String orgCode);
	/**
	 * 近七天业务收入汇总折线图
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> businessIncomeChart(String orgCode);

	/**
	 * 近3月渠道收入汇总
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> initBillsRelateThreeMonthsIncomeData(String orgCode);
	/**
	 * 当日交易预警 
	 * @param orgCode
	 * @return
	 */
	String getWarningCount(String orgCode);
	/**
	 * 退款信息
	 * @param orgCode
	 * @param type 
	 * @param user 
	 * @return
	 */
	Map<String, Object> getRefundCount(String orgCode, String type, User user);
	/**
	 * 渠道名称分类占比
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> thridPie(String orgCode);
	/**
	 * 支付类型分类占比
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> payTypePie(String orgCode);
	/**
	 * 近七天收入分布情况
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> payTypeIncomeChart(String orgCode);
	/**
	 * 现金对账
	 * @param orgCode
	 * @return
	 */
	List<Map<String, Object>> cashInfo(String orgCode);
	/**
	 * 医保对账
	 * @param orgCode
	 * @return
	 */
	Map<String, Object> healthcareInfo(String orgCode);

	String getMonthName(String dateStr);

}
