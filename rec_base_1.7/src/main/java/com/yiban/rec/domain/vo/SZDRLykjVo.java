package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

public class SZDRLykjVo {

	String unit_id;// 医院ID;
	String pay_method;// 支付方式
	Date trade_time;// 交易时间
	String app_id;// 预约id
	String mch_id;// 商户id
	String sub_mch_id;// 子商户id
	String trade_no;// 交易单号
	String mch_trade_no;// 160平台交易单号
	String trade_type;// 交易类型
	String trade_state;// 交易状态
	BigDecimal trade_amt;// 自费交易金额
	String refund_no; //退费单号
	String mch_refund_no;// 商户退费单号
	BigDecimal refund_amt;// 自费退费金额
	String refund_type;// 退费类型
	String refund_state;// 退费状态
	String product_name;// 产品说明
	String fee_rate;// 费率
	String pay_his_invoice_no;// 支付发票号
	String order_type;// 订单类型
	String userName;// 患者姓名
	String businessType;// 业务类型
	String card_no;// 就诊卡号
	String his_pay_no;// 处方单号
	BigDecimal trade_medical_amt;// 医保支付金额
	BigDecimal trade_total_amt;// 支付总金额
	BigDecimal refund_medical_amt;// 医保退费金额
	BigDecimal refund_total_amt;// 退费总金额
	public String getUnit_id() {
		return unit_id;
	}
	public void setUnit_id(String unit_id) {
		this.unit_id = unit_id;
	}
	public String getPay_method() {
		return pay_method;
	}
	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}
	public Date getTrade_time() {
		return trade_time;
	}
	public void setTrade_time(Date trade_time) {
		this.trade_time = trade_time;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getSub_mch_id() {
		return sub_mch_id;
	}
	public void setSub_mch_id(String sub_mch_id) {
		this.sub_mch_id = sub_mch_id;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getMch_trade_no() {
		return mch_trade_no;
	}
	public void setMch_trade_no(String mch_trade_no) {
		this.mch_trade_no = mch_trade_no;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getTrade_state() {
		return trade_state;
	}
	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}
	public BigDecimal getTrade_amt() {
		return trade_amt;
	}
	public void setTrade_amt(BigDecimal trade_amt) {
		this.trade_amt = trade_amt;
	}
	public String getRefund_no() {
		return refund_no;
	}
	public void setRefund_no(String refund_no) {
		this.refund_no = refund_no;
	}
	public String getMch_refund_no() {
		return mch_refund_no;
	}
	public void setMch_refund_no(String mch_refund_no) {
		this.mch_refund_no = mch_refund_no;
	}
	public BigDecimal getRefund_amt() {
		return refund_amt;
	}
	public void setRefund_amt(BigDecimal refund_amt) {
		this.refund_amt = refund_amt;
	}
	public String getRefund_type() {
		return refund_type;
	}
	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}
	public String getRefund_state() {
		return refund_state;
	}
	public void setRefund_state(String refund_state) {
		this.refund_state = refund_state;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getFee_rate() {
		return fee_rate;
	}
	public void setFee_rate(String fee_rate) {
		this.fee_rate = fee_rate;
	}
	public String getPay_his_invoice_no() {
		return pay_his_invoice_no;
	}
	public void setPay_his_invoice_no(String pay_his_invoice_no) {
		this.pay_his_invoice_no = pay_his_invoice_no;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getCard_no() {
		return card_no;
	}
	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}
	public String getHis_pay_no() {
		return his_pay_no;
	}
	public void setHis_pay_no(String his_pay_no) {
		this.his_pay_no = his_pay_no;
	}
	public BigDecimal getTrade_medical_amt() {
		return trade_medical_amt;
	}
	public void setTrade_medical_amt(BigDecimal trade_medical_amt) {
		this.trade_medical_amt = trade_medical_amt;
	}
	public BigDecimal getTrade_total_amt() {
		return trade_total_amt;
	}
	public void setTrade_total_amt(BigDecimal trade_total_amt) {
		this.trade_total_amt = trade_total_amt;
	}
	public BigDecimal getRefund_medical_amt() {
		return refund_medical_amt;
	}
	public void setRefund_medical_amt(BigDecimal refund_medical_amt) {
		this.refund_medical_amt = refund_medical_amt;
	}
	public BigDecimal getRefund_total_amt() {
		return refund_total_amt;
	}
	public void setRefund_total_amt(BigDecimal refund_total_amt) {
		this.refund_total_amt = refund_total_amt;
	}

}
