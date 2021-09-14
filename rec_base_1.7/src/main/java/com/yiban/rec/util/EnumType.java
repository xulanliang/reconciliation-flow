package com.yiban.rec.util;
/**
 * 
 * @author FZX
 *
 */
public enum EnumType {
	
	MESSAGE_SEND_STATE_FIRST("0000","首次发送"),
	MESSAGE_SEND_STATE_REPEAT("0001","重复发送"),
	DATA_SOURCE_TYPE_PLAT_CHANNEL("0157","平台数据"),//
	DATA_SOURCE_TYPE_PAY_CHANNEL("0257","支付渠道数据"),
	DATA_SOURCE_TYPE_ORG_CHANNEL("0357","机构对账数据"),
	HIS_CASH("81","现金交易机构数据来源"),
	DATA_SOURCE_PLAT_CASH("82","现金账单结果"),
	ADMIN_LOGIN("admin","用于超级管理员登录"),
	HANDLE_SUCCESS("1","对账成功"),
	HANDLE_FAIL("0","对账失败"),
	HANDLE_NO_DATA("150","无数据"),
	MSG_FALG("00","消息头标记"),
	REFUND_MSG_FALG("0","退费消息头标记"),
	PAY_TYPE_CASH("43","支付类型-现金"),
	PAY_CODE_CASH("0049","支付code-现金"),
	PAY_TYPE_WEICHAT("45","微信"),
	PAY_TYPE_ALIPAY("46","支付宝"),
	PAY_TYPE_PLAT("163","平台"),
	TRADE_CODE_REFUND("02004","退费交易编码"),
	PAY_SOURCE_REFUND("0048","退费支付来源"),
	PAY_TYPE_WECHAT_REFUND("0249","微信支付类型"),
	PAY_TYPE_ALIPAY_REFUND("0349","支付宝支付类型"),
	NOTICE_WAY_MESSAGE("1","短信"),
	NOTICE_WAY_EMAIL("2","邮件"),
	TRADE_CODE("00001","交易编码"),
	SECOND_HANDLE_TYPE("two_rec","两方对账"),
	THIRD_HANDLE_TYPE("three_rec","三方对账"),
	CASH_HANDLE_TYPE("is_cash","现金对账"),
	NO_CASH_HANDLE_TYPE("no_cash","非现金对账"),
	BILL_SOURCE("bill_source","账单来源")
	;
	
	
	private String value;
	private String name;

	private EnumType(String value,String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
