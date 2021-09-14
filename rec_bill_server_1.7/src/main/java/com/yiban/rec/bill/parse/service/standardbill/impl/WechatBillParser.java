package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
 * @date 2018年7月25日 下午2:06:32 类说明 微信支付解析器
 */
public class WechatBillParser extends AbstractBillParser<ThirdBill> {

	protected final String payStatus = "SUCCESS";

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
		map.put("orgCode", orgCode);
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

	/**
	 * 微信账单解析
	 * 
	 * @param list
	 * @param billData
	 * @param orgCode  void
	 */
	protected void parseBill(List<ThirdBill> list, String billData, String orgCode) {
		// 账单来源
		Set<String> billSourceSet = new HashSet<>();
		// 得到系统来源和字典值配置
		Map<String, String> systemMap = ValueTexts.asMap(super.systemList);
		logger.info("系统配置值：" + systemMap.toString());
		String[] billLines = billData.split("\r\n");
		String firstLine = billLines[0];
		// 得到该账单的系统编码
		String systemCode = firstLine.substring(0, firstLine.indexOf("#"));
		
		// 不参与对账的渠道
		String ignoreSystemCodes = ProConfigManager.getValueByPkey(entityManager, "wechat.bill.ignore.systemcode","");
		if(ignoreSystemCodes.contains(systemCode)){
			return;
		}
		
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
			tb.setPayFlowNo(payShopFlowNo);
			tb.setPayShopNo(payShopNo);
			tb.setTradeDatatime(tradeDatatime);
			// 如果订单表中不存在则采用该值
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

			// 设置账单来源初始值
			String billSource = EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue();
			// 获取翻译后的账单来源
			String code = systemMap.get(systemCode);
			//以线上翻译为准
            if(order!=null) {
            	code=systemMap.get(order.getSystemCode());
            }
			if (StringUtils.isNotBlank(code)) {
				billSource = code;
			}
			if (!billSourceSet.contains(billSource)) {
				billSourceSet.add(billSource);
			}
			tb.setBillSource(billSource);
			// 除了支付成功其他都属于退费
			if (returnPay.equals(payStatus)) {
				tb.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
				tb.setPayAmount(new BigDecimal(payAmount));
			} else {
				// 退费只取退费金额，否则取总金额
				tb.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
				tb.setPayAmount(new BigDecimal(returnAmount));
			}
			list.add(tb);
		}
		super.setBillSource(billSourceSet);
	}

	/**
	 * 从订单池中取出一笔订单
	 * 
	 * @param payFlowNo
	 * @return
	 */
	protected PayOrder loadPayOrder(String payFlowNo) {
		return super.orderMap.get(payFlowNo);
	}
}
