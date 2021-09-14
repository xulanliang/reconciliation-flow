package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.dao.OrderUploadDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.dao.baseinfo.ShopInfoDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.DataSource;
import com.yiban.rec.domain.vo.HisRequestVo;
import com.yiban.rec.domain.vo.HisResponseVo;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.domain.vo.TradeCheckVo;
import com.yiban.rec.service.ElectronicRecService;
import com.yiban.rec.service.HisService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.HisInterfaceType;
import com.yiban.rec.util.RefundStateEnum;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.util.TitleStateEnum;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 电子对账service
 *
 * @author clearofchina
 */
@Service
public class ElectronicRecServiceImpl extends BaseOprService implements ElectronicRecService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PropertiesConfigService propertiesConfigService;

    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private HospitalConfigService hospitalConfigService;

    @Autowired
    private TradeCheckFollowDealDao tradeCheckFollowDealDao;

    @Autowired
    private ThirdBillDao thirdBillDao;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TradeCheckFollowDao tradeCheckFollowDao;
    @Autowired
    private ExcepHandingRecordDao excepHandingRecordDao;
    @Autowired
    private ShopInfoDao shopInfoDao;
    @Autowired
    private HisService hisService;

    @Autowired
    private OrderUploadDao orderUploadDao;

    @Override
    public ResponseResult getFollowRecMap(String orgNo, String startDate, String endDate, String patType) {
        ResponseResult result = ResponseResult.success();
        // 根据配置信息查询结果
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());

        String orgNoSql = this.concatOrgNoSql(orgNo);

        List<Map<String, Object>> listMap = getFollowCount(startDate, endDate, payTypeSql, orgNoSql);
        // 获取长短款笔数的统计 start
        // 获取已处理的异常账单流水号
	/*	List<Map<String, String>> businessNoList = getDealedPayFlowNoList(startDate, endDate, payTypeSql, orgNoSql);
		String businessNos = "";
		for (Map<String, String> businessNo : businessNoList) {
			businessNos += "'" + businessNo.get("businessNo") + "',";
		}
		if (StringUtils.isEmpty(businessNos)) {
			businessNos = "'-1'";
		} else {
			businessNos = businessNos.substring(0, businessNos.length() - 1);
		}
		// 获取未处理的长短款金额和笔数
		List<Map<String, Object>> shotLongAcountList = this.getAllShotLongAcount(startDate, endDate, payTypeSql, orgNoSql, businessNos);
		*/
        // 获取长短款笔数的统计 end

        // 获取所有长短款笔数和金额
        List<Map<String, Object>> shotLongAcountList = this.getAllShotLongAcount(startDate, endDate, payTypeSql, orgNoSql);


        // 1. 获取 总计
        FollowRecResult f = new FollowRecResult();
        if (null != listMap) {
            for (Map<String, Object> map : listMap) {
                // his应收
                if ("his".equals((String) map.get("data_source"))) {
                    f.setHisAllAmount(new BigDecimal(map.get("pay_amount").toString()));
                    f.setHisPayAcount(new BigDecimal(map.get("payAcount").toString()).intValue());
                    continue;
                }
                // 实收
                if ("third".equals((String) map.get("data_source"))) {
                    f.setPayAllAmount(new BigDecimal(map.get("pay_amount").toString()));
                    f.setPayAcount(new BigDecimal(map.get("payAcount").toString()).intValue());
                    continue;
                }
                // his实收
                /*if ("settlement".equals((String) map.get("data_source"))) {
                    f.setSettlementAmount(new BigDecimal(map.get("pay_amount").toString()));
                    f.setSettlementPayAcount(new BigDecimal(map.get("payAcount").toString()).intValue());
                    continue;
                }*/
            }
            // TODO 差异 计算方式错误
            if (null != f.getHisAllAmount() && f.getPayAllAmount() != null) {
                f.setTradeDiffAmount(f.getPayAllAmount().subtract(f.getHisAllAmount()));
                f.setTradeDiffPayAcount(Math.abs(f.getHisPayAcount() - f.getPayAcount()));
            }
            f.setOrgNo(orgNo);

            // 设置未处理单边账笔数
            if (shotLongAcountList.size() > 0) {
                f.setUntreatedHisAcount(Integer.valueOf(shotLongAcountList.get(0).get("shotAcount").toString()));
                f.setUntreatedThirdAcount(Integer.valueOf(shotLongAcountList.get(0).get("longAcount").toString()));
                f.setUntreatedThirdAmount(shotLongAcountList.get(0).get("longAmount") == null ? new BigDecimal(0.00)
                        : new BigDecimal(shotLongAcountList.get(0).get("longAmount").toString()));
                f.setUntreatedHisAmount(shotLongAcountList.get(0).get("shotAmount") == null ? new BigDecimal(0.00)
                        : new BigDecimal(shotLongAcountList.get(0).get("shotAmount").toString()));
            }
        }

        // 2. 获取支付方式数据
        List<Map<String, Object>> list = this.getFollowPayInfoDetail(startDate, endDate, payTypeSql, orgNoSql);
        List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
        Map<String, List<Map<String, Object>>> payDetailMap = this.dealData(list, metaDataList);

        List<String> billSourceList = new ArrayList<>();
        List<String> billSourceListZy = new ArrayList<>();
        List<String> billSourceListMz = new ArrayList<>();

        if (StringUtils.isNotBlank(patType)){
            if ("zy".equals(patType)) {
                // 住院数据
                getRecBillSource(orgNoSql, "rec.zy.billsource", billSourceList);
            } else if ("mz".equals(patType)) {
                // 门诊数据
                getRecBillSource(orgNoSql, "rec.mz.billsource", billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            getRecBillSource(orgNoSql, "rec.mz.billsource", billSourceListMz);
            getRecBillSource(orgNoSql, "rec.zy.billsource", billSourceListZy);
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
        }
        Iterator<Map.Entry<String, List<Map<String, Object>>>> it = payDetailMap.entrySet().iterator();
        if (billSourceListZy.size() > 0 && billSourceListMz.size() > 0) {
            while (it.hasNext()) {
                Map.Entry<String, List<Map<String, Object>>> entry = it.next();
                String key = entry.getKey();
                if (!billSourceList.contains(key)) {
                    it.remove();
                }
            }
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("recResult", f);
        resMap.put("payDetailMap", payDetailMap);
        resMap.put("untreadCount", shotLongAcountList.size() > 0 ? shotLongAcountList.get(0) : null);
        return result.data(resMap);
    }

    public Map<String, List<Map<String, Object>>> dealData(List<Map<String, Object>> list, List<MetaData> metaDataList) {
        Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();

        for (int j = 0; j < metaDataList.size(); j++) {
            MetaData metaData = metaDataList.get(j);
            String billSourceStr = metaData.getValue();
            for (int i = 0, len = list.size(); i < len; i++) {
                Map<String, Object> map = list.get(i);
                // 统计支付方面数据 start
                String billSource = (String) map.get("billSource");
                if (billSource.equals(billSourceStr)) {
                    List<Map<String, Object>> listMap = null;
                    if (dataMap.containsKey(billSource)) {
                        listMap = dataMap.get(billSource);
                    } else {
                        listMap = new ArrayList<>();
                    }
                    // 下面的方形div数据统计
                    HashMap<String, Object> valueMap = new HashMap<>();
                    valueMap.put("payType", map.get("payType"));
                    valueMap.put("payAmount", map.get("payAmount"));
                    valueMap.put("realPayAcount", map.get("realPayAcount"));
                    valueMap.put("refundAcount", map.get("refundAcount"));
                    listMap.add(valueMap);
                    dataMap.put(billSource, listMap);
                    // 统计支付方面数据 end
                } else {
                    continue;
                }
            }
            if (!dataMap.containsKey(billSourceStr)) {
                dataMap.put(billSourceStr, new ArrayList<>());
            }
        }
        // 统计每个方形div的总和
        payInfoSummary(dataMap);
        return dataMap;
    }

    // 对支付详情的总和
    public Map<String, List<Map<String, Object>>> payInfoSummary(Map<String, List<Map<String, Object>>> dataMap) {

        for (List<Map<String, Object>> list : dataMap.values()) {
            BigDecimal payAmountSum = new BigDecimal(0);
            BigDecimal realPayAcountSum = new BigDecimal(0);
            BigDecimal refundAcountSum = new BigDecimal(0);

            for (Map<String, Object> map : list) {
                payAmountSum = payAmountSum.add(new BigDecimal(map.get("payAmount").toString()));
                realPayAcountSum = realPayAcountSum.add(new BigDecimal(map.get("realPayAcount").toString()));
                refundAcountSum = refundAcountSum.add(new BigDecimal(map.get("refundAcount").toString()));
            }
            Map<String, Object> sumMap = new HashMap<>();
            sumMap.put("payType", "sum");
            sumMap.put("payAmountSum", payAmountSum);
            sumMap.put("realPayAcountSum", realPayAcountSum);
            sumMap.put("refundAcountSum", refundAcountSum);
            list.add(0, sumMap);
        }
        return dataMap;
    }

    public static String combinationPayTypeSql(String payType) {
        StringBuilder sql = new StringBuilder();
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())) {// 微信
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())) {// 支付宝
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())) {// 银行
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())) {// 聚合支付
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_HEALTH.getValue())) { // 医保支付
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_HEALTH.getValue() + "'" + ",");
        }
        if(payType.contains(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue())){//一账通
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_WJYZT.getValue() + "'" + ",");
        }
        if(payType.contains(EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue())){//云闪付
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_WEIMAI.getValue())) { // 微脉支付
            sql.append("'" + EnumTypeOfInt.PAY_TYPE_WEIMAI.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())) { //社保卡银行卡
        	sql.append("'" + EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue() + "'" + ",");
        }
        if (payType.contains(EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue())) {
        	sql.append("'" + EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue() + "'" + ",");
        }
        if (sql.length() > 0) {
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFollowCount(String startDate, String endDate, String payTypeSql,
                                                     String orgNo) {
        final String sql = String.format(
                " SELECT 'his' data_source,"
                        + " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END),0.00) pay_amount,"
                        + " IFNULL(SUM(IFNULL(pay_acount,0)), 0) payAcount "
                        + " FROM t_follow_summary  WHERE data_source = 'his' "
                        + " AND org_no IN (%s)" + " AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
                        + " AND rec_pay_type in (%s)  AND settlement_amount<=0  "
                        + " UNION"
                        + " SELECT 'third' data_source,"
                        + " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END), 0.00) pay_amount,"
                        + " IFNULL(SUM(IFNULL(pay_acount,0)), 0) payAcount "
                        + " FROM t_follow_summary  WHERE data_source = 'third' "
                        + " AND org_no IN (%s)" + " AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
                        + " AND rec_pay_type in (%s)",
                orgNo, startDate, endDate, payTypeSql, orgNo, startDate, endDate, payTypeSql);
        logger.info(" getFollowCount sql ============" + sql);
        return super.queryList(sql, null, null);
    }

    /**
     * 查询支付类型的详情
     *
     * @param startDate
     * @param endDate
     * @param payTypeSql
     * @param orgNo
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getFollowPayInfoDetail(String startDate, String endDate, String payTypeSql,
                                                            String orgNo) {
        final String sql = String.format(
                "SELECT tfs.`bill_source` billSource,  tfs.`rec_pay_type` payType," +
                        "	 SUM(CASE order_state WHEN '0156' THEN tfs.`pay_amount` ELSE - ABS(tfs.`pay_amount`) END) payAmount, " +
                        "	 SUM(CASE tfs.`order_state` WHEN '0256' THEN 0 ELSE IF(tfs.`pay_acount` IS NULL, 0, tfs.`pay_acount`) END) realPayAcount, " +
                        "	 SUM(CASE tfs.`order_state` WHEN '0256' THEN tfs.`pay_acount` ELSE 0 END) refundAcount" +
                        " FROM t_follow_summary tfs " +
                        "  WHERE org_no IN (%s)" +
                        "  AND Trade_Date >= '%s'" +
                        "  AND Trade_Date <= '%s'" +
                        "  AND rec_pay_type IN (%s)" +
                        "  AND data_source = 'third' " +
                        " GROUP BY tfs.bill_source, tfs.`rec_pay_type` ORDER BY tfs.`bill_source` DESC",
                orgNo, startDate, endDate, payTypeSql);
        logger.info(" getFollowPayInfoDetail sql============" + sql);
        return super.queryList(sql, null, null);
    }

    /**
     * 查询处理过的异常账单流水号
     *
     * @return
     */
    public List<Map<String, String>> getDealedPayFlowNoList(String startDate, String endDate, String payTypeSql,
                                                            String orgNo) {
        String startDateMHS = startDate + " 00:00:00";
        String endDateMHS = endDate + " 23:59:59";
        final String sql = String.format(
                " SELECT tt.`business_no` businessNo FROM t_trade_check_follow tt INNER JOIN t_exception_handling_record te "
                        + " ON tt.`business_no`=te.`Payment_Request_Flow` WHERE "
                        + " tt.`org_no` IN (%s) AND tt.`trade_date`>='%s' AND tt.`trade_date`<='%s' "
                        + " AND te.`state`=3 AND (te.`father_id` IS NULL OR te.`father_id` = 0) AND tt.`Pay_Name` IN(%s)"
                        + "  AND te.`org_no` IN(%s)  "
                        + " UNION "
                        + " SELECT tt.`business_no` businessNo FROM t_trade_check_follow tt INNER JOIN t_trade_check_follow_deal td "
                        + " ON tt.`business_no`=td.pay_flow_no WHERE "
                        + " tt.`org_no` IN (%s) AND tt.`trade_date`>='%s' AND tt.`trade_date`<='%s' "
                        + " AND td.`org_code` IN (%s) AND td.`trade_datetime` >= '%s' AND td.`trade_datetime` <= '%s'"
                        + " AND tt.`Pay_Name` IN(%s)  AND td.exception_state<>'11' ",
                orgNo, startDate, endDate, payTypeSql, orgNo,
                orgNo, startDate, endDate, orgNo, startDate, endDate,
                payTypeSql);
        logger.info(" getDealedPayFlowNoList sql============" + sql);
        return super.queryList(sql, null, null);
    }

    /**
     * 统计未处理的长短款总笔数
     */
    public List<Map<String, Object>> getAllShotLongAcount(String startDate, String endDate, String payTypeSql,
                                                          String orgNo, String businessNos) {
        final String sql = String.format(
                "SELECT  "
                        + " COUNT(CASE WHEN t.`check_state` IN(3, 5) THEN 1 ELSE NULL END) longAcount, "
                        + " COUNT(CASE WHEN t.`check_state` IN(2, 6) THEN 1 ELSE NULL END) shotAcount, "
                        + " SUM(CASE WHEN t.`check_state` IN(3, 5) THEN t.`trade_amount` ELSE 0 END) longAmount, "
                        + " SUM(CASE WHEN t.`check_state` IN(2, 6) THEN ABS(t.`trade_amount`) ELSE 0 END) shotAmount "
                        + " FROM t_trade_check_follow t WHERE t.`org_no` IN (%s) "
                        + " AND t.`trade_date`>='%s' AND t.`trade_date`<='%s' AND t.`Pay_Name` IN(%s) "
                        + " AND t.`business_no` NOT IN(%s)", orgNo, startDate, endDate, payTypeSql, businessNos);

        logger.info(" getAllShotLongAcount sql============" + sql);
        return super.queryList(sql, null, null);
    }

    /**
     * 统计所有的长短款总笔数
     */
    public List<Map<String, Object>> getAllShotLongAcount(String startDate, String endDate, String payTypeSql,
                                                          String orgNo) {
        final String sql = String.format(
                "SELECT  "
                        + " COUNT(CASE WHEN t.`check_state` IN(3, 5) THEN 1 ELSE NULL END) longAcount, "
                        + " COUNT(CASE WHEN t.`check_state` IN(2, 6) THEN 1 ELSE NULL END) shotAcount, "
                        + " SUM(CASE WHEN t.`check_state` IN(3, 5) THEN t.`trade_amount` ELSE 0 END) longAmount, "
                        + " SUM(CASE WHEN t.`check_state` IN(2, 6) THEN ABS(t.`trade_amount`) ELSE 0 END) shotAmount "
                        + " FROM t_trade_check_follow t WHERE t.`org_no` IN (%s) "
                        + " AND t.`trade_date`>='%s' AND t.`trade_date`<='%s' AND t.`Pay_Name` IN(%s)",
                orgNo, startDate, endDate, payTypeSql);

        logger.info(" getAllShotLongAcount sql============" + sql);
        return super.queryList(sql, null, null);
    }

    private HisInterfaceType returnHisType() {
        String hisOrderType = propertiesConfigService.findValueByPkey(ProConstants.hisOrderType,
                ProConstants.DEFAULT.get(ProConstants.hisOrderType));
        if (StringUtils.equalsIgnoreCase(hisOrderType , HisInterfaceType.HTTP.getValue())) {
            return HisInterfaceType.HTTP;
        } else if (StringUtils.equalsIgnoreCase(hisOrderType, HisInterfaceType.DATASOURCE.getValue())) {
            return HisInterfaceType.DATASOURCE;
        } else if (StringUtils.equalsIgnoreCase(hisOrderType, HisInterfaceType.WEBSERVICE.getValue())) {
            return HisInterfaceType.WEBSERVICE;
        } else if (StringUtils.equalsIgnoreCase(hisOrderType, HisInterfaceType.WEBAPI.getValue())) {
            return HisInterfaceType.WEBAPI;
        } else if (StringUtils.equalsIgnoreCase(hisOrderType, HisInterfaceType.EXE.getValue())) {
            return HisInterfaceType.EXE;
        }
        return HisInterfaceType.HTTP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseResult getExceptionTradeDetail(String recHisId, String recThirdId, String businessNo, String orgNo,
                                                  String orderState, String tradeTime,String billSource) {
        ResponseResult result = ResponseResult.success();
        HashMap<String, Object> map = new HashMap<>();
        DataSource dataSource = new DataSource();
        String datasourceIp = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceIp);
        String datasourceUsername = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceUsername);
        String datasourcePassword = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourcePassword);
        String datasourcePort = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourcePort);
        String datasourceDataBase = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceDatabase);
        dataSource.setIp(datasourceIp);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        dataSource.setPort(StringUtils.isNotBlank(datasourcePort) ? Integer.valueOf(datasourcePort) : 8080);
        dataSource.setDataBaseName(datasourceDataBase);
        HisRequestVo vo = new HisRequestVo();

		/*Map<String, String> params = new HashMap<>();
		params.put("orgNo", orgNo);
		params.put("payFlowNo", businessNo);
		if(orgNo.equals("53399")){//天门
			vo.setType(HisInterfaceType.DATASOURCE);
			vo.setSql("select patName,patType,patCode patNumber,payType,transDate tradeTime,payAmount tradeAmount,agtOrdSerialNo payFlowNo from yyt_zhptdz where agtOrdSerialNo = '"+businessNo+"'");
		}else {
			vo.setType(HisInterfaceType.HTTP);
			vo.setHttpType("post");
		}
		vo.setServerUrl(httpUrl);
		vo.setParams(params);*/
        String webserviceFunctionName = propertiesConfigService.findValueByPkey(ProConstants.hisOrderWebserviceFunctionName);
        String httpType = propertiesConfigService.findValueByPkey(ProConstants.hisOrderHttpType,
                ProConstants.DEFAULT.get(ProConstants.hisOrderHttpType));
        String webserviceUrl=propertiesConfigService.findValueByPkey(ProConstants.hisOrderWebserviceUrl);
        String datasourceSql=propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceSql);
        vo.setDataSource(dataSource);
        vo.setFunctionName(webserviceFunctionName);
        vo.setHttpType(httpType);
        vo.setServerUrl(webserviceUrl);
        vo.setSql(datasourceSql);
        vo.setType(returnHisType());
        vo.setDataSource(dataSource);
        Map<String, String> params = new HashMap<>();
        params.put("orgCode",orgNo);
        params.put("tsnOrderNo",businessNo);
        params.put("startDateTime",tradeTime);
        params.put("EndDateTime",tradeTime);
        vo.setParams(params);

        // 查询此异常账单的处理（平账|追回）记录
        TradeCheckFollowDeal deal = null;
		if (StringUtils.isNotBlank(businessNo)) {
			deal = tradeCheckFollowDealDao.findFirstByPayFlowNoAndOrgCodeAndTradeDatetimeOrderByCreatedDateDesc(
					businessNo, orgNo, DateUtil.formatStringDate("yyyy-MM-dd", tradeTime));
			if (deal != null) {
				map.put("dealDetail", deal);
			}
		}

        List<ThirdBill> thirdBills = new ArrayList<>();
        // 流水号不为空就按照流水号查询
        if (StringUtils.isNotEmpty(businessNo) && !"0".equals(businessNo) && !"000000000000".equals(businessNo)) {
            thirdBills = thirdBillDao.findByOrderNo(businessNo);
        } else if (StringUtils.isNotEmpty(recThirdId)) {
            ThirdBill tBill = thirdBillDao.findOne(Long.valueOf(recThirdId));
            thirdBills.add(tBill);
        }
        // 从线上订单中获取患者就诊号
        String visitNumbe = "";
        String getPayCenterOrderInfo = propertiesConfigService.findValueByPkey(ProConstants.payCenterOrderInfo,
                ProConstants.DEFAULT.get(ProConstants.payCenterOrderInfo));
        if (Boolean.valueOf(getPayCenterOrderInfo )) {
            if (thirdBills.size() > 0) {
                ThirdBill thirdBill = thirdBills.get(0);
                // 获取患者就诊号
                visitNumbe = getVisitNumber(thirdBill.getPayFlowNo(), thirdBill.getOrgNo(), DateUtil.formatStringDate("yyyy-MM-dd", tradeTime));
            }
        }

        if (thirdBills.size() > 0) {
            // 查询订单上送表, 新增'患者类型'、'就诊卡号'、'患者名称'三个字段
            OrderUpload orderUpload = orderUploadDao.findByTsnOrderNo(businessNo);
            if (orderUpload != null) {
                for (ThirdBill thirdBill : thirdBills) {
                    thirdBill.setPatType(orderUpload.getPatType());
                    thirdBill.setCustName(orderUpload.getPatientName());
                    thirdBill.setPatientCardNo(orderUpload.getPatientCardNo());
                    // 患者就诊号
                    thirdBill.setVisitNumbe(visitNumbe);
                }
            } else {
                for (ThirdBill thirdBill : thirdBills) {
                    // 患者就诊号
                    thirdBill.setVisitNumbe(visitNumbe);
                }
            }
            map.put("thirdOrder", thirdBills);
        }

        // HIS订单信息优先获取本地HIS表记录进行展示,本地不存在再实时获取
        String conditionSql = "";
        if (StringUtils.isNotEmpty(businessNo) && !"0".equals(businessNo)) {
            conditionSql += String.format(" And t.`Pay_Flow_No`='%s'  AND t.`org_no`='%s' ", businessNo, orgNo);
        } else if (StringUtils.isNotEmpty(recHisId)) {
            conditionSql += String.format(" And t.`id`='%s' ", recHisId);
        }
        if(StringUtils.isNotEmpty(billSource)) {
            conditionSql += String.format(" And t.bill_source='%s' ",billSource);
        }
        String sql = String.format("SELECT " +
                        " t.`Cust_Name` patientName, t.pat_type patientType, t.`his_flow_no` hisNo,t.visit_number visitNumber," +
                        " t.`Pay_Flow_No` payNo, t.`pay_type` payType, t.`Order_State` orderState," +
                        " t.`Trade_datatime` tradeTime, t.`Pay_Amount` tradeAmount, t.mz_code mzCode,t.invoice_no invoiceNo," +
                        " IF(pat_type='mz',mz_code,pat_code) patientNo,t.Pay_Business_Type payBusinessType ,shop_flow_no shopFlowNo, pay_Shop_No payShopNo, terminal_no terminalNo, reference_num referenceNum " +
                        " FROM t_rec_histransactionflow t WHERE 1=1 %s"
                , conditionSql);

        List<Map<String, Object>> hisOrders = null;
        if (StringUtils.isNotBlank(conditionSql)) {
            hisOrders = super.queryList(sql, null, null);
        }
        if ((hisOrders == null || hisOrders.size() == 0) && "50721".equals(orgNo)) {
            String billSql="";
            if(StringUtils.isNotEmpty(billSource)) {
                billSql=" And t.bill_source = '"+billSource+"'";
            }
            sql = String.format(
                    "select shop_flow_no shop_flow_no,Pay_Flow_No payFlowNo from t_thrid_bill where 1=1 And id = '%s' "+billSql,
                    recThirdId);
            List<Map<String, String>> res = super.queryList(sql, null, null);
            if (res != null && res.size() > 0) {
                sql = String.format("SELECT DISTINCT t.`Cust_Name` patientName,t.visit_number visitNumber, "
                                + "t.pat_type patientType, t.`his_flow_no` hisNo," +
                                " t.`Pay_Flow_No` payNo, t.`pay_type` payType, t.`Order_State` orderState," +
                                " t.`Trade_datatime` tradeTime, t.`Pay_Amount` tradeAmount," +
                                " t.credentials_no patientNo, t.Pay_Business_Type payBusinessType, shop_flow_no shopFlowNo,pay_Shop_No payShopNo,terminal_no terminalNo, reference_num referenceNum " +
                                " FROM `t_rec_histransactionflow` t "
//						+ "LEFT JOIN t_thrid_bill b ON t.`Pay_Flow_No` = b.`shop_flow_no` "
                                + "WHERE t.Pay_Flow_No = '%s' "
                                + "and t.org_no = '%s' "+billSql
                                + "ORDER BY t.Trade_datatime DESC",
                        businessNo, orgNo);
                hisOrders = super.queryList(sql, null, null);
                businessNo = res.get(0).get("shop_flow_no");
                params.put("businessNo", businessNo);
                vo.setParams(params);
            }
        }
        if (hisOrders != null && hisOrders.size() > 0) {
            map.put("hisOrderState", TitleStateEnum.NORMAL.getName());
            for (Map<String, Object> map2 : hisOrders) {
                if (map2.containsKey("tradeTime")) {
                    String date = DateUtil.formatStringDate("yyyy-MM-dd HH:mm:ss", map2.get("tradeTime").toString());
                    map2.put("tradeTime", date);
                }
//                map2.put("visitNumber",visitNumbe);
            }
            map.put("hisOrder", hisOrders);
            return result.data(map);
        }
        String httpUrl = propertiesConfigService.findValueByPkey(ProConstants.hisOrderHttpUrl);
        // 是否接入his接口参数
        if ((httpUrl  == null || StringUtil.isNullOrEmpty(httpUrl))
                && (datasourceIp == null || StringUtil.isNullOrEmpty(datasourceIp))
                && (webserviceUrl == null || StringUtil.isNullOrEmpty(webserviceUrl))) {
            map.put("hisOrderState", TitleStateEnum.UNABUTMENTHIS.getName());
            return result.data(map);
        }
        vo.setServerUrl(httpUrl);
        HisResponseVo responseVo = null;
        try {
            responseVo = hisService.service(vo);
            if (responseVo.getReturnCode().equalsIgnoreCase("success")) {
                map.put("hisOrder", responseVo.getData());
                map.put("hisOrderState", TitleStateEnum.NORMAL.getName());
            } else {
                map.put("hisOrderState", TitleStateEnum.NOTHINGNESS.getName());
            }
        } catch (Exception e) {
            map.put("hisOrderState", TitleStateEnum.NETWORKTIMEOUT.getName());
        }
        return result.data(map);
    }


    /**
     * 通过机构编码、交易流水号查询paycenter中订单信息
     * @param payFlowNo 交易流水号
     * @param orgNo 机构编码
     * @param date  账单日期
     * @return
     */
    private String getVisitNumber(String payFlowNo ,String orgNo, String date){
        String beginTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        String paramsStr = "{\"beginTime\":\"" + beginTime + "\",\"endTime\":\"" + endTime + "\",\"orgCode\":\"" + orgNo + "\"}";
        String extStr = "";
        // 就诊卡号
        String visitNumber = "";
        String payCenterHost = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
        String retStrs ="";
        try {
            retStrs = com.yiban.rec.bill.parse.util.HttpClientUtil.doPostJson(payCenterHost  + "/pay/billLog/query/list", paramsStr);
        } catch (Exception e) {
        }
        if (!StringUtil.isNullOrEmpty(retStrs) && retStrs.startsWith("[")) {
            net.sf.json.JSONArray array = JSONArray.fromObject(retStrs);
            for (int i = 0; i < array.size() - 1; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String tsnNo = "";
                tsnNo = jsonObject.containsKey("tsn") ? jsonObject.getString("tsn") : "";
                if (payFlowNo.equalsIgnoreCase(tsnNo)){
                    extStr = jsonObject.containsKey("extra_param") ? jsonObject.getString("extra_param") : "";
                    // 校验是否json字符串
                    if(!StringUtil.isNullOrEmpty(extStr) && extStr.startsWith("{")){
                        JSONObject extraParamJsonObj = JSONObject.fromObject(extStr);
                        visitNumber = extraParamJsonObj.containsKey("visitNumber") ? extraParamJsonObj.getString("visitNumber") : "";
                    }
                    break;
                }
            }
        }
        return visitNumber;
    }

    /**
     * 电子对账-异常账单查询
     *
     * @param vo
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable) {
        String orgNo = vo.getOrgNo();
        String startDate = vo.getStartDate();
        String endDate = startDate;
        String businessNo = vo.getBusinessNo();
        String dataSourceType = vo.getDataSourceType();
        String billSource = "";
        if (StringUtils.isNotBlank(vo.getBillSource())) {
            billSource = billSourceNameFormat(vo.getBillSource());
        } else {
            List<String> billSourceList = getAllBillSource();
            for (String billSourceStr : billSourceList) {
                if (!"".equals(billSource)) {
                    billSource = billSource + ",'" + billSourceStr + "'";
                } else {
                    billSource = "'" + billSourceStr + "'";
                }
            }
        }
        String[] orgs = null;
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
        List<Organization> orgList = organizationService.findByParentCode(vo.getOrgNo());
        StringBuffer sql = new StringBuffer();
        if (orgList != null && orgList.size() > 0) {
            orgs = new String[orgList.size() + 1];
            orgs[0] = orgNo;
            sql.append(orgNo);
            for (int i = 0; i < orgList.size(); i++) {
                orgs[i + 1] = orgList.get(i).getCode();
                sql.append("," + orgList.get(i).getCode());
            }
        } else {
            orgs = new String[1];
            orgs[0] = orgNo;
            sql.append(orgNo);
        }

        // 只显示配置的渠道数据
        List<String> billSourceList = new ArrayList<>();
        String patType = vo.getPatType();
        String orgNoStr = "";
        List<String> billSourceListExtZy = new ArrayList<>();
        for (int i = 0; i < orgs.length; i++) {
            if (i == 0) {
                orgNoStr = orgs[i];
            } else {
                orgNoStr = orgNoStr + "," + orgs[i];
            }
        }
        if (StringUtils.isNotBlank(patType)) {
            if ("zy".equals(patType)) {
                // 住院数据
                getRecBillSource(orgNoStr, "rec.zy.billsource", billSourceList);
            } else if ("mz".equals(patType)) {
                // 门诊数据
                getRecBillSource(orgNoStr, "rec.mz.billsource", billSourceList);
            }
        } else {
            List<String> billSourceListMz = new ArrayList<>();
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListExtZy);
            if (billSourceListMz.size() > 0 && billSourceListExtZy.size() > 0) {
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListExtZy);
            }
        }
        if (billSource.contains(",") && billSourceList.size() > 0) {
            billSource = "";
            for (int i = 0; i < billSourceList.size(); i++) {
                if (i == 0) {
                    billSource = billSourceList.get(i);
                } else {
                    billSource = billSource + "," + billSourceList.get(i);
                }
            }
        }
        Page<TradeCheckFollow> tcf = null;
        // 首页进来或者 点击查询按钮查询的情况， dataSourceType 为空
        if (dataSourceType != null) {
            Integer[] checkStates = null;
            if ("his".equals(dataSourceType)) {
                checkStates = new Integer[]{CommonEnum.BillBalance.HISDC.getValue(), CommonEnum.BillBalance.HEALTHCAREHIS.getValue()};
            } else if ("third".equals(dataSourceType)) {
                checkStates = new Integer[]{CommonEnum.BillBalance.THIRDDC.getValue(), CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()};
            } else {
                //2 短款  3 长款
                checkStates = new Integer[]{CommonEnum.BillBalance.HISDC.getValue(),//医院多出
                        CommonEnum.BillBalance.HEALTHCAREHIS.getValue(),//医保his多出
                        CommonEnum.BillBalance.THIRDDC.getValue(),//支付渠道多出
                        CommonEnum.BillBalance.HEALTHCAREOFFI.getValue(),//医保中心多出
                        CommonEnum.BillBalance.HANDLER.getValue(),//处理后账平
                        CommonEnum.BillBalance.REFUND.getValue(),//已退费
                        CommonEnum.BillBalance.RECOVER.getValue(),//已追回
                };
            }
            tcf = findByOrgNoAndTradeDateAndCheckState(orgs, vo.getPatType(), startDate, endDate, checkStates,
                    payTypeSql.replaceAll("'", "").split(","),
                    billSource.replaceAll("'", "").split(","), businessNo, pageable);
        } else {
            tcf = tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayName(orgs, startDate, endDate,
                    CommonEnum.BillBalance.zp.getValue(), payTypeSql.replaceAll("'", "").split(","), pageable);
        }
        List<TradeCheckFollow> list = tcf.getContent();

        //添加异常类型
        if (!StringUtil.isNullOrEmpty(list)) {
            for (TradeCheckFollow tradeCheckFollow : list) {
                String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
                String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
                if (tradeCheckFollow.getCheckState() != null && thirdCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                    tradeCheckFollow.setExceptionType("长款");
                } else if (tradeCheckFollow.getCheckState() != null && hisCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                    tradeCheckFollow.setExceptionType("短款");
                }
            }
        }
        //短款手动处理的账单
        handDealFollow(list);
        //长款手动处理的账单
        handExceptionFollow(list);
        //将code 转换为名称
        String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
        String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        if (!StringUtil.isNullOrEmpty(list)) {
            for (TradeCheckFollow tradeCheckFollow : list) {
                Integer checkState = tradeCheckFollow.getCheckState();
                if (hisCheck.contains(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待处理");
                } else if (thirdCheck.contains(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待处理");
                } else if (CommonEnum.BillBalance.WAITEXAMINE.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待审核");
                } else if (CommonEnum.BillBalance.REJECT.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已驳回");
                } else if (CommonEnum.BillBalance.REFUND.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已退费");
                } else if (CommonEnum.BillBalance.HANDLER.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已抹平");
                } else if (CommonEnum.BillBalance.RECOVER.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已追回");
                }
            }
        }
        return tcf;
    }

    /**
     * 异常账单查询
     *
     * @param vo
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TradeCheckFollow> findDataByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable) {
        String orgNo = vo.getOrgNo();
        String startDate = vo.getStartDate().trim();
        String endDate = vo.getEndDate().trim();
        String dataSourceType = vo.getDataSourceType();
        String businessNo = vo.getBusinessNo();
        String hisFlowNo = vo.getHisFlowNo();
        String billSource = "";
        if (StringUtils.isNotBlank(vo.getBillSource())) {
            billSource = billSourceNameFormat(vo.getBillSource());
        } else {
            List<String> billSourceList = getAllBillSource();
            for (String billSourceStr : billSourceList) {
                if (!"".equals(billSource)) {
                    billSource = billSource + ",'" + billSourceStr + "'";
                } else {
                    billSource = "'" + billSourceStr + "'";
                }
            }
        }
        String[] orgs = null;
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());

        List<Organization> orgList = organizationService.findByParentCode(vo.getOrgNo());
        StringBuffer sql = new StringBuffer();
        if (orgList != null && orgList.size() > 0) {
            orgs = new String[orgList.size() + 1];
            orgs[0] = orgNo;
            sql.append(orgNo);
            for (int i = 0; i < orgList.size(); i++) {
                orgs[i + 1] = orgList.get(i).getCode();
                sql.append("," + orgList.get(i).getCode());
            }
        } else {
            orgs = new String[1];
            orgs[0] = orgNo;
            sql.append(orgNo);
        }

        // 只显示配置的渠道数据
        List<String> billSourceList = new ArrayList<>();
        String patType = vo.getPatType();
        String orgNoStr = "";
        for (int i = 0; i < orgs.length; i++) {
            if (i == 0) {
                orgNoStr = orgs[i];
            } else {
                orgNoStr = orgNoStr + "," + orgs[i];
            }
        }
        if (StringUtils.isNotBlank(patType)){
            if ("zy".equals(patType)) {
                // 住院数据
                getRecBillSource(orgNoStr, "rec.zy.billsource", billSourceList);
            } else if ("mz".equals(patType)) {
                // 门诊数据
                getRecBillSource(orgNoStr, "rec.mz.billsource", billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListZy);
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
        }
        if (billSource.contains(",") && StringUtils.isNotBlank(patType)) {
            billSource = "";
            for (int i = 0; i < billSourceList.size(); i++) {
                if (i == 0) {
                    billSource = billSourceList.get(i);
                } else {
                    billSource = billSource + "," + billSourceList.get(i);
                }
            }
        }
        Page<TradeCheckFollow> tcf = null;
        // 首页进来或者 点击查询按钮查询的情况， dataSourceType 为空
        if (dataSourceType != null) {
            Integer[] checkStates = null;
            String correctionSql = "";
            if ("his".equals(dataSourceType)) {
                checkStates = new Integer[]{CommonEnum.BillBalance.HISDC.getValue(), CommonEnum.BillBalance.HEALTHCAREHIS.getValue()};
            } else if ("third".equals(dataSourceType)) {
                checkStates = new Integer[]{CommonEnum.BillBalance.THIRDDC.getValue(), CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()};
            } else {
                //2 短款  3长款
                checkStates = new Integer[]{CommonEnum.BillBalance.HISDC.getValue(),//医院多出
                        CommonEnum.BillBalance.HEALTHCAREHIS.getValue(),//医保his多出
                        CommonEnum.BillBalance.THIRDDC.getValue(),//支付渠道多出
                        CommonEnum.BillBalance.HEALTHCAREOFFI.getValue(),//医保中心多出
                        CommonEnum.BillBalance.HANDLER.getValue(),//处理后账平
                        CommonEnum.BillBalance.REFUND.getValue(),//已退费
                        CommonEnum.BillBalance.RECOVER.getValue(),//已追回
                };
            }
            tcf = findByOrgNoAndTradeDateAndCheckState(vo.getPatType(),orgs, startDate, endDate, checkStates, payTypeSql.replaceAll("'", "").split(","), billSource.replaceAll("'", "").split(","), businessNo, hisFlowNo, pageable);
        } else {
            Integer[] checkStates = new Integer[]{CommonEnum.BillBalance.HISDC.getValue(), CommonEnum.BillBalance.HEALTHCAREHIS.getValue(), CommonEnum.BillBalance.THIRDDC.getValue(), CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()};
            tcf = findByOrgNoAndTradeDateAndCheckState(vo.getPatType(),orgs, startDate, endDate, checkStates, payTypeSql.replaceAll("'", "").split(","), billSource.replaceAll("'", "").split(","), businessNo, hisFlowNo, pageable);
        }
        List<TradeCheckFollow> list = tcf.getContent();

        //添加异常类型
        if (!StringUtil.isNullOrEmpty(list)) {
            for (TradeCheckFollow tradeCheckFollow : list) {
                String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
                String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
                if (tradeCheckFollow.getCheckState() != null && thirdCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                    tradeCheckFollow.setExceptionType("长款");
                } else if (tradeCheckFollow.getCheckState() != null && hisCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                    tradeCheckFollow.setExceptionType("短款");
                }
            }
        }
        
        //短款手动处理的账单
        handDealFollow(list);

        //长款手动处理的账单
        handExceptionFollow(list);

        //将code 转换为名称
        String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
        String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        if (!StringUtil.isNullOrEmpty(list)) {
            for (TradeCheckFollow tradeCheckFollow : list) {
                Integer checkState = tradeCheckFollow.getCheckState();
                if (hisCheck.contains(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待处理");
                } else if (thirdCheck.contains(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待处理");
                } else if (CommonEnum.BillBalance.WAITEXAMINE.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("待审核");
                } else if (CommonEnum.BillBalance.REJECT.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已驳回");
                } else if (CommonEnum.BillBalance.REFUND.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已退费");
                } else if (CommonEnum.BillBalance.HANDLER.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已抹平");
                } else if (CommonEnum.BillBalance.RECOVER.getValue().toString().equals(checkState.toString())) {
                    tradeCheckFollow.setCheckStateValue("已追回");
                }
            }
        }
        return tcf;
    }

    @Override
    public List<TradeCheckFollow> filterDealBill(List<TradeCheckFollow> tradeCheckFollowList){
    	
    	List<TradeCheckFollow> list = new ArrayList<>(tradeCheckFollowList);
    	TradeCheckFollow tradeCheckFollow = null;
    	for(int i=list.size()-1;i>=0;i--){
    		tradeCheckFollow = list.get(i);
    		if (tradeCheckFollow.getCheckState() != null) {
    			Integer checkState = tradeCheckFollow.getCheckState();
                if (checkState == CommonEnum.BillBalance.RECOVER.getValue()||
                		checkState == CommonEnum.BillBalance.HANDLER.getValue()||
                		checkState == CommonEnum.BillBalance.REFUND.getValue()||
                		checkState == CommonEnum.BillBalance.zp.getValue()) {
                    list.remove(i);
                }
    		}
    	}
    	return list;
    }
    
    private String billSourceNameFormat(String billSource) {
        if (StringUtil.isNullOrEmpty(billSource)) {
            return null;
        }
        String sql = "SELECT `value` FROM `t_meta_data` WHERE type_id = 115 AND `name`= '" + billSource + "'";

        return (String) handleNativeSql4SingleRes(sql);
    }

    private List getAllBillSource() {
        String sql = "SELECT `value` text FROM `t_meta_data` WHERE type_id = 115";
        List res = handleNativeSql4SingleCol(sql);
        return res;
    }

    public Page<Map<String, Object>> findByOrgNoAndTradeDateModify(TradeCheckFollowVo vo, PageRequest pageRequest) {
        String startDate = vo.getStartDate();
        String endDate = startDate;
        String dataSourceType = vo.getDataSourceType();

        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());

        String orgNoSql = this.concatOrgNoSql(vo.getOrgNo());
        Page<Map<String, Object>> page = unionQueryListByCondition(dataSourceType, orgNoSql, startDate, endDate, payTypeSql, pageRequest);
        return page;
    }

    /**
     * 查询异常账单列表，区分长款和短款
     *
     * @return
     */
    public Page<Map<String, Object>> unionQueryListByCondition(String dataSourceType, String orgNo, String startDate,
                                                               String endDate, String payTypeSql, PageRequest pageRequest) {
        String orderBySql = concatOrderBySql(pageRequest);
        String querySql = "";
        querySql = "SELECT t.* FROM (";
        if (StringUtil.isEmpty(dataSourceType) || "all".equals(dataSourceType)) {
            querySql += this.concatShortSql(orgNo, startDate, endDate, payTypeSql);
            querySql += " UNION ";
            querySql += this.concatLongSql(orgNo, startDate, endDate, payTypeSql);
            // 短款
        } else if ("his".equals(dataSourceType)) {
            querySql += this.concatShortSql(orgNo, startDate, endDate, payTypeSql);
            // 长款
        } else if ("third".equals(dataSourceType)) {
            querySql += this.concatLongSql(orgNo, startDate, endDate, payTypeSql);
        }
        querySql += ") t ";
        querySql += "ORDER BY " + orderBySql;

        logger.info("unionQueryListByCondition rec exception Bill SQL = " + querySql.toString());
        return handleNativeSql(querySql.toString(), pageRequest,
                new String[]{"id", "businessNo", "hisFlowNo", "tradeName", "payName", "tradeAmount", "patientName",
                        "checkStateValue", "checkState", "billSource", "tradeTime", "businessType", "patType", "exceptionType",
                        "description", "fileLocation"});
    }

    /**
     * 短款SQL拼接
     *
     * @param orgNo
     * @param startDate
     * @param endDate
     * @param payTypeSql
     * @return
     */
    public String concatShortSql(String orgNo, String startDate, String endDate, String payTypeSql) {
        String sql =
                " SELECT " +
                        " tcf.id, tcf.`business_no` businessNo, tcf.`his_flow_no` hisFlowNo, tcf.`trade_name` tradeName, tcf.`Pay_Name` payName, tcf.`trade_amount` tradeAmount,  tcf.`Patient_Name` patientName," +
                        " IF(td.`pay_flow_no` IS NOT NULL, '已处理', '短款') checkStateValue, IF(td.`pay_flow_no` IS NOT NULL, 1, tcf.`check_state`) checkState," +
                        " tcf.`bill_source` billSource," +
                        " tcf.`trade_time` tradeTime,  tcf.`Business_Type` businessType, tcf.`pat_type` patType, '短款' exceptionType," +
                        " td.`description` description, td.`file_location` fileLocation" +
                        "	FROM t_trade_check_follow tcf " +
                        " LEFT JOIN t_trade_check_follow_deal td ON tcf.`business_no`=td.`pay_flow_no`" +
                        " WHERE tcf.`org_no`IN (" + orgNo + ") AND tcf.`trade_date`>='" + startDate + "' AND tcf.`trade_date`<='" + endDate + "'" +
                        " AND tcf.`check_state` IN (2, 6) AND tcf.Pay_Name IN(" + payTypeSql + ")";
        return sql;
    }

    public String concatLongSql(String orgNo, String startDate, String endDate, String payTypeSql) {
        String sql =
                " SELECT " +
                        " tcf.id, tcf.`business_no` businessNo, tcf.`his_flow_no` hisFlowNo, tcf.`trade_name` tradeName, tcf.`Pay_Name` payName, tcf.`trade_amount` tradeAmount, tcf.`Patient_Name` patientName," +
                        " IF(ehr.state IS NOT NULL, CASE WHEN ehr.`state`=1 THEN '待审核' WHEN ehr.`state`=2 THEN '已驳回' WHEN ehr.`state` =3 THEN '已退款' END, '长款') checkStateValue," +
                        " IF(ehr.state IS NOT NULL, ehr.state, tcf.`check_state`) checkState," +
                        " tcf.`bill_source` billSource," +
                        " tcf.`trade_time` tradeTime,  tcf.`Business_Type` businessType, tcf.`pat_type` patType,  '长款' exceptionType, " +
                        " ehr.`Handle_Remark` description, ehr.`img_url` fileLocation" +
                        "	FROM t_trade_check_follow tcf " +
                        " LEFT JOIN t_exception_handling_record ehr ON tcf.`business_no`=ehr.`Payment_Request_Flow` AND (ehr.`father_id` IS NULL OR ehr.`father_id`=0)" +
                        " WHERE tcf.`org_no`IN (" + orgNo + ") AND tcf.`trade_date`>='" + startDate + "' AND tcf.`trade_date`<='" + endDate + "'" +
                        " AND tcf.`check_state` IN (3, 5) AND tcf.Pay_Name IN(" + payTypeSql + ") ";
        return sql;
    }

    public String concatOrderBySql(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort == null) {
            return " id DESC ";
        }
        String orderSql = "";
        Iterator<Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            orderSql += order.getProperty() + " " + order.getDirection() + ",";
        }
        if (StringUtil.isEmpty(orderSql)) {
            orderSql = " id DESC ";
        } else {
            orderSql = orderSql.substring(0, orderSql.lastIndexOf(","));
        }
        return orderSql;
    }

    //长款退费账单处理
    private void handExceptionFollow(List<TradeCheckFollow> list) {
        List<ExcepHandingRecord> excepHandingRecordList = excepHandingRecordDao.findByFatherId();
        if (excepHandingRecordList != null && list != null) {
            for (int j = list.size() - 1; j >= 0; j--) {
                String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
                if (list.get(j).getCheckState() != null && thirdCheck.contains(list.get(j).getCheckState().toString())) {
                    for (int i = excepHandingRecordList.size() - 1; i >= 0; i--) {
                        if (list.get(j).getBusinessNo().equals(excepHandingRecordList.get(i).getPaymentRequestFlow())
                                &&list.get(j).getBillSource().equals(excepHandingRecordList.get(i).getBillSource())) {
                            String state = excepHandingRecordList.get(i).getState();
                            if (RefundStateEnum.unExamine.getValue().equals(state)) {
                                // 未审核1，赋值7待审核
                                list.get(j).setCheckState(CommonEnum.BillBalance.WAITEXAMINE.getValue());
                            } else if (RefundStateEnum.reject.getValue().equals(state)) {
                                // 已驳回2，赋值8已驳回
                                list.get(j).setCheckState(CommonEnum.BillBalance.REJECT.getValue());
                            } else if (RefundStateEnum.refund.getValue().equals(state)) {
                                // 已退费3，赋值9已退费
                                list.get(j).setCheckState(CommonEnum.BillBalance.REFUND.getValue());
                            }

                            list.get(j).setDescription(excepHandingRecordList.get(i).getHandleRemark());
                            list.get(j).setFileLocation(excepHandingRecordList.get(i).getImgUrl());
                        }
                    }
                }
            }
        }
    }

    //短款长款手动抹平的账单处理
    private void handDealFollow(List<TradeCheckFollow> list) {
        List<TradeCheckFollowDeal> dealFollowList = tradeCheckFollowDealDao.findAll();
        if (dealFollowList != null && list != null) {
            String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue()
                    + "," + CommonEnum.BillBalance.THIRDDC.getValue()
                    + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
            for (TradeCheckFollow tradeCheckFollow : list) {
                // 保留原始状态
                tradeCheckFollow.setOriCheckState(String.valueOf(tradeCheckFollow.getCheckState()));

                if (tradeCheckFollow.getCheckState() != null && hisCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                    for (int i = dealFollowList.size() - 1; i >= 0; i--) {
                        if (tradeCheckFollow.getBusinessNo().equals(dealFollowList.get(i).getPayFlowNo())
                                && tradeCheckFollow.getOrgNo().equals(dealFollowList.get(i).getOrgCode())
                                && tradeCheckFollow.getTradeDate().equals(dealFollowList.get(i).getTradeDatetime())) {
                            /**
                             * 处理单号为0的账单，通过recHisId判断账单
                             */
                            if ("0".equals(tradeCheckFollow.getBusinessNo()) && !String.valueOf(tradeCheckFollow.getRecHisId()).equals(dealFollowList.get(i).getRecHisId())) {
                                continue;
                            }
                            if (dealFollowList.get(i).getExceptionState() != null && Integer.parseInt(dealFollowList.get(i).getExceptionState()) == CommonEnum.BillBalance.RECOVER.getValue()) {
                                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.RECOVER.getValue());
                            } else {
                                if (dealFollowList.get(i).getExceptionState() != null && Integer.parseInt(dealFollowList.get(i).getExceptionState()) != 11) {
                                    tradeCheckFollow.setCheckState(CommonEnum.BillBalance.HANDLER.getValue());
                                }
                            }
                            tradeCheckFollow.setDescription(dealFollowList.get(i).getDescription());
                            tradeCheckFollow.setFileLocation(dealFollowList.get(i).getFileLocation());
                        }
                    }
                }
            }
        }
    }

    private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndCheckState(String patType, String[] orgs, String startDate, String endDate, Integer[] checkStates, String[] payTypes, String[] billSource, String businessNo, String hisFlowNo, Pageable pageable) {
        Specification<TradeCheckFollow> spec = new Specification<TradeCheckFollow>() {
            @Override
            public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = new ArrayList<>();
                In<String> in = cb.in(root.get("orgNo"));
                predicate.add(cb.between(root.get("tradeDate").as(String.class), startDate, endDate));
                for (String id : orgs) {
                    in.value(id);
                }
                predicate.add(in);
                if (checkStates != null) {
                    In<Integer> in2 = cb.in(root.get("checkState"));
                    for (Integer id : checkStates) {
                        in2.value(id);
                    }
                    predicate.add(in2);
                }
                in = cb.in(root.get("payName"));
                for (String id : payTypes) {
                    in.value(id);
                }
                predicate.add(in);
                in = cb.in(root.get("billSource"));
                for (String id : billSource) {
                    in.value(id);
                }
                predicate.add(in);
                if (StringUtils.isNotBlank(hisFlowNo)) {
                    predicate.add(cb.like(root.get("hisFlowNo"), "%" + hisFlowNo + "%"));
                }
                if (StringUtils.isNotBlank(businessNo)) {
                    predicate.add(cb.like(root.get("businessNo"), "%" + businessNo + "%"));
                }
                // 数据来源
                if (StringUtils.isNotBlank(patType)) {
                    if ("zy".equals(patType)) {
                        predicate.add(cb.like(root.get("patType"), "%" + patType + "%"));
                    } else {
                        // 门诊数据
                        Predicate[] pre = new Predicate[predicate.size()];
                        Predicate Pre_And = cb.and(predicate.toArray(pre));
                        List<Predicate> listOr = new ArrayList<Predicate>();
                        listOr.add(cb.notLike(root.get("patType"), "zy"));
                        listOr.add(cb.isNull(root.get("patType")));
                        Predicate[] arrayOr = new Predicate[listOr.size()];
                        Predicate Pre_Or = cb.or(listOr.toArray(arrayOr));
                        return query.where(Pre_And, Pre_Or).getRestriction();
                    }
                }
                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return tradeCheckFollowDao.findAll(spec, pageable);
    }

    private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndCheckState(String[] orgs,String patType, String startDate, String endDate, Integer[] checkStates, String[] payTypes, String[] billSource,String businessNo, Pageable pageable) {
        if (!StringUtil.isNullOrEmpty(businessNo)) {
            // 通过订单号查找
            List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
            String[] billSourArr = new String[metaDataList.size()];
            for (int i = 0; i < metaDataList.size(); i++) {
                MetaData metaData = metaDataList.get(i);
                billSourArr[i] = metaData.getValue();
            }
            return tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayNameAndStatusAndBusinessNo(orgs, checkStates, payTypes, billSourArr,businessNo,startDate,endDate, pageable);
        }else {
            if (!StringUtil.isEmpty(patType)) {
                if ("zy".equals(patType)){
                    return tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayNameAndStatus(orgs, startDate, endDate, checkStates, payTypes, billSource, patType, pageable);
                } else {
                    return tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayNameAndStatus("zy",orgs, startDate, endDate, checkStates, payTypes, billSource, pageable);
                }
            } else {
                return tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayNameAndStatus(orgs, startDate, endDate, checkStates, payTypes, billSource, pageable);
            }
        }
    }

    private String concatOrderBySql1(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort == null) {
            return " business_no DESC ";
        }
        String orderSql = "";
        Iterator<Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            orderSql += order.getProperty() + " " + order.getDirection() + ",";
        }
        if (StringUtil.isEmpty(orderSql)) {
            orderSql = " business_no DESC ";
        } else {
            orderSql = orderSql.substring(0, orderSql.lastIndexOf(","));
        }
        return orderSql;
    }

    private List<TradeCheckFollow> findByOrgNoAndTradeDateAndCorrectionNoPage(String orgNo, String startDate, String endDate, Integer checkState) {
        String sql = "SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo + ") AND t.trade_date >= '" + startDate + "' AND t.trade_date <= '" + endDate + "' AND t.check_state <> " + checkState + " AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
                + " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo + ") AND t.trade_date >= '" + startDate + "' AND t.trade_date <= '" + endDate + "' AND t.check_state <> " + checkState + " AND ( ISNULL(t.business_no) OR t.business_no = '' )";
        return handleNativeSql(sql, TradeCheckFollow.class);
    }


    public List<TradeCheckFollow> findByOrgNoAndTradeDateNoPage(String orgNo, String startDate, String endDate, String correction) {
        List<Organization> orgList = organizationService.findByParentCode(orgNo);
        Organization organization = organizationService.findByCode(orgNo);
        orgList.add(organization);
        String[] orgs = null;
        StringBuffer sql = new StringBuffer();
        if (orgList != null && orgList.size() > 0) {
            orgs = new String[orgList.size() + 1];
            orgs[0] = orgNo;
            sql.append(orgNo);
            for (int i = 0; i < orgList.size(); i++) {
                orgs[i + 1] = orgList.get(i).getCode();
                sql.append("," + orgList.get(i).getCode());
            }
        } else {
            orgs = new String[1];
            orgs[0] = orgNo;
            sql.append(orgNo);
        }
        List<TradeCheckFollow> list = null;
        if ("1".equals(correction)) {
            list = findByOrgNoAndTradeDateAndCorrectionNoPage(sql.toString(), startDate, endDate, CommonEnum.BillBalance.zp.getValue());
        } else {
            list = tradeCheckFollowDao.findByOrgNoAndTradeDateNoPage(orgs, startDate, endDate, CommonEnum.BillBalance.zp.getValue());
        }
        if (!StringUtil.isNullOrEmpty(list)) {
            Map<Integer, String> maptwo = CommonEnum.BillBalance.asMap();
            List<MetaData> metaList = metaDataService.findAllMetaData();
            for (TradeCheckFollow tradeCheckFollow : list) {
                tradeCheckFollow.setCheckStateValue(maptwo.get(tradeCheckFollow.getCheckState()));
                for (MetaData m : metaList) {
                    if (tradeCheckFollow.getBillSource() != null && tradeCheckFollow.getBillSource().equals(m.getValue())) {
                        tradeCheckFollow.setBillSource(m.getName());
                    }

                    if (tradeCheckFollow.getPatType() != null && tradeCheckFollow.getPatType().equals(m.getValue())) {
                        tradeCheckFollow.setPatType(m.getName());
                    }

                    if (tradeCheckFollow.getPayName() != null && tradeCheckFollow.getPayName().equals(m.getValue())) {
                        tradeCheckFollow.setPayName(m.getName());
                    }

                    if (tradeCheckFollow.getTradeName() != null && tradeCheckFollow.getTradeName().equals(m.getValue())) {
                        tradeCheckFollow.setTradeName(m.getName());
                    }
                    String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
                    if (tradeCheckFollow.getCheckState() != null && thirdCheck.contains(tradeCheckFollow.getCheckState().toString())) {
                        tradeCheckFollow.setExceptionType("长款（渠道多出）");
                    } else {
                        tradeCheckFollow.setExceptionType("短款（HIS多出）");
                    }
                }
                if (orgList.size() > 0) {
                    for (Organization o : orgList) {
                        if (tradeCheckFollow.getOrgNo() != null && tradeCheckFollow.getOrgNo().equals(o.getCode())) {
                            tradeCheckFollow.setOrgNo(o.getName());
                        }
                    }
                }
            }

        }

        return list;
    }

    @Transactional
    @Override
    public String checkRefund(Long id, User user) throws Exception {
        String resultStr = "";
        try {
            TradeCheckFollow tradeCheckFollow = tradeCheckFollowDao.findOne(id);
            ShopInfo shopInfo = shopInfoDao.findByOrgNoAndMetaDataPayId(tradeCheckFollow.getOrgNo(), tradeCheckFollow.getPayName());
            JSONObject jb = new JSONObject();
            jb.put("Trade_Code", EnumType.TRADE_CODE_REFUND.getValue());
            jb.put("Org_No", tradeCheckFollow.getOrgNo().toString());
            jb.put("Pay_Shop_No", shopInfo.getPayShopNo());
            jb.put("Pay_App_ID", shopInfo.getApplyId());
            jb.put("Pay_Source", EnumType.PAY_SOURCE_REFUND.getValue());
            if (tradeCheckFollow.getPayName() == EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()) {
                jb.put("Pay_Type", EnumType.PAY_TYPE_WECHAT_REFUND.getValue());
            } else {
                jb.put("Pay_Type", EnumType.PAY_TYPE_ALIPAY_REFUND.getValue());
            }
            jb.put("Pay_Flow_No", CommonConstant.REFUND_FLAG + new Date().getTime());
            jb.put("Pay_Amount", tradeCheckFollow.getTradeAmount());
            jb.put("Pay_Round", tradeCheckFollow.getTradeAmount());
            jb.put("Device_No", "ZHZFPT");
            jb.put("Ori_Pay_Flow_No", tradeCheckFollow.getBusinessNo());
            jb.put("Chk", "");
            logger.info("隔日对账调用银医退费接口入参====》" + jb.toString());
            System.out.println("隔日对账调用银医退费接口入参====》" + jb.toString());
            IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
            String result = iPaymentService.entrance(jb.toString());
            JSONObject jsonObject = JSONObject.fromObject(result);
            logger.info("隔日对账调用银医退费接口返回====》" + result);
            System.out.println("隔日对账调用银医退费接口返回====》" + result);
            if (CommonConstant.TRADE_CODE_SUCCESS.equals(jsonObject.getString("Response_Code"))) {
                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.zp.getValue());
                tradeCheckFollowDao.save(tradeCheckFollow);
                refundSeting(tradeCheckFollow, "隔日对账退费-已退费");//记录退费记录表
                resultStr = "退费成功";
            } else {
                resultStr = "退费失败！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultStr = "退费失败！";
            logger.error("退费失败！");
        }
        return resultStr;
    }

    private void refundSeting(TradeCheckFollow tradeCheckFollow, String handleRemark) {
        ExcepHandingRecord ehr = new ExcepHandingRecord();
        ehr.setOrgNo(tradeCheckFollow.getOrgNo());
        ehr.setPaymentRequestFlow(tradeCheckFollow.getBusinessNo());
        ehr.setPaymentFlow(tradeCheckFollow.getPayNo());
        ehr.setPayName(tradeCheckFollow.getPayName());
        ehr.setTradeAmount(tradeCheckFollow.getTradeAmount());
        ehr.setTradeTime(tradeCheckFollow.getTradeTime());
        ehr.setHandleRemark(handleRemark);
        ehr.setHandleDateTime(new Date());
        excepHandingRecordDao.save(ehr);
    }


    private String combinationBillSource(String billSource) {
        if ("0".equals(billSource)) {
            return "";
        }
        return " bill_source, ";
    }

    /**
     * 实收总金额详情
     */
    @Override
    public List<Map<String, Object>> getFollowRecMapDetail(String startDate, String endDate, AppRuntimeConfig hConfig) {
        // 根据配置信息查询结果
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
        String billSource = combinationBillSource(null);

        String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
        List<Map<String, Object>> typeListMap = getFollowCountByType(startDate, endDate, payTypeSql, orgCode , billSource);
        return typeListMap;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFollowCountByType(String startDate, String endDate, String payTypeSql, String orgNo, String billSource) {
        final String sql = String.format(
                "SELECT %s rec_pay_type,SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary " +
                        " WHERE data_source = 'third' " +
                        " AND org_no = '%s'" +
                        " AND Trade_Date >= '%s' AND Trade_Date <= '%s' " +
                        " AND rec_pay_type in (%s)" +
                        " GROUP BY %s rec_pay_type "
                , billSource, orgNo, startDate, endDate, payTypeSql, billSource);
        logger.info(" getFollowCountByType sql============" + sql);
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

    public List<Map<String, Object>> getExportExceptionDetailBill(TradeCheckFollowVo vo) {
        String startDate = vo.getStartDate() + " 00:00:00";
        String endDate = vo.getStartDate() + " 23:59:59";
        String dataSourceType = vo.getDataSourceType();

        String orgNoSql = concatOrgNoSql(vo.getOrgNo());

        List<Map<String, Object>> payFlowNos = getDealedPayFlowNo(orgNoSql);
        // 查询异常账单过滤的支付流水号，2短款抹平，3长款抹平，10短款追回  3已退费
        String payFlowNoSql = "";
        // 存放审核中、已驳回等未处理完成的状态
        Map<String, String> payFlowNoMap = new HashMap<>();
        for (Map<String, Object> map : payFlowNos) {
            // 未处理完成的状态，退费的审核中，已驳回
            if ("refund".equals(map.get("opt").toString())
                    && (RefundStateEnum.unExamine.getValue().equals(map.get("state").toString())
                    || RefundStateEnum.reject.getValue().equals(map.get("state").toString()))) {
                String stateName = "";
                if (map.get("state").toString().equals(RefundStateEnum.unExamine.getValue())) {
                    stateName = "待审核";
                } else if (map.get("state").toString().equals(RefundStateEnum.reject.getValue())) {
                    stateName = "已驳回";
                }
                payFlowNoMap.put(map.get("payFlowNo").toString(), stateName);
            } else {
                payFlowNoSql += "\'" + map.get("payFlowNo") + "\',";
            }
        }
        if (payFlowNoSql.indexOf(",") > -1) {
            payFlowNoSql = payFlowNoSql.substring(0, payFlowNoSql.length() - 1);
        } else {
            payFlowNoSql = "\'-1\'";
        }
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());

        String checkStates = "";
        if ("his".equals(dataSourceType)) {
            checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
        } else if ("third".equals(dataSourceType)) {
            checkStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        } else {
            checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue() + ","
                    + CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        }

        List<Map<String, Object>> hisList = getHisExceptionBill(orgNoSql, payTypeSql, checkStates, startDate, endDate, payFlowNoSql);
        List<Map<String, Object>> thirdList = getThirdExceptionBill(orgNoSql, payTypeSql, checkStates, startDate, endDate, payFlowNoSql);

        // 对账开始
        Map<String, Object> hisMap = null;
        Map<String, Object> thirdMap = null;
        for (int i = hisList.size() - 1; i >= 0; i--) {
            for (int j = thirdList.size() - 1; j >= 0; j--) {
                hisMap = hisList.get(i);
                thirdMap = thirdList.get(j);
                String hisPayFlowNo = hisMap.get("businessNo") == null ? "" : hisMap.get("businessNo").toString();
                BigDecimal hisPayAmount = new BigDecimal(
                        hisMap.get("tradeAmount") == null ? "0" : hisMap.get("tradeAmount").toString());
                String hisOrderState = hisMap.get("tradeName") == null ? "" : hisMap.get("tradeName").toString();
                String hisPayType = hisMap.get("payName") == null ? "" : hisMap.get("payName").toString();

                String thirdPayFlowNo = thirdMap.get("businessNo") == null ? "" : thirdMap.get("businessNo").toString();
                BigDecimal thirdPayAmount = new BigDecimal(
                        thirdMap.get("tradeAmount") == null ? "0" : thirdMap.get("tradeAmount").toString());
                String thirdOrderState = thirdMap.get("tradeName") == null ? "" : thirdMap.get("tradeName").toString();
                String thirdPayType = thirdMap.get("payName") == null ? "" : thirdMap.get("payName").toString();

                if (StringUtils.equals(hisPayFlowNo, thirdPayFlowNo) && hisPayAmount.compareTo(thirdPayAmount) == 0
                        && StringUtils.equals(hisOrderState, thirdOrderState)
                        && StringUtils.equals(hisPayType, thirdPayType)) {
                    hisList.remove(i);
                    thirdList.remove(j);
                    break;
                }
            }
        }

        List<TradeCheckFollow> result = new ArrayList<>();
        result.addAll(hisConvertToTradeCheckFollow(hisList));
        result.addAll(thirdConvertToTradeCheckFollow(thirdList));
        Collections.sort(result, new Comparator<TradeCheckFollow>() {
            @Override
            public int compare(TradeCheckFollow o1, TradeCheckFollow o2) {

                return o1.getBusinessNo().compareTo(o2.getBusinessNo());
            }
        });
        return converToMap(result, payFlowNoMap);
    }

    public List<Map<String, Object>> converToMap(List<TradeCheckFollow> list, Map<String, String> payFlowNoMap) {
        List<Map<String, Object>> tradeCheckMapList = new ArrayList<>();
        Map<String, String> typesMap = ValueTexts.asMap(metaDataService.NameAsList());
        for (TradeCheckFollow tradeCheckFollow : list) {
            HashMap<String, Object> tradeCheckMap = new HashMap<>();
            tradeCheckMap.put("businessNo", tradeCheckFollow.getBusinessNo());
            tradeCheckMap.put("hisFlowNo", tradeCheckFollow.getHisFlowNo());
            tradeCheckMap.put("payName", typesMap.get(tradeCheckFollow.getPayName()));
            tradeCheckMap.put("tradeName", typesMap.get(tradeCheckFollow.getTradeName()));
            tradeCheckMap.put("tradeAmount", tradeCheckFollow.getTradeAmount());
            tradeCheckMap.put("patientName", tradeCheckFollow.getPatientName());
            tradeCheckMap.put("tradeTime", DateUtil.dateTimeToStringLine(tradeCheckFollow.getTradeTime()));
            tradeCheckMap.put("billSource", typesMap.get(tradeCheckFollow.getBillSource()));
            tradeCheckMap.put("checkStateValue",
                    payFlowNoMap.containsKey(tradeCheckFollow.getBusinessNo())
                            ? payFlowNoMap.get(tradeCheckFollow.getBusinessNo())
                            : tradeCheckFollow.getCheckStateValue());
            tradeCheckMap.put("oriCheckState", tradeCheckFollow.getOriCheckState());
            tradeCheckMapList.add(tradeCheckMap);
        }
        return tradeCheckMapList;
    }

    public List<TradeCheckFollow> hisConvertToTradeCheckFollow(List<Map<String, Object>> list) {
        List<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>();
        TradeCheckFollow follow = null;
        for (Map<String, Object> map : list) {

            follow = new TradeCheckFollow();
            follow.setHisFlowNo(map.get("hisFlowNo") == null ? "" : map.get("hisFlowNo").toString());
            follow.setBusinessNo(map.get("businessNo") == null ? "" : map.get("businessNo").toString());
            follow.setPayName(map.get("payName") == null ? "" : map.get("payName").toString());
            String orderState = map.get("tradeName") == null ? "" : map.get("tradeName").toString();
            String checkStateValue = StringUtils.equals(orderState, EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? "长款" : "短款";
            follow.setTradeName(orderState);
            follow.setCheckStateValue(checkStateValue);
            follow.setOriCheckState(checkStateValue);
            follow.setTradeAmount(new BigDecimal(map.get("tradeAmount") == null ? "0" : map.get("tradeAmount").toString()).abs());
            follow.setPatientName(map.get("patName") == null ? "" : map.get("patName").toString());
            if (map.get("tradeTime") != null) {
                follow.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", map.get("tradeTime").toString()));
            }
            follow.setBillSource(map.get("billSource") == null ? "" : map.get("billSource").toString());

            follows.add(follow);
        }
        return follows;
    }

    public List<TradeCheckFollow> thirdConvertToTradeCheckFollow(List<Map<String, Object>> list) {
        List<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>();
        TradeCheckFollow follow = null;
        for (Map<String, Object> map : list) {

            follow = new TradeCheckFollow();
            follow.setHisFlowNo(map.get("hisFlowNo") == null ? "" : map.get("hisFlowNo").toString());
            follow.setBusinessNo(map.get("businessNo") == null ? "" : map.get("businessNo").toString());
            follow.setPayName(map.get("payName") == null ? "" : map.get("payName").toString());
            String orderState = map.get("tradeName") == null ? "" : map.get("tradeName").toString();
            String checkStateValue = StringUtils.equals(orderState, EnumTypeOfInt.TRADE_TYPE_REFUND.getValue()) ?
                    "短款" : "长款";
            follow.setTradeName(orderState);
            follow.setCheckStateValue(checkStateValue);
            follow.setOriCheckState(checkStateValue);
            follow.setTradeAmount(new BigDecimal(map.get("tradeAmount") == null ? "0" : map.get("tradeAmount").toString()).abs());
            follow.setPatientName(map.get("patName") == null ? "" : map.get("patName").toString());
            if (map.get("tradeTime") != null) {
                follow.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", map.get("tradeTime").toString()));
            }
            follow.setBillSource(map.get("billSource") == null ? "" : map.get("billSource").toString());

            follows.add(follow);
        }
        return follows;
    }

    public List<Map<String, Object>> getDealedPayFlowNo(String orgNoSql) {
        String findDealedPayFlowNoSql = String.format(
                "  	SELECT DISTINCT " +
                        "	t.`pay_flow_no` payFlowNo, " +
                        " 	t.`exception_state` state, 'deal' opt " +
                        "	FROM " +
                        "  	t_trade_check_follow_deal t " +
                        "	WHERE t.`org_code` IN (%s) " +
                        " 	AND t.`exception_state` <> '11' " +
                        " 	UNION ALL " +
                        "	SELECT DISTINCT " +
                        "  	t.`Payment_Request_Flow` payFlowNo, " +
                        "  	t.`state` state, 'refund' opt " +
                        "	FROM t_exception_handling_record t " +
                        "	WHERE t.`org_no` IN (%s) ",
                orgNoSql, orgNoSql);
        logger.info(" --- findDealedPayFlowNoSql = " + findDealedPayFlowNoSql);
        return super.query(findDealedPayFlowNoSql);
    }

    public List<Map<String, Object>> getHisExceptionBill(String orgNoSql, String payTypeSql, String checkStateSql,
                                                         String startTime, String endTime, String payFlowNoSql) {
        String hisSql = String.format(
                "SELECT" +
                        "  DISTINCT th.`his_flow_no` hisFlowNo," +
                        "  th.pay_flow_no businessNo," +
                        "  th.`pay_type` payName," +
                        "  th.`Order_State` tradeName," +
                        "  th.`Pay_Amount` tradeAmount," +
                        "  th.`Cust_Name` patName," +
                        "  th.`Trade_datatime` tradeTime," +
                        "  th.`bill_source` billSource" +
                        "  FROM " +
                        "  t_trade_check_follow t" +
                        "  INNER JOIN t_rec_histransactionflow th" +
                        "  ON t.`business_no` = th.`Pay_Flow_No`" +
                        "  WHERE t.`org_no` IN (%s)" +
                        "  AND th.`org_no` IN (%s) " +
                        "  AND t.`Pay_Name` IN (%s)" +
                        "  AND t.`check_state` IN (%s) " +
                        "  AND t.trade_time >= '%s' " +
                        "  AND t.trade_time <= '%s' " +
                        "  AND th.`Trade_datatime`>='%s' " +
                        "  AND th.`Trade_datatime`<='%s' " +
                        "  AND th.`pay_type` IN(%s) " +
                        "  AND th.`Pay_Flow_No` NOT IN(%s) ",
                orgNoSql, orgNoSql, payTypeSql, checkStateSql, startTime, endTime, startTime, endTime, payTypeSql,
                payFlowNoSql);
        logger.info(" -- getHisExceptionBill =  " + hisSql);
        return super.query(hisSql);
    }

    public List<Map<String, Object>> getThirdExceptionBill(String orgNoSql, String payTypeSql, String checkStateSql,
                                                           String startTime, String endTime, String payFlowNoSql) {
        String thirdSql = String.format(
                " SELECT " +
                        " 	DISTINCT th.`out_trade_no` hisFlowNo," +
                        "  th.pay_flow_no businessNo," +
                        "  th.`rec_pay_type` payName," +
                        "  th.`Order_State` tradeName," +
                        "  th.`Pay_Amount` tradeAmount," +
                        "  '' patName," +
                        "  th.`Trade_datatime` tradeTime," +
                        "  th.`bill_source` billSource " +
                        "FROM" +
                        "  t_trade_check_follow t" +
                        "  INNER JOIN t_thrid_bill th" +
                        "    ON t.`business_no` = th.`Pay_Flow_No`" +
                        "WHERE t.`org_no` IN (%s)" +
                        "  AND th.`org_no` IN(%s) " +
                        "  AND t.`Pay_Name` IN (%s)" +
                        "  AND t.`check_state` IN (%s)" +
                        "  AND t.trade_time >= '%s'" +
                        "  AND t.trade_time <= '%s'" +
                        "  AND th.`Trade_datatime`>='%s' " +
                        "  AND th.`Trade_datatime`<='%s' " +
                        "  AND th.`pay_type` IN(%s) " +
                        "  AND th.`Pay_Flow_No` NOT IN (%s) ",
                orgNoSql, orgNoSql, payTypeSql, checkStateSql, startTime, endTime, startTime, endTime, payTypeSql,
                payFlowNoSql);
        logger.info(" -- getThirdExceptionBill =  " + thirdSql);
        return super.query(thirdSql);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeCheckFollow> exportToDcExcel(TradeCheckFollowVo cqvo, List<Organization> orgList, PageRequest pageable) {
        Page<TradeCheckFollow> tradeCheckFollowPage = findByOrgNoAndTradeDate(cqvo, pageable);
        List<TradeCheckFollow> list = tradeCheckFollowPage.getContent();
        return list;
    }

    @Override
    public Map<String, Object> getDiffAmount(TradeCheckFollowVo unusualBillVo) {
        String startDate = unusualBillVo.getStartDate().trim();
        String endDate = unusualBillVo.getEndDate().trim();
        String businessNo = unusualBillVo.getBusinessNo();
        String hisFlowNo = unusualBillVo.getHisFlowNo();
        String dataSourceType = unusualBillVo.getDataSourceType();
        String billSource = "";
        if (StringUtils.isNotBlank(unusualBillVo.getBillSource())) {
            billSource = "'" + billSourceNameFormat(unusualBillVo.getBillSource()) + "'";
        } else {
            List<String> billSourceList = getAllBillSource();
            for (String billSourceStr : billSourceList) {
                if (!"".equals(billSource)) {
                    billSource = billSource + ",'" + billSourceStr + "'";
                } else {
                    billSource = "'" + billSourceStr + "'";
                }
            }
        }
        String orgNoSql = concatOrgNoSql(unusualBillVo.getOrgNo());
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
        String patType = unusualBillVo.getPatType();
        String patTypeSql = "";
        List<String> billSourceList = new ArrayList<>();
        if (StringUtils.isNotBlank(patType)) {
            if ("zy".equals(patType)) {
                // 住院数据
                patTypeSql = " AND pat_type = 'zy' ";
                getRecBillSource(orgNoSql, "rec.zy.billsource", billSourceList);
            } else {
                // 门诊数据
                patTypeSql = " AND ( pat_type != 'zy' or pat_type is null) ";
                getRecBillSource(orgNoSql, "rec.mz.billsource", billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNoSql, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNoSql, "rec.zy.billsource", billSourceListZy);
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
        }
        if (billSource.contains(",") && billSourceList.size() > 0) {
            billSource = "";
            // 只汇总需要展示的渠道的数据
            for (String billSourceStr : billSourceList) {
                if ("".equals(billSource)) {
                    billSource = "'" + billSourceStr + "'";
                } else {
                    billSource = billSource + ",'" + billSourceStr + "'";
                }
            }
        }
        String sql = "SELECT SUM( CASE WHEN t.`check_state` IN (3, 5) THEN t.`trade_amount` ELSE 0 END ) longAmount, "
                + " SUM( CASE WHEN t.`check_state` IN (2, 6) THEN ABS(t.`trade_amount`) ELSE 0 END ) shotAmount "
                /*+ " SUM( CASE WHEN (t.trade_name = '0156') THEN ABS(t.`trade_amount`) " +
                +"   WHEN (t.trade_name = '0256') THEN -ABS(t.`trade_amount`) ELSE 0 END ) shotAmount "*/
                + " FROM t_trade_check_follow t "
                + " WHERE t.`org_no` IN (" + orgNoSql + ") "
                + " AND DATE_FORMAT(t.`trade_date`,'%Y-%m-%d')>='" + startDate + "' AND DATE_FORMAT(t.`trade_date`,'%Y-%m-%d')<='" + endDate + "' AND t.`Pay_Name` IN(" + payTypeSql + ")"
                + " AND t.bill_source in (" + billSource + ") " + patTypeSql;
        if (StringUtils.isNotBlank(businessNo)) {
            sql += " AND t.business_no like '%" + businessNo + "%' ";
        }
        if (StringUtils.isNotBlank(hisFlowNo)) {
            sql += " AND t.his_flow_no like '%" + hisFlowNo + "%' ";
        }
        if (dataSourceType != null) {
            String checkStates = "";
            if ("his".equals(dataSourceType)) {
                checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
            } else if ("third".equals(dataSourceType)) {
                checkStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
            } else {
                checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue() + ","
                        + CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
            }
            sql += " AND t.check_state in (" + checkStates + ") ";
        }
        logger.info(" getDiffAmount sql============" + sql);
        List<Map<String, Object>> queryList = super.queryList(sql, null, null);
        BigDecimal longAmount = queryList.get(0).get("longAmount") == null ? new BigDecimal(0.00)
                : new BigDecimal(queryList.get(0).get("longAmount").toString());
        BigDecimal shortAmount = queryList.get(0).get("shotAmount") == null ? new BigDecimal(0.00)
                : new BigDecimal(queryList.get(0).get("shotAmount").toString());
        Map<String, Object> map = new HashMap<>();
        map.put("diffAmount", longAmount.subtract(shortAmount));

        /*Map<String, Object> map = new HashMap<>();
        map.put("diffAmount", queryList.get(0).get("shotAmount") == null ? new BigDecimal(0.00) :
                queryList.get(0).get("shotAmount").toString());*/
        return map;
    }

    @Override
    public List<Map<String, Object>> getPayStep(String businessNo, String orgCodes, String billSource, String recHisId, String recThirdId) {
        String billSql="";
        if(StringUtils.isNotBlank(billSource)) {
            billSql=" and bill_source= '"+billSource+"'";
        }
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("SELECT * FROM (");
        sbuf.append(" SELECT t.Order_State,");
        sbuf.append(" t.pay_type,t.Pay_Amount,DATE_FORMAT(t.Trade_datatime,'%Y-%m-%d %T') AS tradeDate,'thrid' as billSourece,'0' refundType");
        sbuf.append(" FROM t_thrid_bill t WHERE 1=1 and org_no = '"+orgCodes+"'  "+billSql);

        // 过滤单号为0的情况，通过ID查询
        if (businessNo.equals("0")) {
            if (StringUtils.isNotBlank(recThirdId)) {
                sbuf.append(" and t.id =  '" + recThirdId + "'  ");
            } else {
                sbuf.append(" and t.id =  ''");
            }
        } else {
            sbuf.append(" and t.Pay_Flow_No='").append(businessNo).append("' OR t.shop_flow_no = '").append(businessNo).append("'");
        }
//        +" and t.Pay_Flow_No = '").append(businessNo).append("' ");
        sbuf.append(" UNION ALL ");
        sbuf.append(" SELECT t.Order_State,t.pay_type,t.Pay_Amount,DATE_FORMAT(t.Trade_datatime,'%Y-%m-%d %T') AS tradeDate,'his' as billSourece,'0' refundType");
        sbuf.append(" FROM t_rec_histransactionflow t WHERE 1=1 and org_no = '"+orgCodes+"'  "+billSql);

        // 过滤单号为0的情况，通过ID查询
        if (businessNo.equals("0")) {
            if (StringUtils.isNotBlank(recHisId)) {
                sbuf.append(" and t.id =  '" + recHisId + "'  ");
            } else {
                sbuf.append(" and t.id =  ''");
            }
        } else {
            sbuf.append(" and t.Pay_Flow_No='").append(businessNo).append("' ");
        }
//                +" and t.Pay_Flow_No='").append(businessNo).append("' ");
        sbuf.append(" UNION ALL ");
        sbuf.append(" SELECT '0256' order_state,Pay_Name,(-Trade_Amount) Trade_Amount,DATE_FORMAT(Handle_Date_Time,'%Y-%m-%d %T') AS tradeDate, 'thrid' as billSourece,refund_type refundType");
        sbuf.append(" FROM t_exception_handling_record");
        sbuf.append(" WHERE state=3 and org_no = '"+orgCodes+"' "+billSql+" and (ISNULL(father_id) or father_id='') AND Payment_Request_Flow='").append(businessNo).append("' ");
        sbuf.append(") e");
        sbuf.append(" ORDER BY  e.tradeDate ASC,e.billSourece desc,e.order_state asc");
        return handleNativeSql(sbuf.toString(), new String[]{"orderState", "payType", "payAmount", "tradeDate", "billSource","refundType"});
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, TradeCheckVo> getPatIdMap(List<TradeCheckFollow> hisList) {
        Map<String, TradeCheckVo> patIdMap = new HashMap<>();
        StringBuffer payFlowNoBuffer = new StringBuffer();
        List<String> list = new ArrayList<>();
        for (TradeCheckFollow tradeCheckFollow : hisList) {
            if (StringUtils.isNotEmpty(tradeCheckFollow.getBusinessNo())) {
                payFlowNoBuffer.append("\"").append(tradeCheckFollow.getBusinessNo()).append("\",");
                list.add(tradeCheckFollow.getBusinessNo());
            }
        }
        if (payFlowNoBuffer.length() == 0) {
            return patIdMap;
        }
        if (list.size() > 900) {
            patIdMap = getInParameter(list, patIdMap);
        } else {
            String payflowNo = payFlowNoBuffer.substring(0, payFlowNoBuffer.length() - 1);
            String sql = "select Pay_Flow_No as payFlowNo,IF(pat_type='mz',mz_code,pat_code) AS patId,Cust_Name as custName,his_flow_no as hisFlowNo from t_rec_histransactionflow where 1=1 and Pay_Flow_No in (" + payflowNo + ")";
            List<Map<String, Object>> sqlList = super.queryList(sql,null,null);
            for (Map<String, Object> map : sqlList) {
                TradeCheckVo vo = new TradeCheckVo();
                vo.setPayFlowNo(map.get("0") == null ? "" : map.get("0").toString());
                vo.setPatId(map.get("1") == null ? "" : map.get("1").toString());
                vo.setCustName(map.get("2") == null ? "" : map.get("2").toString());
                if (patIdMap.containsKey(vo.getPayFlowNo()) && !patIdMap.get(vo.getPayFlowNo()).getHisFlowNo().equals(vo.getHisFlowNo())) {
                    vo.setHisFlowNo("");
                } else {
                    vo.setHisFlowNo(map.get("3") == null ? "" : map.get("3").toString());
                }
                patIdMap.put(vo.getPayFlowNo(), vo);
            }
        }

        return patIdMap;
    }

    public Map<String, TradeCheckVo> getInParameter(List list, Map<String, TradeCheckVo> patIdMap) {
        if (!list.isEmpty()) {
            List<String> setList = new ArrayList<String>(0);
            Set set = new HashSet();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i <= list.size(); i++) {
                set.add("'" + list.get(i - 1) + "'");
                if (i % 900 == 0) {//900为阈值
                    setList.add(StringUtils.join(set.iterator(), ","));
                    set.clear();
                }
            }
            if (!set.isEmpty()) {
                setList.add(StringUtils.join(set.iterator(), ","));
            }
            stringBuffer.append(setList.get(0));
            for (int j = 1; j < setList.size(); j++) {
//                stringBuffer.append(") or " + parameter + " in (");
//                stringBuffer.append(setList.get(j));
                String sql = "select Pay_Flow_No as payFlowNo,IF(pat_type='mz',mz_code,pat_code) AS patId from t_rec_histransactionflow where 1=1 and Pay_Flow_No in (" + setList.get(j) + ")";
                List<Map<String, Object>> sqlList = super.queryList(sql, null, null);
                for (Map<String, Object> map : sqlList) {
                    TradeCheckVo vo = new TradeCheckVo();
                    vo.setPayFlowNo(map.get("0") == null ? "" : map.get("0").toString());
                    vo.setPatId(map.get("1") == null ? "" : map.get("1").toString());
                    vo.setCustName(map.get("2") == null ? "" : map.get("2").toString());
                    if (patIdMap.containsKey(vo.getPayFlowNo()) && !patIdMap.get(vo.getPayFlowNo()).getHisFlowNo().equals(vo.getHisFlowNo())) {
                        vo.setHisFlowNo("");
                    } else {
                        vo.setHisFlowNo(map.get("3") == null ? "" : map.get("3").toString());
                    }
                    patIdMap.put(vo.getPayFlowNo(), vo);
//        			patIdMap.put(map.get("payFlowNo").toString(), map.get("patId") == null ? "":map.get("patId").toString());
                }
            }
            return patIdMap;
        } else {
            return patIdMap;
        }
    }

    public static String getpayTypes(String payType) {
        StringBuilder sql = new StringBuilder();
        if(StringUtils.isNotBlank(payType)) {
            String[] payTypes = payType.split(",");
            for(String v:payTypes) {
                sql.append("'" + v + "'" + ",");
            }
        }else {
            return "";
        }
        if (sql.length() > 0) {
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }

    //统计渠道的支付详情
    public ResponseResult payDetails(String orgNo, String startDate, String endDate,String billSource, String patType) {
    	ResponseResult result = ResponseResult.success();
        Map<String, Object> resultmMap= new HashMap<>();
        String orgNoSql = this.concatOrgNoSql(orgNo);
        //判断该支付类型是否存在
        List<Map<String, Object>> dataList=new ArrayList<>();
        // 根据配置信息查询结果
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String payTypeSql = getpayTypes(hConfig.getRecType());
        //获取渠道的支付数据
        List<Map<String, Object>> list = getDetailsData(startDate,endDate,payTypeSql,orgNoSql,billSource, patType);
        //处理没有的支付类型
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> z : list) {
            String v = (String) z.get("payType");
            //拼接处理后详情
            dealDetails(startDate, endDate, v, orgNoSql, z, billSource, patType);
        }
        dataList.addAll(list);
        dataList.addAll(mapList);
        //获取订单来源列表   此处统计个数
        List<Map<String, Object>> lineList = getDate(startDate,endDate,payTypeSql,orgNoSql, patType);
        resultmMap.put("headData", dataList);
        resultmMap.put("headLine", lineList);
        result.data(resultmMap);
        return result;
    }

    private void dealDetails(String startDate, String endDate, String payType,
                             String orgNo,Map<String, Object> z,String billSource, String patType) {
        //合计特殊处理
        if(payType.equals("hj")) {
            payType="";
        }
        //对账后异常金额
        List<Map<String, Object>> payTypeMap = getPayType(startDate,endDate,payType,orgNo,billSource, patType);
        if(payTypeMap==null||payTypeMap.size()==0) {
            Map<String, Object> map=new HashMap<>();
            map.put("afterDifference", new BigDecimal(0));
            map.put("afterAcount", new BigInteger("0"));
            payTypeMap.add(map);
        }
        //调账后异常金额
        List<Map<String, Object>> dealMap = getPayTypeDeal(startDate,endDate,payType,orgNo,billSource, patType);
        if(dealMap==null||dealMap.size()==0) {
            Map<String, Object> map=new HashMap<>();
            map.put("dealAfterDifference", new BigDecimal(0));
            map.put("dealAfterAcount", new BigInteger("0"));
            dealMap.add(map);
        }
        Number objDifference = (Number)payTypeMap.get(0).get("afterDifference");
        Number objDealDifference=(Number)dealMap.get(0).get("dealAfterDifference");
        BigDecimal amount=new BigDecimal(String.format("%.2f", objDifference.doubleValue()));
        BigDecimal dealAmount=new BigDecimal(String.format("%.2f", objDealDifference.doubleValue()));
        z.put("afterDifference", amount);
        z.put("afterAcount", payTypeMap.get(0).get("afterAcount"));
        //计算处理后剩余异常金额
        z.put("dealAfterDifference", amount.add(dealAmount));
//        z.put("dealAfterDifference", amount.subtract(dealAmount));
        z.put("dealAfterAcount", ((BigInteger)payTypeMap.get(0).get("afterAcount")).subtract((BigInteger)dealMap.get(0).get("dealAfterAcount")));
    }

    //合计异常数
    private String dealExcount(String startDate, String endDate,
                               String orgNo,Map<String, Object> z,String billSource, String patType) {
        BigInteger payCount=new BigInteger("0");
        BigInteger dealPayCount=new BigInteger("0");
        //对账后异常金额
        List<Map<String, Object>> payTypeMap = getPayType(startDate,endDate,"",orgNo,billSource, patType);
        //调账后异常金额
        List<Map<String, Object>> dealMap = getPayTypeDeal(startDate,endDate,"",orgNo,billSource, patType);
        //计算异常个数
        for(Map<String, Object> v:payTypeMap) {
            payCount=payCount.add(new BigInteger(v.get("afterAcount").toString()));
        }
        for(Map<String, Object> v:dealMap) {
            dealPayCount=dealPayCount.add(new BigInteger(v.get("dealAfterAcount").toString()));
        }

        return payCount.subtract(dealPayCount).toString();
    }

    /**
     * 获取异常数目
     * @param startDate
     * @param endDate
     * @param payType
     * @param orgNo
     * @param
     * @return
     */
    private List<Map<String, Object>> getDate(String startDate, String endDate, String payType,
                                              String orgNo, String patType) {
        List<Map<String, Object>> mapList= new ArrayList<>();
        Map<String, Object> mapAll= new HashMap<>();
        mapAll.put("name", "全部");
        mapAll.put("value", "");
        mapList.add(mapAll);
        //获取订单来源
        List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
        // 只显示配置的渠道
        List<MetaData> metaDataExt = new ArrayList<>();
        List<String> billSourceList = new ArrayList<>();
        if (StringUtils.isNotBlank(patType)) {
            if ("zy".equals(patType)) {
                getRecBillSource(orgNo, "rec.zy.billsource", billSourceList);
            } else if ("mz".equals(patType)) {
                getRecBillSource(orgNo, "rec.mz.billsource", billSourceList);
            }
            if (billSourceList != null && billSourceList.size() > 0) {
                for (MetaData metaData : metaDataList) {
                    for (String billSource : billSourceList) {
                        if (metaData.getValue().equals(billSource)) {
                            metaDataExt.add(metaData);
                            break;
                        }
                    }
                }
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListZy);
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0){
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
            if (billSourceList != null && billSourceList.size() > 0) {
                for (MetaData metaData : metaDataList) {
                    for (String billSource : billSourceList) {
                        if (metaData.getValue().equals(billSource)) {
                            metaDataExt.add(metaData);
                            break;
                        }
                    }
                }
            }
        }

        if (billSourceList.size() == 0){
            metaDataExt = metaDataList;
        }
        for(MetaData v:metaDataExt) {
            Map<String, Object> map= new HashMap<>();
            map.put("value", v.getValue());
            map.put("name", v.getName());
            map.put("exceptionNum", dealExcount(startDate,endDate,orgNo,map,v.getValue(), patType));
            mapList.add(map);
        }
        return mapList;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getPayTypeDeal(String startDate, String endDate, String payType,
                                                     String orgNo,String billSource, String patType) {
        String sql =null;
        String type="";
        String groupBy="";
        String tradeCheckFollowPayType = "";
        if(StringUtils.isNotBlank(payType)) {
            type=" AND t.pay_type ='"+payType+"'";
            tradeCheckFollowPayType = " AND t.Pay_Name ='"+payType+"'";
            groupBy=" GROUP BY t.pay_type";
        }else {
            type=" AND t.pay_type not in ('0049','0449')";
            tradeCheckFollowPayType=" AND t.Pay_Name not in ('0049','0449')";
        }
        String source="";
        String patTyeSql = "";
        String billSourceSql = "";
        List<String> billSourceList = new ArrayList<>();
        if (!StringUtil.isEmpty(patType)) {
            if ("zy".equals(patType)) {
                // 住院
                patTyeSql = " AND t.pat_type = 'zy'";
                // 只显示配置渠道
                getRecBillSource(orgNo, "rec.zy.billsource", billSourceList);
                billSourceSql = billSourceSql(billSourceList);
            } else {
                // 门诊
                patTyeSql = " AND (t.pat_type != 'zy' or t.pat_type is null)";
                // 只显示配置渠道
                getRecBillSource(orgNo, "rec.mz.billsource", billSourceList);
                billSourceSql = billSourceSql(billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListZy);
            StringBuffer orgStringBuf = new StringBuffer();
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
            for (int i = 0; i < billSourceList.size(); i++) {
                if (i == 0) {
                    orgStringBuf.append("'" + billSourceList.get(i) + "'");
                } else {
                    orgStringBuf.append(",'" + billSourceList.get(i) + "'");
                }
            }
            if (!"".equals(orgStringBuf.toString().trim())) {
                billSourceSql = " and  t.bill_source in (" + orgStringBuf.toString() + ") ";
            }
        }
        if(StringUtils.isNotBlank(billSource)) {
            source=" and t.bill_source='"+billSource+"'";
            billSourceSql="";
        }
        sql = String.format(
                "SELECT IFNULL(SUM(case when ttc.check_state=2 THEN abs(ttc.tradeAmount) else -abs(ttc.tradeAmount) end),0) dealAfterDifference,"
                        + " COUNT(1) dealAfterAcount "
                        + " from t_trade_check_follow_deal t LEFT JOIN t_exception_handling_record te "
                        + " on te.Payment_Request_Flow=t.pay_flow_no and (ISNULL(te.father_id) or te.father_id=0) and t.exception_state='11'"
                        + " INNER JOIN (select t.trade_amount  tradeAmount,t.check_state, t.business_no from t_trade_check_follow t "
                        + " where t.org_no IN (%s) AND t.trade_date >= '%s' AND t.trade_date <= '%s' "
                        + tradeCheckFollowPayType + source + patTyeSql + billSourceSql + " ) ttc ON t.pay_flow_no=ttc.business_no "
                        + " where t.org_code IN (%s) AND t.trade_datetime >= '%s' AND t.trade_datetime <= '%s'"
                        + type + source + patTyeSql + billSourceSql
                        + " and (te.state=3 or ISNULL(te.state)) "
                        + groupBy, orgNo, startDate, endDate, orgNo, startDate, endDate);
//        sql = String.format(
//        		" SELECT if(ISNULL(SUM(t.deal_amount)),0,SUM(t.deal_amount))dealAfterDifference,"
//        				+ "COUNT(1) dealAfterAcount"
//        				+ " from t_trade_check_follow_deal  t"
//        				+ " LEFT JOIN t_exception_handling_record te on te.Payment_Request_Flow=t.pay_flow_no and (ISNULL(te.father_id) or te.father_id=0)"
//        				+ " and t.exception_state='11' "
//        				+ " where t.org_code IN (%s) AND t.trade_datetime >= '%s' AND t.trade_datetime <= '%s'"
//        				+ type + source + patTyeSql + billSourceSql
//        				+ " and (te.state=3 or ISNULL(te.state)) "
//        				+ groupBy, orgNo, startDate, endDate);
        logger.info(" getPayTypeDeal sql ============" + sql);
        return super.queryList(sql,null,null);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getPayType(String startDate, String endDate, String payType,
                                                 String orgNo,String billSource, String patType) {
    	String sql =null;
        String type="";
        String groupBy="";
        if(StringUtils.isNotBlank(payType)) {
            type=" AND t.Pay_Name ='"+payType+"'";
            groupBy=" GROUP BY t.Pay_Name";
        }else {
            type=" AND t.Pay_Name not in ('0049','0449')";
        }
        String source = "";
        String patTypeSql = "";
        String billSourceSql = "";
        List<String> billSourceList = new ArrayList<>();
        if (!StringUtil.isEmpty(patType)) {
            if ("zy".equals(patType)) {
                // 住院
                patTypeSql = " and t.pat_type = 'zy'";
                // 只显示配置的渠道
                billSourceSql = getRecBillSource(orgNo, "rec.zy.billsource", billSourceList);
            } else {
                // 门诊
                patTypeSql = " and (t.pat_type != 'zy' or t.pat_type is null)";
                // 只显示配置的渠道
                billSourceSql = getRecBillSource(orgNo, "rec.mz.billsource", billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListZy);
            StringBuffer orgStringBuf = new StringBuffer();
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0){
                billSourceList.addAll(billSourceListMz);
                billSourceList.addAll(billSourceListZy);
            }
            for (int i = 0; i < billSourceList.size(); i++) {
                if (i == 0) {
                    orgStringBuf.append("'" + billSourceList.get(i) + "'");
                } else {
                    orgStringBuf.append(",'" + billSourceList.get(i) + "'");
                }
            }
            if (!"".equals(orgStringBuf.toString().trim())) {
                billSourceSql = " and  t.bill_source in (" + orgStringBuf.toString() + ") ";
            }
        }
        if (StringUtils.isNotBlank(billSource)) {
            source = " and t.bill_source='" + billSource + "'";
            billSourceSql="";
        }
        /*sql = String.format(
                " SELECT IFNULL("
                        + "(ABS(SUM(if(ISNULL(rec_his_id),0,CASE trade_name WHEN '0156' THEN `trade_amount` ELSE - ABS(`trade_amount`) END )))-"
                        + "ABS(SUM(if(ISNULL(rec_thrid_id),0,CASE trade_name WHEN '0156' THEN `trade_amount` ELSE - ABS(`trade_amount`) END )))),0) afterDifference,"
                        + "COUNT(1) afterAcount"
                        + " from t_trade_check_follow  where org_no IN (%s) AND trade_date >= '%s' AND trade_date <= '%s'" + patTypeSql + billSourceSql
                        + type + source
                        + groupBy, orgNo, startDate, endDate);*/

        sql = String.format(
                "SELECT IFNULL((SUM(CASE t.check_state WHEN '3' THEN ABS(t.trade_amount) ELSE 0 END ) - " +
                        "    SUM(CASE t.check_state WHEN '2' THEN ABS(t.trade_amount) ELSE 0 END )),0) AS afterDifference, " +
                        "    COUNT(1) afterAcount " +
                        "FROM (select * from t_trade_check_follow where trade_date >= '%s' and trade_date <= '%s') t" +
                        " WHERE 1=1 " + patTypeSql + billSourceSql +
                        " and t.org_no IN (%s) " +
                        source + type +
                        " AND t.Pay_Name NOT IN ('0049','0449')" + groupBy, startDate, endDate, orgNo
        );
        logger.info(" getDetailsSum sql ============" + sql);
        return super.queryList(sql,null,null);
    }
    
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getDetailsData(String startDate, String endDate, String payTypeSql,
                                                     String orgNo, String billSource, String patType) {
        String source = "";
        if (StringUtils.isNotBlank(billSource)) {
            source = " and bill_source='%s'";
        }
        String patTyeSql = "";
        String billSourceSql = "";
        List<String> billSourceList = new ArrayList<>();
        if (!StringUtil.isEmpty(patType)) {
            if ("zy".equals(patType)) {
                // 住院
                patTyeSql = " AND pat_type = 'zy'";
                // 只显示配置渠道
                billSourceSql = getRecBillSource(orgNo, "rec.zy.billsource", billSourceList);
            } else {
                // 门诊
                patTyeSql = " AND (pat_type != 'zy' or pat_type is null)";
                // 只显示配置渠道
                billSourceSql = getRecBillSource(orgNo, "rec.mz.billsource", billSourceList);
            }
        } else {
            // 不区分住院门诊，若配置了显示渠道值，那仅显示配置好的渠道
            List<String> billSourceListMz = new ArrayList<>();
            getRecBillSource(orgNo, "rec.mz.billsource", billSourceListMz);
            List<String> billSourceListZy = new ArrayList<>();
            getRecBillSource(orgNo, "rec.zy.billsource", billSourceListZy);
            StringBuffer orgStringBuf = new StringBuffer();
            if (billSourceListMz.size() > 0 && billSourceListZy.size() > 0) {
                for (int i = 0; i < billSourceList.size(); i++) {
                    if (i == 0) {
                        orgStringBuf.append("'" + billSourceList.get(i) + "'");
                    } else {
                        orgStringBuf.append(",'" + billSourceList.get(i) + "'");
                    }
                }
            }
            if (!"".equals(orgStringBuf.toString().trim())) {
                billSourceSql = " and  bill_source in (" + orgStringBuf.toString() + ") ";
            }
        }
        final String sql = String.format(
                " SELECT if(ISNULL(rec_pay_type),'hj',rec_pay_type) payType,"
                        + "SUM( IF ( data_source = 'his', CASE order_state WHEN '0156' THEN `pay_amount` ELSE - ABS(`pay_amount`) END, 0 )) hisPayAmount,"
                        + "SUM( IF ( data_source = 'third', CASE order_state WHEN '0156' THEN `pay_amount` ELSE - ABS(`pay_amount`) END, 0 )) thridPayAmount,"
                        + "SUM( IF ( data_source = 'his', CASE `order_state` WHEN '0256' THEN 0 ELSE IF ( `pay_acount` IS NULL, 0, `pay_acount` ) END, 0 )) hisRealPayAcount,"
                        + "SUM( IF ( data_source = 'third', CASE `order_state` WHEN '0256' THEN 0 ELSE IF ( `pay_acount` IS NULL, 0, `pay_acount` ) END, 0 )) thridRealPayAcount,"
                        + "SUM( IF ( data_source = 'his', CASE `order_state` WHEN '0256' THEN `pay_acount` ELSE 0 END, 0 )) hisRefundAcount,"
                        + "SUM( IF ( data_source = 'third', CASE `order_state` WHEN '0256' THEN `pay_acount` ELSE 0 END, 0 )) thirdRefundAcount"
                        + " FROM t_follow_summary WHERE org_no IN (%s)" + "AND Trade_Date >= '%s' AND Trade_Date <= '%s'" + patTyeSql + billSourceSql
                        + " AND rec_pay_type in (%s)"
                        + " AND rec_pay_type != '0049' "
                        + source
                        + " GROUP BY rec_pay_type WITH ROLLUP",
                orgNo, startDate, endDate, payTypeSql, billSource);
        logger.info(" getDetailsData sql ============" + sql);
        return super.queryList(sql, null, null);
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

    private String billSourceSql(List<String> billSouceList) {
        String billSourceSql = "";
        if (billSouceList != null) {
            StringBuffer orgStringBuf = new StringBuffer();
            for (int i = 0; i < billSouceList.size(); i++) {
                if (i == 0) {
                    orgStringBuf.append("'" + billSouceList.get(i) + "'");
                } else {
                    orgStringBuf.append(",'" + billSouceList.get(i) + "'");
                }
            }
            if (!"".equals(orgStringBuf.toString().trim())) {
                billSourceSql = " and  t.bill_source in (" + orgStringBuf.toString() + ") ";
            }
        }
        return billSourceSql;
    }

}
