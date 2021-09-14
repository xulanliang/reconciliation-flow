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
import com.yiban.rec.domain.vo.PatTypeSummaryVo;
import com.yiban.rec.service.PatTypeSummaryService;
import com.yiban.rec.service.TradeCheckFollowService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumTypeOfInt;

@Service
public class PatTypeSummaryServiceImpl extends BaseOprService implements PatTypeSummaryService {

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private TradeCheckFollowService tradeCheckFollowService;
	private String orgCodes;
	private String startTime="";
	private String endTime="";
	private String source="";
	
	private String sDate="";
	private String eDate="";
	
	@Override
	public List<PatTypeSummaryVo> count(String orgCode, String date,String endDate) {
		List<PatTypeSummaryVo> list=new ArrayList<>();
		orgCodes = initOrgCodeSet(orgCode,null);
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
		List<PatTypeSummaryVo> hisList = allCount("t_rec_histransactionflow");
		list.addAll(hisList);
		//支付渠道
		source="支付渠道";
		List<PatTypeSummaryVo> billList = allCount("t_thrid_bill");
		list.addAll(billList);
		//支付结果上送表
		source="银医自助机";
		orgCodes = initOrgCodeSet(orgCode,"t_order_upload");
		if(StringUtils.isNotBlank(date)) {
			startTime=" and trade_date_time >=\'"+date+"\'";
		}else {
			startTime="";
		}
		if(StringUtils.isNotBlank(endDate)) {
			endTime=" and trade_date_time <=\'"+endDate+" 23:59:59\'";
		}else {
			endTime="";
		}
		List<PatTypeSummaryVo> orderList = allCount("t_order_upload");
		
		list.addAll(orderList);
		return list;
	}
	
	private List<PatTypeSummaryVo>	allCount(String surface) {
		String payFlowNo="";
		if(surface.equals("t_thrid_bill")||surface.equals("t_order_upload")) {
			payFlowNo=tradeCheckFollowService.getTradeCheckFollow(sDate, eDate,surface);
			if(StringUtils.isNotBlank(payFlowNo)) {
				if(surface.equals("t_order_upload")) {
					payFlowNo=" and tsn_order_no not in ("+payFlowNo+")";
				}else {
					payFlowNo=" and Pay_Flow_No not in ("+payFlowNo+")";
				}
			}
		}
		String sql="select case when pat_type is not null and pat_type!='' then pat_type else 'qt' end patType,COUNT(pay_type) count,SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) payAmount from "+
				   surface+" where 1=1 "+payFlowNo+orgCodes+startTime+endTime;
		List<PatTypeSummaryVo> list=new ArrayList<>();
		String payType=" and  pay_type";
		if(surface.equals("t_thrid_bill")) {
			payType=" and rec_pay_type";
		}
		//得出需要统计的字段名称
		String[] fields = "patType,count,payAmount".split(",");
		//微信
		String wechatPayType=payType+"="+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue();
		String wechatSql=sql+wechatPayType+" GROUP BY pat_type";
		List<Map<String, Object>> wechat = handleNativeSqlColumns(wechatSql,fields);
		setVo(list,wechat,EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
		
		//支付宝
		String aliPayPayType=payType+"="+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue();
		String aliPaySql=sql+aliPayPayType+" GROUP BY pat_type";
		List<Map<String, Object>> aliPay = handleNativeSqlColumns(aliPaySql,fields);
		setVo(list,aliPay,EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
		//银行
		String bankPayType=payType+"="+EnumTypeOfInt.PAY_TYPE_BANK.getValue();
		String bankSql=sql+bankPayType+" GROUP BY pat_type";
		List<Map<String, Object>> bank = handleNativeSqlColumns(bankSql,fields);
		setVo(list,bank,EnumTypeOfInt.PAY_TYPE_BANK.getValue());
		//现金
		if(surface.equals("t_thrid_bill")) {
			String cash="select case when pat_type is not null then pat_type else 'qt' end patType,COUNT(pay_type) count,SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) payAmount from t_rec_cash where 1=1 "+payFlowNo+orgCodes+startTime+endTime;
			String cashSql=cash+" GROUP BY pat_type";
			List<Map<String, Object>> cashVo = handleNativeSqlColumns(cashSql,fields);
			setVo(list,cashVo,EnumTypeOfInt.CASH_PAYTYPE.getValue());
		}else {
			String cashType=payType+"="+EnumTypeOfInt.CASH_PAYTYPE.getValue();
			String cashSql=sql+cashType+" GROUP BY pat_type";
			List<Map<String, Object>> cash = handleNativeSqlColumns(cashSql,fields);
			setVo(list,cash,EnumTypeOfInt.CASH_PAYTYPE.getValue());
		}
		//聚合支付
		String getType=payType+"="+EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue();
		String getSql=sql+getType+" GROUP BY pat_type";
		List<Map<String, Object>> get = handleNativeSqlColumns(getSql,fields);
		setVo(list,get,EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
		//其他
		String otherType=payType+" not in ("+"\'"+EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue()+"\',"+"\'"+EnumTypeOfInt.CASH_PAYTYPE.getValue()+"\',"
				+"\'"+EnumTypeOfInt.PAY_TYPE_BANK.getValue()+"\',"+"\'"+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue()+"\',"+"\'"+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()+"\'"+")";
		String otherSql=sql+otherType+" GROUP BY pat_type";
		List<Map<String, Object>> other = handleNativeSqlColumns(otherSql,fields);
		setVo(list,other,"qt");
		//合计
		BigInteger wechatCountSum=new BigInteger("0");
		BigDecimal wechatPayAmountSum=new BigDecimal(0);
		BigInteger aliPayCountSum=new BigInteger("0");
		BigDecimal aliPayPayAmountSum=new BigDecimal(0);
		BigInteger bankCountSum=new BigInteger("0");
		BigDecimal bankPayAmountSum=new BigDecimal(0);
		BigInteger cashCountSum=new BigInteger("0");
		BigDecimal cashPayAmountSum=new BigDecimal(0);
		BigInteger otherCountSum=new BigInteger("0");
		BigDecimal otherPayAmountSum=new BigDecimal(0);
		BigInteger countSum=new BigInteger("0");
		BigDecimal payAmountSum=new BigDecimal(0);
		BigInteger polyCount=new BigInteger("0");
		BigDecimal polyPayAmount=new BigDecimal(0);
		
		int num=0;
		for(PatTypeSummaryVo v:list) {
			//行合计
			v.setCount(v.getWechatCount().add(v.getAliPayCount().add(v.getBankCount().add(v.getCashCount().add(v.getOtherCount().add(v.getPolyCount()))))));
			v.setPayAmount(v.getWechatPayAmount().add(v.getAliPayPayAmount().add(v.getBankPayAmount().add(v.getCashPayAmount().add(v.getOtherPayAmount().add(v.getPolyPayAmount()))))));
			//列合计
			wechatCountSum=wechatCountSum.add(v.getWechatCount());
			wechatPayAmountSum=wechatPayAmountSum.add(v.getWechatPayAmount());
			
			aliPayCountSum=aliPayCountSum.add(v.getAliPayCount());
			aliPayPayAmountSum=aliPayPayAmountSum.add(v.getAliPayPayAmount());
			
			bankCountSum=bankCountSum.add(v.getBankCount());
			bankPayAmountSum=bankPayAmountSum.add(v.getBankPayAmount());
			
			cashCountSum=cashCountSum.add(v.getCashCount());
			cashPayAmountSum=cashPayAmountSum.add(v.getCashPayAmount());
			
			otherCountSum=otherCountSum.add(v.getOtherCount());
			otherPayAmountSum=otherPayAmountSum.add(v.getOtherPayAmount());
			polyCount=polyCount.add(v.getPolyCount());
			polyPayAmount=polyPayAmount.add(v.getPolyPayAmount());
			
			countSum=countSum.add(v.getCount());
			payAmountSum=payAmountSum.add(v.getPayAmount());
			if(list.size()-1==num) {
				v.setWechatCount(wechatCountSum);
				v.setWechatPayAmount(wechatPayAmountSum);
				v.setAliPayCount(aliPayCountSum);
				v.setAliPayPayAmount(aliPayPayAmountSum);
				v.setBankCount(bankCountSum);
				v.setBankPayAmount(bankPayAmountSum);
				v.setCashCount(cashCountSum);
				v.setCashPayAmount(cashPayAmountSum);
				v.setOtherCount(otherCountSum);
				v.setOtherPayAmount(otherPayAmountSum);
				v.setPolyCount(polyCount);
				v.setPolyPayAmount(polyPayAmount);
				v.setCount(countSum);
				v.setPayAmount(payAmountSum);
			}
			num++;
		}
		return list;
	}
	
	private List<PatTypeSummaryVo> setVo(List<PatTypeSummaryVo> list,List<Map<String, Object>> typeList,String type) {
		List<String> patTypeList=new ArrayList<>();
		patTypeList.add(EnumTypeOfInt.PAT_TYPE_MZ.getCode()+","+EnumTypeOfInt.PAT_TYPE_MZ.getValue());
		patTypeList.add(EnumTypeOfInt.PAT_TYPE_ZY.getCode()+","+EnumTypeOfInt.PAT_TYPE_ZY.getValue());
		patTypeList.add(EnumTypeOfInt.PAT_TYPE_QT.getCode()+","+EnumTypeOfInt.PAT_TYPE_QT.getValue());
		patTypeList.add("合计,hj");
		List<PatTypeSummaryVo> listVo=new ArrayList<>();
		int num=0;
		for(String v:patTypeList) {//循环4次/门诊住院其他合计
			PatTypeSummaryVo vo=null;
			if(list.size()>0) {
				vo=list.get(num);
			}else {
				vo=new PatTypeSummaryVo();
				vo.setSource(source);
			}
			String[] array = v.split(",");
			if((typeList==null||typeList.size()<=0)&&list.size()<=0) {
				vo.setPatType(array[0]);
				listVo.add(vo);
				continue;
			}else {
				if(typeList.toString().indexOf(array[1])<=-1) {
					if(list.size()<=0) {
						vo.setPatType(array[0]);
						listVo.add(vo);
					}
					continue;
				}
				for(Map<String, Object> z:typeList) {
					if(!array[1].equals(z.get("patType")))continue;
					//注入支付方式
					if(list.size()<=0) {
						vo.setPatType(array[0]);
					}
					//注入支付方式数据
					if(type.equals(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())) {//微信
						vo.setWechatCount(vo.getWechatCount().add((BigInteger)z.get("count")));
						vo.setWechatPayAmount(vo.getWechatPayAmount().add((BigDecimal)z.get("payAmount")));
					}else if(type.equals(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())) {//支付宝
						vo.setAliPayCount(vo.getAliPayCount().add((BigInteger)z.get("count")));
						vo.setAliPayPayAmount(vo.getAliPayPayAmount().add((BigDecimal)z.get("payAmount")));
					}else if(type.equals(EnumTypeOfInt.PAY_TYPE_BANK.getValue())) {//银行
						vo.setBankCount(vo.getBankCount().add((BigInteger)z.get("count")));
						vo.setBankPayAmount(vo.getBankPayAmount().add((BigDecimal)z.get("payAmount")));
					}else if(type.equals(EnumTypeOfInt.CASH_PAYTYPE.getValue())) {//现金
						vo.setCashCount(vo.getCashCount().add((BigInteger)z.get("count")));
						vo.setCashPayAmount(vo.getCashPayAmount().add((BigDecimal)z.get("payAmount")));
					}else if(type.equals(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())) {//聚合支付
						vo.setPolyCount(vo.getPolyCount().add((BigInteger)z.get("count")));
						vo.setPolyPayAmount(vo.getPolyPayAmount().add((BigDecimal)z.get("payAmount")));
					}else if(type.equals("qt")) {//其他
						vo.setOtherCount(vo.getOtherCount().add((BigInteger)z.get("count")));
						vo.setOtherPayAmount(vo.getOtherPayAmount().add((BigDecimal)z.get("payAmount")));
					}
				}
				
			}
			listVo.add(vo);
			num++;
		}
		//添加
		if(list.size()<=0) {
			list.addAll(listVo);
		}
		return list;
	}
	
	private String initOrgCodeSet(String orgNo,String type) {
		String orgSql = "";
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		if("t_order_upload".equals(type)) {
			if(null == orgList){
				orgSql = " and org_code= '" + orgNo + "'";
			}else{
				orgSql = " and org_code IN (";
				for (Organization organization : orgList) {
					orgSql =orgSql + "'"+ organization.getCode() + "',";
				}
				orgSql = orgSql + "'"+orgNo+"')";
			}
		}else {
			if(null == orgList){
				orgSql = " and org_no= '" + orgNo + "'";
			}else{
				orgSql = " and org_no IN (";
				for (Organization organization : orgList) {
					orgSql =orgSql + "'"+ organization.getCode() + "',";
				}
				orgSql = orgSql + "'"+orgNo+"')";
			}
		}
		
		return orgSql;
	}
	
}
