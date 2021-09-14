package com.yiban.rec.bill.parse.service.hospital.wujin;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.impl.AliBillParser;
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
 * @Date: 2020/11/27 17:43
 * @Description:
 */
public class LuoYangAliBillParse extends AliBillParser {

    /**
     * 支付宝账单解析
     */
    @Override
    protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
        logger.info("支付宝账单解析");
        String serverUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
                ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
        String url = serverUrl + "/pay/billLog/getAliBill";
        Gson gson = new Gson();
        Map<String,String> map = new HashMap<>();
        map.put("orgCode", orgCode);
        map.put("time", date);
        logger.info("支付宝请求url："+url+"--入参：orgCode:"+ orgCode +"date" + date);
        List<ThirdBill> list = new ArrayList<ThirdBill>();
        String response = null;
        try {
            response = HttpClientUtil.doPostJson(url, gson.toJson(map).toString());
            logger.info("支付宝请求返回结果："+response);
            Map<?, ?> rmap = gson.fromJson(response, Map.class);
            if(rmap != null){
                Boolean result = (Boolean) rmap.get("success");
                if(result){
                    @SuppressWarnings("unchecked")
                    ArrayList<String> strLists = (ArrayList<String>) rmap.get("data");
                    if(strLists.size() > 0){
                        // 多个账号数据
                        for (String billData : strLists) {
                            // 解析账单
                            parseBill(list, billData, orgCode);
                        }
                    }
                }else {
                    throw new BillParseException(rmap.get("message").toString());
                }
            }else {
                throw new BillParseException("服务器暂无响应");
            }
        } catch (Exception e) {
            throw new BillParseException(e.getMessage());
        }
        logger.info("支付宝插入数据行数："+list.size());
        return list;
    }

    @Override
    protected void parseBill(List<ThirdBill> list, String billData , String orgCode) {
        // 账单来源
        Set<String> billSourceList = new HashSet<>();

        //得到系统来源和字典值配置
        Map<String, String> systemMap = ValueTexts.asMap(super.systemList);
        logger.info("系统配置值："+systemMap.toString());

        String[] rows = billData.split("\r\n"); // 获取每一行

        for (int i = 0; i < rows.length; i++) {
            if (i < 5 || i >= (rows.length - 4)) {
                continue;
            }
            String[] columns = rows[i].split(",");// 获取每列值

            // 商户订单号
            String payShopFlowNo = columns[1].trim();
            // 支付支付商户流水号
            String payFlowNo = columns[0].trim();
            // 支付用户账号
            String payShopNo = columns[10];
            // 交易时间
            String tradeDatatime = columns[5];
            // 支付金额
            String payAmount = columns[11];
            // 是否退款
            String returnPay = columns[2];

            ThirdBill tb = new ThirdBill();
            tb.setShopFlowNo(payShopFlowNo);
            tb.setPayFlowNo(payFlowNo);
            tb.setPayShopNo(payShopNo);
            tb.setPayTermNo(columns[9].trim());
            tb.setTradeDatatime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", tradeDatatime));
            // 如果订单表中不存在则采用该值
            tb.setOrgNo(orgCode);
            // 无法拿到订单类型统一变为支付宝支付
            tb.setPayType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
            // 无法拿到订单类型统一变为支付宝支付
            tb.setRecPayType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
            // 获取订单信息
            PayOrder order = loadPayOrder(payFlowNo);

            //注入业务流水号
            if(order!=null) {
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

            String source = columns[3].trim().substring(0,3);
            if (source.equals("自助机")) {
                tb.setBillSource(com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue());
            } else {
                tb.setBillSource(com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_CK.getValue());
            }

            // 退费只取退费金额，否则取总金额
            if (returnPay.equals(strFourth)) {
                tb.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
                tb.setPayAmount(new BigDecimal(payAmount));
            } else {
                tb.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
                if (StringUtils.isNotBlank(payAmount) && !payAmount.equals(strThird)) {
                    String[] amountArr = payAmount.split("-");
                    tb.setPayAmount(new BigDecimal(amountArr[1]));
                }
            }
            billSourceList.add(tb.getBillSource());
            list.add(tb);
        }
        super.setBillSource(billSourceList);
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
