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
import com.yiban.rec.service.SummaryService;
import com.yiban.rec.service.base.BaseOprService;


/**
 * @author swing
 * @date 2018年8月8日 下午6:12:07 类说明 自助机结算实现
 * 基于统计的快速实现,初步完成 业务需求，不考虑优雅方案及性能问题(带需求整理明确后续可以优化)
 */
@Service
public class SummaryServiceImpl extends BaseOprService implements SummaryService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SummaryServiceImpl.class);
	
	@Autowired
	private OrganizationService organizationService;
	
	@Override
	public List<Map<String, Object>> findAllOfSummaryByPayType(SummaryByPayTypeVo query) {
        
		//机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		for (String orgCode : orgCodeSet) {
			orgCodesb.append(orgCode + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length()-1);
		//时间处理
		String startTime = query.getBeginTime() + " 00:00:00";
		String endTime = query.getEndTime() + " 23:59:59";
		String terminalNoSql = "";
		if(StringUtils.isNotBlank(query.getTerminalNo()) && !"admin".equals(query.getTerminalNo())){
			terminalNoSql = " AND terminal_no = '"+query.getTerminalNo()+"'";
		}
		//查询sql拼接
		String sql = String.format(
                  " SELECT *,(wechatAmountAdd-wechatAmountSub) wechatAmount,(zfbAmountAdd-zfbAmountSub) zfbAmount," 
				+ " (bankAmountAdd-bankAmountSub) bankAmount,(cashAmountAdd-cashAmountSub) cashAmount,"
				+ " (unionAmountAdd-unionAmountSub) unionAmount,(allAmountAdd-allAmountSub) allAmount"
                + " from ( select  "
				+ " if(ISNULL(terminal_no),IFNULL(Cashier,'未知'),terminal_no) terminalNo,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0249' THEN 1 ELSE null END) wechatAcount,"
				
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0349' THEN 1 ELSE null END) zfbAcount,"
				
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) bankAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) bankAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0149' THEN 1 ELSE null END) bankAcount,"
				
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) cashAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) cashAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0049' THEN 1 ELSE null END) cashAcount,"
				
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) unionAmountAdd,"
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) unionAmountSub,"
				+ " COUNT(CASE WHEN pay_type='1649'  THEN 1 ELSE null END) unionAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " COUNT(1) allAcount"
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
				+ terminalNoSql
				+ " GROUP BY terminalNo	"
				+ " UNION"
				+ " SELECT "
				+ " '合计' terminalNo,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0249' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0249' THEN 1 ELSE null END) wechatAcount,"
				
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0349' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0349' THEN 1 ELSE null END) zfbAcount,"
				
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) bankAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0149' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) bankAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0149' THEN 1 ELSE null END) bankAcount,"
				
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) cashAmountAdd,"
				+ " SUM(CASE WHEN pay_type='0049' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) cashAmountSub,"
				+ " COUNT(CASE WHEN pay_type='0049' THEN 1 ELSE null END) cashAcount,"
				
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) unionAmountAdd,"
				+ " SUM(CASE WHEN pay_type='1649' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) unionAmountSub,"
				+ " COUNT(CASE WHEN pay_type='1649'  THEN 1 ELSE null END) unionAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " COUNT(1) allAcount"
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
				+ terminalNoSql
				+ "  ) t "
				,startTime,endTime,orgCodesb.toString(),startTime,endTime,orgCodesb.toString());
		LOGGER.info(" findAllOfSummaryByPayType sql: " + sql);
		return super.queryList(sql, null, null);
	}
	
	/**
	 * 获取子机构编码（如果有）
	 * 
	 * @param orgCode
	 * @return
	 */
	private Set<String> initOrgCodeSet(String orgCode) {
		Set<String> orgCodeSet = new HashSet<>();
		if(orgCode != null){
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

	@Override
	public List<Map<String, Object>> findAllOfSummaryByBuisinessType(SummaryByPayTypeVo query) {
		//机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		for (String orgCode : orgCodeSet) {
			orgCodesb.append(orgCode + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length()-1);
		//时间处理
		String startTime = query.getBeginTime() + " 00:00:00";
		String endTime = query.getEndTime() + " 23:59:59";
		String terminalNoSql = "";
		if(StringUtils.isNotBlank(query.getTerminalNo()) && !"admin".equals(query.getTerminalNo())){
			terminalNoSql = " AND terminal_no = '"+query.getTerminalNo()+"'";
		}
		//查询sql拼接
		String sql = String.format(
				  " SELECT *,(registerAmountAdd-registerAmountSub) registerAmount,(makeAppointmentAmountAdd-makeAppointmentAmountSub) makeAppointmentAmount," 
				+ " (payAmountAdd-payAmountSub) payAmount,(clinicAmountAdd-clinicAmountSub) clinicAmount,"
				+ " (prepaymentForHospitalizationAmountAdd-prepaymentForHospitalizationAmountSub) prepaymentForHospitalizationAmount,(allAmountAdd-allAmountSub) allAmount"
                + " from ( select  "
				+ " if(ISNULL(terminal_no),IFNULL(Cashier,'未知'),terminal_no) terminalNo,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) registerAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) registerAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0451' THEN 1 ELSE null END) registerAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0851' THEN 1 ELSE null END) makeAppointmentAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) payAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) payAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0551' THEN 1 ELSE null END) payAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0151' THEN 1 ELSE null END) clinicAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0751' THEN 1 ELSE null END) prepaymentForHospitalizationAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " COUNT(1) allAcount"
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
				+ terminalNoSql
				+ " GROUP BY terminalNo	"
				+ " UNION"
				+ " SELECT "
				+ " '合计' terminalNo,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) registerAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0451' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) registerAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0451' THEN 1 ELSE null END) registerAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0851' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) makeAppointmentAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0851' THEN 1 ELSE null END) makeAppointmentAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) payAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) payAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0551' THEN 1 ELSE null END) payAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) clinicAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0151' THEN 1 ELSE null END) clinicAcount,"
				
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0751' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) prepaymentForHospitalizationAmountSub,"
				+ " COUNT(CASE WHEN Pay_Business_Type='0751' THEN 1 ELSE null END) prepaymentForHospitalizationAcount,"
				
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) allAmountAdd,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) allAmountSub,"
				+ " COUNT(1) allAcount"
				
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
				+ terminalNoSql
				+ "  ) t "
				,startTime,endTime,orgCodesb.toString(),startTime,endTime,orgCodesb.toString());
		LOGGER.info(" findAllOfSummaryByBuisinessType sql: " + sql);
		return super.queryList(sql, null, null);
	}

	@Override
	public List<Map<String, Object>> findAllOfSummaryByDay(SummaryByPayTypeVo query) {
		//机构处理
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		StringBuilder orgCodesb = new StringBuilder();
		orgCodesb.append(query.getOrgCode() + ",");
		for (String orgCode : orgCodeSet) {
			orgCodesb.append(orgCode + ",");
		}
		orgCodesb.deleteCharAt(orgCodesb.length()-1);
		//时间处理
		String startTime = query.getBeginTime() + " 00:00:00";
		String endTime = query.getEndTime() + " 23:59:59";
		String dateType = "%Y-%m-%d";
		if(query.getDateType() != null && query.getDateType().equals("year")){
			dateType = "%Y";
		}else if(query.getDateType() != null && query.getDateType().equals("month")){
			dateType = "%Y-%m";
		}
		
		//查询sql拼接
		String sql = 
                  " SELECT * ,(hisAllAmount - hisRegisterAmount - hisRechargeAmount - hisPayAmount ) hisOtherAmount,  "
				+ " (wechatAllAmount - wechatRegisterAmount - wechatRechargeAmount - wechatPayAmount ) wechatOtherAmount, "
				+ " (aliAllAmount - aliRegisterAmount - aliRechargeAmount - aliPayAmount ) aliOtherAmount, "
				+ " (bankAllAmount - bankRegisterAmount - bankRechargeAmount - bankPayAmount ) bankOtherAmount,"
				
				+ " (hisAllAmount - wechatAllAmount - aliAllAmount - bankAllAmount ) otherAllAmount,"
				+ " (hisRegisterAmount - wechatRegisterAmount - aliRegisterAmount - bankRegisterAmount ) otherRegisterAmount,"
				+ " (hisRechargeAmount - wechatRechargeAmount - aliRechargeAmount - bankRechargeAmount ) otherRechargeAmount,"
				+ " (hisPayAmount - wechatPayAmount - aliPayAmount - bankPayAmount ) otherPayAmount FROM ( "
				
				+ " SELECT *,(hisRegisterAmountAdd-hisRegisterAmountSub) hisRegisterAmount,"
				+ " (hisRechargeAmountAdd-hisRechargeAmountSub) hisRechargeAmount,"
				+ " (hisPayAmountAdd-hisPayAmountSub) hisPayAmount,"
				+ " (hisAllAmountAdd-hisAllAmountSub) hisAllAmount,"
				
				+ " (wechatRegisterAmountAdd-wechatRegisterAmountSub) wechatRegisterAmount,"
				+ " (wechatRechargeAmountAdd-wechatRechargeAmountSub) wechatRechargeAmount,"
				+ " (wechatPayAmountAdd-wechatPayAmountSub) wechatPayAmount,"
				+ " (wechatAllAmountAdd-wechatAllAmountSub) wechatAllAmount,"
				
				+ " (aliRegisterAmountAdd-aliRegisterAmountSub) aliRegisterAmount,"
				+ " (aliRechargeAmountAdd-aliRechargeAmountSub) aliRechargeAmount,"
				+ " (aliPayAmountAdd-aliPayAmountSub) aliPayAmount,"
				+ " (aliAllAmountAdd-aliAllAmountSub) aliAllAmount,"
				
				+ " (bankRegisterAmountAdd-bankRegisterAmountSub) bankRegisterAmount,"
				+ " (bankRechargeAmountAdd-bankRechargeAmountSub) bankRechargeAmount,"
				+ " (bankPayAmountAdd-bankPayAmountSub) bankPayAmount,"
				+ " (bankAllAmountAdd-bankAllAmountSub) bankAllAmount"
				
				+ " FROM ( "
				+ " SELECT DATE_FORMAT(Trade_datatime,'"+dateType+"') tradeTime, "
				// his
				+ " SUM(CASE WHEN (Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisRegisterAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisRechargeAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisPayAmountAdd,"
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisAllAmountAdd,"
				
				+ " SUM(CASE WHEN (Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisRegisterAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisRechargeAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisPayAmountSub,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisAllAmountSub,"
				
				// wechat
				+ " SUM(CASE WHEN (Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0249' THEN abs(Pay_Amount) ELSE 0 END) wechatRegisterAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0249' THEN abs(Pay_Amount) ELSE 0 END) wechatRechargeAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0249' THEN abs(Pay_Amount) ELSE 0 END) wechatPayAmountAdd,"
				+ " SUM(CASE WHEN pay_type = '0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0249' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) wechatAllAmountSub,"
				
				// ali
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRegisterAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRechargeAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliPayAmountAdd,"
				+ " SUM(CASE WHEN (pay_type = '0349' and order_state='0156') THEN abs(Pay_Amount) ELSE 0 END) aliAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0349' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) aliAllAmountSub,"
				
				// bank
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRegisterAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRechargeAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankPayAmountAdd,"
				+ " SUM(CASE WHEN (pay_type = '0149' and order_state='0156') THEN abs(Pay_Amount) ELSE 0 END) bankAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0149' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) bankAllAmountSub"
				
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '"+startTime+"' and Trade_datatime<= '"+endTime+"' AND org_no IN("+orgCodesb.toString()+")"
				+ " GROUP BY tradeTime "
				
				+ " union" 
				
				+ " SELECT '合计', "
				// his
				+ " SUM(CASE WHEN (Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisRegisterAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisRechargeAmountAdd,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisPayAmountAdd,"
				+ " SUM(CASE WHEN order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) hisAllAmountAdd,"
				
				+ " SUM(CASE WHEN (Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisRegisterAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0151' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisRechargeAmountSub,"
				+ " SUM(CASE WHEN Pay_Business_Type='0551' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisPayAmountSub,"
				+ " SUM(CASE WHEN order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) hisAllAmountSub,"
				
				// wechat
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRegisterAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRechargeAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatPayAmountAdd,"
				+ " SUM(CASE WHEN (pay_type = '0249' and order_state='0156') THEN abs(Pay_Amount) ELSE 0 END) wechatAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0249') THEN abs(Pay_Amount) ELSE 0 END) wechatPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0249' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) wechatAllAmountSub,"
				
				// ali
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRegisterAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRechargeAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliPayAmountAdd,"
				+ " SUM(CASE WHEN (pay_type = '0349' and order_state='0156') THEN abs(Pay_Amount) ELSE 0 END) aliAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0349') THEN abs(Pay_Amount) ELSE 0 END) aliPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0349' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) aliAllAmountSub,"
				
				// bank
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRegisterAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRechargeAmountAdd,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0156' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankPayAmountAdd,"
				+ " SUM(CASE WHEN (pay_type = '0149' and order_state='0156') THEN abs(Pay_Amount) ELSE 0 END) bankAllAmountAdd,"
				
				+ " SUM(CASE WHEN ((Pay_Business_Type='0451' or Pay_Business_Type='0851') and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRegisterAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0151' and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankRechargeAmountSub,"
				+ " SUM(CASE WHEN (Pay_Business_Type='0551' and order_state='0256' AND pay_type = '0149') THEN abs(Pay_Amount) ELSE 0 END) bankPayAmountSub,"
				+ " SUM(CASE WHEN (pay_type = '0149' and order_state='0256') THEN abs(Pay_Amount) ELSE 0 END) bankAllAmountSub"
				
				+ " FROM t_rec_histransactionflow "
				+ " WHERE Trade_datatime >= '"+startTime+"' and Trade_datatime<= '"+endTime+"' AND org_no IN("+orgCodesb.toString()+")"
				+ " ) t ) t"
				;
		LOGGER.info(" findAllOfSummaryByDay sql: " + sql);
		return super.queryList(sql, null, null);
	}
}
