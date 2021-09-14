package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.service.WeiNanThirdTradeService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;
@Service
public class WeiNanThirdTradeServiceImpl extends BaseOprService implements WeiNanThirdTradeService {
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
		
		StringBuffer ccbSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1 and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer pfSql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer jhSql = new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		StringBuffer jdWechatSql = new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_thrid_bill where 1=1 and is_deleted=0 and is_actived=1  and rec_pay_type !="+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		
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
			//渭南
			ccbSql.append(strOrg);
			pfSql.append(strOrg);
			jhSql.append(strOrg);
			jdWechatSql.append(strOrg);
		}
		if(StringUtils.isNotBlank(billSource)) {
			String strBillSource=" and bill_source='"+billSource+"'";
			wechatSql.append(strBillSource);
			aliSql.append(strBillSource);
			//jdBankSql.append(strBillSource);
			//thridBankSql.append(strBillSource);
			yibaoSql.append(strBillSource);
			cashSql.append(strBillSource);
			//渭南
			ccbSql.append(strBillSource);
			pfSql.append(strBillSource);
			jhSql.append(strBillSource);
			jdWechatSql.append(strBillSource);
		}
		
		if(StringUtils.isNotBlank(payFlowNo)) {
			String strPayFlowNo=" and Pay_Flow_No='"+payFlowNo+"'";
			wechatSql.append(strPayFlowNo);
			aliSql.append(strPayFlowNo);
			jdBankSql.append(strPayFlowNo);
			thridBankSql.append(strPayFlowNo);
			yibaoSql.append(strPayFlowNo);
			cashSql.append(strPayFlowNo);
			//渭南
			ccbSql.append(strPayFlowNo);
			pfSql.append(strPayFlowNo);
			jhSql.append(strPayFlowNo);
			jdWechatSql.append(strPayFlowNo);
		}
		
		if(StringUtils.isNotBlank(startDate)) {
			String startTime=" AND Trade_datatime >='"+startDate+"'";
			wechatSql.append(startTime);
			aliSql.append(startTime);
			jdBankSql.append(startTime);
			thridBankSql.append(startTime);
			yibaoSql.append(startTime);
			cashSql.append(startTime);
			//渭南
			ccbSql.append(startTime);
			pfSql.append(startTime);
			jhSql.append(startTime);
			jdWechatSql.append(startTime);
		}
		if(StringUtils.isNotBlank(endDate)) {
			String endTime=" AND Trade_datatime <='"+endDate+"'";
			wechatSql.append(endTime);
			aliSql.append(endTime);
			jdBankSql.append(endTime);
			thridBankSql.append(endTime);
			yibaoSql.append(endTime);
			cashSql.append(endTime);
			//渭南
			ccbSql.append(endTime);
			pfSql.append(endTime);
			jhSql.append(endTime);
			jdWechatSql.append(endTime);
		}
		if(StringUtils.isNotBlank(orderState)){
			String orderStateSql = " AND order_state = '"+orderState+"' ";
			wechatSql.append(orderStateSql);
			aliSql.append(orderStateSql);
			jdBankSql.append(orderStateSql);
			thridBankSql.append(orderStateSql);
			yibaoSql.append(orderStateSql);
			//渭南
			ccbSql.append(orderStateSql);
			pfSql.append(orderStateSql);
			jhSql.append(orderStateSql);
			jdWechatSql.append(orderStateSql);
		}
		
		if(StringUtils.isNotBlank(shopFlowNo)){
			String shopFlowNoSql = " AND shop_flow_no = '"+shopFlowNo+"' ";
			wechatSql.append(shopFlowNoSql);
			aliSql.append(shopFlowNoSql);
			jdBankSql.append(shopFlowNoSql);
			thridBankSql.append(shopFlowNoSql);
			yibaoSql.append(shopFlowNoSql);
			//渭南
			ccbSql.append(shopFlowNoSql);
			pfSql.append(shopFlowNoSql);
			jhSql.append(shopFlowNoSql);
			jdWechatSql.append(shopFlowNoSql);
			cashSql.append(" AND Business_Flow_No = '").append(shopFlowNo).append("' ");
		}
		
		
		Map<String,Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(payType)) {
			wechatSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			aliSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			jdBankSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_BANK.getValue() +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			thridBankSql.append(" AND rec_pay_type ="+EnumTypeOfInt.PAY_TYPE_BANK.getValue() +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			//渭南
			ccbSql.append(" AND rec_pay_type ='"+EnumTypeOfInt.PAY_TYPE_BANK.getValue()+"'" +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			pfSql.append(" AND rec_pay_type ='"+EnumTypeOfInt.PAY_TYPE_BANK.getValue()+"'" +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_WNPF.getValue()+"'");
			jhSql.append(" AND rec_pay_type ='"+EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue()+"'" +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			jdWechatSql.append(" AND rec_pay_type ='"+EnumTypeOfInt.PAY_TYPE_JDWECHAT.getValue()+"'" +" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_WNJD.getValue()+"'");
			wechatList(wechatSql.toString(),map);
			aliList(aliSql.toString(),map);
			jdBankList(jdBankSql.toString(),map);
			thridBankList(thridBankSql.toString(),map);
			//yibaoList(yibaoSql.toString(),map);
			cashList(cashSql.toString(),map);
			//渭南定制
			ccbList(ccbSql.toString(),map);
			pfList(pfSql.toString(),map);
			jhList(jhSql.toString(),map);
			jdwechatList(jdWechatSql.toString(),map);
			//汇总
			allList(map);
		}else {
			Map<String, Object> wechatMap=null;
			Map<String, Object> alipayMap=null;
			Map<String, Object> jdBankMap=null;
			Map<String, Object> thridBankMap=null;
			Map<String, Object> yibaoMap=null;
			Map<String, Object> cashMap=null;
			//渭南定制
			Map<String, Object> ccbMap=null;
			Map<String, Object> pfMap=null;
			Map<String, Object> jhMap=null;
			Map<String, Object> jdwechatMap=null;
			String strPayType=" AND rec_pay_type ='"+payType+"'" ;
			wechatSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			aliSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			jdBankSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			thridBankSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			//渭南定制
			ccbSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			pfSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_WNPF.getValue()+"'");
			jhSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue()+"'");
			jdWechatSql.append(strPayType+" AND bill_Source = '"+EnumTypeOfInt.BILL_SOURCE_WNJD.getValue()+"'");
			
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
				
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
			}else if(EnumTypeOfInt.PAY_TYPE_BANK.getValue().equals(payType)) {//银行
				jdBankList(jdBankSql.toString(),map);
				ccbList(ccbSql.toString(),map);
				pfList(pfSql.toString(),map);
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
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
			}else if(EnumTypeOfInt.PAY_TYPE_CCBBANK.getValue().equals(payType)){//建行
				ccbList(ccbSql.toString(),map);
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
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(jhMap); 
				map.putAll(pfMap);
				map.putAll(jdwechatMap);
			}else if(EnumTypeOfInt.PAY_TYPE_PFBANK.getValue().equals(payType)){//浦发
				pfList(pfSql.toString(),map);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
			}else if(EnumTypeOfInt.PAY_TYPE_JDWECHAT.getValue().equals(payType)){//金蝶微信
				jdwechatList(jdWechatSql.toString(),map);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
			}else if(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue().equals(payType)){//聚合支付
				jhList(jhSql.toString(),map);
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jdwechatMap);
			}
			else {
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
				ccbMap=new HashMap<String, Object>();
				ccbMap.put("ccbAllNum", 0);
				ccbMap.put("ccbAllAmount", new BigDecimal(0));
				pfMap=new HashMap<String, Object>();
				pfMap.put("pfAllNum", 0);
				pfMap.put("pfAllAmount", new BigDecimal(0));
				jhMap=new HashMap<String, Object>();
				jhMap.put("jhAllNum", 0);
				jhMap.put("jhAllAmount", new BigDecimal(0));
				jdwechatMap=new HashMap<String, Object>();
				jdwechatMap.put("jdwechatAllNum", 0);
				jdwechatMap.put("jdwechatAllAmount", new BigDecimal(0));
				map.putAll(ccbMap);
				map.putAll(pfMap);
				map.putAll(jhMap);   
				map.putAll(jdwechatMap);
			}
			//汇总
			allList(map);
		}
		return map;
	}
	private void jdwechatList(String sql, Map<String, Object> map) {
		List<Map<String, Object>> jdwechatList = super.handleNativeSql(sql,new String[]{"jdwechatAllNum","jdwechatAllAmount"});
		jdwechatList.get(0).put("jdwechatAllAmount", StringUtil.isNullOrEmpty(jdwechatList.get(0).get("jdwechatAllAmount"))?new BigDecimal(0):jdwechatList.get(0).get("jdwechatAllAmount"));
		map.putAll(jdwechatList.get(0));
	}
	private void jhList(String sql, Map<String, Object> map) {
		List<Map<String, Object>> jhList = super.handleNativeSql(sql,new String[]{"jhAllNum","jhAllAmount"});
		jhList.get(0).put("jhAllAmount", StringUtil.isNullOrEmpty(jhList.get(0).get("jhAllAmount"))?new BigDecimal(0):jhList.get(0).get("jhAllAmount"));
		map.putAll(jhList.get(0));
	}
	private void pfList(String sql, Map<String, Object> map) {
		List<Map<String, Object>> pfList = super.handleNativeSql(sql,new String[]{"pfAllNum","pfAllAmount"});
		pfList.get(0).put("pfAllAmount", StringUtil.isNullOrEmpty(pfList.get(0).get("pfAllAmount"))?new BigDecimal(0):pfList.get(0).get("pfAllAmount"));
		map.putAll(pfList.get(0));
	}
	private void ccbList(String sql, Map<String, Object> map) {
		List<Map<String, Object>> ccbList = super.handleNativeSql(sql,new String[]{"ccbAllNum","ccbAllAmount"});
		ccbList.get(0).put("ccbAllAmount", StringUtil.isNullOrEmpty(ccbList.get(0).get("ccbAllAmount"))?new BigDecimal(0):ccbList.get(0).get("ccbAllAmount"));
		map.putAll(ccbList.get(0));
	}
	//总汇
	public void allList(Map<String,Object> map) {
		allMap.put("allAccount", 
				((BigDecimal) map.get("wechatAllAmount"))
				.add((BigDecimal) map.get("aliAllAmount"))
				.add((BigDecimal) map.get("ccbAllAmount"))
				.add((BigDecimal) map.get("pfAllAmount"))
				.add((BigDecimal) map.get("jhAllAmount"))
				.add((BigDecimal) map.get("jdwechatAllAmount"))
				/*.add((BigDecimal) map.get("jdBankAllAmount"))
				.add((BigDecimal) map.get("thridBankAllAmount"))
				.add((BigDecimal) map.get("thridBankAllAmount"))
				.add((BigDecimal) map.get("yibaoAllAmount"))
				.add((BigDecimal) map.get("cashAllAmount"))*/);
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
