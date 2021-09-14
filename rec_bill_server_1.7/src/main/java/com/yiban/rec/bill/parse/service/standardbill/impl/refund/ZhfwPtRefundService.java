package com.yiban.rec.bill.parse.service.standardbill.impl.refund;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.vo.RefundVo;

/**
 * 智慧服务平台退费
 * @date 2021年5月13日
 */
public class ZhfwPtRefundService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EntityManager entityManager;
	
	private String payCenterSignUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.newPayCenterUrl);
	private String appKey = ProConfigManager.getValueByPkey(entityManager, ProConstants.zhfwAppKey);
	private String appSecret = ProConfigManager.getValueByPkey(entityManager, ProConstants.zhfwAppSecret);
	
	public void doRefund(RefundVo vo) {
		Gson gson = new Gson();
        Map<String, String> params = new HashMap<>();
        Map<String, String> headParams = new HashMap<>();
        // 机构编码
        params.put("org_code", vo.getOrgCode());
        params.put("mch_appid", appKey);
        params.put("mch_order_id", vo.getShopNo() + RandomStringUtils.randomNumeric(3));
        String payType = vo.getPayType();
        String channelCode = payType.equals("0349") ? "AliPay" : "WxPay";
        params.put("channel_code", channelCode);
        params.put("pay_total_fee", vo.getPayAmount());
        params.put("out_order_no", vo.getOrderNo());
        params.put("refund_amt", vo.getTradeAmount());
        params.put("refund_reason", vo.getReason());
        // 获取token
        headParams.put("Authorization-Token", this.getToken());
        String url = payCenterSignUrl + "/jdpay/core/specialrefund";
        String paramsJsonStr = gson.toJson(params);
        logger.info("##### 智慧服务平台特殊退费地址：{}，入参：{}", url, paramsJsonStr);
        String refundResult = HttpClientUtil.doPostJson(url, headParams, paramsJsonStr);
        logger.info("##### 智慧服务平台特殊退费出参：{}", refundResult);
	}
	
	
	private String getToken() {
		JSONObject tokenJson = new JSONObject();
		tokenJson.put("appKey", appKey);
		tokenJson.put("appSecret", appSecret);
		String url=payCenterSignUrl+"/sys-auth/channel/oauth/token";
		logger.info("请求智慧服务平台获取token，url:{},appkey:{},appSecret:{}", url, appKey, appSecret);
		String response = HttpClientUtil.doPostJson(url, tokenJson.toString());
		logger.info("请求智慧服务平台获取token响应:{}", response);
		JSONObject resJson = JSONObject.parseObject(response);
		return resJson.getJSONObject("result").getString("accessToken");
	}
}
