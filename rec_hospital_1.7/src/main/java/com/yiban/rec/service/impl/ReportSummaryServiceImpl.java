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
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.service.base.BaseOprService;

/**
 * 报表汇总service
 */
@Service
public class ReportSummaryServiceImpl extends BaseOprService implements ReportSummaryService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSummaryServiceImpl.class);
	@Autowired
	private OrganizationService organizationService;
	
	/**
	 * 处理业务类型汇总报表
	 */
	@Override
	public List<Map<String, Object>> findAllOfSummaryByBusinessType(SummaryQuery query) {
		//机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		for (String orgCode : orgCodeSet) {
			orgCodesb.append(orgCode + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length()-1);
		
		//时间处理
		String startTime = query.getBeginTime().trim();
		String endTime = query.getEndTime().trim();
		
		// 处理payType存在的情况
		String paytypeSql = "";
		if (StringUtils.isNotEmpty(query.getPayType())) {
			paytypeSql = String.format(" and pay_type = '%s'", query.getPayType());
		}
		
		// 通过Device_No分组，说明是自助机汇总
		String deviceNoSql ="";
		if (StringUtils.isNotEmpty(query.getColumnSql()) && query.getColumnSql().indexOf("Device_No") > -1) {
			// 自助机汇总：cashier字段，并且cashier字段的数据等于device表的数据
			query.setColumnSql(" Cashier ");
			deviceNoSql = getDeviceNoSql(orgCodesb.toString());
		}
		
		//查询sql拼接
		String sql = String.format(
				  " SELECT *,(registerAmountAdd-registerAmountSub) registerAmount,(makeAppointmentAmountAdd-makeAppointmentAmountSub) makeAppointmentAmount," 
				+ " (payAmountAdd-payAmountSub) payAmount,(clinicAmountAdd-clinicAmountSub) clinicAmount,"
				+ " (prepaymentForHospitalizationAmountAdd-prepaymentForHospitalizationAmountSub) prepaymentForHospitalizationAmount,"
				+ " (allAmountAdd-allAmountSub) allAmount "
                + " from ( select  "
				+  query.getColumnSql() + " businessType,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) registerAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) registerAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' THEN pay_acount ELSE 0 END) registerAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' THEN pay_acount ELSE 0 END) makeAppointmentAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) payAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) payAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' THEN pay_acount ELSE 0 END) payAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' THEN pay_acount ELSE 0 END) clinicAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' THEN pay_acount ELSE 0 END) prepaymentForHospitalizationAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " SUM(pay_acount) allAcount"
				+ " FROM t_his_report "
				+ " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ paytypeSql
				+ deviceNoSql
				+ " GROUP BY businessType"
				
				+ " UNION"
				
				+ " SELECT "
				+ " '合计' businessType,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) registerAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) registerAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' THEN pay_acount ELSE 0 END) registerAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' THEN pay_acount ELSE 0 END) makeAppointmentAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) payAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) payAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' THEN pay_acount ELSE 0 END) payAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' THEN pay_acount ELSE 0 END) clinicAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' THEN pay_acount ELSE 0 END) prepaymentForHospitalizationAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " SUM(pay_acount) allAcount"
				
				+ " FROM t_his_report "
				+ " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ paytypeSql
				+ deviceNoSql
				+ "  ) t "
				,startTime,endTime,orgCodesb.toString(),startTime,endTime,orgCodesb.toString());
		LOGGER.info(" findAllOfSummaryByBusinessType sql: " + sql);
		
		return super.queryList(sql, null, null);
	}

	/**
	 * 处理支付方式汇总报表
	 */
	public List<Map<String, Object>> findAllOfSummaryByPayType(SummaryQuery query) {
		//机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		for (String orgCode1 : orgCodeSet) {
			orgCodesb.append(orgCode1 + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length()-1);
		
		//时间处理
		String startTime = query.getBeginTime().trim();
		String endTime = query.getEndTime().trim();
		
		// 处理业务类型的sql
		String patTypeSql = "";
		if (StringUtils.isNotEmpty(query.getPatType())) {
			patTypeSql = String.format(" AND pat_type='%s' ", query.getPatType());
		}
		// 通过Device_No分组，说明是自助机汇总
		String deviceNoSql = "";
		if (StringUtils.isNotEmpty(query.getColumnSql()) && query.getColumnSql().indexOf("Device_No") > -1) {
			// 自助机汇总：cashier字段，并且cashier字段的数据等于device表的数据
			query.setColumnSql(" Cashier ");
			deviceNoSql = getDeviceNoSql(orgCodesb.toString());
		}
		//查询sql拼接
		String sql = String.format(
                  " SELECT *,"
                + "	(wechatAmountAdd-wechatAmountSub) wechatAmount, 	(zfbAmountAdd-zfbAmountSub) zfbAmount," 
				+ " (bankAmountAdd-bankAmountSub) bankAmount, 		(cashAmountAdd-cashAmountSub) cashAmount,"
				+ " (unionAmountAdd-unionAmountSub) unionAmount, 	(yibaoAmountAdd-yibaoAmountSub) yibaoAmount "
                + " from ( select  "
				+ query.getColumnSql() + " businessType,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' THEN pay_acount ELSE 0 END) wechatAcount,"
				
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' THEN pay_acount ELSE 0 END) zfbAcount,"
				
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) bankAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) bankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' THEN pay_acount ELSE 0 END) bankAcount,"
				
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) cashAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) cashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' THEN pay_acount ELSE 0 END) cashAcount,"
				
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) unionAmountAdd,"
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) unionAmountSub,"
				+ " SUM(CASE WHEN pay_type='1649'  THEN pay_acount ELSE 0 END) unionAcount,"
				
				+ " SUM(CASE WHEN pay_type='0559' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) yibaoAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0559' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) yibaoAmountSub,"
				+ " SUM(CASE WHEN pay_type='0559'  THEN pay_acount ELSE 0 END) yibaoAcount "
				
				+ " FROM t_his_report "
				+ " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ patTypeSql
				+ deviceNoSql
				+ " GROUP BY businessType	"
				
				+ " UNION"
				
				+ " SELECT "
				+ " '合计' businessType,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountSub,"
				+ " SUM(CASE WHEN pay_type='0249' THEN pay_acount ELSE 0 END) wechatAcount,"
				
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountSub,"
				+ " SUM(CASE WHEN pay_type='0349' THEN pay_acount ELSE 0 END) zfbAcount,"
				
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) bankAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) bankAmountSub,"
				+ " SUM(CASE WHEN pay_type='0149' THEN pay_acount ELSE 0 END) bankAcount,"
				
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) cashAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) cashAmountSub,"
				+ " SUM(CASE WHEN pay_type='0049' THEN pay_acount ELSE 0 END) cashAcount,"
				
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) unionAmountAdd,"
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) unionAmountSub,"
				+ " SUM(CASE WHEN pay_type='1649'  THEN pay_acount ELSE 0 END) unionAcount,"
				
				+ " SUM(CASE WHEN pay_type='0559' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) yibaoAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0559' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) yibaoAmountSub, "
				+ " SUM(CASE WHEN pay_type='0559' THEN pay_acount ELSE 0 END) yibaoAcount "
				
				+ " FROM t_his_report "
				+ " WHERE trade_date >= '%s' and trade_date<='%s' AND org_code IN(%s)"
				+ patTypeSql
				+ deviceNoSql
				+ "  ) t "
				,startTime,endTime,orgCodesb.toString(),startTime,endTime,orgCodesb.toString());
		LOGGER.info(" findAllOfSummaryByPayType sql: " + sql);
		return super.queryList(sql, null, null);
	}
	
	/**
	 * 支付方式报表
	 * 
	 * @param query
	 * @param       payType：参考pay_type字典
	 * @return
	 */
	public List<Map<String, Object>> findPayTypeSummary(SummaryQuery query) {
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
		
		// 支付类型
		String payType = query.getPayType();

		//查询sql拼接
		String sql = String.format("SELECT t.*,  " + 
		"(t.allAmount-registerAmount-makeAppointmentAmount-payAmount-clinicAmount-prepaymentForHospitalizationAmount) otherAmount, " + 
		"(t.allAcount-registerAcount-makeAppointmentAcount-payAcount-clinicAcount-prepaymentForHospitalizationAcount) otherAcount " + 
		"FROM ( " + 
		" SELECT    " + 
		" '支付' businessType,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0451' AND order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) registerAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0451' AND order_state='0156' THEN pay_acount ELSE 0 END) registerAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0851' AND order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) makeAppointmentAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0851' AND order_state='0156' THEN pay_acount ELSE 0 END) makeAppointmentAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0551' AND order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) payAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0551' AND order_state='0156' THEN pay_acount ELSE 0 END) payAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0151' AND order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) clinicAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0151' AND order_state='0156' THEN pay_acount ELSE 0 END) clinicAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0751' AND order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0751' AND order_state='0156' THEN pay_acount ELSE 0 END) prepaymentForHospitalizationAcount,  " + 
		" SUM(CASE WHEN order_state='0156' THEN ABS(Pay_Amount) ELSE 0 END) allAmount,  " + 
		" SUM(CASE WHEN order_state='0156' THEN pay_acount ELSE 0 END) allAcount  " + 
		" FROM t_his_report  WHERE trade_date >= '%s' AND trade_date<='%s' AND org_code IN(%s) AND pay_type='%s' " + 
		"  UNION " + 
		"  SELECT    " + 
		" '退款' businessType,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0451' AND order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) registerAmount, " + 
		" SUM(CASE WHEN Pay_Business_Type='0451' AND order_state='0256' THEN pay_acount ELSE 0 END) registerAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0851' AND order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) makeAppointmentAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0851' AND order_state='0256' THEN pay_acount ELSE 0 END) makeAppointmentAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0551' AND order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) payAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0551' AND order_state='0256' THEN pay_acount ELSE 0 END) payAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0151' AND order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) clinicAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0151' AND order_state='0256' THEN pay_acount ELSE 0 END) clinicAcount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0751' AND order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmount,  " + 
		" SUM(CASE WHEN Pay_Business_Type='0751' AND order_state='0256' THEN pay_acount ELSE 0 END) prepaymentForHospitalizationAcount,  " + 
		" SUM(CASE WHEN order_state='0256' THEN ABS(Pay_Amount) ELSE 0 END) allAmount,  " + 
		" SUM(CASE WHEN order_state='0256' THEN pay_acount ELSE 0 END) allAcount  " + 
		" FROM t_his_report  WHERE trade_date >= '%s' AND trade_date<='%s' AND org_code IN(%s) AND pay_type='%s' " + 
		"UNION  " + 
		"  SELECT  '合计' businessType,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0451' AND order_state='0156' THEN ABS(Pay_Amount) WHEN Pay_Business_Type='0451' AND order_state='0256' THEN -ABS(pay_amount) ELSE 0 END) registerAmount, " + 
		"  SUM(CASE WHEN Pay_Business_Type='0451' AND (order_state='0156' OR order_state='0256') THEN pay_acount ELSE 0 END) registerAcount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0851' AND order_state='0156' THEN ABS(Pay_Amount) WHEN Pay_Business_Type='0851' AND order_state='0256' THEN -ABS(pay_amount) ELSE 0 END) makeAppointmentAmount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0851' AND (order_state='0156' OR order_state='0256') THEN pay_acount ELSE 0 END) makeAppointmentAcount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0551' AND order_state='0156' THEN ABS(Pay_Amount) WHEN Pay_Business_Type='0551' AND order_state='0256' THEN -ABS(Pay_Amount) ELSE 0 END) payAmount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0551' AND (order_state='0156' OR order_state='0256') THEN pay_acount ELSE 0 END) payAcount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0151' AND order_state='0156' THEN ABS(Pay_Amount) WHEN Pay_Business_Type='0151' AND order_state='0256' THEN -ABS(Pay_Amount) ELSE 0 END) clinicAmount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0151' AND (order_state='0156' OR order_state='0256') THEN pay_acount ELSE 0 END) clinicAcount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0751' AND order_state='0156' THEN ABS(Pay_Amount) WHEN Pay_Business_Type='0751' AND order_state='0256' THEN -ABS(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmount,  " + 
		"  SUM(CASE WHEN Pay_Business_Type='0751' AND (order_state='0156' OR order_state='0256') THEN pay_acount ELSE 0 END) prepaymentForHospitalizationAcount,  " + 
		"  SUM(CASE WHEN order_state='0156' THEN ABS(Pay_Amount) WHEN order_state='0256' THEN -ABS(pay_amount) ELSE 0 END) allAmount,  " + 
		"  SUM(CASE WHEN order_state='0156' OR order_state='0256' THEN pay_acount ELSE 0 END) allAcount " + 
		"   FROM t_his_report  WHERE trade_date >= '%s' AND trade_date<='%s' AND org_code IN(%s) AND pay_type ='%s') t ",
				startTime, endTime, orgCodesb.toString(), payType , 
				startTime, endTime, orgCodesb.toString(), payType,
				startTime, endTime, orgCodesb.toString(), payType);
		LOGGER.info(" findPayTypeSummary sql: " + sql);
		return super.queryList(sql, null, null);
	}
	
	/**
	 * 病历本报表
	 */
	@Override
	public List<Map<String, Object>> findBlbSummary(SummaryQuery query) {
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
		
		//查询sql拼接
		String sql = String.format("SELECT" + 
				"  t.`pay_type` businessType," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-001' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp001PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-001' THEN t.`pay_acount` ELSE 0 END) sdp001Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-002' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp002PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-002' THEN t.`pay_acount` ELSE 0 END) sdp002Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-003' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp003PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-003' THEN t.`pay_acount` ELSE 0 END) sdp003Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-004' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp004PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-004' THEN t.`pay_acount` ELSE 0 END) sdp004Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-005' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp005PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-005' THEN t.`pay_acount` ELSE 0 END) sdp005Acount" + 
				" FROM t_his_report t WHERE t.`trade_date` >= '%s' AND t.`trade_date` <= '%s' AND t.`org_code` IN (%s)" + 
				"	AND t.`bill_source`='blb' AND t.`terminal_no` IS NOT NULL GROUP BY businessType" + 
				" UNION ALL " + 
				" SELECT " + 
				"  '合计' businessType," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-001' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp001PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-001' THEN t.`pay_acount` ELSE 0 END) sdp001Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-002' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp002PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-002' THEN t.`pay_acount` ELSE 0 END) sdp002Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-003' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp003PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-003' THEN t.`pay_acount` ELSE 0 END) sdp003Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-004' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp004PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-004' THEN t.`pay_acount` ELSE 0 END) sdp004Acount," + 
				"  SUM(CASE WHEN t.`terminal_no` = 'SDP-7M-005' THEN IF(t.`order_state`='0156', ABS(t.`pay_amount`), -ABS(t.`pay_amount`))ELSE 0 END) sdp005PayAmount," + 
				"  SUM(CASE WHEN t.`terminal_no`='SDP-7M-005' THEN t.`pay_acount` ELSE 0 END) sdp005Acount" + 
				" FROM t_his_report t WHERE t.`trade_date` >= '%s' AND t.`trade_date` <= '%s' AND t.`org_code` IN (%s) AND t.`bill_source`='blb'" +
				" AND t.`terminal_no` IS NOT NULL",
				startTime, endTime, orgCodesb, 
				startTime, endTime, orgCodesb);
		LOGGER.info(" findBlbSummary sql: " + sql);
		return super.queryList(sql, null, null);
	}
	
	public String getDeviceNoSql(String orgNoSql) {
		String sql = String.format("SELECT t.device_no FROM t_device t WHERE t.org_no IN (%s)", orgNoSql);
		//此处自助机的编码从his表中获得，不需配置
		//String sql = String.format("SELECT distinct t.Device_No FROM t_rec_histransactionflow t WHERE t.org_no IN (%s)", orgNoSql);
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
}
