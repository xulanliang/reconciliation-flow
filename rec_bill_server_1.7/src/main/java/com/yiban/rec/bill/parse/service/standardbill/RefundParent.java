package com.yiban.rec.bill.parse.service.standardbill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yiban.rec.bill.parse.util.HttpClientUtil;

public abstract class RefundParent {

	/** GSON对象 */
	protected Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 调用地址
	public String url;

	public RefundParent(String url) {
		this.url = url;
	}

	/**
	 * 退费
	 * 
	 * @return
	 * @throws Exception
	 */
	public String refund() throws Exception {
		// 获取请求参数
		String data = getRequestData();
		logger.info("RefundParent 请求地址：{}，请求参数：{}", url, data);
		String result = requestRefund(data);
		logger.info("退款响应参数：{}", result);
		return analysisResult(result);
	}

	/**
	 * 这里默认使用xml方式请求，如果是其他格式的请求则重写此方法
	 */
	public String requestRefund(String data) {
		String result = HttpClientUtil.doPostXml(url, data);
		return result;
	}

	// 获取请求参数
	public abstract String getRequestData() throws Exception;

	// 解析返回值
	public abstract String analysisResult(String result) throws Exception;
}
