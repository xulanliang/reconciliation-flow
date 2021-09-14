package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.domain.vo.PayMethodSummaryVo;
import com.yiban.rec.service.PayMethodSummaryService;
import com.yiban.rec.service.TradeCheckFollowService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumTypeOfInt;
@Service
public class PayMethodSummaryServiceImpl extends BaseOprService implements PayMethodSummaryService {

	@Autowired
	private OrganizationService organizationService;
	
	
	@Autowired
	private TradeCheckFollowService tradeCheckFollowService;
	

	
	private String orgCodes;
	private String startTime="";
	private String endTime="";
	private String source="";
	private String payType="";
	
	private String sDate="";
	private String eDate="";
	
	@Override
	public List<PayMethodSummaryVo> count(String orgCode, String date, String endDate) {
		List<PayMethodSummaryVo> list=new ArrayList<>();
		orgCodes = initOrgCodeSet(orgCode);
		if(StringUtils.isNotBlank(date)) {
			startTime=" and Trade_datatime >=\'"+date+"\'";
			sDate=date;
		}else {
			startTime="";
			sDate="";
		}
		if(StringUtils.isNotBlank(endDate)) {
			endTime=" and Trade_datatime <=\'"+endDate+" 23:59:59\'";
			eDate=endDate;
		}else {
			endTime="";
			eDate="";
		}
		//his
		source="HIS";
		payType="pay_type";
		List<Map<String, Object>> hisList = allCount("t_rec_histransactionflow");
		//组装到PayMethodSummaryVo实体
		list.addAll(setVo(hisList));
		//支付渠道
		source="支付渠道";
		payType="rec_pay_type";
		List<Map<String, Object>> billList = allCount("t_thrid_bill");
		list.addAll(setVo(billList));
		return list;
	}
	
	private List<PayMethodSummaryVo> setVo(List<Map<String, Object>> typeList) {
		List<PayMethodSummaryVo> list= new ArrayList<>();
		List<String> patTypeList=new ArrayList<>();
		patTypeList.add(EnumTypeOfInt.PAY_TYPE_WECHAT.getCode()+","+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
		patTypeList.add(EnumTypeOfInt.PAY_TYPE_ALIPAY.getCode()+","+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
		patTypeList.add(EnumTypeOfInt.PAY_TYPE_BANK.getCode()+","+EnumTypeOfInt.PAY_TYPE_BANK.getValue());
		patTypeList.add(EnumTypeOfInt.CASH_PAYTYPE.getCode()+","+EnumTypeOfInt.CASH_PAYTYPE.getValue());
		patTypeList.add(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getCode()+","+EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
		patTypeList.add("其他,qt");
		patTypeList.add("合计,hj");
		for(String v:patTypeList) {
			String[] array = v.split(",");
			PayMethodSummaryVo vo=new PayMethodSummaryVo();
			vo.setSource(source);
			vo.setPayType(array[0]);
			for(Map<String, Object> z:typeList) {
				if(!array[1].equals(z.get("payType"))) continue;
				//注入支付方式数据
				vo.setAppointmentCount(vo.getAppointmentCount().add((BigInteger)z.get("appointmentCount")));
				vo.setAppointmentPayAmount(vo.getAppointmentPayAmount().add((BigDecimal)z.get("appointmentPayAmount")));
				vo.setCount(vo.getCount().add((BigInteger)z.get("count")));
				vo.setPayAmount(vo.getPayAmount().add((BigDecimal)z.get("payAmount")));
				vo.setOtherCount(vo.getOtherCount().add((BigInteger)z.get("otherCount")));
				vo.setOtherPayAmount(vo.getOtherPayAmount().add((BigDecimal)z.get("otherPayAmount")));
				vo.setPayCount(vo.getPayCount().add((BigInteger)z.get("payCount")));
				vo.setPayPayAmount(vo.getPayPayAmount().add((BigDecimal)z.get("payPayAmount")));
				vo.setPrePaymentCount(vo.getPrePaymentCount().add((BigInteger)z.get("prePaymentCount")));
				vo.setPrePaymentPayAmount(vo.getPrePaymentPayAmount().add((BigDecimal)z.get("prePaymentPayAmount")));
				vo.setRechargeCount(vo.getRechargeCount().add((BigInteger)z.get("rechargeCount")));
				vo.setRechargePayAmount(vo.getRechargePayAmount().add((BigDecimal)z.get("rechargePayAmount")));
				vo.setRegCount(vo.getRegCount().add((BigInteger)z.get("regCount")));
				vo.setRegPayAmount(vo.getRegPayAmount().add((BigDecimal)z.get("regPayAmount")));
			}
			list.add(vo);
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> allCount(String surface){
		String cash="";
		String payFlowNo="";
		if(surface.equals("t_thrid_bill")) {
			payFlowNo=tradeCheckFollowService.getTradeCheckFollow(sDate, eDate,surface);
			if(StringUtils.isNotBlank(payFlowNo)) {
				payFlowNo=" and Pay_Flow_No not in ("+payFlowNo+")";
			}
		}
		if("t_thrid_bill".equals(surface)) {
			cash=" UNION ALL SELECT" +
				" 	CASE WHEN pay_type <=> null THEN 'hj' ELSE pay_type END payType,"+
				"	COUNT(CASE WHEN pay_business_type='0451' THEN 1 ELSE null END) regCount," + 
				"	SUM(CASE WHEN pay_business_type='0451' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) regPayAmount," + 
				"   COUNT(CASE WHEN pay_business_type='0851' THEN 1 ELSE null END) appointmentCount," + 
				"	SUM(CASE WHEN pay_business_type='0851' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) appointmentPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0551' THEN 1 ELSE null END) payCount," + 
				"	SUM(CASE WHEN pay_business_type='0551' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) payPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0151' THEN 1 ELSE null END) rechargeCount," + 
				"	SUM(CASE WHEN pay_business_type='0151' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) rechargePayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0751' THEN 1 ELSE null END) prePaymentCount," + 
				"	SUM(CASE WHEN pay_business_type='0751' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) prePaymentPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type not in ('0451','0851','0551','0151','0751') or  ISNULL(pay_business_type) THEN 1 ELSE null END) otherCount," + 
				"	SUM(CASE WHEN pay_business_type not in ('0451','0851','0551','0151','0751') or  ISNULL(pay_business_type) THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) otherPayAmount,"
				+ "COUNT(1) count,"
				+ "SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) payAmount" + 
				" FROM " + 
				"t_rec_cash where 1=1 "+orgCodes+startTime+endTime+ 
				"GROUP BY pay_type with rollup"; 
		}
		
		String sql="SELECT" +
				" 	CASE WHEN "+payType+" <=> null THEN 'hj' ELSE "+payType+" END payType,"+
				"	COUNT(CASE WHEN pay_business_type='0451' THEN 1 ELSE null END) regCount," + 
				"	SUM(CASE WHEN pay_business_type='0451' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) regPayAmount," + 
				"   COUNT(CASE WHEN pay_business_type='0851' THEN 1 ELSE null END) appointmentCount," + 
				"	SUM(CASE WHEN pay_business_type='0851' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) appointmentPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0551' THEN 1 ELSE null END) payCount," + 
				"	SUM(CASE WHEN pay_business_type='0551' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) payPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0151' THEN 1 ELSE null END) rechargeCount," + 
				"	SUM(CASE WHEN pay_business_type='0151' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) rechargePayAmount," + 
				"	COUNT(CASE WHEN pay_business_type='0751' THEN 1 ELSE null END) prePaymentCount," + 
				"	SUM(CASE WHEN pay_business_type='0751' THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) prePaymentPayAmount," + 
				"	COUNT(CASE WHEN pay_business_type not in ('0451','0851','0551','0151','0751') or  ISNULL(pay_business_type) THEN 1 ELSE null END) otherCount," + 
				"	SUM(CASE WHEN pay_business_type not in ('0451','0851','0551','0151','0751') or  ISNULL(pay_business_type) THEN IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount) ELSE 0 END) otherPayAmount,"
				+ "COUNT(1) count,"
				+ "SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) payAmount" + 
				" FROM " + 
				surface+" where 1=1 "+payFlowNo+orgCodes+startTime+endTime+ 
				"GROUP BY " +payType+" with rollup"+cash; 
		
		return super.queryList(sql, null, null);
	}
	
	
	
	
	
	private String initOrgCodeSet(String orgNo) {
		String orgSql = "";
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		if(null == orgList){
			orgSql = " and org_no= '" + orgNo + "'";
		}else{
			orgSql = " and org_no IN (";
			for (Organization organization : orgList) {
				orgSql =orgSql + "'"+ organization.getCode() + "',";
			}
			orgSql = orgSql + "'"+orgNo+"')";
		}
		return orgSql;
	}
	
}
