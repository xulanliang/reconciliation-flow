package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.ThridFollowingRecService;
import com.yiban.rec.service.base.BaseOprService;
@Service
public class ThridFollowingRecServiceImpl extends BaseOprService implements ThridFollowingRecService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeCheckServiceImpl.class);
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	public List<FollowRecResult> getFollowRecMap(String startDate,String endDate,AppRuntimeConfig hConfig) {
			
			//获取配置信息
			StringBuilder searchSql = new StringBuilder();
//			if("1".equals(hConfig.getIsBillsSources())){//处理账单来源
				searchSql.append(" ,bill_source ");
//			}
//			if("1".equals(hConfig.getIsOutpatient())){//拼接门诊/住院字段
				searchSql.append(" ,pat_type ");
//			}
				
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			List<Map<String, Object>> listMap = getFollowCount(searchSql.toString(), startDate, endDate,orgCode );
			List<FollowRecResult> followList = new ArrayList<FollowRecResult>();
			for(Map<String, Object> map : listMap){
				FollowRecResult f = new FollowRecResult();
				f.setHisAllAmount((BigDecimal)map.get("his_all_amount"));
				f.setPayAllAmount((BigDecimal)map.get("pay_all_amount"));
				f.setTradeDiffAmount((BigDecimal)map.get("trade_diff_amount"));
				f.setAlipayAllAmount((BigDecimal)map.get("alipay_all_amount"));
				f.setWechatAllAmount((BigDecimal)map.get("wechat_all_amount"));
				f.setBankAllAmount((BigDecimal)map.get("bank_all_amount"));
				f.setCashAllAmount((BigDecimal)map.get("cash_all_amount"));
				f.setSocialInsuranceAmount((BigDecimal)map.get("social_insurance_amount"));
				f.setHandlerDiffAmount((BigDecimal)map.get("handler_diff_amount"));
				f.setSettlementAmount((BigDecimal)map.get("settlement_amount"));
				f.setBillSource((String)map.get("bill_source"));
				f.setPatType((String)map.get("pat_type"));
				f.setExceptionResult((String)map.get("exceptionResult"));
				f.setOrgNo((String)map.get("org_no"));
				f.setRecPayAllAmount((BigDecimal)map.get("recPayAllAmount"));
				f.setTradeDate(startDate + " - " + endDate);
				followList.add(f);
			}
			
			return followList;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getFollowCount(String searchSql, String startDate, String endDate,String orgNo) {
		String sql="SELECT org_no "+searchSql+" ,SUM(his_all_amount) his_all_amount,SUM(pay_all_amount) pay_all_amount,"
				+" SUM(trade_diff_amount) trade_diff_amount,SUM(alipay_all_amount) alipay_all_amount,SUM(wechat_all_amount) wechat_all_amount, "
				+" SUM(bank_all_amount) bank_all_amount,SUM(cash_all_amount) cash_all_amount,SUM(social_insurance_amount) social_insurance_amount, "
				+" SUM(handler_diff_amount) handler_diff_amount,SUM(settlement_amount) settlement_amount,sum(rec_pay_all_amount) recPayAllAmount,"
				+" exception_result as exceptionResult"
				+" FROM t_follow_rec_result WHERE "
				+" Trade_Date>='"+startDate
				+"' AND  Trade_Date<='"+endDate+"' "
				+" AND org_no = '"+orgNo+"' "
				+" GROUP BY org_no " + searchSql ;
		LOGGER.info("collectCount t_rec_histransactionflow sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
}
