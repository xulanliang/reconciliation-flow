package com.yiban.rec.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.service.ReportSummaryServiceForXiNingSanYuan;
import com.yiban.rec.service.base.BaseOprService;

/**
 * 报表汇总service
 */
@Service
public class ReportSummaryServiceImplForXiNingSanYuan extends BaseOprService
		implements ReportSummaryServiceForXiNingSanYuan {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSummaryServiceImplForXiNingSanYuan.class);
	@Autowired
	private OrganizationService organizationService;

	/**
	 * 获取子机构编码（如果有）
	 */
	private Set<String> initOrgCodeSet(String orgCode) {
		Set<String> orgCodeSet = new HashSet<>();
		if (orgCode != null) {
			orgCodeSet.add(orgCode);
			Organization org = organizationService.findByCode(orgCode);
			if (org != null) {
				for (Organization child : org.getChildren()) {
					orgCodeSet.add(child.getCode());
				}
			}
		}
		return orgCodeSet;
	}

	/**
	 * 支付方式和业务类型汇总报表
	 * 
	 */
	@Override
	public List<Map<String, Object>> findPayTypeAndBussinessTypeSummary(SummaryQuery query) {

		// 机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		for (String orgCode1 : orgCodeSet) {
			orgCodesb.append(orgCode1 + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length() - 1);

		// 时间处理
		String startTime = query.getBeginTime().trim();
		String endTime = query.getEndTime().trim();

		// 通过Device_No分组，说明是自助机汇总
		String deviceNoSql = "";
		if (StringUtils.isNotEmpty(query.getColumnSql()) && query.getColumnSql().indexOf("Device_No") > -1) {
			// 自助机汇总：cashier字段，并且cashier字段的数据等于device表的数据
			query.setColumnSql(" Cashier ");
			deviceNoSql = getDeviceNoSql(orgCodesb.toString());
		}
		// 查询sql拼接
		String sql = String.format("SELECT *,"
				+ "(drghWechatAmountAdd-drghWechatAmountSub) drghWechatAmount, (drghZfbAmountAdd-drghZfbAmountSub) drghZfbAmount,"
				+ "(drghBankAmountAdd-drghBankAmountSub) drghBankAmount, (drghCashAmountAdd-drghCashAmountSub) drghCashAmount ,"

				+ "(yyghWechatAmountAdd-yyghWechatAmountSub) yyghWechatAmount, (yyghZfbAmountAdd-yyghZfbAmountSub) yyghZfbAmount,"
				+ "(yyghBankAmountAdd-yyghBankAmountSub) yyghBankAmount, (yyghCashAmountAdd-yyghCashAmountSub) yyghCashAmount ,"

				+ "(jfWechatAmountAdd-jfWechatAmountSub) jfWechatAmount, (jfZfbAmountAdd-jfZfbAmountSub) jfZfbAmount,"
				+ "(jfBankAmountAdd-jfBankAmountSub) jfBankAmount, (jfCashAmountAdd-jfCashAmountSub) jfCashAmount ,"

				+ "(zyyjjWechatAmountAdd-zyyjjWechatAmountSub) zyyjjWechatAmount, (zyyjjZfbAmountAdd-zyyjjZfbAmountSub) zyyjjZfbAmount,"
				+ "(zyyjjBankAmountAdd-zyyjjBankAmountSub) zyyjjBankAmount, (zyyjjCashAmountAdd-zyyjjCashAmountSub) zyyjjCashAmount, "

				+ "(cyjsWechatAmountAdd-cyjsWechatAmountSub) cyjsWechatAmount, (cyjsZfbAmountAdd-cyjsZfbAmountSub) cyjsZfbAmount,"
				+ "(cyjsBankAmountAdd-cyjsBankAmountSub) cyjsBankAmount, (cyjsCashAmountAdd-cyjsCashAmountSub) cyjsCashAmount ,"

				+ " (allAmountAdd-allAmountSub) allAmount ,"

				+ "( drghAmountAdd-drghAmountSub) drghAmount,"
				+ "( jfAmountAdd-jfAmountSub) jfAmount," 
				+ "( yyghAmountAdd-yyghAmountSub) yyghAmount,"
				+ "( zyyjjAmountAdd-zyyjjAmountSub) zyyjjAmount," 
				+ "( cyjsAmountAdd-cyjsAmountSub) cyjsAmount"

				+ " from(select " + query.getColumnSql() + "businessType,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsCashAcount,"

				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " SUM(pay_acount) allAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjAcount,	"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsAcount"

				+ " FROM t_his_report " + " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ deviceNoSql + " GROUP BY businessType	"

				+ " UNION"

				+ " SELECT " + " '合计' businessType,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjCashAcount,"

				+ "SUM(CASE WHEN pay_type='0249' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsWechatAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0249' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsWechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsWechatAcount,"

				+ "SUM(CASE WHEN pay_type='0349' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsZfbAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0349' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsZfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsZfbAcount,"

				+ "SUM(CASE WHEN pay_type='0149' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsBankAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0149' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsBankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsBankAcount,"

				+ "SUM(CASE WHEN pay_type='0049' and order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsCashAmountAdd,"
				+ "SUM(CASE WHEN pay_type='0049' and order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsCashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' and pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsCashAcount,"

				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " SUM(pay_acount) allAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0451' THEN abs(pay_amount) ELSE 0 END) drghAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0451'  THEN pay_acount ELSE 0 END) drghAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0551' THEN abs(pay_amount) ELSE 0 END) jfAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0551'  THEN pay_acount ELSE 0 END) jfAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0851' THEN abs(pay_amount) ELSE 0 END) yyghAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0851'  THEN pay_acount ELSE 0 END) yyghAcount,"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='0751' THEN abs(pay_amount) ELSE 0 END) zyyjjAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='0751'  THEN pay_acount ELSE 0 END) zyyjjAcount,	"

				+ "SUM(CASE WHEN order_state='0156' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsAmountAdd,"
				+ "SUM(CASE WHEN order_state='0256' and pay_business_type='1251' THEN abs(pay_amount) ELSE 0 END) cyjsAmountSub,"
				+ "SUM(CASE WHEN pay_business_type='1251'  THEN pay_acount ELSE 0 END) cyjsAcount"

				+ " FROM t_his_report " + " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ deviceNoSql + "  ) t ", startTime, endTime, orgCodesb.toString(), startTime, endTime,
				orgCodesb.toString());

		LOGGER.info(" findPayTypeAndBussinessTypeSummary sql: " + sql);
		return super.queryList(sql, null, null);
	}

	public String getDeviceNoSql(String orgNoSql) {
		// String sql = String.format("SELECT t.device_no FROM t_device t WHERE t.org_no
		// IN (%s)", orgNoSql);
		// 此处自助机的编码从his表中获得，不需配置
		String sql = String.format("SELECT distinct t.Device_No FROM t_rec_histransactionflow t WHERE t.org_no IN (%s)",
				orgNoSql);
		List<Object> strList = super.handleNativeSql4SingleCol(sql, null);
		String cashierSql = " AND Cashier IN(";
		if (strList != null && strList.size() > 0) {
			for (Object str : strList) {
				cashierSql += "'" + str + "',";
			}
			cashierSql = cashierSql.substring(0, cashierSql.length() - 1) + ") ";
		} else {
			// 避免查询出cashier空的数据
			cashierSql = cashierSql + "'-1') ";
		}
		return cashierSql;
	}

}
