package com.yiban.rec.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.service.base.BaseOprService;

/**
 * @Description
 * @Author xll 芜湖报表定制汇总数据
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-03-20 16:13
 */
@Service
public class WuHuExcelDatSum extends BaseOprService {

    @Autowired
    private OrganizationService organizationService;

    public List<Map<String, Object>> getFollowCount(String startDate, String endDate, String payTypeSql, String orgNo) {
        String orgNoSql = this.concatOrgNoSql(orgNo);
        final String sql = String.format(
                " SELECT data_source,MAX(CASE WHEN pay_type = '0149' THEN pay_amount END ) '银行卡'," +
                        "MAX(CASE WHEN pay_type = '0249' and pat_type = 'mz' THEN pay_amount END ) '微信（门诊）'," +
                        "MAX(CASE WHEN pay_type = '0249' and pat_type = 'zy' THEN pay_amount END ) '微信（住院）'," +
                        "MAX(CASE WHEN pay_type = '0349' and pat_type = 'mz' THEN pay_amount END ) '支付宝（门诊）', " +
                        "MAX(CASE WHEN pay_type = '0349' and pat_type = 'zy' THEN pay_amount END ) '支付宝（住院）' FROM(" +
                        "SELECT 'his' data_source,pat_type,pay_type,"
                        + " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END),0.00) pay_amount,"
                        + " IFNULL(SUM(IFNULL(pay_acount,0)), 0) payAcount "
                        + " FROM t_follow_summary  WHERE data_source = 'his' "
                        + " AND org_no IN (%s)" + " AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
                        + " AND rec_pay_type in (%s)  AND settlement_amount<=0 group by pay_type,pat_type "
                        + " UNION"
                        + " SELECT 'third' data_source,'mz' pat_type,pay_type,"
                        + " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END), 0.00) pay_amount,"
                        + " IFNULL(SUM(IFNULL(pay_acount,0)), 0) payAcount "
                        + " FROM t_follow_summary  WHERE data_source = 'third' "
                        + " AND org_no IN (%s)" + " AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
                        + " AND rec_pay_type in (%s) group by pay_type"
                        + " UNION"
                        + " SELECT 'settlement' as data_source,'mz' pat_type,pay_type,"
                        + "	IFNULL(SUM(CASE order_state WHEN '0156' THEN settlement_amount ELSE -ABS(settlement_amount) END),0.00) pay_amount,"
                        + " IFNULL(SUM(IF(settlement_amount>0, IFNULL(pay_acount, 0), 0)), 0) payAcount "
                        + " FROM t_follow_summary  WHERE data_source = 'his' "
                        + " AND org_no IN (%s)" + " AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
                        + " AND rec_pay_type in (%s)  AND settlement_amount>0 group by pay_type) a GROUP BY data_source ",
                orgNoSql, startDate, endDate, payTypeSql, orgNoSql, startDate, endDate, payTypeSql, orgNoSql, startDate, endDate,
                payTypeSql);
        logger.info(" getFollowCount sql ============" + sql);
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

}
