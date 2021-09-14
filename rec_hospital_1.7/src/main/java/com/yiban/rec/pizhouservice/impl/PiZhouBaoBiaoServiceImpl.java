package com.yiban.rec.pizhouservice.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.pizhouservice.PiZhouBaoBiaoService;
import com.yiban.rec.service.ReportSummaryService.SummaryQuery;
import com.yiban.rec.service.base.BaseOprService;

@Service
public class PiZhouBaoBiaoServiceImpl extends BaseOprService implements PiZhouBaoBiaoService {
	
	@Autowired
    private OrganizationService organizationService;
	
	@Override
	public List<Map<String, Object>> findPiZhouBaoBiao(SummaryQuery query,String selectType) {
		// TODO Auto-generated method stub
		
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		String orgList=StringUtils.join(orgCodeSet, ",");
		String startDate = "'"+query.getBeginTime() + " 00:00:00"+"'";
        String endDate = "'"+query.getEndTime() + " 23:59:59"+"'";
        String code=query.getOrgCode();
        String tableName="";
        String sql=null;
        if("中国银行".equals(selectType))
        	sql=getSummaryZgyhSql(orgList, startDate, endDate);
        else if("农商行".equals(selectType))
        	sql=getSummaryNshSql(orgList, startDate, endDate);
        else 
        	sql=getSummmaryAllSql(orgList,startDate,endDate);
        
		logger.info(sql.toString());
		return super.handleNativeSql(sql, new String[] {"ly","registrationNum","registrationAmount",
				"payNum","payAmount","outpatientRechargeNum","outpatientRechargeAmount","zyyjjNum","zyyjjAmount"});
	}
	/**
	 * 汇总所有银行的数据
	 * @param orgList
	 * @return
	 */
	private String getSummmaryAllSql(String orgList,String startDate,String endDate) {
		
		String sql="SELECT '农商行' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149'\r\n" + 
				"	AND bill_source='wnqt' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) yhk\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT '中国银行' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyh\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				"  \r\n" + 
				"  SELECT '中国银行微信' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1}  \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1}  \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyhwx\r\n" + 
				"  \r\n" + 
				"UNION ALL\r\n" + 
				"  \r\n" + 
				"SELECT '中国银行支付宝' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyhzfb\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				"  \r\n" + 
				"SELECT '微信' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249'\r\n" + 
				"	AND bill_source='szzk' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) wx\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				"  \r\n" + 
				"  SELECT '支付宝' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349'\r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self')  \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zfb\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				" \r\n" + 
				" SELECT '总计' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zj;";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	/**
	 * 汇总农商行的数据
	 * @param orgList
	 * @return
	 */
	private String getSummaryNshSql(String orgList,String startDate,String endDate) {
		String sql="SELECT '农商行' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='wnqt'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149'\r\n" + 
				"	AND bill_source='wnqt' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) yhk\r\n" + 
				"UNION ALL\r\n" + 
				"  \r\n" + 
				"SELECT '微信' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='szzk'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249'\r\n" + 
				"	AND bill_source='szzk' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) wx\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				"  \r\n" + 
				"  SELECT '支付宝' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self')\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349'\r\n" + 
				"	AND ( bill_source='szzk' or bill_source='self') \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zfb\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				" \r\n" + 
				" SELECT '总计' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zj;";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	/**
	 * 汇总中国银行的数据
	 * @param orgList
	 * @return
	 */
	private String getSummaryZgyhSql(String orgList,String startDate,String endDate) {
		String sql="SELECT '中国银行' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0149'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyh\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				"  \r\n" + 
				"  SELECT '中国银行微信' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1}  \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1}  \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0249'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyhwx\r\n" + 
				"  \r\n" + 
				"UNION ALL\r\n" + 
				"  \r\n" + 
				"SELECT '中国银行支付宝' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349' \r\n" + 
				"	AND bill_source='zgyh'\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type='0349'\r\n" + 
				"	AND bill_source='zgyh' \r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zgyhzfb\r\n" + 
				"  \r\n" + 
				"  UNION ALL\r\n" + 
				" \r\n" + 
				" SELECT '总计' ly,SUM(registration_num) registration_num,SUM(registration_amount) registration_amount,SUM(pay_num) pay_num,SUM(pay_amount) pay_amount,SUM(outpatient_recharge_num) outpatient_recharge_num,SUM(outpatient_recharge_amount) outpatient_recharge_amount,SUM(zyyjj_num) zyyjj_num,SUM(zyyjj_amount) zyyjj_amount\r\n" + 
				"FROM (\r\n" + 
				"	SELECT COUNT(id) registration_num,IFNULL(SUM(Pay_Amount),0) registration_amount,0 pay_num,0 pay_amount,0 outpatient_recharge_num,0 outpatient_recharge_amount,0 zyyjj_num,0 zyyjj_amount\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0451'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('zgyh')\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0,0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0551'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('zgyh')\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0),0,0\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0151'\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('zgyh')\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT 0,0,0,0,0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"	FROM t_rec_histransactionflow \r\n" + 
				"	WHERE Pay_Business_Type='0751'\r\n" + 
				"	AND pay_type IN ('0149','0249','0349')\r\n" + 
				"	AND bill_source IN ('zgyh')\r\n" + 
				"	AND org_no IN ({0})\r\n" + 
				"	AND Trade_datatime>={1} \r\n" + 
				"	AND Trade_datatime<={2}\r\n" + 
				"  ) zj;";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	
	private Set<String> initOrgCodeSet(String orgCode) {
        Set<String> orgCodeSet = new HashSet<>();
        if (orgCode != null) {
            orgCodeSet.add(orgCode);
            Organization org = organizationService.findByCode(orgCode);
            if (org != null) {
                for (Organization child : org.getChildren()) {
                    orgCodeSet.add(child.getCode());
                }
            }
        }
        return orgCodeSet;
    }

	/**
	 * 汇总邳州住院和门诊的笔数和金额
	 */
	@Override
	public List<Map<String,Object>> findPiZhouBaoBiaoSummary(SummaryQuery query, String selectType) {
		// TODO Auto-generated method stub
		Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
		String orgList=StringUtils.join(orgCodeSet, ",");
		String startDate = "'"+query.getBeginTime() + " 00:00:00"+"'";
        String endDate = "'"+query.getEndTime() + " 23:59:59"+"'";
        String code=query.getOrgCode();
		String sql=null;
		if("中国银行".equals(selectType))
        	sql=getPizhouDataSummaryZgyh(orgList, startDate, endDate);
        else if("农商行".equals(selectType))
        	sql=getPizhouDataSummaryNsh(orgList, startDate, endDate);
        else 
        	sql=getPizhouDataSummaryAll(orgList, startDate, endDate);
		
		logger.info(sql.toString());
		return super.handleNativeSql(sql.toString(), new String[] {"mzNum","mzAmount","zyNum",
				"zyAmount"});
	}
	
	/**
	 * 汇总邳州所有住院和门诊的数据
	 * @param orgList
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private String getPizhouDataSummaryAll(String orgList,String startDate,String endDate) {
		String sql="SELECT SUM(nsh_mz_num) nsh_mz_num,SUM(nsh_mz_amount) nsh_mz_amount,SUM(nsh_zy_num) nsh_zy_num,SUM(nsh_zy_amount) nsh_zy_amount\r\n" + 
				"FROM (\r\n" + 
				"SELECT COUNT(id) nsh_mz_num,IFNULL(SUM(Pay_Amount),0) nsh_mz_amount,0 nsh_zy_num,0 nsh_zy_amount\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0451','0551','0151')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0751')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				") summary;";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	/**
	 * 汇总邳州农商行住院和门诊的数据
	 * @param orgList
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private String getPizhouDataSummaryNsh(String orgList,String startDate,String endDate) {
		String sql="SELECT SUM(nsh_mz_num) nsh_mz_num,SUM(nsh_mz_amount) nsh_mz_amount,SUM(nsh_zy_num) nsh_zy_num,SUM(nsh_zy_amount) nsh_zy_amount\r\n" + 
				"FROM (\r\n" + 
				"SELECT COUNT(id) nsh_mz_num,IFNULL(SUM(Pay_Amount),0) nsh_mz_amount,0 nsh_zy_num,0 nsh_zy_amount\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0451','0551','0151')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0751')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND bill_source IN ('szzk','wnqt','self')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				") nsh;\r\n" + 
				"";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	/**
	 * 汇总邳州中国银行的住院和门诊的数据
	 * @param orgList
	 * @param Date
	 * @param endDate
	 * @return
	 */
	private String getPizhouDataSummaryZgyh(String orgList,String startDate,String endDate) {
		String sql="SELECT SUM(nsh_mz_num) nsh_mz_num,SUM(nsh_mz_amount) nsh_mz_amount,SUM(nsh_zy_num) nsh_zy_num,SUM(nsh_zy_amount) nsh_zy_amount\r\n" + 
				"FROM (\r\n" + 
				"SELECT COUNT(id) nsh_mz_num,IFNULL(SUM(Pay_Amount),0) nsh_mz_amount,0 nsh_zy_num,0 nsh_zy_amount\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0451','0551','0151')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND bill_source IN ('zgyh')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT 0,0,COUNT(id),IFNULL(SUM(Pay_Amount),0)\r\n" + 
				"FROM t_rec_histransactionflow\r\n" + 
				"WHERE Pay_Business_Type IN ('0751')\r\n" + 
				"AND pay_type IN ('0149','0249','0349')\r\n" + 
				"AND bill_source IN ('zgyh')\r\n" + 
				"AND org_no IN ({0})\r\n" + 
				"AND Trade_datatime>={1} \r\n" + 
				"AND Trade_datatime<={2}\r\n" + 
				") zgyh;";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", orgList);
		map.put("1", startDate);
		map.put("2", endDate);		
		return replaceByMap(sql,map);
	}
	
	private String replaceByMap(String content, Map<String, String> param) {
		String regex = "\\{(\\w+)\\}";
		Matcher m = Pattern.compile(regex).matcher(content);
		while (m.find()) {
			String key0 = m.group();
			String key = m.group().substring(1, m.group().length() - 1);
			//System.out.print("key0=" + key0);
			//System.out.print("  key=" + key);
			String value = param.get(key);
			//System.out.println("  value=" + value);
			if (value != null) {
				content = content.replace(key0, param.get(key));
			}
		}
		return content;
	}
	
	public static void main(String[] args) {
		PiZhouBaoBiaoServiceImpl p=new PiZhouBaoBiaoServiceImpl();
		
		System.out.println(p.getSummmaryAllSql("1418701,1418702", "'2020-04-01 00:00:00'", "'2020-04-25 23:59:59'"));
		
	}
	
}
