package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.bill.parse.service.standardbill.HisReportSummaryService;
import com.yiban.rec.dao.HisReportDao;
import com.yiban.rec.dao.HospitalConfigDao;
import com.yiban.rec.domain.HospitalConfiguration;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.base.BaseOprService;

/**
 * his报表汇总service
 */
@Service
public class HisReportSummaryServiceImpl extends BaseOprService implements HisReportSummaryService {

    @Autowired
    private HisReportDao hisReportDao;

    @Autowired
    private HospitalConfigDao hospitalConfigDao;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 删除当日汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String orgCodeSql, String date) {
        final String sql = String.format("delete from t_follow_summary where trade_date='%s'and org_no IN (%s)", date, orgCodeSql);
        super.execute(sql);
    }

    /**
     * 重新写入汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void summary(String orgCode, String date) {
        logger.info("开始异步进行对账汇总...");
        //父机构，需要将子机构一起查询
        Set<String> orgCodeSet = new HashSet<>();
        orgCodeSet.add(orgCode);
        Organization org = organizationService.findByCode(orgCode);
        if (org != null) {
            for (Organization child : org.getChildren()) {
                orgCodeSet.add(child.getCode());
            }
        }
        AppRuntimeConfig runtimeConfig = this.loadConfig();
        String payType = runtimeConfig.getRecType();
        String[] payTypes = payType.split(",");
        StringBuffer sb = new StringBuffer();
        for (String s : payTypes) {
            sb.append("'");
            sb.append(s);
            sb.append("',");
        }
        sb.delete(sb.length() - 1, sb.length());
        payType = sb.toString();

        //日期拼接
        Long start = System.currentTimeMillis();
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";

        // 先清除历史数据
        String orgCodes = StringUtils.join(orgCodeSet);
        logger.info("需要进行汇总的机构为：" + orgCodes);

        this.delete(orgCodes, date);
        // 将子机构和父机构分开汇总
        for (String orgCodeStr : orgCodeSet) {
            Long countStart = System.currentTimeMillis();

            final String str = "insert into t_follow_summary(org_no,trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state,pay_amount,pay_acount,settlement_amount)"
                    + "select '%s' org_no,A.* from ("
                    + "SELECT DATE(Trade_datatime) trade_date,bill_source,'his' data_source, pay_location," +
                    " CASE pat_type WHEN 'zy' THEN 'zy' ELSE 'mz' END AS pat_type,pay_type AS rec_pay_type,pay_type,order_state, SUM(ABS(Pay_Amount)) pay_amount ,COUNT(1) pay_acount,0 FROM "
                    + "t_rec_histransactionflow WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' "
                    + "AND pay_type IN ('0049'," + payType + ") AND org_no ='%s' "
                    + "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
                    + "UNION "
                    + "SELECT DATE(settlement_date) trade_date,bill_source,'his' data_source, pay_location," +
                    " CASE pat_type WHEN 'zy' THEN 'zy' ELSE 'mz' END AS pat_type,pay_type AS rec_pay_type,pay_type,order_state, 0,COUNT(1) pay_acount, SUM(ABS(Pay_Amount)) pay_amount FROM "
                    + "t_rec_histransactionflow WHERE settlement_date >= '%s' and settlement_date<='%s' "
                    + "AND pay_type IN ('0049'," + payType + ") AND org_no ='%s' "
                    + "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
                    + "UNION "
                    + "SELECT DATE(Trade_datatime) trade_date,bill_source,'third' data_source,'' pay_location," +
                    " CASE pat_type WHEN 'zy' THEN 'zy' ELSE 'mz' END AS pat_type, rec_pay_type, pay_type ,order_state,SUM(ABS(Pay_Amount)) pay_amount,COUNT(1) pay_acount,0 FROM  "
                    + "t_thrid_bill WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no  ='%s' "
                    + "AND rec_pay_type IN ('jdwechat','ccbbank','pfbank'," + payType + ") "
//					+ "AND Pay_Flow_No NOT IN ( " + substractAutoRefundNum(date) + " ) "
                    + "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
                    + "UNION "
                    + "SELECT DATE(Trade_datatime) trade_date,bill_source,'third' data_source, pay_location," +
                    " CASE pat_type WHEN 'zy' THEN 'zy' ELSE 'mz' END AS pat_type,pay_type AS rec_pay_type,pay_type, order_state,SUM(ABS(Pay_Amount)) pay_amount,COUNT(1) pay_acount,0 FROM "
                    + "t_rec_cash WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no  ='%s' "
                    + "GROUP BY trade_date,bill_source,data_source,pay_location,pat_type,rec_pay_type,pay_type,order_state "
                    + ")A";
            String sql = String.format(str,
                    orgCodeStr, startTime, endTime, orgCodeStr, startTime, endTime, orgCodeStr, startTime, endTime, orgCodeStr,
                    startTime, endTime, orgCodeStr);
            try {
				int count = super.execute(sql);
				logger.info(" 对账汇总.summary: " + sql + " \n 汇总条数：" + count + ", 耗时：" + (System.currentTimeMillis() - countStart));
			} catch (Exception e) {
				logger.error("t_follow_summary 汇总异常：{}", e);
			}
        }
        logger.info(orgCode + " 汇总summary总耗时：" + (System.currentTimeMillis() - start));
    }

    public String substractAutoRefundNum(String payDate) {
        String endPayDate = payDate + " 23:59:59";

        String sql = "SELECT f.Pay_Flow_No " +
                "    FROM t_thrid_bill f   " +
                "    WHERE f.Trade_datatime >= '" + payDate + "' " +
                "    AND f.Trade_datatime <= '" + endPayDate + "'  " +
                "    AND f.Order_State = '0256'" +
                "    AND f.Pay_Flow_No NOT IN" +
                "    (" +
                "    SELECT c.business_no Pay_Flow_No FROM t_trade_check_follow c" +
                "    WHERE c.Trade_time >='" + payDate + "'" +
                "    AND c.Trade_time <= '" + endPayDate + "'" +
                "    )" +
                "    AND NOT EXISTS (" +
                "    SELECT 1 FROM t_rec_histransactionflow t" +
                "    WHERE t.Trade_datatime >= '" + payDate + "' " +
                "    AND t.Trade_datatime <= '" + endPayDate + "'  " +
                "    AND t.Order_State = '0256'" +
                "    )";
        return sql;
    }

    @Override
    @Transactional
    public void hisSummary(String orgCode, String date) {
        logger.info("开始异步进行his汇总...");

        // 获取子机构
        List<Organization> orgList = organizationService.findByParentCode(orgCode);
        if (orgList == null) {
            return;
        }
        String orgStr = orgCode;
        for (Organization organization : orgList) {
            if (orgStr.trim().equals("")) {
                orgStr = "'" + organization.getCode() + "'";
            } else {
                orgStr = orgStr + ",'" + organization.getCode() + "'";
            }
        }

        Long start = System.currentTimeMillis();
        // 先清除历史数据
        date = date.trim();
        int deleteCount = hisReportDao.deleteByOrgCodeAndTradeDate(orgCode, date);
        logger.info("已清除历史数据: " + deleteCount);

        // 重新汇总
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        final String summarySql = String.format("INSERT INTO t_his_report ( " +
                "  org_code, " +
                "  trade_date, " +
                "  bill_source, " +
                "  pay_business_type, " +
                "  pat_type, " +
                "  pay_location, " +
                "  order_state, " +
                "  pay_type, " +
                "  cashier, " +
                "  terminal_no, " +
                "  pay_amount, " +
                "  pay_acount " +
                ") " +
                "SELECT " +
                "  org_no org_code, " +
                "  DATE(t.`Trade_datatime`) tradedate, " +
                "  t.`bill_source`, " +
                "  t.`Pay_Business_Type`, " +
                "  t.`pat_type`, " +
                "  t.`pay_location`, " +
                "  t.`Order_State`, " +
                "  t.`pay_type`, " +
                "  t.`Cashier`, " +
                "  t.`terminal_no`, " +
                "  SUM(ABS(t.`Pay_Amount`)), " +
                "  COUNT(1) " +
                "FROM " +
                "  t_rec_histransactionflow t " +
                "WHERE t.`org_no` in ( %s )" +
                "AND t.trade_datatime >='%s' " +
                "AND t.trade_datatime <='%s' " +
                "GROUP BY t.`org_no`, " +
                "  tradedate, " +
                "  t.`bill_source`, " +
                "  t.`Pay_Business_Type`, " +
                "  t.`pat_type`, " +
                "  t.`pay_location`, " +
                "  t.`Order_State`, " +
                "  t.`pay_type`, " +
                "  t.`Cashier`, " +
                "  t.`terminal_no` "
                + " UNION "
                // 医保汇总
                + " SELECT org_no org_code, DATE(trade_datatime) tradedate,bill_source,busness_type Pay_Business_Type,pat_type,'' pay_location,Order_State,'0559' pay_type, cashier Cashier, '' terminal_no ," +
                " SUM(ABS(cost_total_insurance)) Pay_Amount, COUNT(1)" +
                "FROM t_healthcare_his " +
                "WHERE `org_no` in ( %s ) " +
                "AND trade_datatime >='%s' " +
                "AND trade_datatime <='%s' " +
                "GROUP BY `org_no`, bill_source,Pay_Business_Type,pat_type,pay_location,Order_State,pay_type,Cashier,terminal_no ", orgStr, startTime, endTime, orgStr, startTime, endTime);
        logger.info("his报表汇总 summarysql =" + summarySql);
		try {
			int count = super.execute(summarySql);
			logger.info("受影响条数：" + count + ", 耗时 ：" + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			logger.error("t_his_report 报表汇总异常，{}", e);
		}
    }

    public AppRuntimeConfig loadConfig() {
        final List<String> igns = Arrays.asList(new String[]{"orgCode", "isBillsSources",
                "isOutpatient", "isHealthAccount", "healthCheckWays", "checkWays"});
        AppRuntimeConfig dict = new AppRuntimeConfig();
        Class<AppRuntimeConfig> c = AppRuntimeConfig.class;
        List<HospitalConfiguration> configList = hospitalConfigDao.findByActive(1);
        for (HospitalConfiguration config : configList) {
            String fileName = config.getKeyWord();
            String fileValue = config.getKeyValue();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(fileValue)) {
                Field f;
                if (igns.contains(fileName)) {
                    continue;
                }
                try {
                    f = c.getDeclaredField(fileName);
                    f.setAccessible(true);
                    String className = f.getType().getName();
                    if (className.equals(String.class.getName())) {
                        f.set(dict, fileValue);
                    } else if (className.equals(Integer.class.getName())) {
                        f.set(dict, Integer.parseInt(fileValue));
                    } else if (className.equals(Double.class.getName())) {
                        f.set(dict, Double.parseDouble(fileValue));
                    } else if (className.equals(Float.class.getName())) {
                        f.set(dict, Float.parseFloat(fileValue));
                    } else if (className.equals(Boolean.class.getName())) {
                        f.set(dict, Boolean.parseBoolean(fileValue));
                    }
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                        | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return dict;
    }
}
