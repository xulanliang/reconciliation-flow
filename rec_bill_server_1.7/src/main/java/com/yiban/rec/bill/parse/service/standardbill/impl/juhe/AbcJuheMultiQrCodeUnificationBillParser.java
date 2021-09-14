package com.yiban.rec.bill.parse.service.standardbill.impl.juhe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.util.HttpClientUtil;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.util.EnumTypeOfInt;

import net.sf.json.JSONObject;

/**
 * 农行聚合支付[多码合一支付]账单解析. 标准版。可通过继承父类去实现子类定制化需求
 * http://pay.clearofchina.com/order/version-two/abc/gate-way/downloadTrace
 */
public class AbcJuheMultiQrCodeUnificationBillParser extends AbstractBillParser<ThirdBill> {

	static final String RET_CODE_SUCCESS = "0000";

	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		List<ThirdBill> tbs = new ArrayList<>();
		try {
			tbs = parse(orgCode, this.request(orgCode, date));
		} catch (Exception e) {
			logger.error("农行聚合支付[多码合一支付]账单解析异常：{}", e);
		}
		return tbs;
	}

	public String request(String orgCode, String date) {

		String systemCodeKey = ProConstants.systemCodeUnified;
		String systemCode = ProConfigManager.getValueByPkey(super.entityManager, systemCodeKey);
		String payCenterUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));

		String requestUrl = payCenterUrl + "/order/version-two/abc/gate-way/downloadTrace";

		JSONObject params = new JSONObject();
		// YYYYMMDD
		params.put("billDate", date.replace("-", ""));
		params.put("orgCode", orgCode);
		params.put("systemCode", systemCode);

		logger.info("农行聚合支付[多码合一支付]账单拉取地址：requestUrl = {} ", requestUrl);
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

		String[] lineArr = data.split("\n");

		if (lineArr.length <= 3) {
			return tbs;
		}
		String tArr[] = lineArr[0].replace(",", "").split("\\|");
		for (int i = 1, len = lineArr.length; i < len; i++) {
			String line = lineArr[i];
			if (StringUtils.isBlank(line)) {
				continue;
			}

			line = line.replace(",", "");
			String[] cols = line.split("\\|");

			if (cols.length < 34) {
				continue;
			}

			ThirdBill tb = new ThirdBill();
			tb.setOrgNo(orgCode);
			tb.setRecPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
			tb.setPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
			tb.setBillSource(EnumTypeOfInt.BILL_SOURCE_SELF.getValue());

			// 交易时间
			Date tradeDatatime = DateUtil.transferStringToDate("yyyyMMddHHmmss", cols[1] + cols[2]);
			tb.setTradeDatatime(tradeDatatime);
			// 支付流水号
			tb.setPayFlowNo(cols[16]);
			// 商户号
			tb.setPayShopNo(cols[21]);
			tb.setShopFlowNo(cols[4]);
			// 交易状态
			String orderStateFlag = cols[34];
			if ("PAY_SUCCESS".equals(orderStateFlag.trim())) {
				tb.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
				tb.setPayAmount(new BigDecimal(cols[13]).abs());

			} else if ("REFUND_SUCCESS".equals(orderStateFlag.trim())) {
				tb.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
				tb.setPayAmount(new BigDecimal(cols[28]).abs());
			} else {
				continue;
			}

			if (cols.length > 35) {
				tb.setUnionPayType(cols[35]);
			}
			if (cols.length > 36) {
				tb.setUnionPayCode(cols[36]);
			}
			if (cols.length > 37) {
				tb.setUnionSystemCode(cols[37]);
			}

			tbs.add(tb);
		}
		logger.info("农行聚合支付[多码合一支付]账单解析.，账单数量：{}", tbs.size());
		return tbs;
	}
}
