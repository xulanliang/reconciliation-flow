package com.yiban.rec.domain.basicInfo;

import java.util.Date;


public class OrderQueryVo {
	private String orgCode;// 机构code
	private String orderNo;// 订单系统订单号
	// 来源 1:IOS 2:安卓 3:自助机 4:PC 5:Pad
	private String platFormType;
	
	private Date orderStartTime;
	private Date orderEndTime;
	// 订单状态:1 未支付 2 已支付 3 退款 4 退款成功5 退款失败6 已取消7 已关闭
	private String orderState;
	private String payCode;// 用户选择的支付方式的code
	private String payState;// 支付状态 0:未付款 1:已付款';
	private String tsn;// 第三方平台订单号
	private String outTradeNo ;//业务系统订单号
	private String refundState;//退款状态
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPlatFormType() {
		return platFormType;
	}
	public void setPlatFormType(String platFormType) {
		this.platFormType = platFormType;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getPayState() {
		return payState;
	}
	public void setPayState(String payState) {
		this.payState = payState;
	}
	public String getTsn() {
		return tsn;
	}
	public void setTsn(String tsn) {
		this.tsn = tsn;
	}
	public Date getOrderStartTime() {
		return orderStartTime;
	}
	public void setOrderStartTime(Date orderStartTime) {
		this.orderStartTime = orderStartTime;
	}
	public Date getOrderEndTime() {
		return orderEndTime;
	}
	public void setOrderEndTime(Date orderEndTime) {
		this.orderEndTime = orderEndTime;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getRefundState() {
		return refundState;
	}
	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}
	
}
