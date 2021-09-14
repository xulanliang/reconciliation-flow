package com.yiban.rec.bill.parse.service.hospital.wujin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Auther: Kevin Liu
 * @Date: 2020/11/27 15:58
 * @Description:
 */
public class LuoYangHisHttpParser extends AbstractHisBillParser<HisTransactionFlow> {
    @Override
    protected List<HisTransactionFlow> getHisList(String startTime, String endTime, String orgCode) throws BillParseException {
        // 获取接口地址
        String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisUrl);
        String hisOrgCode = ProConfigManager.getValueByPkey(entityManager, ProConstants.WUJIN_HIS_ORG_CODE ,"320412467357716");
        Map<String, String> params = new HashMap<>();
        params.put("orgCode", hisOrgCode);
        params.put("startDateTime", startTime);
        params.put("endDateTime", endTime);
        JSONObject paramsJson = JSONObject.fromObject(params);
        String result = HttpClientUtil.doPostJson(url, paramsJson.toString());
        logger.info("#### 武进洛阳His账单接口返回：{}", result);
        List<HisTransactionFlow> hisTransactionFlowList = parseData(orgCode, result);
        return hisTransactionFlowList;
    }

    private List<HisTransactionFlow> parseData(String orgCode, String result) {
        List<HisTransactionFlow> hisTransactionFlowList = new ArrayList<>();
        JSONObject resultJson = JSONObject.fromObject(result);
        String resultCode = resultJson.getString("resultCode");
        if (!resultCode.equalsIgnoreCase("SUCCESS")) {
            return hisTransactionFlowList;
        }
        JSONArray orderJsonArr = resultJson.getJSONArray("orderItems");
        String cashierList = ProConfigManager.getValueByPkey(entityManager, ProConstants.WUJIN_CASHIER_LIST);
        for (int i = 0; i < orderJsonArr.size(); i++) {

            HisTransactionFlow vo = new HisTransactionFlow();
            JSONObject orderJson = orderJsonArr.getJSONObject(i);
            if(EnumTypeOfInt.CASH_PAYTYPE.getValue().equals(String.valueOf(orderJson.get("payType")))){
                continue;
            }
            if (cashierList != null) {
                List<String> cashiers = Arrays.asList(cashierList.split(","));
                if (cashiers.contains(String.valueOf(orderJson.get("cashier")))){
                    vo.setBillSource(EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue());
                    super.billSource.add(vo.getBillSource());
                } else {
                    vo.setBillSource(EnumTypeOfInt.BILL_SOURCE_CK.getValue());
                    super.billSource.add(vo.getBillSource());
                }
            } else {
                vo.setBillSource(EnumTypeOfInt.BILL_SOURCE_CK.getValue());
                super.billSource.add(vo.getBillSource());
            }
            vo.setOrgNo(orgCode);
            vo.setPayType(String.valueOf(orderJson.get("payType")));
            vo.setPayFlowNo( formatString(orderJson.get("tsnOrderNo")) != null? String.valueOf(orderJson.get("tsnOrderNo")) :String.valueOf(orderJson.get("outTradeNo")));
            //如果为江南银行的支付payFlowNo重新set值
            if(vo.getPayFlowNo().contains("JN")) {
            	vo.setOriPayFlowNo(String.valueOf(orderJson.get("tsnOrderNo")));
            	vo.setPayFlowNo(String.valueOf(orderJson.get("outTradeNo")));
            	vo.setPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
            }
            
            String payAmount = orderJson.get("payAmount").toString().trim();
            vo.setPayAmount(new BigDecimal(payAmount).abs());
            vo.setTradeDatatime(DateUtil.getBeginDayOfTomorrow(String.valueOf(orderJson.get("tradeDateTime")), "yyyy-MM-dd HH:mm:ss"));
            String orderState = String.valueOf(orderJson.get("orderState"));
            if(orderState.equals("交易正常")){
                if(payAmount.startsWith("-")) {
                    vo.setOrderState(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue());
                }else {
                    vo.setOrderState(EnumTypeOfInt.TRADE_TYPE_PAY.getValue());
                }
            }else if(orderState.equals("已退费")){
                if(payAmount.startsWith("-")) {
                    vo.setOrderState(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue());
                }else {
                    vo.setOrderState(EnumTypeOfInt.TRADE_TYPE_PAY.getValue());
                }
            }else {
                vo.setOrderState(EnumTypeOfInt.TRADE_TYPE_FAIL.getValue());
            }
            vo.setPayBusinessType(String.valueOf(orderJson.get("payBusinessType")));
            vo.setPatType(String.valueOf(orderJson.get("patType")));
            vo.setCustName(String.valueOf(orderJson.get("patientName")));
            vo.setCashier(String.valueOf(orderJson.get("cashier")));
            vo.setHisFlowNo(formatString(orderJson.getString("hisOrderNo")));
            vo.setBusinessFlowNo(String.valueOf(orderJson.get("outTradeNo")));
            hisTransactionFlowList.add(vo);
        }
        return hisTransactionFlowList;
    }

    /**
     * 对 null支付串处理
     * @param obj
     * @return
     */
    private String formatString(Object obj){
        if(StringUtil.checkNotNull(String.valueOf(obj)) && !"null".equals(String.valueOf(obj))){
            return String.valueOf(obj);
        }
        return null;
    }
}
