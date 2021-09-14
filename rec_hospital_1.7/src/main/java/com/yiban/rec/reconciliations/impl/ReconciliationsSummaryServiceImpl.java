package com.yiban.rec.reconciliations.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.reconciliations.ReconciliationsSummaryService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.base.BaseOprService;

/**
 * @author swing
 * @date 2018年7月19日 下午5:26:41 
 * 对账汇总实现
 * 
 */
@Service
public class ReconciliationsSummaryServiceImpl extends BaseOprService implements ReconciliationsSummaryService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	/**
	 * 删除当日汇总数据
	 */
	@Transactional(rollbackFor=Exception.class)
	@Override
	public void delete(String orgCode, String date) {
		final String sql = String.format("delete from t_follow_summary where trade_date='%s'and org_no='%s'", date,orgCode);
		super.execute(sql);
	}

	/**
	 * 重新写入汇总数据
	 */
	@Transactional(rollbackFor=Exception.class)
	@Override
	public void summary(String orgCode, String date) {
		//父机构，需要将子机构一起查询
		Set<String> orgCodeSet = new HashSet<>();
		orgCodeSet.add(orgCode);
		Organization org = organizationService.findByCode(orgCode);
		if (org != null) {
			for (Organization child : org.getChildren()) {
				orgCodeSet.add(child.getCode());
			}
		}
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		String payType=runtimeConfig.getRecType();
		
		//日期拼接
		Long start = System.currentTimeMillis(); 
		String startTime = date + " 00:00:00";
		String endTime = date + " 23:59:59";
		String orgCodes = StringUtils.join(orgCodeSet);
		final String str ="insert into t_follow_summary(org_no,trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state,pay_amount,pay_acount,settlement_amount)"
						+ "select '%s' org_no,A.* from ("
						+ "SELECT DATE(Trade_datatime) trade_date,bill_source,'his' data_source, pay_location,pat_type,pay_type AS rec_pay_type,pay_type,order_state, SUM(ABS(Pay_Amount)) pay_amount ,COUNT(1) pay_acount,0 FROM "
						+ "t_rec_histransactionflow WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' "
						+ "AND pay_type IN ('0049',"+payType+") AND org_no IN(%s) "
						+ "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
						+ "UNION "
						+ "SELECT DATE(settlement_date) trade_date,bill_source,'his' data_source, pay_location,pat_type,pay_type AS rec_pay_type,pay_type,order_state, 0,COUNT(1) pay_acount, SUM(ABS(Pay_Amount)) pay_amount FROM "
						+ "t_rec_histransactionflow WHERE settlement_date >= '%s' and settlement_date<='%s' "
						+ "AND pay_type IN ('0049',"+payType+") AND org_no IN(%s) "
						+ "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
						+ "UNION "
						+ "SELECT DATE(Trade_datatime) trade_date,bill_source,'third' data_source,'' pay_location,pat_type, rec_pay_type, pay_type ,order_state,SUM(ABS(Pay_Amount)) pay_amount,COUNT(1) pay_acount,0 FROM  "
						+ "t_thrid_bill WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s) "
						+ "AND rec_pay_type IN ('jdwechat','ccbbank','pfbank',"+payType+") "
						+ "AND Pay_Flow_No NOT IN ( " + substractAutoRefundNum(date) + " ) "
						+ "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
						+ "UNION "
						+ "SELECT DATE(Trade_datatime) trade_date,bill_source,'third' data_source, pay_location,pat_type,pay_type AS rec_pay_type,pay_type, order_state,SUM(ABS(Pay_Amount)) pay_amount,COUNT(1) pay_acount,0 FROM "
						+ "t_rec_cash WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s) "
						+ "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
						+ ")A";
		String sql = String.format(str,
				orgCode, startTime, endTime, orgCodes, startTime, endTime, orgCodes, startTime, endTime, orgCodes,
				startTime, endTime, orgCodes);
		log.info(" ReconciliationsSummaryServiceImpl.summary: " + sql);
		int count = super.execute(sql);
		log.info("汇总summary耗时：" + (System.currentTimeMillis() - start) + ", 汇总条数：" + count);
	}
	
	public String substractAutoRefundNum(String payDate){
		String endPayDate = payDate+" 23:59:59";
//		String sql = " SELECT Pay_Flow_No FROM t_thrid_bill f "+
//				" WHERE Trade_datatime >= '"+payDate+"' AND Trade_datatime <= '"+endPayDate+"' "+
//				" GROUP BY Pay_Flow_No, shop_flow_no, order_no, out_trade_no,org_no, ABS(Pay_Amount) "+
//				" HAVING COUNT(*) = 2 "+
//				" AND  NOT EXISTS "+
//				" ( SELECT 1 FROM t_rec_histransactionflow t "+
//				" WHERE t.Trade_datatime >= '"+payDate+"' AND t.Trade_datatime <= '"+endPayDate+"' "+
//				" AND  f.org_no = t.org_no " +
//				" AND (f.Pay_Flow_No = t.Pay_Flow_No OR f.shop_flow_no = t.Pay_Flow_No OR f.order_no = t.Pay_Flow_No OR f.out_trade_no = t.Pay_Flow_No) "+
//				" GROUP BY t.Pay_Flow_No, ABS(t.Pay_Amount) "+
//				" HAVING COUNT(*) = 2 ) ";
		
		String sql = "SELECT f.Pay_Flow_No " + 
		        "    FROM t_thrid_bill f   " + 
		        "    WHERE f.Trade_datatime >= '"+ payDate +"' " + 
		        "    AND f.Trade_datatime <= '"+ endPayDate +"'  " + 
		        "    AND f.Order_State = '0256'" + 
		        "    AND f.Pay_Flow_No NOT IN" + 
		        "    (" + 
		        "    SELECT c.business_no Pay_Flow_No FROM t_trade_check_follow c" + 
		        "    WHERE c.Trade_time >='"+ payDate+ "'" + 
		        "    AND c.Trade_time <= '"+ endPayDate +"'" + 
		        "    )" + 
		        "    AND NOT EXISTS (" + 
		        "    SELECT 1 FROM t_rec_histransactionflow t" + 
		        "    WHERE t.Trade_datatime >= '"+payDate+"' " + 
		        "    AND t.Trade_datatime <= '"+endPayDate+"'  " + 
		        "    AND t.Order_State = '0256'" + 
		        "    )";
		return sql;
	}
	
	//拼接医保对账金额sql
	@SuppressWarnings("unused")
    private String spliteHealthcarePayAmount(){
		StringBuilder sb = new StringBuilder();
		String healthAmountType = propertiesConfigService.findValueByPkey(ProConstants.healthAmountTypeKey);
		if(null == healthAmountType  || "costAll,costBasic,costAccount,costCash,costWhole,costRescue,costSubsidy".equals(healthAmountType)){
			return "SUM(ABS(cost_all)) + SUM(ABS(cost_basic)) + SUM(ABS(cost_account)) + SUM(ABS(cost_cash)) + SUM(ABS(cost_whole))  + SUM(ABS(cost_rescue))  + SUM(ABS(cost_subsidy))";
		}
		if(healthAmountType.contains("costAll")){
			sb.append("SUM(ABS(cost_all)) +");
		}
		if(healthAmountType.contains("costBasic")){
			sb.append("SUM(ABS(cost_basic)) +");
		}
		if(healthAmountType.contains("costAccount")){
			sb.append("SUM(ABS(cost_account)) +");
		}
		if(healthAmountType.contains("costCash")){
			sb.append("SUM(ABS(cost_cash)) +");
		}
		if(healthAmountType.contains("costWhole")){
			sb.append("SUM(ABS(cost_whole)) +");
		}
		if(healthAmountType.contains("costRescue")){
			sb.append("SUM(ABS(cost_rescue)) +");
		}
		if(healthAmountType.contains("costSubsidy")){
			sb.append("SUM(ABS(cost_subsidy)) +");
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
}
