package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.HealthExceptionVo;
import com.yiban.rec.service.HealthBillService;
import com.yiban.rec.service.base.BaseOprService;
@Service
public class HealthBillServiceImpl extends BaseOprService implements HealthBillService {

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;

	private HashMap<String, String> healthAmountTypeMap = new HashMap<>();

	//	@PostConstruct
	public void init() {
		String healthAmountTypeKeySql=propertiesConfigService.findValueByPkey(ProConstants.healthAmountTypeKeySql,
				ProConstants.DEFAULT.get(ProConstants.healthAmountTypeKeySql));
		String healthAmountTypeName=propertiesConfigService.findValueByPkey(ProConstants.healthAmountTypeName, ProConstants.DEFAULT.get(ProConstants.healthAmountTypeName));
		// 将所有的医保对账类型缓存起来
		String[] health = healthAmountTypeKeySql.split(",");
		String[] healthName = healthAmountTypeName.split(",");
		for (int i = 0; i < health.length; i++) {
			healthAmountTypeMap.put(health[i], healthName[i]);
		}
		healthAmountTypeMap.put("合计", "合计");
	}

	/**
	 * 医保汇总
	 */
	public Map<String, Map<String, Object>> getCount(String orgNo, String orgCode,String startDate,String endDate, String payNo, String orderState, String dataSource) {
		Map<String, Map<String, Object>> map=new HashMap<>();
		try {
			// 根据配置获取医院机构的医保对账金额类型（costAll,costBasic）， 然后将单词格式化(cost_all..)
			/*AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
			String healthAmountTypeDB = hConfig.getHealthAmountType();
			char[] charArray = healthAmountTypeDB.toCharArray();
			healthAmountTypeDB = "";
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
					healthAmountTypeDB += "_" + (char)(charArray[i]+32);
				} else {
					healthAmountTypeDB += charArray[i];
				}
			}*/
			// 初始化参数
			init();

			String healthAmountTypeKeySql=propertiesConfigService.findValueByPkey(ProConstants.healthAmountTypeKeySql,
					ProConstants.DEFAULT.get(ProConstants.healthAmountTypeKeySql));
			String[] health = healthAmountTypeKeySql.split(",");

			String healthNameSql="";

			String sql="select ";
			String count=null;
			String where=" where 1=1";
			if (StringUtils.isNotBlank(orgNo)) {
				orgNo = getOrgNo(orgNo);
				where = where + orgNo;
			}
			if (StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)) {
				where = where + " and trade_datatime>=\'" + startDate + "\' and trade_datatime<=\'" + endDate + " 23:59:59\'";
			}
			if (StringUtils.isNotBlank(payNo)) {
				where = where + " and pay_flow_no=\'" + payNo + "\'";
			}
			if (StringUtils.isNotBlank(orderState)) {
				where = where + " and order_state=\'" + orderState + "\'";
			}
			// 数据来源筛选
			if(StringUtils.isNotBlank(dataSource)){
				where = where + " and pat_type in(" + forMaterDataSource(dataSource) + ")";
			}

			if (health != null && health.length > 0) {
				for (int i = 0; i < health.length; i++) {
					sql = sql + "IFNULL(SUM( IF ( Order_State = 0256 ,- ABS(" + health[i] + "), " + health[i]
							+ " )) ,0)" + healthAmountTypeMap.get(health[i]) + ",";
					if (count == null) {
						count = health[i];
					} else {
						count = count + "+" + health[i];
					}
				}
				sql = sql + "IFNULL(SUM( IF ( Order_State = 0256 ,- ABS(" + count + ")," + count + ")),0) 合计";
			} else {
				return map;
			}
			healthNameSql = healthAmountTypeKeySql + ",合计";
			String[] str = healthNameSql.split(",");

			//医保中心汇总
			List<Map<String, Object>> billList = createSQLQuery(sql + " from t_healthcare_official" + where);
			//医保his汇总
			List<Map<String, Object>> hisList = createSQLQuery(sql + " from t_healthcare_his" + where);

			Map<String, Object> hisData = hisList.get(0);
			Map<String, Object> billData = billList.get(0);

			//2方差异汇总
			// 保持str的顺序，使用linkedHashMap
			LinkedHashMap<String, Object> xMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> hisMap= new LinkedHashMap<>();
			LinkedHashMap<String, Object> billMap = new LinkedHashMap<>();

			for (String v : str) {
				String key = healthAmountTypeMap.get(v);
				BigDecimal billAmount = (BigDecimal) billData.get(key);
				BigDecimal hisAmount = (BigDecimal) hisData.get(key);

				BigDecimal diff = (billAmount).abs().subtract((hisAmount));
				xMap.put(key, diff);
				hisMap.put(key, hisAmount);
				billMap.put(key, billAmount);
			}
			map.put("hisList", hisMap);
			map.put("billList", billMap);
			map.put("xList", xMap);
			// 医保抹平处理意见
			List<TradeCheckFollowDeal> listData = tradeCheckFollowDealDao.findByPayFlowNoAndOrgCode(payNo, orgCode);
			if (listData != null && listData.size() > 0) {
				TradeCheckFollowDeal tradeCheckFollowDeal = listData.get(0);
				Map<String, Object> dealMap = new HashMap<>();
				dealMap.put("dealReason", tradeCheckFollowDeal.getDescription());
				dealMap.put("fileLocation", tradeCheckFollowDeal.getFileLocation());
				map.put("dealMap", dealMap);
			}
			return map;
		} catch (Exception e) {
			logger.error("查询医保对账汇总异常：" + e);
			return map;
		}
	}


	public Page<Map<String, Object>> healthException(HealthExceptionVo vo, PageRequest pageable) {
		String srHealthCustom=propertiesConfigService.findValueByPkey(ProConstants.srHealthCustom,
				"false").equals("true")?" and t.costTotalInsurance != 0 or t.costTotalInsuranceHis !=0":"";
		String orgNo = "";
		String where = " where 1=1";
		String columns = "shopFlowNo,busnessType,orgCode,tradeDataTime,healthCode,socialComputerNumber,crossDayRec,payFlowNo,orderState,patientName,healthType,billState,hisState,costTotalInsuranceHis,costTotalInsurance,billCostWhole,billCostAccount,billCostAll,hisCostWhole,hisCostAccount,hisCostAll";
		if (StringUtils.isNotBlank(vo.getOrgNo())) {
			orgNo = getOrgNo(vo.getOrgNo());
			where = where + orgNo;
		}
		if (StringUtils.isNotBlank(vo.getStartTime())&&StringUtils.isNotBlank(vo.getEndTime())) {
			where = where + " and trade_data_time>=\'" + vo.getStartTime() + "\' and trade_data_time<=\'" + vo.getEndTime() + " 23:59:59\'";
		}
		if (StringUtils.isNotBlank(vo.getHealthType())) {
			where = where + " and health_type=\'" + vo.getHealthType() + "\'";
		}
		if (StringUtils.isNotBlank(vo.getDataSource())) {
			where = where + " and pat_type in (" + forMaterDataSource(vo.getDataSource()) + ")";
		}
		String sql = "select * from (" +
				"		select shop_flow_no as shopFlowNo,case group_concat(busness_type) when '0551' then '缴费' when '0851' then '挂号' else busness_type end as busnessType," +
				"		t.org_no orgCode,t.trade_data_time tradeDataTime,group_concat(health_code) healthCode," +
				"		group_concat(social_computer_number) socialComputerNumber, " +
				"		group_concat(cross_day_rec) crossDayRec, " +
				"		t.pay_flow_no payFlowNo,t.order_state orderState,t.patient_name patientName,t.health_type healthType," +
				" 		sum(case when ISNULL(cost_total_insurance)=1 then 0 ELSE 1 end) billState," +
				" 		sum(case when ISNULL(cost_total_insurance_his)=1 then 0 ELSE 1 end) hisState," +
				" 		sum(CASE WHEN order_state = '0256' THEN -ABS(IFNULL(cost_total_insurance_his,0)) else ABS(IFNULL(cost_total_insurance_his,0))end) costTotalInsuranceHis," +
				" 		sum(CASE WHEN order_state = '0256' THEN -ABS(IFNULL(cost_total_insurance,0)) else ABS(IFNULL(cost_total_insurance,0))end) costTotalInsurance," +
				" 		SUM(IF(order_state=0256,-ABS(if(t.check_state=5,t.cost_whole,0)),if(t.check_state=5,t.cost_whole,0))) billCostWhole," +
				"	 	SUM(IF(order_state=0256,-ABS(if(t.check_state=5,t.cost_account,0)),if(t.check_state=5,t.cost_account,0))) billCostAccount," +
				"	 	SUM(IF(order_state=0256,-ABS(if(t.check_state=5,t.cost_all,0)),if(t.check_state=5,t.cost_all,0))) billCostAll," +
				"	 	SUM(IF(order_state=0256,-ABS(if(t.check_state=6,t.cost_whole,0)),if(t.check_state=6,t.cost_whole,0))) hisCostWhole," +
				"	 	SUM(IF(order_state=0256,-ABS(if(t.check_state=6,t.cost_account,0)),if(t.check_state=6,t.cost_account,0))) hisCostAccount," +
				"	 	SUM(IF(order_state=0256,-ABS(if(t.check_state=6,t.cost_all,0)),if(t.check_state=6,t.cost_all,0))) hisCostAll" +
				"	 	from t_health_exception t " + where + " GROUP BY pay_flow_no,order_state) t where 1=1 "+srHealthCustom;
		Log.info("healthException list sql = " + sql);
		Page<Map<String, Object>> data = handleNativeSql(sql, pageable, columns.split(","));
		List<Map<String, Object>> contentData = data.getContent();
		// 查询医保手动平账账单
		List<TradeCheckFollowDeal> listData = tradeCheckFollowDealDao.findByTradeDatetimeBetween(vo.getStartTime().trim(),vo.getEndTime().trim());

		if (listData != null && listData.size() > 0 && contentData != null && contentData.size() > 0) {
			for (Map<String, Object> map : contentData) {
				for (TradeCheckFollowDeal tradeCheckFollowDeal : listData) {
					String payFlowNo = tradeCheckFollowDeal.getPayFlowNo();
					String orgCode = tradeCheckFollowDeal.getOrgCode();
					if (payFlowNo.equals(map.get("payFlowNo")) && orgCode.equals(map.get("orgCode"))) {
						map.put("state", "已抹平");
						break;
					}
					map.put("state", "待处理");
				}
				// 记录隔日平账
				if (Boolean.valueOf(String.valueOf(map.get("crossDayRec")))) {
					map.put("state", "已平账");
				}
			}
		} else {
			for (Map<String, Object> map : contentData) {
				// 记录隔日平账
				if (Boolean.valueOf(String.valueOf(map.get("crossDayRec")))) {
					map.put("state", "已平账");
				} else {
					map.put("state", "待处理");
				}
			}
		}
		return data;
	}

	private String forMaterDataSource(String dataSourceStr){
		String resultStr = "";
		dataSourceStr = (dataSourceStr == null) ? "" : dataSourceStr;
		String[] dataSourceArr = dataSourceStr.split(",");
		for(String dataSource : dataSourceArr){
			if(resultStr.trim().equals("")){
				resultStr = "'" + dataSource + "'";
			} else {
				resultStr = resultStr + ",'" + dataSource + "'";
			}
		}
		return resultStr;
	}



	public String getOrgNo(String orgNo) {
		List<Organization> orgList = null;
		if (null != orgNo) {
			orgList = organizationService.findByParentCode(orgNo);
		}
		String strOrg = " and org_no in(\'" + orgNo + "\'";
		if (orgList != null) {
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}

		}
		strOrg = strOrg + ")";
		return strOrg;
	}
}
