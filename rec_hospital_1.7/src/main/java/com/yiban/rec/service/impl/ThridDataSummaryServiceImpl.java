package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.service.DataSummaryService;
import com.yiban.rec.service.base.BaseOprService;

/**
 * 报表汇总service
 */
@Service
public class ThridDataSummaryServiceImpl extends BaseOprService implements DataSummaryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThridDataSummaryServiceImpl.class);
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private EntityManager entityManager;


    @Override
    public List<Map<String, Object>> findAllSummaryByDate(SummaryQuery query) {

        List<String> billSourceList = new ArrayList<>();
        List<String> billSourceListZy = new ArrayList<>();
        List<String> billSourceListMz = new ArrayList<>();

        String orgNoSql = this.concatOrgNoSql(query.getOrgCode());

        // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
        getRecBillSource(orgNoSql, "rec.mz.billsource", billSourceListMz);
        getRecBillSource(orgNoSql, "rec.zy.billsource", billSourceListZy);
        if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
            billSourceList.addAll(billSourceListMz);
            billSourceList.addAll(billSourceListZy);
        }
        String orgNoSqlStr = "";
        if (StringUtils.isNotBlank(orgNoSql)) {
            orgNoSqlStr = " and org_no in(" + orgNoSql + ")";
        }
        String billSourceStr = "";
        if (billSourceList.size() > 0) {
            for (String billSource : billSourceList) {
                if ("".equals(billSourceStr)) {
                    billSourceStr = " and bill_source in(\'" + billSource + "\'";
                } else {
                    billSourceStr = billSourceStr + ",\'" + billSource + "\'";
                }
            }
            billSourceStr = billSourceStr + " )";
        }
        String table = "thrid".equals(query.getDataSource()) ? "third" : "his";
        /*String beginTime = query.getBeginTime() + " 00:00:00";
        String endTime = query.getEndTime() + " 23:59:59";*/
        String sql = String.format("SELECT tb.org_no orgNo,tb.name," +
                "CASE tb.pat_type WHEN 'mz' THEN '门诊' WHEN 'zy' THEN '住院' END patType," +
                "SUM(CASE tb.pay_type WHEN '0349' THEN tb.payAmount ELSE 0 END) 'aliPay'," +
                "SUM(CASE tb.pay_type WHEN '0349' THEN tb.`count` ELSE 0 END) 'aliCount'," +
                "SUM(CASE tb.pay_type WHEN '0249' THEN tb.payAmount ELSE 0 END) 'weCheat'," +
                "SUM(CASE tb.pay_type WHEN '0249' THEN tb.`count` ELSE 0 END) 'weCheatCount'," +
                "SUM(CASE tb.pay_type WHEN '0149' THEN tb.payAmount ELSE 0 END) 'bank'," +
                "SUM(CASE tb.pay_type WHEN '0149' THEN tb.`count` ELSE 0 END) 'bankCount', " +
                "SUM(tb.payAmount) 'sumPay',SUM(tb.`count`) 'sumCount'" +
                "FROM ( SELECT CASE t.org_no WHEN '1529000' THEN '总院' ELSE '分院' END org_no,bill_source,  t2.name , " +
                "t.pay_type,CASE t.pat_type WHEN 'zy' THEN 'zy' ELSE 'mz' END AS pat_type, " +
                "SUM(CASE t.order_state WHEN '0156' THEN t.pay_acount WHEN '0256' THEN -t.pay_acount ELSE 0 END) AS `count`, " +
                "SUM(CASE t.order_state WHEN '0156' THEN ABS(t.Pay_Amount) WHEN '0256' THEN -ABS(t.Pay_Amount) ELSE 0 END) AS payAmount " +
                "FROM t_follow_summary t LEFT JOIN (SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115') t2 ON t.bill_source = t2.value " +
                "WHERE trade_date >= '%s' and trade_date <= '%s' AND pay_type IN ('0149','0249','0349') " +
                "AND data_source = '" + table + "'" + billSourceStr + orgNoSqlStr +
                "GROUP BY t.bill_source,t.pay_type,t.org_no,pat_type ) tb " +
                "GROUP BY tb.bill_source,tb.org_no,tb.pat_type " +
                "UNION " +
                "SELECT '合计' orgNo,'' `name`,'' patType, " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0349') THEN ABS(Pay_Amount) WHEN (order_state ='0256' AND pay_type ='0349') THEN -ABS(Pay_Amount) ELSE 0 END) AS 'aliPay', " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0349') THEN pay_acount WHEN (order_state ='0256' AND pay_type ='0349') THEN -pay_acount ELSE 0 END) AS 'aliCount', " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0249') THEN ABS(Pay_Amount) WHEN (order_state ='0256' AND pay_type ='0249') THEN -ABS(Pay_Amount) ELSE 0 END) AS 'weCheat', " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0249') THEN pay_acount WHEN (order_state ='0256' AND pay_type ='0249') THEN -pay_acount ELSE 0 END) AS 'weCheatCount', " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0149') THEN ABS(Pay_Amount) WHEN (order_state ='0256' AND pay_type ='0149') THEN -ABS(Pay_Amount) ELSE 0 END) AS 'bank', " +
                "SUM(CASE  WHEN (order_state ='0156' AND pay_type ='0149') THEN pay_acount WHEN (order_state ='0256' AND pay_type ='0149') THEN -pay_acount ELSE 0 END) AS 'bankCount', " +
                "SUM(CASE  WHEN (order_state ='0156') THEN ABS(Pay_Amount) WHEN (order_state ='0256') THEN -ABS(Pay_Amount) ELSE 0 END) AS 'sumPay', " +
                "SUM(CASE  WHEN (order_state ='0156') THEN pay_acount WHEN (order_state ='0256') THEN -pay_acount ELSE 0 END) AS 'sumCount' " +
                "FROM t_follow_summary " +
                "WHERE trade_date >= '%s' and trade_date <= '%s' " + billSourceStr + orgNoSqlStr +
                "AND data_source = '" + table + "'" +
                "AND pay_type IN ('0149','0249','0349')", query.getBeginTime().trim(), query.getEndTime().trim(), query.getBeginTime().trim(), query.getEndTime().trim());
        return super.queryList(sql, null, null);
    }

    public String concatOrgNoSql(String orgNo) {
        List<Organization> orgList = null;
        if (null != orgNo) {
            orgList = organizationService.findByParentCode(orgNo);
        }
        String strOrg = "\'" + orgNo + "\'";
        if (orgList != null) {
            for (Organization v : orgList) {
                strOrg = strOrg + ",\'" + v.getCode() + "\'";
            }

        }
        return strOrg;
    }

    /**
     * 查询配置需要显示的渠道
     *
     * @param orgNo
     * @param pkey
     * @param billSourceList
     * @return
     */
    public String getRecBillSource(String orgNo, String pkey, List<String> billSourceList) {
        String billSourceSql = "";
        // 遍历机构编码
        String[] orgArr = orgNo.split(",");
        for (String orgCode : orgArr) {
            String billSourceStr = ProConfigManager.getValueByPkey(entityManager, pkey + "." +
                    orgCode.replaceAll("'", ""), "");
            if (billSourceStr != null && !"".equals(billSourceStr.trim())) {
                String[] billSourceArr = billSourceStr.split(",");
                // 添加需要显示的渠道
                for (String billSource : billSourceArr) {
                    if (!billSourceList.contains(billSource.trim())) {
                        billSourceList.add(billSource);
                    }
                }
            }
        }
        StringBuffer orgStringBuf = new StringBuffer();
        for (int i = 0; i < billSourceList.size(); i++) {
            if (i == 0) {
                orgStringBuf.append("'" + billSourceList.get(i) + "'");
            } else {
                orgStringBuf.append(",'" + billSourceList.get(i) + "'");
            }
        }
        if (!"".equals(orgStringBuf.toString().trim())) {
            billSourceSql = " and  bill_source in (" + orgStringBuf.toString() + ") ";
        }
        return billSourceSql;
    }

}
