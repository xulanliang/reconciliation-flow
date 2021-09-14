package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.service.BillDataReportService;
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.service.base.BaseOprService;

@Service
public class BillDataReportServiceImpl extends BaseOprService implements BillDataReportService {

    Logger logger = LoggerFactory.getLogger(BillDataReportServiceImpl.class);

    @Autowired
    private OrganizationService organizationService;

    @Override
    public List<Map<String, Object>> findAllBillAndHisDataByDate(ReportSummaryService.SummaryQuery query) {
        List<Map<String, Object>> retList = new ArrayList<>();
        String startDate = query.getBeginTime() + " 00:00:00";
        String endDate = query.getEndTime() + " 23:59:59";
        // 获取子机构编码
        Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
        StringBuilder orgCodesb = new StringBuilder();
        for (String orgCode1 : orgCodeSet) {
            orgCodesb.append(orgCode1 + ",");
        }
        if (!orgCodesb.toString().trim().equals("")) {
            orgCodesb.deleteCharAt(orgCodesb.length() - 1);
        } else {
            orgCodesb.append("''");
        }
        // 渠道数据
        String sqlBill = getParamsSql("LEFT JOIN", startDate, endDate, orgCodesb.toString());
        List<Map<String, Object>> listBill = super.queryList(sqlBill, null, null);
        retList.addAll(listBill);
        // His数据
        String sqlHis = getParamsSql("RIGHT JOIN", startDate, endDate, orgCodesb.toString());
        List<Map<String, Object>> listHis = super.queryList(sqlHis, null, null);
        retList.addAll(listHis);
        // 统计数据
        String sumSql = "SELECT * FROM (  " +
                "SELECT '合计' Cashiera, ABS(SUM(CASE t.Order_State WHEN '0156' THEN 1 WHEN '0256' THEN -1 ELSE 0 END)) countBill ,   " +
                "SUM(CASE t.`Order_State` WHEN '0156' THEN ABS(t.Pay_Amount) WHEN '0256' THEN -ABS(t.Pay_Amount) ELSE 0 END) 'payAmountBill' ,  " +
                "SUM(CASE WHEN (t.pay_type ='0149' AND t.`Order_State`= '0156') THEN ABS(t.Pay_Amount) WHEN (t.pay_type ='0149' AND t.`Order_State`= '0256') THEN -ABS(t.Pay_Amount) ELSE 0 END) AS 'bankBill',  " +
                "SUM(CASE WHEN (t.pay_type ='0249' AND t.`Order_State`= '0156') THEN ABS(t.Pay_Amount) WHEN (t.pay_type ='0249' AND t.`Order_State`= '0256') THEN -ABS(t.Pay_Amount) ELSE 0 END) AS 'wechatBill',  " +
                "SUM(CASE WHEN (t.pay_type ='0349' AND t.`Order_State`= '0156') THEN ABS(t.Pay_Amount) WHEN (t.pay_type ='0349' AND t.`Order_State`= '0256') THEN -ABS(t.Pay_Amount) ELSE 0 END) AS 'alipayBill'  " +
                "FROM t_thrid_bill t   " +
                "WHERE 1=1   " +
                "AND t.Trade_datatime >= '" + startDate + "' AND t.Trade_datatime <= '" + endDate + "'" +
                "AND t.org_no in (" + orgCodesb.toString() + ")" +
                "AND t.pay_type in ('0149','0249','0349')" +
                ") t5  " +
                "LEFT JOIN   " +
                "(SELECT '合计' Cashierb, ABS(SUM(CASE hisb.Order_State WHEN '0156' THEN 1 WHEN '0256' THEN -1 ELSE 0 END)) countHis , " +
                "SUM(CASE hisb.`Order_State` WHEN '0156' THEN ABS(hisb.Pay_Amount) WHEN '0256' THEN -ABS(hisb.Pay_Amount) ELSE 0 END) 'payAmountHis' ,  " +
                "SUM(CASE WHEN (hisb.pay_type ='0149' AND hisb.`Order_State`= '0156') THEN ABS(hisb.Pay_Amount) WHEN (hisb.pay_type ='0149' AND hisb.`Order_State`= '0256') THEN -ABS(hisb.Pay_Amount) ELSE 0 END) AS 'bankHis',  " +
                "SUM(CASE WHEN (hisb.pay_type ='0249' AND hisb.`Order_State`= '0156') THEN ABS(hisb.Pay_Amount) WHEN (hisb.pay_type ='0249' AND hisb.`Order_State`= '0256') THEN -ABS(hisb.Pay_Amount) ELSE 0 END) AS 'wechatHis',  " +
                "SUM(CASE WHEN (hisb.pay_type ='0349' AND hisb.`Order_State`= '0156') THEN ABS(hisb.Pay_Amount) WHEN (hisb.pay_type ='0349' AND hisb.`Order_State`= '0256') THEN -ABS(hisb.Pay_Amount) ELSE 0 END) AS 'alipayHis'  " +
                "FROM t_rec_histransactionflow hisb   " +
                "WHERE 1=1   " +
                "AND hisb.Trade_datatime >= '" + startDate + "' AND hisb.Trade_datatime <= '" + endDate + "'" +
                "AND hisb.pay_type in ('0149','0249','0349') " +
                ") t6 ON t5.Cashiera = t6.Cashierb";
        if (retList.size() > 1){
            List<Map<String, Object>> sumListData = super.queryList(sumSql, null, null);
            retList.addAll(sumListData);
        }
        if (retList.size() > 1) {
            // 退款汇总
            String paramsSql = "SELECT COUNT(1) `count`, COALESCE(SUM(t1.Pay_Amount),0) sumPayAmount FROM  " +
                    "(SELECT Order_State, Pay_Flow_No, Pay_Amount FROM t_rec_histransactionflow WHERE Order_State = '0256' AND Trade_datatime >= '" + startDate + "' AND Trade_datatime <= '" + endDate + "' AND pay_type IN ('0149','0249','0349') AND org_no IN(" + orgCodesb.toString() + ")) t1  " +
                    "LEFT JOIN  " +
                    "(SELECT Order_State, Pay_Flow_No, Pay_Amount FROM t_thrid_bill WHERE Order_State = '0256' AND Trade_datatime >= '" + startDate + "' AND Trade_datatime <= '" + endDate + "' AND pay_type IN ('0149','0249','0349') AND org_no IN(" + orgCodesb.toString() + ")) t2 " +
                    "ON (t1.`Pay_Flow_No` = t2.`Pay_Flow_No` AND t1.`Order_State` = t1.`Order_State`) " +
                    "WHERE t2.Pay_Flow_No IS NULL";
            List<Map<String, Object>> refundData = super.queryList(paramsSql, null, null);
            if (refundData.size() > 0) {
                Map<String, Object> refundDataMap = new HashMap<>();
                Map<String, Object> data = refundData.get(0);
                refundDataMap.put("Cashiera", "注：其中含His异常退款" + data.get("count") + "笔，合计 " + data.get("sumPayAmount") + " 元");
                retList.add(refundDataMap);
            }
        }
        return retList;
    }

    public String getParamsSql(String joinName, String startDateStr, String endDateStr, String orgStr) {
        String sql = "SELECT * FROM (  " +
                "SELECT his.Cashier Cashiera," +
                "ABS(SUM(CASE WHEN th.`Order_State` = '0156' THEN 1 WHEN th.`Order_State` = '0256' THEN -1 ELSE 0 END )) 'countBill'," +
                "SUM(CASE th.`Order_State` WHEN '0156' THEN ABS(th.Pay_Amount) WHEN '0256' THEN -ABS(th.Pay_Amount) ELSE 0 END) 'payAmountBill' ,  " +
                "SUM(CASE WHEN (th.pay_type ='0149' AND th.`Order_State`= '0156') THEN ABS(th.Pay_Amount) WHEN (th.pay_type ='0149' AND th.`Order_State`= '0256') THEN -ABS(th.Pay_Amount) ELSE 0 END) AS 'bankBill',  " +
                "SUM(CASE WHEN (th.pay_type ='0249' AND th.`Order_State`= '0156') THEN ABS(th.Pay_Amount) WHEN (th.pay_type ='0249' AND th.`Order_State`= '0256') THEN -ABS(th.Pay_Amount) ELSE 0 END) AS 'wechatBill',  " +
                "SUM(CASE WHEN (th.pay_type ='0349' AND th.`Order_State`= '0156') THEN ABS(th.Pay_Amount) WHEN (th.pay_type ='0349' AND th.`Order_State`= '0256') THEN -ABS(th.Pay_Amount) ELSE 0 END) AS 'alipayBill'  " +
                "FROM `t_thrid_bill` th  LEFT JOIN t_rec_histransactionflow his " +
                "ON (th.`Pay_Flow_No` = his.`Pay_Flow_No` AND th.`Order_State` = his.`Order_State` ) " +
                "WHERE th.Trade_datatime >= '" + startDateStr +
                "' AND th.Trade_datatime <= '" + endDateStr + "'  AND his.Trade_datatime >= '" + startDateStr + "' AND his.Trade_datatime <= '" + endDateStr + "'" +
                "  AND th.org_no in ( " + orgStr + ")" + " AND th.org_no in ( " + orgStr + ")" +
                "  AND th.pay_type in ('0149','0249','0349') AND his.pay_type in ( '0149','0249','0349')" +
                " GROUP BY his.Cashier" +
                " ) t1  " + joinName +
                " (SELECT hise.Cashier Cashierb, " +
                "ABS(SUM(CASE WHEN hise.`Order_State` = '0156' THEN 1 WHEN hise.`Order_State` = '0256' THEN -1 ELSE 0 END )) 'countHis',  " +
                "SUM(CASE hise.`Order_State` WHEN '0156' THEN ABS(hise.Pay_Amount) WHEN '0256' THEN -ABS(hise.Pay_Amount) ELSE 0 END) 'payAmountHis' ,  " +
                "SUM(CASE WHEN (hise.pay_type ='0149' AND hise.`Order_State`= '0156') THEN ABS(hise.Pay_Amount) WHEN (hise.pay_type ='0149' AND hise.`Order_State`= '0256') THEN -ABS(hise.Pay_Amount) ELSE 0 END) AS 'bankHis',  " +
                "SUM(CASE WHEN (hise.pay_type ='0249' AND hise.`Order_State`= '0156') THEN ABS(hise.Pay_Amount) WHEN (hise.pay_type ='0249' AND hise.`Order_State`= '0256') THEN -ABS(hise.Pay_Amount) ELSE 0 END) AS 'wechatHis',  " +
                "SUM(CASE WHEN (hise.pay_type ='0349' AND hise.`Order_State`= '0156') THEN ABS(hise.Pay_Amount) WHEN (hise.pay_type ='0349' AND hise.`Order_State`= '0256') THEN -ABS(hise.Pay_Amount) ELSE 0 END) AS 'alipayHis'   " +
                "FROM t_rec_histransactionflow hise WHERE hise.Trade_datatime >= '" + startDateStr + "' AND hise.Trade_datatime <= '" + endDateStr + "' " +
                " AND hise.org_no in ( " + orgStr + ")" + " AND hise.org_no in ( " + orgStr + ")" +
                " AND hise.pay_type in ('0149','0249','0349') AND hise.pay_type in ( '0149','0249','0349')" +
                "GROUP BY hise.Cashier ) t2 ON t1.Cashiera = t2.Cashierb ";
        return sql;
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


}
