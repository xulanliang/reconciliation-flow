package com.yiban.framework.core.api.sms.vo;

import java.io.Serializable;

public class SmsResult implements Serializable {
	private static final long serialVersionUID = 1L;
	// 000000 成功，100000 失败,404 找不到服务 ,500 服务异常
	private String statusCode = "100000"; // 默认状态为失败
	private String resultMsg = "error";
    
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public SmsResult(String statusCode, String resultMsg) {
		this(statusCode);
		this.resultMsg = resultMsg;
	}

	public SmsResult(String statusCode) {
		this.statusCode = statusCode;
	}

	public SmsResult() {
	}

	@Override
	public String toString() {
		return String.format("{\"statusCode\":\"%s\",\"resultMsg\":\"%s\"}", this.statusCode, this.resultMsg);
	}


}
