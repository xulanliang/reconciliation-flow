package com.yiban.rec.service.impl;

import java.math.BigDecimal;
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
import com.yiban.rec.domain.reports.AdvancePaySummaryDetailListVo;
import com.yiban.rec.domain.reports.AdvancePaySummaryListVo;
import com.yiban.rec.service.AdvancePaySummaryService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.StringUtil;

@Service
public class AdvancePaySummaryServiceImpl extends BaseOprService implements AdvancePaySummaryService {

    Logger logger = LoggerFactory.getLogger(AdvancePaySummaryServiceImpl.class);

    @Autowired
    private OrganizationService organizationService;

    @Override
    public List<Map<String, Object>> findAdvancePaySummaryByParams(AdvancePaySummaryListVo queryListVo) {

        String recDate = queryListVo.getDate();
        String orgCodeSql = "";
        // 获取子机构编码
        Set<String> orgCodeSet = initOrgCodeSet(queryListVo.getOrgCode());
        StringBuilder orgCodesb = new StringBuilder();
        for (String orgCode1 : orgCodeSet) {
            orgCodesb.append(orgCode1 + ",");
        }
        if (!orgCodesb.toString().trim().equals("")) {
            orgCodesb.deleteCharAt(orgCodesb.length() - 1);
        } else {
            orgCodesb.append("''");
        }
        if (!StringUtil.isNullOrEmpty(orgCodesb.toString())) {
            orgCodeSql = (" and org_no in (" + orgCodesb.toString() + ") ");
        }
        String billSource = queryListVo.getBillSource();
        String billSourceSql = "";
        if (!StringUtil.isNullOrEmpty(billSource)) {
            billSourceSql = " and bill_source = '" + billSource + "' ";
        }
        // 增加款
        String increSumAmountSql = "SELECT SUM(t1.amount) increSumAmount,t1.org_no orgNo,case t1.org_no when '1529000' then '总院' else '分院' end as orgNoName," +
                " t1.bill_source billSource, t3.name billSourceName FROM " +
                "(SELECT * FROM advance_pay_summary   WHERE 1=1 and pay_type != '0049' " +
                "AND order_type = '0' AND DATE_FORMAT(pay_time,'%Y-%m') = '" + recDate + "'" + orgCodeSql + billSourceSql + " ) t1 " +
                "LEFT JOIN " +
                "(SELECT pay_flow_no FROM advance_pay_summary WHERE 1=1 and pay_type != '0049' " +
                "AND order_type IN('1','2') AND DATE_FORMAT(pay_time,'%Y-%m') = '" + recDate + "'" + orgCodeSql + billSourceSql + " AND DATE_FORMAT(recon_date,'%Y-%m') ='" + recDate + "' " +
                ") t2 ON t1.pay_flow_no = t2.pay_flow_no " +
                "LEFT JOIN " +
                " (SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115') t3 on t1.bill_source = t3.value " +
                "WHERE 1=1 AND t2.pay_flow_no IS NULL " +
                "GROUP BY t1.org_no,t1.bill_source ";
        logger.info("##### 增加款：{}", increSumAmountSql);
        List<Map<String, Object>> increSumAmount = super.queryList(increSumAmountSql, null, null);

        // 减少款
        String reduceSumAmountSql = "SELECT tb1.org_no orgNo, case tb1.org_no when '1529000' then '总院' else '分院' end as orgNoName," +
                "tb2.bill_source billSource,SUM(tb1.amount) reduceSumAmount,t3.name billSourceName FROM " +
                "(SELECT * FROM advance_pay_summary WHERE pay_type != '0049' and order_type = '1' AND DATE_FORMAT(recon_date,'%Y-%m') = '" + recDate + "'" + orgCodeSql + billSourceSql + " AND DATE_FORMAT(pay_time,'%Y-%m') != '" + recDate + "' ) tb1  " +
                "LEFT JOIN (SELECT pay_flow_no,bill_source FROM advance_pay_summary WHERE pay_type != '0049' and order_type = '0' AND DATE_FORMAT(pay_time,'%Y-%m') != '" + recDate + "'" + orgCodeSql + billSourceSql + ") tb2 ON tb1.pay_flow_no = tb2.pay_flow_no " +
                "LEFT JOIN " +
                " (SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115') t3 on tb2.bill_source = t3.value " +
                "WHERE  tb2.pay_flow_no IS NOT NULL " +
                "GROUP BY tb1.org_no,tb2.bill_source";
        logger.info("##### 减少款：{}", reduceSumAmountSql);
        List<Map<String, Object>> reduceSumAmount = super.queryList(reduceSumAmountSql, null, null);

        // 拼接数据
        List<Map<String, Object>> returnMapData = getMapData(increSumAmount, reduceSumAmount);
        if (returnMapData.size() < 1) {
            return returnMapData;
        }
        // 添加合计
        Map<String, Object> sumMapData = new HashMap<>();
        BigDecimal increSumAmountSum = new BigDecimal(0);
        BigDecimal reduceSumAmountSum = new BigDecimal(0);
        BigDecimal sum = new BigDecimal(0);
        for (Map<String, Object> map : returnMapData) {
            BigDecimal increSumAmountExt = (BigDecimal) map.get("increSumAmount");
            BigDecimal reduceSumAmountExt = (BigDecimal) map.get("reduceSumAmount");
            BigDecimal sumExt = (BigDecimal) map.get("sum");
            increSumAmountSum = increSumAmountSum.add(increSumAmountExt);
            reduceSumAmountSum = reduceSumAmountSum.add(reduceSumAmountExt);
            sum = sum.add(sumExt);
        }
        sumMapData.put("orgNoName", "合计");
        sumMapData.put("reduceSumAmount", reduceSumAmountSum);
        sumMapData.put("increSumAmount", increSumAmountSum);
        sumMapData.put("sum", sum);
        returnMapData.add(sumMapData);
        return returnMapData;
    }

    @Override
    public List<Map<String, Object>> findAdvancePaySummaryDetailListByParams(AdvancePaySummaryDetailListVo queryListVo) {
        // 增加款
        String increSumAmountDetailListSql = "SELECT t1.bill_source billSource,t3.name billSourceName,t1.org_no orgNo,case t1.org_no when '1529000' then '总院' else '分院' end as orgNoName," +
                "t1.pay_flow_no payFlowNo,case t1.pay_type when '0349' then '支付宝' when '0249' then '微信' when '0149' then '银行卡' else '现金' end as payTypeName, " +
                "t1.pay_type payType,t1.amount,DATE_FORMAT(t1.pay_time,'%Y-%m-%d') payTime " +
                "FROM (SELECT * FROM advance_pay_summary   WHERE 1=1 and pay_type != '0049' " +
                "AND order_type = '0' AND DATE_FORMAT(pay_time,'%Y-%m') = '" + queryListVo.getDate() + "' and org_no = '" +
                queryListVo.getOrgCode() + "' and bill_source = '" + queryListVo.getBillSource() + "' ) t1 " +
                "LEFT JOIN " +
                "(SELECT pay_flow_no FROM advance_pay_summary WHERE 1=1 and pay_type != '0049' " +
                "AND order_type IN('1','2') AND DATE_FORMAT(pay_time,'%Y-%m') = '" + queryListVo.getDate() + "' and org_no = '" +
                queryListVo.getOrgCode() +
                "' AND DATE_FORMAT(recon_date,'%Y-%m') ='" + queryListVo.getDate() + "' " +
                ") t2 ON t1.pay_flow_no = t2.pay_flow_no " +
                "LEFT JOIN " +
                "(SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115') t3 on t1.bill_source = t3.value " +
                " WHERE 1=1 AND t2.pay_flow_no IS NULL ";

        // 减少款
        String reduceSumAmountDetailListSql = "SELECT tb2.bill_source billSource,t3.name billSourceName,tb1.org_no orgNo,case tb1.org_no when '1529000' then '总院' else '分院' end as orgNoName, " +
                "tb1.pay_flow_no payFlowNo,case tb1.pay_type when '0349' then '支付宝' when '0249' then '微信' when '0149' then '银行卡' else '现金' end as payTypeName," +
                "tb1.pay_type payType,tb1.amount,DATE_FORMAT(tb1.pay_time,'%Y-%m-%d') payTime,DATE_FORMAT(tb1.server_date,'%Y-%m-%d') serverDate " +
                " FROM (SELECT * FROM advance_pay_summary WHERE order_type = '1' and pay_type != '0049' AND DATE_FORMAT(recon_date,'%Y-%m') = '" + queryListVo.getDate() + "' and org_no = '" +
                queryListVo.getOrgCode() + "' and bill_source = '" + queryListVo.getBillSource() + "' AND DATE_FORMAT(pay_time,'%Y-%m') != '" + queryListVo.getDate() + "' ) tb1  " +
                "LEFT JOIN (SELECT pay_flow_no,bill_source FROM advance_pay_summary WHERE pay_type != '0049' and order_type = '0' AND DATE_FORMAT(pay_time,'%Y-%m') != '"
                + queryListVo.getDate() + "' and org_no = '" + queryListVo.getOrgCode() + "' and bill_source = '" + queryListVo.getBillSource() + "') tb2 ON tb1.pay_flow_no = tb2.pay_flow_no " +
                "LEFT JOIN " +
                "(SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115') t3 on tb1.bill_source = t3.value " +
                " WHERE  tb2.pay_flow_no IS NOT NULL ";
        if ("1".equals(queryListVo.getDetailType())) {
            // 增加款
            logger.info("##### 增加款详情列表：{}", increSumAmountDetailListSql);
            return super.queryList(increSumAmountDetailListSql, null, null);
        } else {
            // 增加款
            logger.info("##### 减少款详情列表：{}", reduceSumAmountDetailListSql);
            return super.queryList(reduceSumAmountDetailListSql, null, null);
        }
    }

    /**
     * 获取渠道下拉列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getBillSourceDataList() {
        String sql = "SELECT `name`,`value` FROM t_meta_data WHERE type_id = '115'";
        return super.queryList(sql, null, null);
    }

    /**
     * 组装数据
     *
     * @param increSumAmountList  增加款
     * @param reduceSumAmountList 减少款
     * @return
     */
    private List<Map<String, Object>> getMapData(List<Map<String, Object>> increSumAmountList, List<Map<String, Object>> reduceSumAmountList) {
        List<String> keyList = new ArrayList<>();
        List<Map<String, Object>> returnData = new ArrayList<>();

        if (increSumAmountList.size() == 0 && reduceSumAmountList.size() == 0) {
            return returnData;
        } else if (increSumAmountList.size() == 0 && reduceSumAmountList.size() > 0) {
            for (Map<String, Object> dataMap : reduceSumAmountList) {
                BigDecimal increSumAmount = new BigDecimal(0);
                BigDecimal reduceSumAmount = (BigDecimal) dataMap.get("reduceSumAmount");
                Map<String, Object> currentMap = new HashMap<>();
                currentMap.put("orgNo", dataMap.get("orgNo"));
                currentMap.put("billSource", dataMap.get("billSource"));
                currentMap.put("orgNoName", dataMap.get("orgNoName"));
                currentMap.put("billSourceName", dataMap.get("billSourceName"));
                currentMap.put("increSumAmount", increSumAmount);
                currentMap.put("reduceSumAmount", reduceSumAmount);
                currentMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                returnData.add(currentMap);
            }
            return returnData;
        } else if (increSumAmountList.size() > 0 && reduceSumAmountList.size() == 0) {
            for (Map<String, Object> dataMap : increSumAmountList) {
                BigDecimal increSumAmount = (BigDecimal) dataMap.get("increSumAmount");
                BigDecimal reduceSumAmount = new BigDecimal(0);
                Map<String, Object> currentMap = new HashMap<>();
                currentMap.put("orgNo", dataMap.get("orgNo"));
                currentMap.put("billSource", dataMap.get("billSource"));
                currentMap.put("billSourceName", dataMap.get("billSourceName"));
                currentMap.put("orgNoName", dataMap.get("orgNoName"));
                currentMap.put("increSumAmount", increSumAmount);
                currentMap.put("reduceSumAmount", reduceSumAmount);
                currentMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                returnData.add(currentMap);
            }
            return returnData;
        }

        // 先循环增加款
        for (int i = 0; i < increSumAmountList.size(); i++) {
            Map<String, Object> increMap = increSumAmountList.get(i);
            String increOrgNo = String.valueOf(increMap.get("orgNo"));
            String increBillSource = String.valueOf(increMap.get("billSource"));
            if (!keyList.contains(increOrgNo + increBillSource)) {
                // 不重复
                for (int j = 0; j < reduceSumAmountList.size(); j++) {
                    Map<String, Object> reduceMap = reduceSumAmountList.get(j);
                    String reduceOrgNo = String.valueOf(reduceMap.get("orgNo"));
                    String reduceBillSource = String.valueOf(reduceMap.get("billSource"));
                    if ((increOrgNo.equals(reduceOrgNo) && increBillSource.equals(reduceBillSource))) {
                        BigDecimal increSumAmount = (BigDecimal) increMap.get("increSumAmount");
                        BigDecimal reduceSumAmount = (BigDecimal) reduceMap.get("reduceSumAmount");
                        Map<String, Object> addMap = new HashMap<>();
                        addMap.put("orgNo", increOrgNo);
                        addMap.put("billSource", increBillSource);
                        addMap.put("billSourceName", increMap.get("billSourceName"));
                        addMap.put("orgNoName", increMap.get("orgNoName"));
                        addMap.put("increSumAmount", increSumAmount);
                        addMap.put("reduceSumAmount", reduceSumAmount);
                        addMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                        returnData.add(addMap);
                        break;
                    } else if (j == reduceSumAmountList.size() - 1) {
                        BigDecimal increSumAmount = (BigDecimal) increMap.get("increSumAmount");
                        BigDecimal reduceSumAmount = new BigDecimal(0);
                        Map<String, Object> addMap = new HashMap<>();
                        addMap.put("orgNo", increOrgNo);
                        addMap.put("billSource", increBillSource);
                        addMap.put("billSourceName", increMap.get("billSourceName"));
                        addMap.put("orgNoName", increMap.get("orgNoName"));
                        addMap.put("increSumAmount", increSumAmount);
                        addMap.put("reduceSumAmount", reduceSumAmount);
                        addMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                        returnData.add(addMap);
                        break;
                    }
                }
                keyList.add(increOrgNo + increBillSource);
            } else {
                // 重复
                continue;
            }
        }

        // 再循环减少款
        for (int i = 0; i < reduceSumAmountList.size(); i++) {
            Map<String, Object> reduceMap = reduceSumAmountList.get(i);
            String reduceOrgNo = String.valueOf(reduceMap.get("orgNo"));
            String reduceBillSource = String.valueOf(reduceMap.get("billSource"));
            if (!keyList.contains(reduceOrgNo + reduceBillSource)) {
                // 不重复
                for (int j = 0; j < increSumAmountList.size(); j++) {
                    Map<String, Object> increMap = increSumAmountList.get(j);
                    String increOrgNo = String.valueOf(increMap.get("orgNo"));
                    String increBillSource = String.valueOf(increMap.get("billSource"));
                    if ((reduceOrgNo.equals(increOrgNo) && reduceBillSource.equals(increBillSource))) {
                        BigDecimal increSumAmount = (BigDecimal) increMap.get("increSumAmount");
                        BigDecimal reduceSumAmount = (BigDecimal) reduceMap.get("reduceSumAmount");
                        Map<String, Object> addMap = new HashMap<>();
                        addMap.put("orgNo", reduceOrgNo);
                        addMap.put("billSource", reduceBillSource);
                        addMap.put("billSourceName", reduceMap.get("billSourceName"));
                        addMap.put("orgNoName", reduceMap.get("orgNoName"));
                        addMap.put("increSumAmount", increSumAmount);
                        addMap.put("reduceSumAmount", reduceSumAmount);
                        addMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                        returnData.add(addMap);
                        break;
                    } else if (j == increSumAmountList.size() - 1) {
                        BigDecimal increSumAmount = new BigDecimal(0);
                        BigDecimal reduceSumAmount = (BigDecimal) reduceMap.get("reduceSumAmount");
                        Map<String, Object> addMap = new HashMap<>();
                        addMap.put("orgNo", reduceOrgNo);
                        addMap.put("billSource", reduceBillSource);
                        addMap.put("billSourceName", reduceMap.get("billSourceName"));
                        addMap.put("orgNoName", reduceMap.get("orgNoName"));
                        addMap.put("increSumAmount", increSumAmount);
                        addMap.put("reduceSumAmount", reduceSumAmount);
                        addMap.put("sum", increSumAmount.subtract(reduceSumAmount));
                        returnData.add(addMap);
                        break;
                    }
                }
                keyList.add(reduceOrgNo + reduceBillSource);
            } else {
                // 重复
                continue;
            }
        }
        return returnData;
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
