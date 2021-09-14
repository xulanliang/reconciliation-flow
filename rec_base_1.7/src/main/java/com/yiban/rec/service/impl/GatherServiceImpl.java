package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("gatherService")
public class GatherServiceImpl extends BaseOprService implements GatherService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GatherServiceImpl.class);
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private MetaDataService metaDataService;

	@Override
	public Map<String,Object> getGatherData(String orgNo, String payDate,AppRuntimeConfig hConfig ) {
		Map<String,Object> map = new HashMap<String, Object>();
		JSONArray jsonArray = new JSONArray();
//		String recType = hConfig.getCheckWays(); 
		
		//单边账
		List<Map<String,Object>> singleList = null;
//		if(EnumTypeOfInt.REC_TYPE_THREE.getValue().equals(recType)) {
//			JSONArray platJson = packageDataPay(orgNo,payDate,"plat",hConfig,null);
//			if(null != platJson){
//				jsonArray.addAll(platJson);//交易平台
//			}
//			singleList=getSingleAccount(orgNo,payDate);
//		}else {
			singleList=getSingleAccountTwo(orgNo,payDate);
//		}
		
		//汇总
//		if("0".equals(hConfig.getIsBillsSources())){
//			JSONArray thridJson = packageDataPay(orgNo,payDate,"thrid",hConfig,null);//支付渠道
//			if(null != thridJson){
//				jsonArray.addAll(thridJson);
//			}
//		}else{
			List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue(EnumType.BILL_SOURCE.getValue());
			if(null != metaDataList){
				for (MetaData metaData : metaDataList) {
					JSONArray thridJson = packageDataPay(orgNo,payDate,"thrid",hConfig,metaData);//支付渠道
					if(null != thridJson){
						jsonArray.addAll(thridJson);
					}
				}
			}
//		}
		
		
		
		JSONArray hisJson = packageDataPay(orgNo,payDate,"his",hConfig,null);//his
		if(null != hisJson){
			jsonArray.addAll(hisJson);
		}
		
		map.put("gatherList", jsonArray);
		map.put("singleList", singleList);
		return map;
	}
	
	private Map<String,String> assemblyPayTypes(String payType,List<String> thridTables,List<String> hisTables,List<String> platTables){
		
		Map<String,String> map = new HashMap<String,String>();
		StringBuilder payTypeSql = new StringBuilder();
		StringBuilder hisPayTypeSql = new StringBuilder();
		StringBuilder platPayTypeSql = new StringBuilder();
		
		if(payType == null || payType.length() ==0 ){
			payTypeSql.append("");
			hisPayTypeSql.append("");
			platPayTypeSql.append("");
		}else{
			payTypeSql.append(" and rec_pay_type in ( ");
			hisPayTypeSql.append(" and pay_type in ( ");
			platPayTypeSql.append(" and pay_type in ( ");
			
			boolean contain = false;
			boolean patContain = false;
			if(payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())){// 微信
				payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'" + ",");
				hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'"  + ",");
				platPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'"  + ",");
				contain = true;
				patContain = true;
			}
			if(payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())){// 支付宝
				payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
				hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
				platPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
				contain = true;
				patContain = true;
			}
			if(payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())){// 银行
				payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
				hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
				platPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
				contain = true;
				patContain = true;
			}
			if(payType.contains(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())){// 聚合支付
				payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
				hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
				platPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
				contain = true;
				patContain = true;
			}
			if(payType.contains(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())){// 医保
				platPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue() + "'" + ",");
				patContain = true;
				thridTables.add(CommonConstant.T_HEALTHCARE_OFFICIAL);
				hisTables.add(CommonConstant.T_HEALTHCARE_HIS);
			}
			if(payType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())){// 现金
				patContain = true;
				thridTables.add(CommonConstant.T_REC_CASH);
			}
			if(contain){
				hisPayTypeSql.deleteCharAt(hisPayTypeSql.length()-1);
				payTypeSql.deleteCharAt(payTypeSql.length()-1);
				payTypeSql.append( " ) ");
				hisPayTypeSql.append(" ) ");
			}else{
				hisPayTypeSql.delete(0, hisPayTypeSql.length());
				payTypeSql.delete(0, payTypeSql.length());
			}
			if(patContain){
				platPayTypeSql.deleteCharAt(platPayTypeSql.length()-1);
				platPayTypeSql.append(" ) ");
			}else{
				platPayTypeSql.delete(0, platPayTypeSql.length());
			}
		}
		map.put("his", hisPayTypeSql.toString());
		map.put("third", payTypeSql.toString());
		map.put("plat", platPayTypeSql.toString());
		return map;
	}
	
	private JSONArray packageDataPay(String orgNo, String payDate,String flag,AppRuntimeConfig hConfig,MetaData metaData){
		
		//需要查询的表名称
		List<String> searchTables = null;
		String searchTabel = "";
		String substractAutoRefundNumSql = substractAutoRefundNum(payDate);
		List<String> thridTables = new ArrayList<String>(); //渠道
		thridTables.add(CommonConstant.T_REC_THRID_BILL);
		
		List<String> hisTables = new ArrayList<String>(); //his
		hisTables.add(CommonConstant.T_REC_HIS_TRANSACTION);
		
		List<String> platTables = new ArrayList<String>(); //平台
		platTables.add(CommonConstant.T_REC_PAY_RESULT);
		
		//根据配置来过滤条件表
		Map<String,String> map = assemblyPayTypes(hConfig.getRecType(),thridTables,hisTables,platTables);
		if("thrid".equals(flag)){
			searchTables = thridTables;
			searchTabel = EnumType.DATA_SOURCE_TYPE_PAY_CHANNEL.getValue();
		}else if("his".equals(flag)){
			searchTables = hisTables;
			searchTabel = EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue();
		}else {
			searchTables = platTables;
			searchTabel = EnumType.DATA_SOURCE_TYPE_PLAT_CHANNEL.getValue();
		}
		
		//根据账单来源获取数据
		JSONArray jsonArray = new JSONArray();
		if("thrid".equals(flag)){
//			if("0".equals(hConfig.getIsBillsSources())){
//				jsonArray = changeDataToPageData(orgNo, payDate, searchTables, searchTabel, substractAutoRefundNumSql, map);
//			}else{
				if(null != metaData){
					jsonArray = changeDataToPageData(orgNo, payDate, searchTables, searchTabel, substractAutoRefundNumSql, map,metaData);
				}
//			}
		}else{
			jsonArray = changeDataToPageData(orgNo, payDate, searchTables, searchTabel, substractAutoRefundNumSql, map);
		}
		return jsonArray;
	}
	
	// 将数据转换为页面数据
	private JSONArray changeDataToPageData(String orgNo, String payDate, List<String> searchTables, String searchTabel,
			String substractAutoRefundNumSql, Map<String, String> map) {
		JSONArray jsonArray;
		jsonArray = JSONArray.fromObject(createSQLQuery(spliceGatherSql(searchTables,orgNo,payDate,false,map,substractAutoRefundNumSql,null)));
		JSONArray lsChr = JSONArray.fromObject(createSQLQuery(spliceGatherSql(searchTables,orgNo,payDate,true,map,substractAutoRefundNumSql,null)));
		if(null != jsonArray){
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jbo = jsonArray.getJSONObject(i);
				if("null".equals(jbo.getString("id"))){
					return null;
				}
				jbo.put("dataSource", searchTabel);
				jbo.put("parent", "root");
				jbo.put("id", searchTabel); 
				for(int j=0;j<lsChr.size();j++){
					if(!StringUtil.isNullOrEmpty(lsChr)){
						JSONObject cbo = lsChr.getJSONObject(j);
						cbo.put("parent", searchTabel);
					}
				}
			}
		}
		jsonArray.addAll(lsChr);
		return jsonArray;
	}
	
	// 将数据转换为页面数据
	private JSONArray changeDataToPageData(String orgNo, String payDate, List<String> searchTables, String searchTabel,
			String substractAutoRefundNumSql, Map<String, String> map,MetaData metaData) {
		JSONArray jsonArray;
		jsonArray = JSONArray.fromObject(createSQLQuery(spliceGatherSql(searchTables,orgNo,payDate,false,map,substractAutoRefundNumSql,metaData.getValue())));
		JSONArray lsChr = JSONArray.fromObject(createSQLQuery(spliceGatherSql(searchTables,orgNo,payDate,true,map,substractAutoRefundNumSql,metaData.getValue())));
		if(null != jsonArray){
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jbo = jsonArray.getJSONObject(i);
				if("null".equals(jbo.getString("id"))){
					return null;
				}
				jbo.put("dataSource", searchTabel+metaData.getValue());
				jbo.put("parent", "root");
				jbo.put("id", searchTabel+metaData.getId()); 
				for(int j=0;j<lsChr.size();j++){
					if(!StringUtil.isNullOrEmpty(lsChr)){
						JSONObject cbo = lsChr.getJSONObject(j);
						cbo.put("parent", searchTabel+metaData.getId());
					}
				}
			}
		}
		jsonArray.addAll(lsChr);
		return jsonArray;
	}
	
	//渠道减去自动冲正数据
	public String substractAutoRefundNum(String payDate){
		String endPayDate = payDate+" 23:59:59";
		String sql = " SELECT Pay_Flow_No FROM t_thrid_bill f "+
					" WHERE Trade_datatime >= '"+payDate+"' AND Trade_datatime <= '"+endPayDate+"' "+
					" GROUP BY Pay_Flow_No, shop_flow_no, order_no, out_trade_no,org_no, ABS(Pay_Amount) "+
					" HAVING COUNT(*) = 2 "+
					" AND  NOT EXISTS "+
					" ( SELECT 1 FROM t_rec_histransactionflow t "+
					" WHERE t.Trade_datatime >= '"+payDate+"' AND t.Trade_datatime <= '"+endPayDate+"' "+
					" AND  f.org_no = t.org_no " +
					" AND (f.Pay_Flow_No = t.Pay_Flow_No OR f.shop_flow_no = t.Pay_Flow_No OR f.order_no = t.Pay_Flow_No OR f.out_trade_no = t.Pay_Flow_No) "+
					" ) ";
		return sql;
	}
	
	/**
	 * 
	* @author： tanjian
	* @date：2018年6月24日 
	* @Description：包含查询总数的sql、区分类型的SQL
	* 
	* 滁州 2018-06-22 查询模型
	* 查询总数的sql：
	* SELECT id,Trade_datatime,COUNT(CASE Order_State WHEN '0156' THEN 1 ELSE NULL END) paySum,SUM(CASE Order_State WHEN '0156' THEN Pay_Amount ELSE 0 END) payAmount,COUNT(CASE Order_State WHEN '0256' THEN 1 ELSE NULL END) refundSum,SUM(CASE Order_State WHEN '0256' THEN ABS(Pay_Amount) ELSE 0 END) refundAmount FROM(
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,Pay_Amount
					FROM t_thrid_bill t 
					WHERE  org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 
	  UNION ALL 
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,Pay_Amount
					FROM t_rec_cash t 
					WHERE  org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 	
	  UNION ALL 
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,cost_whole AS Pay_Amount,
					FROM t_healthcare_official t 
					WHERE org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 				
	 )t;
	*  
	* 区分类型的SQL：
	* SELECT dataSource,id,Trade_datatime,COUNT(CASE Order_State WHEN '0156' THEN 1 ELSE NULL END) paySum,SUM(CASE Order_State WHEN '0156' THEN Pay_Amount ELSE 0 END) payAmount,COUNT(CASE Order_State WHEN '0256' THEN 1 ELSE NULL END) refundSum,SUM(CASE Order_State WHEN '0256' THEN ABS(Pay_Amount) ELSE 0 END) refundAmount FROM(
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,Pay_Amount,Pay_Type AS dataSource 
					FROM t_thrid_bill t 
					WHERE  org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 
			UNION ALL 
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,Pay_Amount,'0049' AS dataSource 
					FROM t_rec_cash t 
					WHERE  org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 	
			UNION ALL 
			SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State,cost_whole AS Pay_Amount,'0449' AS dataSource 
					FROM t_healthcare_official t 
					WHERE org_no IN ('5307601','5307602','5307603','5307604','5307605','5307606','5307607','5307608','53076') 
					AND t.is_deleted=0 AND t.is_actived=1 AND t.Trade_datatime>='2018-06-22' AND t.Trade_datatime<='2018-06-22 23:59:59' 				
		)t
		GROUP BY dataSource 
		;
	* 
	 */
	private String spliceGatherSql(List<String> tableNameList,String orgNo, String payDate, boolean isPayType,Map<String,String> map,String substractAutoRefundNumSql,String billSource){
		StringBuilder sb = new StringBuilder();
		String endPayDate = payDate+" 23:59:59";
		
		//区分类型
		String out_first_pay_type = "";
		String inner_pay_type = " ,Pay_Type AS dataSource ";
		String last_first_pay_type = "";
		if(isPayType){
			out_first_pay_type = " dataSource, ";
			last_first_pay_type = " GROUP BY dataSource ";
		}
		
		//拼接单个表，获取单个表的记录
		if(null != tableNameList){
			//最外层，统计总记录数和金额总和
			sb.append("SELECT "+out_first_pay_type+" id,Trade_datatime,COUNT(CASE Order_State WHEN '"+EnumTypeOfInt.TRADE_TYPE_PAY.getValue()+"' THEN 1 ELSE NULL END) paySum,SUM(CASE Order_State WHEN '"+EnumTypeOfInt.TRADE_TYPE_PAY.getValue()+"' THEN Pay_Amount ELSE 0 END) payAmount,COUNT(CASE Order_State WHEN '"+EnumTypeOfInt.TRADE_TYPE_REFUND.getValue()+"' THEN 1 ELSE NULL END) refundSum,SUM(CASE Order_State WHEN '"+EnumTypeOfInt.TRADE_TYPE_REFUND.getValue()+"' THEN ABS(Pay_Amount) ELSE 0 END) refundAmount FROM(");
//			String groupColumn = null;
			for (int i=0;i<tableNameList.size();i++) {
				if(i != 0 ){
					sb.append(" UNION ALL ");
				}
				String payType = null;
				String tableAmount = " Pay_Amount";
				//渠道表
				if(CommonConstant.T_REC_THRID_BILL.equals(tableNameList.get(i))){
					payType = map.get("third");
//					groupColumn = " GROUP BY Pay_Flow_No ,shop_flow_no,order_no,out_trade_no, ABS(Pay_Amount) HAVING count(*) = 1 )t ";
				}
				//his表
				if(CommonConstant.T_REC_HIS_TRANSACTION.equals(tableNameList.get(i))){
					payType = map.get("his");
				}
				//平台表
				if(CommonConstant.T_REC_PAY_RESULT.equals(tableNameList.get(i))){
					payType = map.get("plat");
				}
				//现金
				if(CommonConstant.T_REC_CASH.equals(tableNameList.get(i))){
					inner_pay_type = " ,'0049' AS dataSource ";
				}
				//医保
				if(CommonConstant.T_HEALTHCARE_OFFICIAL.equals(tableNameList.get(i)) || CommonConstant.T_HEALTHCARE_HIS.equals(tableNameList.get(i))){
					inner_pay_type = " ,'0449' AS dataSource ";
					tableAmount = " cost_whole AS Pay_Amount";
				}
				sb.append(" SELECT id, DATE_FORMAT(Trade_datatime,'%Y-%m-%d') AS Trade_datatime,org_no,Order_State," + tableAmount);
				sb.append(inner_pay_type);  
				sb.append(" FROM " + tableNameList.get(i) + " t ");
				sb.append(" where t.is_deleted=0 and t.is_actived=1");
				if(null != payType){
					sb.append(payType); 
				}
				//过滤渠道自动冲正的数据
				if(CommonConstant.T_REC_THRID_BILL.equals(tableNameList.get(i))){
					if(null != billSource){
						sb.append(" AND bill_source = '"+billSource+"' ");
					}
					sb.append(" AND Pay_Flow_No not in (" + substractAutoRefundNumSql + ") "); 
				}
				if(!StringUtil.isNullOrEmpty(orgNo)){
					String orgSql = generateAllOrgSql(orgNo);//拼接机构
					sb.append(orgSql);
				}
				sb.append(" AND t.Trade_datatime>='"+payDate+"' AND t.Trade_datatime<='"+endPayDate+"'");
			}
			//最外层，收尾
			sb.append(")t ");
			sb.append(last_first_pay_type);
		}
		String sql = sb.toString();
		LOGGER.info(" 对账汇总sql: " + sql) ;
		return sql;
	}
	
	private List<Map<String, Object>> getSingleAccount(String orgNo, String payDate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select count(1) as count,sum(ABS(Platform_Amount)+ABS(Org_Amount)+ABS(Third_Amount)) as platformAmount from t_rec_reconciliation   where Is_Different >0 and is_deleted=0 and is_actived=1 and pay_type != '"+EnumTypeOfInt.CASH_PAYTYPE.getValue()+"'");
		if(!StringUtil.isNullOrEmpty(orgNo)){
			String orgSql = generateAllOrgSql(orgNo);//拼接机构
			sb.append(orgSql);
		}
		if (!StringUtil.isNullOrEmpty(payDate)) {// 
			sb.append(" and Pay_Date_stam >= ").append("'").append(DateUtil.transferStringToDate("yyyy-MM-dd",payDate).getTime()/1000).append("'");
			sb.append(" and Pay_Date_stam <= ").append("'").append(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",payDate+" 23:59:59").getTime()/1000).append("'");
		}
		return createSQLQuery(sb.toString());
	}
	
	private List<Map<String, Object>> getSingleAccountTwo(String orgNo, String payDate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select count(1) as count,sum(ABS(trade_amount)) as platformAmount from t_trade_check_follow   where check_state >0 and is_deleted=0");
		if(!StringUtil.isNullOrEmpty(orgNo)){
			String orgSql = generateAllOrgSql(orgNo);//拼接机构
			sb.append(orgSql);
		}
		if (!StringUtil.isNullOrEmpty(payDate)) {// 
			sb.append(" and trade_time >= ").append("'").append(payDate).append("'");
			sb.append(" and trade_time <= ").append("'").append(payDate+" 23:59:59").append("'");
		}
		return createSQLQuery(sb.toString());
	}
	
	private String generateAllOrgSql(String orgNo) {
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
	
	@Override
	public Map<String,Object> getOrgMap(){
		List<Organization> list = organizationService.findAllOrganizations();
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringUtil.isNullOrEmpty(list)){
			for(Organization org : list){
				map.put(String.valueOf(org.getCode()), org.getName());
			}
		}
		return map;
	}
	
	@Override
	public Map<String,Object> getOrgMapFromCode(){
		List<Organization> list = organizationService.findAllOrganizations();
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringUtil.isNullOrEmpty(list)){
			for(Organization org : list){
				map.put(String.valueOf(org.getCode()), org.getName());
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getOrgIdFromCode() {
		List<Organization> list = organizationService.findAllOrganizations();
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringUtil.isNullOrEmpty(list)){
			for(Organization org : list){
				map.put(String.valueOf(org.getCode()), org.getId());
			}
		}
		return map;
	}
}
