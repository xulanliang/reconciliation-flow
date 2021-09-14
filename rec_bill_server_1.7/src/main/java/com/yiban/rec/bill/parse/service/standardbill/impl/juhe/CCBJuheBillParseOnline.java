package com.yiban.rec.bill.parse.service.standardbill.impl.juhe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.util.EnumTypeOfInt;

import net.sf.json.JSONObject;

/**
 * 拉取线上的建行聚合支付账单：只需调用拉取接口，线上接口整合(预下载、下载文件、解析文件返回文本内容)这三个步骤,直接返回账单文本内容
 */
public class CCBJuheBillParseOnline extends AbstractBillParser<ThirdBill> {

	private final String RETURN_CODE = "000000";

	protected String acount;

	public void init() {
		acount = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheAcount);
	}

	@Override
	protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		return this.parseBill(this.doPost(date), orgCode);
	}

	@SuppressWarnings("unchecked")
	private List<ThirdBill> parseBill(String response, String orgCode) {
		logger.info("拉取线上的建行聚合支付账单 请求返回结果：" + response);
		// {returnCode=000000, returnMsg=下载成功, data=[]}
		Map<String, Object> rmap = gson.fromJson(response, Map.class);
		if (rmap == null) {
			return null;
		}
		String returnCode = rmap.get("returnCode").toString();
		if (!RETURN_CODE.equals(returnCode)) {
			return null;
		}
		ArrayList<String> lines = (ArrayList<String>) rmap.get("data");

		List<ThirdBill> bills = new ArrayList<>();

		// 将lines解析成一行行的账单，再转换成实体
		for (int i = 2, len = lines.size(); i < len; i++) {
			String line = lines.get(i);
			convertToBean(line, orgCode, bills);
		}

		return bills;
	}

	/**
	 * 将文本信息转换成bean对象
	 * 
	 * @param line
	 * @return
	 */
	public void convertToBean(String line, String orgCode, List<ThirdBill> bills) {
		if (StringUtils.isNotEmpty(line)) {

			String[] arr = line.split("\t");
			if (arr.length > 0) {
				ThirdBill thirdBill = new ThirdBill();
				thirdBill.setOrgNo(orgCode);
				// 业务类型
//				thirdBill.setPayBusinessType(getPayBusinessType());

				// 支付类型：1649 聚合支付
				if (arr[13].equals("支付宝")) {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
				} else if (arr[13].equals("微信")) {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
				} else {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
				}
				thirdBill.setRecPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
				// 商户流水号
				thirdBill.setOrderNo(arr[3]);
				// 银行流水号
				thirdBill.setShopFlowNo(arr[2]);
				// 订单号(建行聚合支付用此单号对账)
				thirdBill.setPayFlowNo(arr[4]);

				thirdBill.setTradeDatatime(DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", arr[0]));
				// 交易金额
				BigDecimal amount = new BigDecimal(arr[9]);
				thirdBill.setPayAmount(amount.abs());
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
					// 退费
					thirdBill.setOrderState(EnumTypeOfInt.REFUND_CODE.getValue());
				} else {
					// 缴费
					thirdBill.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
				}

				// 柜台号
				String posCode = arr[12];
				// 如果要过滤某个柜台号的账单，子类可以实现getBillSource方法，默认全部账单都解析
				String billSource = getBillSource(posCode);
				if (StringUtils.isBlank(billSource)) {
					return;
				}
				thirdBill.setBillSource(billSource);
//				23 - payType
//				24 - payCode
//				25 - systemCode
				//值写死。不会变，线上的单这些值可能为空，导致无法退费
				thirdBill.setUnionPayType("151");
				thirdBill.setUnionPayCode("ccb");
				thirdBill.setUnionSystemCode("50");
				/*if (arr.length > 23) {
					thirdBill.setUnionPayType(arr[23]);
				}
				if (arr.length > 24) {
					thirdBill.setUnionPayCode(arr[24]);
				}
				if (arr.length > 25) {
					thirdBill.setUnionSystemCode(arr[25]);
				}*/
				bills.add(thirdBill);
			}
		}
	}

	/**
	 * 如果医院有不同的判断方式，可继承重写此方法
	 * 
	 * @param posCode
	 * @return
	 */
	public String getBillSource(String posCode) {
		return EnumTypeOfInt.BILL_SOURCE_SELF.getValue();
	}

	/**
	 * 请求下载文件
	 * 
	 * @param date 账单日期
	 * @return
	 */
	public String doPost(String date) {

		init();

		String payCenterUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String juheDownloadUrl = payCenterUrl + "/order/ccb/bill/downloadBillData";

		JSONObject json = new JSONObject();
		json.put("account", acount);
		// 请求序列号 只可以使用数字
		json.put("requestSn", System.currentTimeMillis());

		json.put("billDate", date.replaceAll("-", ""));
		// 0:未结流水,1:已结流水
		json.put("kind", 1);
		// 1：txt（默认），2：excel（一点接商户不支持excel文件格式下载）
		json.put("fileType", 1);
		// 0：支付流水；1：退款流水 （实际上下载的文件都是一样的）
		json.put("type", 0);
		// 排序： 1:交易日期,2:订单号
		json.put("nOrderBy", 1);
		// 流水状态: 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		json.put("status", 3);
		// 订单号：按订单号查询时，时间段不起作用
		json.put("orderId", "");
		// 柜台号
		json.put("posCode", "");
		// 1：新对账单（且KIND为1时，新版对账单），0或空：旧对账单（默认）
		json.put("billFlag", "");

		logger.info("建行聚合支付拉取线上账单，参数:" + json.toString() + ", juheDownloadUrl = " + juheDownloadUrl);

		return HttpClientUtil.doPostJson(juheDownloadUrl, json.toString());
	}
}
