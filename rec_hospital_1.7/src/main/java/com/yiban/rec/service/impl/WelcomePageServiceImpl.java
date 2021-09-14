package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.WelcomePageService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;

@Service
public class WelcomePageServiceImpl extends BaseOprService implements WelcomePageService {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private OrganizationService organizationService;
	
	/**
	 * 合并机构
	 * @param orgNo
	 * @return
	 * String
	 */
	public String combinationOrgCodeSql(String orgNo){
		StringBuilder sql = new StringBuilder();
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		sql.append("'" + orgNo + "'" + ",");
		if(orgList != null && orgList.size() > 0){
			for (Organization organization : orgList) {
				sql.append("'" + organization.getCode() + "'" + ",");
			}
		}
		sql.deleteCharAt(sql.length()-1);
		return sql.toString();
	}
	
	public String combinationPayTypeSql(String payType){
		StringBuilder sql = new StringBuilder();
		if(StringUtils.isBlank(payType)) {
		    return "";
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())){// 微信
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())){// 支付宝
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())){// 银行
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())){// 现金
			sql.append("'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())){// 医保
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())){// 聚合支付
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue())){//一账通
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_WJYZT.getValue() + "'" + ",");
		}
        if(payType.contains(EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue())){//云闪付
        	sql.append("'" + EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue() + "'" + ",");
        }
		sql.deleteCharAt(sql.length()-1);
		return sql.toString();
	}
	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> electronInfo(String orgCode) {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
	    String recType = hConfig.getRecType();
		
	    String recDate = getRecDate();
	    String payTypeSql = combinationPayTypeSql(recType);
		String orgCodeSql = combinationOrgCodeSql(orgCode);
	    
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("SELECT a.billSource,SUM(a.a),SUM(a.b),SUM(a.c),SUM(a.d),SUM(a.a)-SUM(a.c),SUM(a.f) FROM ( ");
		//渠道金额
		sbuf.append(" SELECT tb.bill_source billSource,SUM(CASE WHEN tb.order_state = '0256' THEN -ABS(tb.pay_amount) ELSE ABS(tb.pay_amount) END) a, ");
		sbuf.append(" SUM(tb.pay_acount) b,0 c,0 d,0 e,0 f FROM t_follow_summary tb ");
		sbuf.append(" WHERE tb.trade_date = '"+recDate+"' ");
		sbuf.append(" AND tb.pay_type != '0049' ");
		sbuf.append(" AND tb.data_source = 'third' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tb.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND tb.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY tb.bill_source ");
		
		sbuf.append(" UNION ");
		//his账单金额
		sbuf.append(" SELECT th.bill_source billSource,0 a,0 b,SUM(CASE WHEN th.order_state = '0256' THEN -ABS(th.pay_amount) ELSE ABS(th.pay_amount) END) c, ");
		sbuf.append(" SUM(th.pay_acount) d,0 e,0 f FROM t_follow_summary th ");
		sbuf.append(" WHERE trade_date = '"+recDate+"' ");
		sbuf.append(" AND th.pay_type != '0049' ");
		sbuf.append(" AND th.data_source = 'his' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.bill_source ");
		
		sbuf.append(" UNION ");
		//差异笔数
		sbuf.append(" SELECT tc.bill_source,0 a,0 b,0 c,0 d,0 e,");
		sbuf.append(" COUNT(tc.id) FROM t_trade_check_follow tc ");
		sbuf.append(" WHERE tc.trade_date = '"+recDate+"' ");
		sbuf.append(" AND tc.Pay_Name != '0049' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tc.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND tc.Pay_Name in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY tc.bill_source");
		sbuf.append(" ) a ");
		sbuf.append(" GROUP BY billSource ");
		return this.handleNativeSql(sbuf.toString(),new String[]{"billSource","thridAmount","thridCount","hisAmount","hisCount","diffAmount","diffCount"});
	}
	
//	@Override
//	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
//	public List<Map<String, Object>> electronInfo(String orgCode) {
//		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//	    String recType = hConfig.getRecType();
//		
//	    String recDate = getRecDate();
//	    String payTypeSql = combinationPayTypeSql(recType);
//		String orgCodeSql = combinationOrgCodeSql(orgCode);
//	    
//		StringBuffer sbuf = new StringBuffer();
//		sbuf.append("SELECT a.billSource,SUM(a.a),SUM(a.b),SUM(a.c),SUM(a.d),SUM(a.a)-SUM(a.c),SUM(a.f) FROM ( ");
//		//渠道金额
//		sbuf.append(" SELECT tb.bill_source billSource,SUM(CASE WHEN tb.Order_State = '0256' THEN -ABS(tb.Pay_Amount) ELSE ABS(tb.Pay_Amount) END) a, ");
//		sbuf.append(" COUNT(tb.id) b,0 c,0 d,0 e,0 f FROM t_thrid_bill tb ");
//		sbuf.append(" WHERE DATE_FORMAT(tb.Trade_datatime,'%Y-%m-%d') = '"+recDate+"' ");
//		sbuf.append(" AND tb.pay_type != '0049' ");
//		if(StringUtils.isNotBlank(orgCodeSql)){
//			sbuf.append(" AND tb.org_no in (").append(orgCodeSql).append(")");
//		}
//		if(StringUtils.isNotBlank(payTypeSql)){
//			sbuf.append(" AND tb.pay_type in (").append(payTypeSql).append(")");
//		}
//		sbuf.append(" GROUP BY tb.bill_source ");
//		
//		sbuf.append(" UNION ");
//		//his账单金额
//		sbuf.append(" SELECT th.bill_source billSource,0 a,0 b,SUM(CASE WHEN th.Order_State = '0256' THEN -ABS(th.Pay_Amount) ELSE ABS(th.Pay_Amount) END), ");
//		sbuf.append(" COUNT(th.id),0 e,0 f FROM t_rec_histransactionflow th ");
//		sbuf.append(" WHERE DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') = '"+recDate+"' ");
//		sbuf.append(" AND th.pay_type != '0049' ");
//		if(StringUtils.isNotBlank(orgCodeSql)){
//			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
//		}
//		if(StringUtils.isNotBlank(payTypeSql)){
//			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
//		}
//		sbuf.append(" GROUP BY th.bill_source ");
//		
//		sbuf.append(" UNION ");
//		//差异笔数
//		sbuf.append(" SELECT tc.bill_source,0 a,0 b,0 c,0 d,0 e,");
//		sbuf.append(" COUNT(tc.id) FROM t_trade_check_follow tc ");
//		sbuf.append(" WHERE DATE_FORMAT(tc.trade_date,'%Y-%m-%d') = '"+recDate+"' ");
//		sbuf.append(" AND tc.Pay_Name != '0049' ");
//		if(StringUtils.isNotBlank(orgCodeSql)){
//			sbuf.append(" AND tc.org_no in (").append(orgCodeSql).append(")");
//		}
//		if(StringUtils.isNotBlank(payTypeSql)){
//			sbuf.append(" AND tc.Pay_Name in (").append(payTypeSql).append(")");
//		}
//		sbuf.append(" GROUP BY tc.bill_source");
//		sbuf.append(" ) a ");
//		sbuf.append(" GROUP BY billSource ");
//		return this.handleNativeSql(sbuf.toString(),new String[]{"billSource","thridAmount","thridCount","hisAmount","hisCount","diffAmount","diffCount"});
//	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> billSourceLine(String orgCode, String billSource) {
		
		
	    String recDate = getRecDate();
	    String startDate = DateUtil.transferDateToDateFormat("yyyy-MM-dd", DateUtil.addDay(DateUtil.stringLineToDateTime(recDate, "yyyy-MM-dd"), -6));
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT th.bill_source,th.trade_date,SUM(CASE WHEN th.order_state = '0256' THEN -ABS(th.pay_amount) ELSE ABS(th.pay_amount) END)");
		sbuf.append("  FROM t_follow_summary th");
		sbuf.append(" WHERE DATE_FORMAT(th.trade_date,'%Y-%m-%d') <='").append(recDate).append("'");
		sbuf.append(" AND DATE_FORMAT(th.trade_date,'%Y-%m-%d') >= '").append(startDate).append("'");
		sbuf.append(" AND th.pay_type != '0049' ");
		sbuf.append(" AND th.data_source = 'his' ");
		if(StringUtils.isNotBlank(billSource)){
			sbuf.append(" AND th.bill_source = '").append(billSource).append("' ");
		}
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.bill_source,th.trade_date");
		
		return this.handleNativeSql(sbuf.toString(),new String[]{"billSource","date","amount"});
	}
	/**
	 * 获取支付类型
	 * @return
	 */
	public String getPayTypeSql(){
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
	    String recType = hConfig.getRecType();
	    String payTypeSql = combinationPayTypeSql(recType);
	    return payTypeSql;
	}
	
	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public String getRecDate() {
		
		StringBuffer sbuf=new StringBuffer("SELECT DATE_FORMAT(Trade_date,'%Y-%m-%d') FROM t_his_report ORDER BY Trade_date DESC LIMIT 1 ");
		try {
			Object date = super.handleNativeSql4SingleRes(sbuf.toString());
			if(date!=null){
				return date.toString();
			}
		} catch (Exception e) {
			
		}
		
		Date now = new Date();
		final String CRON_DATE_FORMAT = "HH:mm:ss";
		String autoRecJobTime = propertiesConfigService.findValueByPkey(ProConstants.autoRecJobTime,
				ProConstants.DEFAULT.get(ProConstants.autoRecJobTime));
        Date recTime = DateUtil.addMinute(DateUtil.stringLineToDateTime(autoRecJobTime, CRON_DATE_FORMAT), 10);
        Date nowTime = DateUtil.transferDateToDate("HH-mm-ss", now);
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        int checkTime = Integer.parseInt(hConfig.getCheckTime());
//        return "2019-02-20";
        if(nowTime.getTime() > recTime.getTime()){
        	return DateUtil.transferDateToDateFormat("yyyy-MM-dd",DateUtil.addDay(now, (0-checkTime)));
        }
        return DateUtil.transferDateToDateFormat("yyyy-MM-dd",DateUtil.addDay(now, (0-checkTime-1)));
	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> businessIncomeSummary(String orgCode) {
		String recDate = getRecDate();
	    String startDate = DateUtil.transferDateToDateFormat("yyyy-MM-dd", DateUtil.addDay(DateUtil.stringLineToDateTime(recDate, "yyyy-MM-dd"), -6));
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT IFNULL(th.Pay_Business_Type,'0052'),SUM(CASE WHEN th.Order_State = '0256' THEN -ABS(th.Pay_Amount) ELSE ABS(th.Pay_Amount) END) ");
		sbuf.append(" FROM t_rec_histransactionflow th ");
		sbuf.append(" WHERE DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') <='").append(recDate).append("'");
		sbuf.append(" AND DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') >= '").append(startDate).append("'");
		sbuf.append(" AND th.Pay_Business_Type in ('0151','0451','0551') ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.Pay_Business_Type");
		return this.handleNativeSql(sbuf.toString(),new String[]{"businessType","amount"});
	}

	@Override
	public String getWarningCount(String orgCode) {
		String recDate = getRecDate();
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT COUNT(tou.id) FROM t_order_upload tou ");
		sbuf.append(" WHERE ((tou.order_state = '1809300' AND refund_order_state IS NULL) ");
		sbuf.append(" OR tou.refund_order_state = '1809300')");
		sbuf.append(" AND DATE_FORMAT(tou.trade_date_time,'%Y-%m-%d') = '"+recDate+"' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tou.org_code in (").append(orgCodeSql).append(")");
		}
//		if(StringUtils.isNotBlank(payTypeSql)){
//			sbuf.append(" AND tou.pay_type in (").append(payTypeSql).append(")");
//		}
		return super.handleNativeSql4SingleRes(sbuf.toString()).toString();
	}

	@Override
	public Map<String, Object> getRefundCount(String orgCode,String type,User user) {
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT tr.state,COUNT(tr.id) FROM t_exception_handling_record tr ");
		sbuf.append(" WHERE 1=1 ");
		sbuf.append(" AND (tr.father_id = 0 or tr.father_id IS NULL) ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tr.org_no in (").append(orgCodeSql).append(")");
		}
		if("2".equals(type)){
			sbuf.append(" AND tr.operation_user_id = '").append(user.getId()+"'");
		}
		sbuf.append(" GROUP BY tr.state ");
		List<Map<String, Object>> list = super.handleNativeSql(sbuf.toString(),new String[]{"state","count"});
		Map<String, Object> map = new HashMap<>();
		map.put("0", "0");
		map.put("1", "0");//'状态  0:无意义  1：待审核，2已驳回，3已退费'
		map.put("2", "0");
		map.put("3", "0");
		for(Map<String, Object> m:list){
			map.put(m.get("state").toString(), m.get("count"));
		}
		return map;
	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> thridPie(String orgCode) {
		String recDate = getRecDate();
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT tb.bill_source,SUM(CASE WHEN tb.order_state = '0256' THEN -ABS(tb.pay_amount) ELSE ABS(tb.pay_amount) END) FROM t_follow_summary tb");
		sbuf.append(" WHERE tb.trade_date = '").append(recDate).append("' ");
		sbuf.append(" AND data_source = 'third' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tb.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND tb.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY tb.bill_source ");
		return super.handleNativeSql(sbuf.toString(),new String[]{"name","value"});
	}
	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> payTypePie(String orgCode) {
		String recDate = getRecDate();
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT tb.pay_type,SUM(CASE WHEN tb.order_state = '0256' THEN -ABS(tb.pay_amount) ELSE ABS(tb.pay_amount) END) FROM t_follow_summary tb");
		sbuf.append(" WHERE tb.trade_date = '").append(recDate).append("' ");
		sbuf.append(" AND data_source = 'third' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tb.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND tb.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY tb.pay_type ");
		return super.handleNativeSql(sbuf.toString(),new String[]{"name","value"});
	}
	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> businessIncomeChart(String orgCode) {
		String recDate = getRecDate();
	    String startDate = DateUtil.transferDateToDateFormat("yyyy-MM-dd", DateUtil.addDay(DateUtil.stringLineToDateTime(recDate, "yyyy-MM-dd"), -6));
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT IFNULL(th.Pay_Business_Type,'0052'),DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d'),SUM(CASE WHEN th.Order_State = '0256' THEN -ABS(th.Pay_Amount) ELSE ABS(th.Pay_Amount) END) ");
		sbuf.append(" FROM t_rec_histransactionflow th ");
		sbuf.append(" WHERE DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') <='").append(recDate).append("'");
		sbuf.append(" AND DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') >= '").append(startDate).append("'");
		sbuf.append(" AND th.Pay_Business_Type in ('0151','0451','0551') ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.Pay_Business_Type,DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d')");
		return this.handleNativeSql(sbuf.toString(),new String[]{"businessType","date","amount"});
	}

	/**
	 * 近3月渠道收入汇总
	 * @param orgCode
	 * @return
	 */
	@Override
	public List<Map<String, Object>> initBillsRelateThreeMonthsIncomeData(String orgCode) {
		Date currentDate = new Date();
		// 开始时间
		String startDateStr = DateUtil.getSpecifiedDayBeforeMonth(currentDate, 3);
		String startDate = startDateStr.substring(0,startDateStr.length()-2)+"01";
		// 结束时间
		String endDateStr = DateUtil.getSpecifiedDayBeforeMonth(currentDate, 0);
		String endDate = endDateStr.substring(0,endDateStr.length()-2)+"01";

		String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf = new StringBuffer();
		/*sbuf.append(" SELECT IFNULL(th.Pay_Business_Type,'0052'),DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d'),SUM(CASE WHEN th.Order_State = '0256' THEN -ABS(th.Pay_Amount) ELSE ABS(th.Pay_Amount) END) ");
		sbuf.append(" FROM t_rec_histransactionflow th ");
		sbuf.append(" WHERE DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') <='").append(recDate).append("'");
		sbuf.append(" AND DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d') >= '").append(startDate).append("'");
		sbuf.append(" AND th.Pay_Business_Type in ('0151','0451','0551') ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.Pay_Business_Type,DATE_FORMAT(th.Trade_datatime,'%Y-%m-%d')");*/
		sbuf.append("SELECT trade_date,bill_source, ");
		sbuf.append("SUM(CASE WHEN order_state = '0256' THEN -ABS(pay_amount) ELSE ABS(pay_amount) END)");
		sbuf.append(" FROM t_follow_summary ");
		sbuf.append(" WHERE trade_date >= '").append(startDate);
		sbuf.append("' AND trade_date < '").append(endDate).append("'");
		sbuf.append(" AND data_source = 'third' ");
		if (StringUtils.isNotBlank(payTypeSql)) {
			sbuf.append(" AND pay_type in (").append(payTypeSql).append(")");
		}
		if (StringUtils.isNotBlank(orgCodeSql)) {
			sbuf.append(" AND org_no in (").append(orgCodeSql).append(")");
		}
		sbuf.append(" GROUP BY bill_source,DATE_FORMAT(trade_date,'%Y-%m')");
		List<Map<String, Object>> data = this.handleNativeSql(sbuf.toString(), new String[]{"date", "billSource", "amount"});
		if (data != null) {
			data = getRelateMonth(data);
		}
		return data;
	}

	private List<Map<String, Object>> getRelateMonth(List<Map<String, Object>> data){
		for(Map<String, Object> lineDataMap : data){
			String dateStr = String.valueOf(lineDataMap.get("date"));
			lineDataMap.put("date", getMonthName(dateStr));
		}
		return data;
	}

	/**
	 * 通过日期返回中文月份  2019-08
	 * @param dateStr
	 * @return  八月
	 */
	@Override
	public String getMonthName(String dateStr){
		String[] dateArr = dateStr.split("-");
		String monthStr = dateArr[1];
		String monthName = "";
		switch (monthStr) {
			case "01":
				monthName = "一月";
				break;
			case "02":
				monthName = "二月";
				break;
			case "03":
				monthName = "三月";
				break;
			case "04":
				monthName = "四月";
				break;
			case "05":
				monthName = "五月";
				break;
			case "06":
				monthName = "六月";
				break;
			case "07":
				monthName = "七月";
				break;
			case "08":
				monthName = "八月";
				break;
			case "09":
				monthName = "九月";
				break;
			case "10":
				monthName = "十月";
				break;
			case "11":
				monthName = "十一月";
				break;
			case "12":
				monthName = "十二月";
				break;
		}
		return monthName;
	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> payTypeIncomeChart(String orgCode) {
		String recDate = getRecDate();
	    String startDate = DateUtil.transferDateToDateFormat("yyyy-MM-dd", DateUtil.addDay(DateUtil.stringLineToDateTime(recDate, "yyyy-MM-dd"), -6));
	    String payTypeSql = getPayTypeSql();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT th.pay_type,trade_date,SUM(CASE WHEN th.order_state = '0256' THEN -ABS(th.pay_amount) ELSE ABS(th.pay_amount) END) ");
		sbuf.append(" FROM t_follow_summary th ");
		sbuf.append(" WHERE trade_date <='").append(recDate).append("'");
		sbuf.append(" AND trade_date >= '").append(startDate).append("'");
		sbuf.append(" AND data_source = 'his' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		if(StringUtils.isNotBlank(payTypeSql)){
			sbuf.append(" AND th.pay_type in (").append(payTypeSql).append(")");
		}
		sbuf.append(" GROUP BY th.pay_type,trade_date ");
		return this.handleNativeSql(sbuf.toString(),new String[]{"payType","date","amount"});
	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public List<Map<String, Object>> cashInfo(String orgCode) {
		String recDate = getRecDate();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT a.bill_source,SUM(a.a),SUM(a.b),SUM(a.a)-SUM(a.b) FROM ( ");
		//现金表
		sbuf.append(" SELECT trc.bill_source,SUM(CASE WHEN trc.Order_State = '0256' THEN -ABS(trc.Pay_Amount) ELSE ABS(trc.Pay_Amount) END) a,0 b FROM t_rec_cash trc ");
		sbuf.append(" WHERE trc.Trade_datatime >= '"+recDate+"' ");
		sbuf.append(" AND trc.Trade_datatime <= '").append(recDate).append(" 23:59:59' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND trc.org_no in (").append(orgCodeSql).append(")");
		}
		sbuf.append(" GROUP BY trc.bill_source ");
		sbuf.append(" UNION ");
		//his现金金额
		sbuf.append(" SELECT th.bill_source,0 a,SUM(CASE WHEN th.order_state = '0256' THEN -ABS(th.pay_amount) ELSE ABS(th.pay_amount) END) b FROM t_follow_summary th ");
		sbuf.append(" WHERE DATE_FORMAT(th.trade_date,'%Y-%m-%d') = '"+recDate+"' ");
		sbuf.append(" AND th.pay_type = '0049' ");
		sbuf.append(" AND th.data_source = 'his' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND th.org_no in (").append(orgCodeSql).append(")");
		}
		sbuf.append(" GROUP BY th.bill_source ");
		sbuf.append(" ) a GROUP BY a.bill_source ");
		return this.handleNativeSql(sbuf.toString(),new String[]{"billSource","cashAmount","hisAmount","diffAmount"});
	}

	@Override
	//@Cacheable(value = "user_list",key ="#root.methodName", unless = "#result==null")
	public Map<String, Object> healthcareInfo(String orgCode) {
		String recDate = getRecDate();
		String orgCodeSql = combinationOrgCodeSql(orgCode);
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT SUM(a.a),SUM(a.b),SUM(a.b)-SUM(a.a) FROM ( ");
		//医保his
		sbuf.append(" SELECT SUM(CASE WHEN thh.Order_State = '0256' THEN -ABS(thh.cost_all) ELSE ABS(thh.cost_all) END) a,0 b FROM t_healthcare_his thh ");
		sbuf.append(" WHERE thh.trade_datatime >= '"+recDate+"' ");
		sbuf.append(" AND thh.trade_datatime <= '").append(recDate).append(" 23:59:59' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND thh.org_no in (").append(orgCodeSql).append(")");
		}
		
		sbuf.append(" UNION  ");
		
		//医保中心
		sbuf.append(" SELECT 0 a,SUM(CASE WHEN tho.Order_State = '0256' THEN -ABS(tho.cost_all) ELSE ABS(tho.cost_all) END) b FROM t_healthcare_official tho ");
		sbuf.append(" WHERE tho.trade_datatime >= '"+recDate+"' ");
		sbuf.append(" AND tho.trade_datatime <= '").append(recDate).append(" 23:59:59' ");
		if(StringUtils.isNotBlank(orgCodeSql)){
			sbuf.append(" AND tho.org_no in (").append(orgCodeSql).append(")");
		}
		sbuf.append(" )a ");
		List<Map<String, Object>> list = this.handleNativeSql(sbuf.toString(),new String[]{"hisAmount","healthcareAmount","diffAmount"});
		return list.get(0);
	}

}
