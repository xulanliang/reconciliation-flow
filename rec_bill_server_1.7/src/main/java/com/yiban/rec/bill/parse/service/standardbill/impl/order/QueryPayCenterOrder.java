package com.yiban.rec.bill.parse.service.standardbill.impl.order;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yiban.rec.bill.parse.util.AESUtils;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.vo.ExtraParamVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryPayCenterOrder implements QueryOrder {
	private static Logger logger = LoggerFactory.getLogger(QueryPayCenterOrder.class);

	// 解密全流程加密的拓展字段数据
	private static String key = "AaBbCcDd1234!@#$";
	private static String initVector = "WwXxYyZz1234!@#$";

	// 请求接口参数
	private String requestUrl, orgCode, beginTime, endTime;

	public QueryPayCenterOrder(String requestUrl, String orgCode, String beginTime, String endTime) {
		this.requestUrl = requestUrl;
		this.orgCode = orgCode;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	// 方法入口
	public HashMap<String, ExtraParamVo> query() {
		try {
			return parse(request());
		} catch (Exception e) {
			logger.error("线上账单解析异常, 不影响业务, {}", e);
		}
		return null;
	}

	private HashMap<String, ExtraParamVo> parse(String billListStr) {
		if (StringUtils.isBlank(billListStr)) {
			logger.error("线上账单返回为空");
			return null;
		}
		JSONArray billArr = JSONArray.fromObject(billListStr);
		if (billArr.size() == 0) {
			logger.error("线上账单返回为空");
			return null;
		}

		HashMap<String, ExtraParamVo> billMap = new HashMap<>(256);
		JSONObject json = null;
		for (int i = 0, len = billArr.size(); i < len; i++) {
			json = billArr.getJSONObject(i);
			String systemCode = json.containsKey("system_code") ? json.get("system_code").toString() : "";
			if (!"51".equals(systemCode)) {
				continue;
			}

			String payFlowNo = json.getString("tsn");
			if (StringUtils.isBlank(payFlowNo)) {
				continue;
			}
			String extraParamEncrypt = json.containsKey("extra_param") ? json.get("extra_param").toString() : "";
			if (StringUtils.isBlank(extraParamEncrypt) || extraParamEncrypt.equals("null")) {
				continue;
			}

			String extraParam = AESUtils.aesCBCDecrypt(extraParamEncrypt, "utf-8", key, initVector);
			if (StringUtils.isBlank(extraParam)) {
				continue;
			}
			try {
				String extParamStr = JSONObject.fromObject(extraParam).getString("extParam");
				if (StringUtils.isBlank(extParamStr)) {
					continue;
				}
				JSONObject extParamJSON = JSONObject.fromObject(extParamStr);
				ExtraParamVo vo = new Gson().fromJson(extParamJSON.toString(), ExtraParamVo.class);

				billMap.put(payFlowNo, vo);
			} catch (JsonSyntaxException e) {
				logger.error("解析扩展字段信息异常，不影响业务, {}", e);
			}
		}
		logger.info("解析扩展字段信息：billMap = {}", billMap);
		return billMap;
	}

	private String request() {
		JSONObject param = new JSONObject();
		param.put("orgCode", orgCode);
		param.put("beginTime", beginTime);
		param.put("endTime", endTime);
		String result = HttpClientUtil.doPostJson(requestUrl, param.toString());
		return result;
	}

	public static void main(String[] args) {
		String requestUrl = "https://pay.clearofchina.com/pay/billLog/query/list";
		String orgCode = "5307604";
		String beginTime = "2019-06-18 00:00:00";
		String endTime = "2019-06-19 00:00:00";
		HashMap<String, ExtraParamVo> map = new QueryPayCenterOrder(requestUrl, orgCode, beginTime, endTime).query();
		System.out.println(map);
	}
}
