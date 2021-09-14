package com.yiban.rec.bill.parse.service.standardbill;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.MD5Util;
import com.yiban.rec.domain.vo.RefundVo;

import net.sf.json.JSONObject;

/**
 * 通过统一网关退款的公共类
 */
public class UnifiedGatewayRefund extends RefundParent {

	final String RET_SUCCESS_CODE = "0000";

	// 调用接口使用签名key
	final static String key = "clear123";

	private RefundVo vo;

	public UnifiedGatewayRefund(RefundVo vo, String url) {
		super(url);
		this.vo = vo;
	}

	@Override
	public String getRequestData() throws Exception {
		if (vo == null) {
			throw new Exception("退款失败：入参vo为空");
		} else if (StringUtils.isAnyBlank(vo.getOrgCode(), vo.getUnionPayCode(), vo.getUnionPayType(),
				vo.getUnionSystemCode(), vo.getTradeAmount(), vo.getOrderNo(), vo.getReason())) {
			throw new Exception("退款失败：Some parameters are empty");
		}

		JSONObject params = new JSONObject();
		params.put("payCode", vo.getUnionPayCode());
		params.put("payType", vo.getUnionPayType());
		params.put("systemCode", vo.getUnionSystemCode());
		params.put("orgCode", vo.getOrgCode());
		params.put("reason", vo.getReason());
		params.put("outTradeNo", vo.getOrderNo());
		params.put("tranAmt", vo.getTradeAmount());
		params.put("refundNo", String.valueOf(System.currentTimeMillis()));
		return params.toString();
	}

	@Override
	public String requestRefund(String data) {
		HashMap<String, String> headerMap = new HashMap<>();
		headerMap.put("sign", sign(data));
		String result = HttpClientUtil.doPostJson(url, headerMap, data);
		return result;
	}

	@SuppressWarnings("unchecked")
	public String sign(String data) {
		JSONObject paramJson = JSONObject.fromObject(data);
		TreeMap<String, String> treeMap = new TreeMap<>();
		treeMap.putAll(paramJson);

		String signStr = "";
		for (String key : treeMap.keySet()) {
			String value = treeMap.get(key);
			signStr = signStr + "&" + key + "=" + value;
		}
		signStr += "&key=" + key;
		signStr = signStr.substring(1);
		logger.info("参与签名的数据：{}", signStr);
		try {
			signStr = MD5Util.getMd5(signStr.getBytes("UTF-8")).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			logger.error("md5报错：{}", e);
			signStr = "";
		}
		logger.info("签名后字符串：{}", signStr);
		return signStr;
	}

	public static void main(String[] args) {
//		String data = "{\"payCode\":\"abc\",\"payType\":\"151\",\"systemCode\":\"50\",\"orgCode\":\"11864\",\"reason\":\"csdcsd\",\"outTradeNo\":\"xllNoo19\",\"tranAmt\":\"0.01\",\"refundNo\":\"1571827705269\"}";
//		String sign = new UnifiedGatewayRefund(null, null).sign(data);

		String signStr = "orgCode=11864&outTradeNo=SDP-7D-20920191029153447&payCode=ccb&payType=151&reason=退费1&refundNo=1572505735950&systemCode=53&tranAmt=1&key=clear123";
		String md5 = MD5Util.getMd5(signStr).toUpperCase();
		System.out.println(md5);
	}

	@Override
	public String analysisResult(String result) throws Exception {
		if (StringUtils.isBlank(result)) {
			throw new Exception("退款响应失败：返回空");
		}
		JSONObject ret = JSONObject.fromObject(result);
		String returnCode = ret.getString("returnCode");
		if (!RET_SUCCESS_CODE.equals(returnCode)) {
			throw new Exception("退款响应失败:" + ret.getString("returnMsg"));
		}
		String refundStatus = ret.getString("refundStatus");
		if ("0".equals(refundStatus)) {
			throw new Exception("退款响应失败!");
		}
		return "退款成功";
	}

}
