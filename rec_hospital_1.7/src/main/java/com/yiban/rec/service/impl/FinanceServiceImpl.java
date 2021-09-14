package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.FinanceVo;
import com.yiban.rec.service.FinanceService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.StringUtil;
@Service("financeService")
public class FinanceServiceImpl extends BaseOprService implements FinanceService{
	

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;
	@Override
	public Page<Map<String, Object>> getFinanceData(OprPageRequest pagerequest, FinanceVo fvo,List<String> orgList) {
		StringBuilder sb=new StringBuilder();
//		if(EnumType.DATA_SOURCE_TYPE_PLAT_CHANNEL.getValue().equals(fvo.getDataSource())){
//			sb.append(spliceSql(CommonConstant.T_REC_PLATFORMFLOW_LOG,fvo.getOrgNo(),orgList));
//		}else if(EnumType.DATA_SOURCE_TYPE_PAY_CHANNEL.getValue().equals(fvo.getDataSource())){
//			sb.append(spliceSql(CommonConstant.T_REC_THRID_BILL,fvo.getOrgNo(),orgList));
//		}else{
//		}
		sb.append(spliceSql(CommonConstant.T_REC_PAY_RESULT,null,fvo.getStartDate(),fvo.getEndDate(),orgList));
		if(!StringUtil.isNullOrEmpty(fvo.getPayType())){
			if(fvo.getPayType().indexOf(",")>-1){
				String[] payTypeArr = fvo.getPayType().split(",");
				sb.append(" AND Pay_Type in ").append(StringUtil.arrToString(payTypeArr));
			}else{
				sb.append(" AND Pay_Type= '").append(fvo.getPayType()).append("'");
			}
		}
		if(!StringUtil.isNullOrEmpty(fvo.getPaySource())){
			if(fvo.getPaySource().indexOf(",")>-1){
				String[] paySourceArr = fvo.getPaySource().split(",");
				sb.append(" AND Pay_Source in ").append(StringUtil.arrToString(paySourceArr));
			}else{
				sb.append(" AND Pay_Source = '").append(fvo.getPaySource()).append("'");
			}
		}
		//查询条件过滤
		String businessType = fvo.getBusinessType();
		if(!StringUtil.isEmpty(businessType)) {
		    sb.append(" AND Pay_Business_Type = '" + businessType +"'");
		}
		sb.append(" group by Trade_datatime,Pay_Source,pay_type,org_no,Pay_Business_Type");
		System.out.println(sb.toString());
		Page<Map<String,Object>> page = super.handleNativeSql(sb.toString(),pagerequest,new String[]
				{"tradeDatatime","paySource","orgNo","payType","payBusinessType","paySum","payAmount","revokeSum","revokeAmount","refundSum","refundAmount","receiptSum","receiptAmount"});
		return page;
	}
	
	private String spliceSql(String tableName,String orgNo,String stateDate,String endDate,List<String> orgList){
		StringBuilder sb = new StringBuilder();
		String endTime = endDate+" 23:59:59";
		sb.append(" SELECT Trade_datatime as tradeDatatime,Pay_Source as paySource,org_no as orgNo,Pay_Type as payType,Pay_Business_Type as payBusinessType,SUM(1cut) paySum,SUM(1Pay_Amount) payAmount,SUM(2cut) revokeSum,SUM(2Pay_Amount) revokeAmount,SUM(3cut) refundSum,SUM(3Pay_Amount) refundAmount, ");
		sb.append(" (SUM(1cut)-SUM(2cut)-SUM(3cut)) as receiptSum,(SUM(1Pay_Amount)-sUM(2Pay_Amount)-SUM(3Pay_Amount)) as receiptAmount");
		sb.append(" FROM ( SELECT DATE_FORMAT(Trade_datatime,'%Y-%m-%d') as Trade_datatime,Pay_Source,org_no,Pay_Type,Pay_Business_Type,'支付',COUNT(id) 1cut,SUM(t.Pay_Amount) 1Pay_Amount,'撤销',0 2cut,0 2Pay_Amount,'退费',0 3cut,0 3Pay_Amount FROM ");
		sb.append(tableName);
//		sb.append(" t WHERE t.Trade_Code in ("+EnumTypeOfInt.TRADE_CODE_PAY.getValue()+","+EnumTypeOfInt.TRADE_CODE_BAR.getValue()+","+EnumTypeOfInt.TRADE_CODE_RREORDER.getValue()+") and t.is_deleted=0 and t.is_actived=1");
		sb.append(" t WHERE t.Pay_Amount>0 and  t.is_deleted=0 and t.is_actived=1");
		if(!StringUtil.isNullOrEmpty(orgNo)){
			sb.append(" AND t.org_no= '").append(orgNo).append("'");
		}else{
			if(StringUtil.isNullOrEmpty(orgList)){
				sb.append(" AND t.org_no= '").append(0).append("'");
			}else{
				sb.append(" AND t.org_no in ").append(orgList.toString().replaceAll("\\[", "(").replaceAll("\\]", ")")).append("");
			}
		}
		sb.append(" AND t.Trade_datatime>='"+stateDate+"' AND t.Trade_datatime<='"+endTime+"'");
		sb.append("GROUP BY t.Trade_datatime,t.Pay_Source,t.Org_No,t.Pay_Type,t.Pay_Business_Type UNION ALL SELECT DATE_FORMAT(Trade_datatime,'%Y-%m-%d') as Trade_datatime,Pay_Source,org_no,Pay_Type,Pay_Business_Type,'支付',0 1cut,0 1Pay_Amount,'撤销',COUNT(id) 2cut,SUM(t.Pay_Amount) 2Pay_Amount,'退费',0 3cut,0 3Pay_Amount FROM ");
		sb.append(tableName);		
//		sb.append(" t WHERE t.Trade_Code = "+EnumTypeOfInt.TRADE_CODE_REVOKE.getValue()+" and t.is_deleted=0 and t.is_actived=1");
		sb.append(" t WHERE t.Pay_Amount=0 and t.is_deleted=0 and t.is_actived=1");
		if(!StringUtil.isNullOrEmpty(orgNo)){
			sb.append(" AND t.org_no= '").append(orgNo).append("'");
		}else{
			if(StringUtil.isNullOrEmpty(orgList)){
				sb.append(" AND t.org_no= '").append(0).append("'");
			}else{
				sb.append(" AND t.org_no in ").append(orgList.toString().replaceAll("\\[", "(").replaceAll("\\]", ")")).append("");
			}
		}
		sb.append(" AND t.Trade_datatime>='"+stateDate+"' AND t.Trade_datatime<='"+endTime+"'");
		sb.append(" GROUP BY t.Trade_datatime,t.Pay_Source,t.Org_No,t.Pay_Type,t.Pay_Business_Type UNION ALL SELECT DATE_FORMAT(Trade_datatime,'%Y-%m-%d') as Trade_datatime,Pay_Source,org_no,Pay_Type,Pay_Business_Type,'支付',0 1cut,0 1Pay_Amount,'撤销',0 2cut,0 2Pay_Amount,'退费',COUNT(id) 3cut,SUM(t.Pay_Amount) 3Pay_Amount FROM ");
		sb.append(tableName);
//		sb.append(" t WHERE t.Trade_Code  in ("+EnumTypeOfInt.TRADE_CODE_RETURN.getValue()+","+EnumTypeOfInt.TRADE_CODE_REFUND.getValue()+","+EnumTypeOfInt.TRADE_CODE_REVERSAL.getValue()+") and t.is_deleted=0 and t.is_actived=1 ");
		sb.append(" t WHERE t.Pay_Amount<0 and t.is_deleted=0 and t.is_actived=1 ");
		if(!StringUtil.isNullOrEmpty(orgNo)){
			sb.append(" AND t.org_no= '").append(orgNo).append("'");
		}else{
			if(StringUtil.isNullOrEmpty(orgList)){
				sb.append(" AND t.org_no= '").append(0).append("'");
			}else{
				sb.append(" AND t.org_no in ").append(orgList.toString().replaceAll("\\[", "(").replaceAll("\\]", ")")).append("");
			}
		}
		sb.append(" AND t.Trade_datatime>='"+stateDate+"' AND t.Trade_datatime<='"+endTime+"'");
		sb.append("GROUP BY t.Trade_datatime,t.Pay_Source,t.Org_No,t.Pay_Type,t.Pay_Business_Type) a where 1=1 ");
	    return sb.toString();
	}

	@Override
	public List<Map<String, Object>> exportFinanceData(FinanceVo fvo,List<String> orgList) {
		StringBuilder sb=new StringBuilder();
//		if(EnumType.DATA_SOURCE_TYPE_PLAT_CHANNEL.getValue().equals(fvo.getDataSource())){
//			sb.append(spliceSql(CommonConstant.T_REC_PLATFORMFLOW_LOG,fvo.getOrgNo(),orgList));
//		}else if(EnumType.DATA_SOURCE_TYPE_PAY_CHANNEL.getValue().equals(fvo.getDataSource())){
//			sb.append(spliceSql(CommonConstant.T_REC_THRID_BILL,fvo.getOrgNo(),orgList));
//		}else{
//		}
		sb.append(spliceSql(CommonConstant.T_REC_PAY_RESULT,null,fvo.getStartDate(),fvo.getEndDate(),orgList));
//		sb.append(spliceSql(CommonConstant.T_REC_PAY_RESULT,fvo.getOrgNo(),fvo.getStartDate(),fvo.getEndDate(),orgList));
		sb.append(" group by Trade_datatime,Pay_Source,pay_type,org_no,Pay_Business_Type");
		System.out.println(sb.toString());
		List<Map<String,Object>> list = super.handleNativeSql(sb.toString(),new String[]
				{"tradeDatatime","paySource","orgNo","payType","payBusinessType","paySum","payAmount","revokeSum","revokeAmount","refundSum","refundAmount","receiptSum","receiptAmount"});
		return changeFinanceMoIdToName(list);
	}
	
	private List<Map<String,Object>> changeFinanceMoIdToName(List<Map<String,Object>> list){
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String,Object> orgMap = gatherService.getOrgMap();
		if(!StringUtil.isNullOrEmpty(list)){
			Map<String,Object> mapAll = new HashMap<String, Object>();
			Double payAmountAll = 0.00  ;
			Double refundAmountAll = 0.00;
			for(Map<String,Object> map : list){
				map.put("orgNo", orgMap.get(String.valueOf(map.get("orgNo"))));
				map.put("payBusinessType", metaMap.get(String.valueOf(map.get("payBusinessType"))));
				map.put("payType", metaMap.get(String.valueOf(map.get("payType"))));
				map.put("paySource", metaMap.get(String.valueOf(map.get("paySource"))));
				payAmountAll+= Double.valueOf( map.get("payAmount")+"");
				refundAmountAll+=Double.valueOf( map.get("refundAmount")+"");
				
			}
			mapAll.put("tradeDatatime", "合计:");
			mapAll.put("payAmount", new BigDecimal(payAmountAll).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			mapAll.put("refundAmount", new BigDecimal(refundAmountAll).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			list.add(mapAll);
		}
		return list;
	}
	

}
