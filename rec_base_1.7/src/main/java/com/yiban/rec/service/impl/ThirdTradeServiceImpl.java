package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.service.ThirdTradeService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;
@Service
public class ThirdTradeServiceImpl extends BaseOprService implements ThirdTradeService {
	private Map<String, Object> allMap=new HashMap<String, Object>();
	
	@Override
	public Map<String, Object> getTradeCollect(String orgNo, String startDate, String endDate,String payType,String payFlowNo,String billSource,List<Organization> orgList,String shopFlowNo,String orderState) {
		StringBuffer wechatSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1 and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer aliSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer jdBankSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1 and (bill_source=\'"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"\' or bill_source=\'"+EnumTypeOfInt.BILL_SOURCE_SELF.getValue()+"\')  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer thridBankSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1 and (bill_source=\'"+EnumTypeOfInt.BILL_SOURCE_THIRD.getValue()+" \' or bill_source is null)  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		//医保
		StringBuffer yibaoSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(cost_account),cost_account)) Pay_Amount  FROM t_healthcare_official  where 1=1  and is_deleted=0 and is_actived=1 ");
		//现金
		StringBuffer cashSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_rec_cash where 1=1  and is_deleted=0 and is_actived=1 ");
		
		
		
		if(StringUtils.isNotBlank(orgNo)) {
			//String strOrg=" and org_no='"+orgNo+"'";
			String strOrg=" and org_no in(\'"+orgNo+"\'";
			for(Organization v:orgList) {
				strOrg=strOrg+",\'"+v.getCode()+"\'";
			}
			strOrg=strOrg+")";
			wechatSql.append(strOrg);
			aliSql.append(strOrg);
			jdBankSql.append(strOrg);
			thridBankSql.append(strOrg);
			yibaoSql.append(strOrg);
			cashSql.append(strOrg);
		}
		if(StringUtils.isNotBlank(billSource)) {
			String strBillSource=" and bill_source='"+billSource+"'";
			wechatSql.append(strBillSource);
			aliSql.append(strBillSource);
			//jdBankSql.append(strBillSource);
			//thridBankSql.append(strBillSource);
			yibaoSql.append(strBillSource);
			cashSql.append(strBillSource);
		}
		
		if(StringUtils.isNotBlank(payFlowNo)) {
			String strPayFlowNo=" and Pay_Flow_No='"+payFlowNo+"'";
			wechatSql.append(strPayFlowNo);
			aliSql.append(strPayFlowNo);
			jdBankSql.append(strPayFlowNo);
			thridBankSql.append(strPayFlowNo);
			yibaoSql.append(strPayFlowNo);
			cashSql.append(strPayFlowNo);
		}
		
		if(StringUtils.isNotBlank(startDate)) {
			String startTime=" AND Trade_datatime >='"+startDate+"'";
			wechatSql.append(startTime);
			aliSql.append(startTime);
			jdBankSql.append(startTime);
			thridBankSql.append(startTime);
			yibaoSql.append(startTime);
			cashSql.append(startTime);
		}
		if(StringUtils.isNotBlank(endDate)) {
			String endTime=" AND Trade_datatime <='"+endDate+"'";
			wechatSql.append(endTime);
			aliSql.append(endTime);
			jdBankSql.append(endTime);
			thridBankSql.append(endTime);
			yibaoSql.append(endTime);
			cashSql.append(endTime);
		}
		if(StringUtils.isNotBlank(orderState)){
			String orderStateSql = " AND order_state = '"+orderState+"' ";
			wechatSql.append(orderStateSql);
			aliSql.append(orderStateSql);
			jdBankSql.append(orderStateSql);
			thridBankSql.append(orderStateSql);
			yibaoSql.append(orderStateSql);
		}
		
		if(StringUtils.isNotBlank(shopFlowNo)){
			String shopFlowNoSql = " AND shop_flow_no = '"+shopFlowNo+"' ";
			wechatSql.append(shopFlowNoSql);
			aliSql.append(shopFlowNoSql);
			jdBankSql.append(shopFlowNoSql);
			thridBankSql.append(shopFlowNoSql);
			yibaoSql.append(shopFlowNoSql);
			cashSql.append(" AND Business_Flow_No = '").append(shopFlowNo).append("' ");
		}
		
		
		Map<String,Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(payType)) {
			wechatSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
			aliSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
			jdBankSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_BANK.getValue());
			thridBankSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_BANK.getValue());
			wechatList(wechatSql.toString(),map);
			aliList(aliSql.toString(),map);
			jdBankList(jdBankSql.toString(),map);
			thridBankList(thridBankSql.toString(),map);
			//yibaoList(yibaoSql.toString(),map);
			cashList(cashSql.toString(),map);
			//汇总
			allList(map);
		}else {
			Map<String, Object> wechatMap=null;
			Map<String, Object> alipayMap=null;
			Map<String, Object> jdBankMap=null;
			Map<String, Object> thridBankMap=null;
			Map<String, Object> yibaoMap=null;
			Map<String, Object> cashMap=null;
			String strPayType=" AND rec_pay_type ="+payType;
			wechatSql.append(strPayType);
			aliSql.append(strPayType);
			jdBankSql.append(strPayType);
			thridBankSql.append(strPayType);
			if(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue().equals(payType)) {
				wechatList(wechatSql.toString(),map);
				alipayMap=new HashMap<String, Object>();
				alipayMap.put("aliAllNum", 0);
				alipayMap.put("aliAllAmount", new BigDecimal(0));
				jdBankMap=new HashMap<String, Object>();
				jdBankMap.put("jdBankAllNum", 0);
				jdBankMap.put("jdBankAllAmount", new BigDecimal(0));
				thridBankMap=new HashMap<String, Object>();
				thridBankMap.put("thridBankAllNum", 0);
				thridBankMap.put("thridBankAllAmount", new BigDecimal(0));
				yibaoMap=new HashMap<String, Object>();
				yibaoMap.put("yibaoAllNum", 0);
				yibaoMap.put("yibaoAllAmount", new BigDecimal(0));
				cashMap=new HashMap<String, Object>();
				cashMap.put("cashAllNum", 0);
				cashMap.put("cashAllAmount", new BigDecimal(0));
				map.putAll(alipayMap);
				map.putAll(yibaoMap);
				map.putAll(cashMap);
				map.putAll(jdBankMap);   
				map.putAll(thridBankMap);
			}else if(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue().equals(payType)) {
				aliList(aliSql.toString(),map);
				wechatMap=new HashMap<String, Object>();
				wechatMap.put("wechatAllNum", 0);
				wechatMap.put("wechatAllAmount", new BigDecimal(0));
				jdBankMap=new HashMap<String, Object>();
				jdBankMap.put("jdBankAllNum", 0);
				jdBankMap.put("jdBankAllAmount", new BigDecimal(0));
				thridBankMap=new HashMap<String, Object>();
				thridBankMap.put("thridBankAllNum", 0);
				thridBankMap.put("thridBankAllAmount", new BigDecimal(0));
				yibaoMap=new HashMap<String, Object>();
				yibaoMap.put("yibaoAllNum", 0);
				yibaoMap.put("yibaoAllAmount", new BigDecimal(0));
				cashMap=new HashMap<String, Object>();
				cashMap.put("cashAllNum", 0);
				cashMap.put("cashAllAmount", new BigDecimal(0));
				map.putAll(wechatMap);
				map.putAll(yibaoMap);
				map.putAll(cashMap);
				map.putAll(jdBankMap);   
				map.putAll(thridBankMap);
			}else if(EnumTypeOfInt.PAY_TYPE_BANK.getValue().equals(payType)) {//银行
				jdBankList(jdBankSql.toString(),map);
				thridBankList(thridBankSql.toString(),map);
				wechatMap=new HashMap<String, Object>();
				wechatMap.put("wechatAllNum", 0);
				wechatMap.put("wechatAllAmount", new BigDecimal(0));
				alipayMap=new HashMap<String, Object>();
				alipayMap.put("aliAllNum", 0);
				alipayMap.put("aliAllAmount", new BigDecimal(0));
				yibaoMap=new HashMap<String, Object>();
				yibaoMap.put("yibaoAllNum", 0);
				yibaoMap.put("yibaoAllAmount", new BigDecimal(0));
				cashMap=new HashMap<String, Object>();
				cashMap.put("cashAllNum", 0);
				cashMap.put("cashAllAmount", new BigDecimal(0));
				map.putAll(wechatMap);
				map.putAll(yibaoMap);
				map.putAll(cashMap);
				map.putAll(alipayMap);
			}else if(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue().equals(payType)){//医保
				//yibaoList(yibaoSql.toString(),map);
				wechatMap=new HashMap<String, Object>();
				wechatMap.put("wechatAllNum", 0);
				wechatMap.put("wechatAllAmount", new BigDecimal(0));
				alipayMap=new HashMap<String, Object>();
				alipayMap.put("aliAllNum", 0);
				alipayMap.put("aliAllAmount", new BigDecimal(0));
				jdBankMap=new HashMap<String, Object>();
				jdBankMap.put("jdBankAllNum", 0);
				jdBankMap.put("jdBankAllAmount", new BigDecimal(0));
				thridBankMap=new HashMap<String, Object>();
				thridBankMap.put("thridBankAllNum", 0);
				thridBankMap.put("thridBankAllAmount", new BigDecimal(0));
				cashMap=new HashMap<String, Object>();
				cashMap.put("cashAllNum", 0);
				cashMap.put("cashAllAmount", new BigDecimal(0));
				map.putAll(wechatMap);
				map.putAll(cashMap);
				map.putAll(alipayMap);
				map.putAll(jdBankMap);   
				map.putAll(thridBankMap);
			}else if(EnumTypeOfInt.CASH_PAYTYPE.getValue().equals(payType)){//现金
				cashList(cashSql.toString(),map);
				wechatMap=new HashMap<String, Object>();
				wechatMap.put("wechatAllNum", 0);
				wechatMap.put("wechatAllAmount", new BigDecimal(0));
				alipayMap=new HashMap<String, Object>();
				alipayMap.put("aliAllNum", 0);
				alipayMap.put("aliAllAmount", new BigDecimal(0));
				jdBankMap=new HashMap<String, Object>();
				jdBankMap.put("jdBankAllNum", 0);
				jdBankMap.put("jdBankAllAmount", new BigDecimal(0));
				thridBankMap=new HashMap<String, Object>();
				thridBankMap.put("thridBankAllNum", 0);
				thridBankMap.put("thridBankAllAmount", new BigDecimal(0));
				yibaoMap=new HashMap<String, Object>();
				yibaoMap.put("yibaoAllNum", 0);
				yibaoMap.put("yibaoAllAmount", new BigDecimal(0));
				map.putAll(wechatMap);
				map.putAll(yibaoMap);
				map.putAll(alipayMap);
				map.putAll(jdBankMap);   
				map.putAll(thridBankMap);
			}else {
				wechatMap=new HashMap<String, Object>();
				wechatMap.put("wechatAllNum", 0);
				wechatMap.put("wechatAllAmount", new BigDecimal(0));
				alipayMap=new HashMap<String, Object>();
				alipayMap.put("aliAllNum", 0);
				alipayMap.put("aliAllAmount", new BigDecimal(0));
				jdBankMap=new HashMap<String, Object>();
				jdBankMap.put("jdBankAllNum", 0);
				jdBankMap.put("jdBankAllAmount", new BigDecimal(0));
				thridBankMap=new HashMap<String, Object>();
				thridBankMap.put("thridBankAllNum", 0);
				thridBankMap.put("thridBankAllAmount", new BigDecimal(0));
				yibaoMap=new HashMap<String, Object>();
				yibaoMap.put("yibaoAllNum", 0);
				yibaoMap.put("yibaoAllAmount", new BigDecimal(0));
				cashMap=new HashMap<String, Object>();
				cashMap.put("cashAllNum", 0);
				cashMap.put("cashAllAmount", new BigDecimal(0));
				map.putAll(wechatMap);
				map.putAll(alipayMap);
				map.putAll(yibaoMap);
				map.putAll(cashMap);
				map.putAll(jdBankMap);   
				map.putAll(thridBankMap);
			}
			//汇总
			allList(map);
		}
		return map;
	}
	//总汇
	public void allList(Map<String,Object> map) {
		allMap.put("allAccount", ((BigDecimal) map.get("wechatAllAmount")).add((BigDecimal) map.get("aliAllAmount")).add((BigDecimal) map.get("jdBankAllAmount")).add((BigDecimal) map.get("thridBankAllAmount"))/*.add((BigDecimal) map.get("thridBankAllAmount")).add((BigDecimal) map.get("yibaoAllAmount"))*/.add((BigDecimal) map.get("cashAllAmount")));
		map.putAll(allMap);
	}
	//微信
	public void wechatList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> wechatList = super.handleNativeSql(sql,new String[]{"wechatAllNum","wechatAllAmount"});
		wechatList.get(0).put("wechatAllAmount", StringUtil.isNullOrEmpty(wechatList.get(0).get("wechatAllAmount"))?new BigDecimal(0):wechatList.get(0).get("wechatAllAmount"));
		map.putAll(wechatList.get(0));
	}
	//支付宝
	public void aliList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> aliList = super.handleNativeSql(sql,new String[]{"aliAllNum","aliAllAmount"});
		aliList.get(0).put("aliAllAmount", StringUtil.isNullOrEmpty(aliList.get(0).get("aliAllAmount"))?new BigDecimal(0):aliList.get(0).get("aliAllAmount"));
		map.putAll(aliList.get(0));
	}
	//巨鼎银行
	public void jdBankList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> bankList = super.handleNativeSql(sql,new String[]{"jdBankAllNum","jdBankAllAmount"});
		bankList.get(0).put("jdBankAllAmount", StringUtil.isNullOrEmpty(bankList.get(0).get("jdBankAllAmount"))?new BigDecimal(0):bankList.get(0).get("jdBankAllAmount"));
		map.putAll(bankList.get(0));
	}
	
	//第三方银行
	public void thridBankList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> bankList = super.handleNativeSql(sql,new String[]{"thridBankAllNum","thridBankAllAmount"});
		bankList.get(0).put("thridBankAllAmount", StringUtil.isNullOrEmpty(bankList.get(0).get("thridBankAllAmount"))?new BigDecimal(0):bankList.get(0).get("thridBankAllAmount"));
		map.putAll(bankList.get(0));
	}
	
	//医保
	/*public void yibaoList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> yibaoList = super.handleNativeSql(sql,new String[]{"yibaoAllNum","yibaoAllAmount"});
		yibaoList.get(0).put("yibaoAllAmount", StringUtil.isNullOrEmpty(yibaoList.get(0).get("yibaoAllAmount"))?new BigDecimal(0):yibaoList.get(0).get("yibaoAllAmount"));
		map.putAll(yibaoList.get(0));
	}*/
	//现金
	public void cashList(String sql,Map<String,Object> map) {
		List<Map<String, Object>> cashList = super.handleNativeSql(sql,new String[]{"cashAllNum","cashAllAmount"});
		cashList.get(0).put("cashAllAmount", StringUtil.isNullOrEmpty(cashList.get(0).get("cashAllAmount"))?new BigDecimal(0):cashList.get(0).get("cashAllAmount"));
		map.putAll(cashList.get(0));
	}
	@Override
	public Map<String, Object> summary(String orgNo, String startDate, String endDate, String payType, String payFlowNo,
			String billSource, List<Organization> orgList, String shopFlowNo, String orderState) {
		// 电子支付类型汇总
		StringBuilder elecPayTypeSql = new StringBuilder(
				"SELECT count(1) count, sum(CASE WHEN t.Order_State = '0156' THEN abs(t.pay_amount) ELSE -abs(t.Pay_Amount) END) payAmount FROM t_thrid_bill t WHERE 1=1 and is_deleted=0 and is_actived=1 ");
		// 医保支付汇总
		StringBuilder healthcareSql = new StringBuilder(
				"select COUNT(1), IFNULL(SUM(IF(Order_State='0256',-ABS(cost_account),cost_account)), 0) Pay_Amount from t_healthcare_official t where 1=1 and is_deleted=0 and is_actived=1 ");
		// 现金支付汇总
		StringBuilder cashSql = new StringBuilder(
				"SELECT COUNT(1), IFNULL(SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)),0) Pay_Amount  FROM t_rec_cash where 1=1  and is_deleted=0 and is_actived=1 ");

		if (StringUtils.isNotBlank(orgNo)) {
			String strOrg = " and org_no in(\'" + orgNo + "\'";
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}
			strOrg = strOrg + ")";
			elecPayTypeSql.append(strOrg);
			healthcareSql.append(strOrg);
			cashSql.append(strOrg);
		}

		if (StringUtils.isNotBlank(billSource)) {
			String strBillSource = " and bill_source='" + billSource + "'";
			elecPayTypeSql.append(strBillSource);
			healthcareSql.append(strBillSource);
			cashSql.append(strBillSource);
		}

		if (StringUtils.isNotBlank(payFlowNo)) {
			String strPayFlowNo = " and Pay_Flow_No='" + payFlowNo + "'";
			elecPayTypeSql.append(strPayFlowNo);
			healthcareSql.append(strPayFlowNo);
			cashSql.append(strPayFlowNo);
		}

		if (StringUtils.isNotBlank(startDate)) {
			String startTime = " AND Trade_datatime >='" + startDate + "'";
			elecPayTypeSql.append(startTime);
			healthcareSql.append(startTime);
			cashSql.append(startTime);
		}
		if (StringUtils.isNotBlank(endDate)) {
			String endTime = " AND Trade_datatime <='" + endDate + "'";
			elecPayTypeSql.append(endTime);
			healthcareSql.append(endTime);
			cashSql.append(endTime);
		}
		if (StringUtils.isNotBlank(orderState)) {
			String orderStateSql = " AND order_state = '" + orderState + "' ";
			elecPayTypeSql.append(orderStateSql);
			healthcareSql.append(orderStateSql);
			cashSql.append(orderStateSql);
		}

		if (StringUtils.isNotBlank(shopFlowNo)) {
			String shopFlowNoSql = " AND shop_flow_no = '" + shopFlowNo + "' ";
			elecPayTypeSql.append(shopFlowNoSql);
			healthcareSql.append(shopFlowNoSql);
			cashSql.append(" AND Business_Flow_No = '").append(shopFlowNo).append("' ");
		}

		if (StringUtils.isNotBlank(payType)) {
			String payTypeSql = " AND rec_pay_type='" + payType + "'";
			elecPayTypeSql.append(payTypeSql);
			cashSql.append(" AND pay_type='" + payType + "'");
		}

		String[] elecColumns = new String[] { "elecCount", "elecAmount" };
		String[] healthcareColumns = new String[] { "healthcareCount", "healthcareAmount" };
		String[] cashColumns = new String[] { "cashCount", "cashAmount" };

		HashMap<String, Object> map = new HashMap<>();
		map.putAll(this.querySum(elecPayTypeSql.toString(), elecColumns));
		map.putAll(this.querySum(healthcareSql.toString(), healthcareColumns));
		map.putAll(this.querySum(cashSql.toString(), cashColumns));
		return map;
	}
	
	/**
	 * 
	 * @param sql
	 * @param     columName,如 new String[]{"jdBankAllNum","jdBankAllAmount"}
	 * @return
	 */
	public Map<String, Object> querySum(String sql, String[] columName) {
		logger.info("查询汇总结果：{}", sql);
		List<Map<String, Object>> list = super.handleNativeSql(sql, columName);
		return list.get(0);
	}
}
