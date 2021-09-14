package com.yiban.rec.bill.parse.service.hospital.wujin;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.impl.WechatBillParser;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.vo.ExtraParamVo;
import com.yiban.rec.bill.parse.vo.PayOrder;
import com.yiban.rec.domain.ThirdBill;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: Kevin Liu
 * @Date: 2020/11/27 17:44
 * @Description:
 */
public class LuoYangWechatBillParser extends WechatBillParser {

    /**
     * 微信账单解析
     */
    @Override
    protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {

        // get the wechat bill
        String serverUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
                ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
        String url = serverUrl + "/pay/billLog/getWechatBill";
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("orgCode", orgCode );
        map.put("time", date);
        logger.info("微信请求url：" + url + "--入参：orgCode:" + orgCode + "date" + date);

        List<ThirdBill> list = new ArrayList<ThirdBill>();
        String response;
        try {
            response = HttpClientUtil.doPostJson(url, gson.toJson(map).toString());
            logger.info("微信请求返回结果：" + response);
            @SuppressWarnings("rawtypes")
            Map rmap = gson.fromJson(response, Map.class);
            if (rmap != null) {
                boolean result = (boolean) rmap.get("success");
                if (result) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> strLists = (ArrayList<String>) rmap.get("data");
                    if (strLists != null && strLists.size() > 0) {
                        // 多个账号数据
                        for (String billData : strLists) {
                            // 解析账单
                            parseBill(list, billData, orgCode);
                        }
                    }
                } else {
                    throw new BillParseException(rmap.get("message").toString());
                }
            } else {
                throw new BillParseException("服务器暂无响应");
            }
        } catch (Exception e) {
            throw new BillParseException(e.getMessage());
        }
        super.setBillSource(billSource);
        logger.info("微信插入数据行数：" + list.size());
        return list;
    }


    @Override
    protected void parseBill(List<ThirdBill> list, String billData, String orgCode) {
        // 账单来源
        Set<String> billSourceSet = new HashSet<>();
        // 得到系统来源和字典值配置
        Map<String, String> systemMap = ValueTexts.asMap(super.systemList);
        logger.info("系统配置值：" + systemMap.toString());
        String[] billLines = billData.split("\r\n");

        int lines = billLines.length;
        for (int i = 1; i < lines - 2; i++) {
            String line = billLines[i].replaceAll("`", "");
            String[] rows = line.split(",");
            // 商户订单号
            String payShopFlowNo = rows[6];
            // 微信订单号
            String payFlowNo = rows[5];
            // 支付商户号
            String payShopNo = rows[2];
            // 交易时间
            Date tradeDatatime = DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", rows[0]);
            // 支付金额
            String payAmount = rows[12];
            // 是否退款
            String returnPay = rows[9];
            // 退费金额
            String returnAmount = rows[16];
            ThirdBill tb = new ThirdBill();
            tb.setShopFlowNo(payShopFlowNo);
            tb.setPayFlowNo(payFlowNo);
            tb.setPayShopNo(payShopNo);
            tb.setPayTermNo(rows[4]);
            tb.setTradeDatatime(tradeDatatime);

            tb.setOrgNo(orgCode);
            // 订单中无法找到则采用该值
            tb.setPayType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
            // 订单中无法找到则采用该值
            tb.setRecPayType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
            // 获取订单信息
            PayOrder order = loadPayOrder(payFlowNo);

            // 注入业务流水号
            if (order != null) {
                tb.setOutTradeNo(order.getOutTradeNo());
                tb.setOrderNo(order.getOrderNo());
            }else {//如果没有查到，则查单条记录
                tb.setOutTradeNo(super.initOnePayOrders(payFlowNo));
                tb.setOrderNo(payShopFlowNo);
            }
            // 获取扩展字段信息
            ExtraParamVo vo = super.extraParamVoMap.get(payFlowNo);
            if (vo != null) {
                tb.setCustName(vo.getBsName());
                tb.setPatientCardNo(vo.getBsCardNo());
                tb.setCardType(vo.getCardType());
            }

            String source = rows[20].trim().substring(0,3);
            if (source.equals("自助机")) {
                tb.setBillSource(com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue());
            } else {
                tb.setBillSource(com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_CK.getValue());
            }

            // 除了支付成功其他都属于退费
            if (returnPay.equals(payStatus)) {
                tb.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
                tb.setPayAmount(new BigDecimal(payAmount));
            } else {
                // 退费只取退费金额，否则取总金额
                tb.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
                tb.setPayAmount(new BigDecimal(returnAmount));
            }
            billSourceSet.add(tb.getBillSource());
            list.add(tb);
        }
        super.setBillSource(billSourceSet);
    }

    /**
     * 清除已有的账单数据
     *
     * @param orgCode
     * @param date
     */
    @Override
    protected void clearBill(String orgCode, String date, EntityManager entityManager, String payType) {

        Map<String, String> map = new HashMap<>();
        map.put("tableName" , "t_thrid_bill");
        //get parameters
        String tableName = map.get("tableName");
        if(StringUtils.isNotBlank(tableName)) {
            String sDate = date + " 00:00:00";
            String eDate = date + " 23:59:59";
            String payTypeSql = getPayTypeSql(payType,tableName);
            StringBuffer sb = new StringBuffer();
            sb.append("DELETE FROM " + tableName);
            sb.append(" WHERE org_no = '" + orgCode +"'");
            sb.append(" AND Trade_datatime >= '"+ sDate +"'");
            sb.append(" AND Trade_datatime <= '"+ eDate +"'");
            sb.append(payTypeSql);
            if(null == billSource || billSource.isEmpty()) {
                sb.append(" AND bill_source = '" + com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_SELF.getValue() + "'");
            }else {
                sb.append(getBillSourceSql(billSource));
            }
            String sql = sb.toString();
            logger.info("clearBill sql = " + sql);
            // get session
            Session session = entityManager.unwrap(org.hibernate.Session.class);
            SQLQuery query = session.createSQLQuery(sql);
            int count = query.executeUpdate();
            logger.info("clearBill count = " + count);
        }
    }

}
