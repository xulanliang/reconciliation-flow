package com.yiban.rec.domain.basicInfo;

public class HisChannelParaSendInfo {
	/**
	 * 机构ID
	 */
	private String org_no;
	/**
	 * 支付渠道ID
	 */
	private String pay_type;
	/**
	 * 开始日期
	 */
	private String pay_date;
	
	//交易编码
	private String trade_code;
	
	public String getOrg_no() {
		return org_no;
	}
	public void setOrg_no(String org_no) {
		this.org_no = org_no;
	}
	
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getPay_date() {
		return pay_date;
	}
	public void setPay_date(String pay_date) {
		this.pay_date = pay_date;
	}
	public String getTrade_code() {
		return trade_code;
	}
	public void setTrade_code(String trade_code) {
		this.trade_code = trade_code;
	}
	
	
}
