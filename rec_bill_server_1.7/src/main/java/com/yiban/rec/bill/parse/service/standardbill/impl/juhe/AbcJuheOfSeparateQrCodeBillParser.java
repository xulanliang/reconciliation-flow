package com.yiban.rec.bill.parse.service.standardbill.impl.juhe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.util.HttpClientUtil;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.util.DateUtil;

import net.sf.json.JSONObject;

/**
 * 农行聚合支付[分开不同码支付]账单解析. 标准版。可通过继承父类去实现子类定制化需求
 * http://pay.clearofchina.com/order/abc/gate-way/settleRequestAliWX
 * 
 * 账单示例： { "Format": "JSON", "Merchant": { "MerchantID": "103881768000236",
 * "ECMerchantType": "EBUS" }, "Version": "V3.0.0", "TrxResponse": {
 * "ZIPDetailRecords": "", "TrxType": "SettleAliWx", "ReturnCode": "0000",
 * "SettleType": "", "DetailRecords":
 * "商户号|交易类型|订单编号|交易时间|交易金额|商户账号|商户动账金额|客户账号|账户类型|商户回佣手续费|商户分期手续费|会计日期|主机流水号|9014流水号|原订单号^^103881768000236|weixinpay|101910081643129|20191008130610|0.04|17631201941000058|0.04|otDNot-VlCVy0pui0GuwPM-pk7Nk|HXB_CREDIT|0|0.00|20191009|321829570|A8ECEP01130139251873|101910081643129^^103881768000236|weixinpay|101910081643126|20191008130459|0.04|17631201941000058|0.04|otDNot-VlCVy0pui0GuwPM-pk7Nk|HXB_CREDIT|0|0.00|20191009|321829570|A8ECEP01130122240834|101910081643126^^103881768000236|weixinpay|101910081643138|20191008130827|0.04|17631201941000058|0.04|otDNot-VlCVy0pui0GuwPM-pk7Nk|HXB_CREDIT|0|0.00|20191009|321829570|A8ECEP01130709417302|101910081643138",
 * "ErrorMessage": "成功", "SettleDate": "2019/10/09" } }
 */
public class AbcJuheOfSeparateQrCodeBillParser extends AbstractBillParser<ThirdBill> {

	static final String RET_CODE_SUCCESS = "0000";

	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		List<ThirdBill> tbs = new ArrayList<>();
		try {
			tbs = parse(orgCode, this.request(orgCode, date));
		} catch (Exception e) {
			logger.error("农行聚合支付[分开不同码支付]账单解析异常：{}", e);
		}
		return tbs;
	}

	public String request(String orgCode, String date) {

		String systemCodeKey = ProConstants.systemCode;
		String systemCode = ProConfigManager.getValueByPkey(super.entityManager, systemCodeKey,
				ProConstants.DEFAULT.get(systemCodeKey));
		String payCenterUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));

		String requestUrl = payCenterUrl + "/order/abc/gate-way/settleRequestAliWX";

		Date reqDate = DateUtil.transferStringToDate(DateUtil.STANDARD_SHORT_FORMAT, date);
		String settleDate = DateUtil.stringToDateTime(DateUtil.nextDay(reqDate));

		JSONObject params = new JSONObject();
		// YYYY/MM/DD
		params.put("settleDate", settleDate);
		// 1：压缩；0：否
		params.put("zip", "0");
		params.put("orgCode", orgCode);
		params.put("systemCode", systemCode);

		logger.info("农行聚合支付[分开不同码支付]账单拉取地址：requestUrl = {} ", requestUrl);
		logger.info("入参：{}", params.toString());
		String result = HttpClientUtil.doPostJson(requestUrl, params.toString());
		logger.info("出参：{}", result);

		return result;
	}

	public List<ThirdBill> parse(String orgCode, String result) {
		List<ThirdBill> tbs = new ArrayList<ThirdBill>();

		if (StringUtils.isBlank(result)) {
			return tbs;
		}

		JSONObject json = JSONObject.fromObject(result);
		String returnCode = json.getString("returnCode");
		if (!RET_CODE_SUCCESS.equals(returnCode)) {
			return tbs;
		}

		String data = json.getString("data");
		if (StringUtils.isBlank(data)) {
			return tbs;
		}
		String[] lineArr = data.split("\\^\\^");
		// 第一行是标题
		if (lineArr.length < 1) {
			return tbs;
		}
		for (int i = 1, len = lineArr.length; i < len; i++) {

			String[] cols = lineArr[i].split("\\|");

			ThirdBill thirdBill = new ThirdBill();
			// 机构编码
			thirdBill.setOrgNo(orgCode);
			thirdBill.setBillSource(EnumTypeOfInt.BILL_SOURCE_SELF.getValue());
			thirdBill.setRecPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());

			// 商户号
			thirdBill.setPayShopNo(cols[0]);
			// 支付流水号
			String payFlowNo = cols[14];
			thirdBill.setPayFlowNo(payFlowNo);
			// 交易时间
			thirdBill.setTradeDatatime(DateUtil.transferStringToDate("yyyyMMddHHmmss", cols[3]));

			String payType = cols[1];
			if (payType.contains("weixin") || payType.contains("WeiXin")) {
				thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());

			} else if (payType.contains("ALI") || payType.contains("Ali")) {
				thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());

			} else {
				thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
			}

			// 支付金额
			thirdBill.setPayAmount(new BigDecimal(cols[4]).abs());
			// 订单状态
			String orderState = cols[1];
			if (orderState.contains("Refund")) {
				thirdBill.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
			} else {
				thirdBill.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
			}
			if (cols.length > 15) {
				thirdBill.setUnionPayType(cols[15]);
			}
			if (cols.length > 16) {
				thirdBill.setUnionPayCode(cols[16]);
			}
			if (cols.length > 17) {
				thirdBill.setUnionSystemCode(cols[17]);
			}
			tbs.add(thirdBill);
		}

		logger.info("农行聚合支付[分开不同码支付]账单解析.，账单数量：{}", tbs.size());
		return tbs;
	}
}
