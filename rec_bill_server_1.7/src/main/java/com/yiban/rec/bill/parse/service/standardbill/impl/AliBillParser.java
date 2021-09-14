package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.vo.ExtraParamVo;
import com.yiban.rec.bill.parse.vo.PayOrder;
import com.yiban.rec.domain.ThirdBill;

/**
* @author swing
* @date 2018年7月25日 下午2:03:34
* 类说明 支付宝账单解析器
*/
public class AliBillParser extends AbstractBillParser<ThirdBill>{
	protected String strThird="0.00";
	protected String strFourth="交易\t";
	
	/**
	 * 支付宝账单解析
	 */
	@Override
	protected List<ThirdBill> doParse(String orgCode,String date) throws  BillParseException {
		logger.info("支付宝账单解析");
		String serverUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String url = serverUrl + "/pay/billLog/getAliBill";
		Gson gson = new Gson();
		Map<String,String> map = new HashMap<>();
		map.put("orgCode", orgCode);
		map.put("time", date);
		logger.info("支付宝请求url："+url+"--入参：orgCode:"+orgCode +"date" + date);
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
			e.printStackTrace();
		    throw new BillParseException(e.getMessage());
		}
		logger.info("支付宝插入数据行数："+list.size());
		return list;
	}
	
	protected void parseBill(List<ThirdBill> list, String billData ,String orgCode) {
	    // 账单来源
        Set<String> billSourceList = new HashSet<>();
        
	    //得到系统来源和字典值配置
        Map<String, String> systemMap = ValueTexts.asMap(super.systemList);
        logger.info("系统配置值："+systemMap.toString());
        
        String[] rows = billData.split("\r\n"); // 获取每一行
        String firstLine = rows[0];
	    //得到该账单的系统编码
	    String systemCode = firstLine.substring(0, firstLine.indexOf("#"));
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
			
            // 设置账单来源初始值
            String billSource = EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue();
            //获取翻译后的账单来源
			String code = systemMap.get(systemCode);
			//以线上翻译为准
            if(order!=null) {
            	code=systemMap.get(order.getSystemCode());
            }
			if(StringUtils.isNotBlank(code)) {
			    billSource = code;
			}
            if(!billSourceList.contains(billSource)) {
                billSourceList.add(billSource);
            }
            tb.setBillSource(billSource);
            
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
            list.add(tb);
        }
        super.setBillSource(billSourceList);
	}
	
	/**
	 * 从订单池中取出一笔订单
	 * @param payFlowNo
	 * @return
	 */
	protected PayOrder loadPayOrder(String payFlowNo) {
		return  super.orderMap.get(payFlowNo);
	}
}
