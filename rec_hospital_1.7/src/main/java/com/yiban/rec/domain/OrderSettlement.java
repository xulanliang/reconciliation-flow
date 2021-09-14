package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * @author swing
 * @date 2018年8月9日 下午2:46:42 类说明
 */
@Entity
@Table(name = "t_order_settlement")
public class OrderSettlement extends IdEntity {
	private static final long serialVersionUID = 1L;
	private String orgCode;
	private String deviceNo;
	private String patType;// zy:住院,mz:门诊
	private String tradeDateTime;
	private Integer rechargeCount=0;// 充值次数
	private Integer payCount=0;// 缴费次数
	private Double advanceAmount=0d;// 预交金
	private Integer wxRechargeCount=0;// 微信充值次数
	private Integer wxPayCount=0;// 微信缴费次数
	private Integer aliRechargeCount=0;// 支付宝充值次数
	private Integer aliPayCount=0;// 支付宝缴费次数
	private Double wxAmount=0d;// 微信收入
	private Double wxRefund=0d;// 微信退款
	private Double aliAmount=0d;// 支付宝收入
	private Double aliRefund=0d;// 支付宝退款
	private Double bankAmount=0d;// 银联收入
	private Double bankRefund=0d;// 银联退款
	private Double cashAmount=0d;// 现金收入
	private Double cashRefund=0d;// 现金退款
	private Double yibaoProvinceAmount=0d;// 医保省
	private Double yibaoCityAmount=0d;// 医保市
	@Transient
	private Double wxTotal = 0d;
	@Transient
	private Double aliTotal = 0d;
	@Transient
	private Double bankTotal = 0d;
	@Transient
	private Double cashTotal = 0d;
	@Transient
	private Double yibaoTotal = 0d;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

	

	public String getTradeDateTime() {
		return tradeDateTime;
	}

	public void setTradeDateTime(String tradeDateTime) {
		this.tradeDateTime = tradeDateTime;
	}

	public Integer getRechargeCount() {
		return rechargeCount;
	}

	public void setRechargeCount(Integer rechargeCount) {
		this.rechargeCount = rechargeCount;
	}

	public Integer getPayCount() {
		return payCount;
	}

	public void setPayCount(Integer payCount) {
		this.payCount = payCount;
	}

	public Double getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(Double advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	public Integer getWxRechargeCount() {
		return wxRechargeCount;
	}

	public void setWxRechargeCount(Integer wxRechargeCount) {
		this.wxRechargeCount = wxRechargeCount;
	}

	public Integer getWxPayCount() {
		return wxPayCount;
	}

	public void setWxPayCount(Integer wxPayCount) {
		this.wxPayCount = wxPayCount;
	}

	public Integer getAliRechargeCount() {
		return aliRechargeCount;
	}

	public void setAliRechargeCount(Integer aliRechargeCount) {
		this.aliRechargeCount = aliRechargeCount;
	}

	public Integer getAliPayCount() {
		return aliPayCount;
	}

	public void setAliPayCount(Integer aliPayCount) {
		this.aliPayCount = aliPayCount;
	}

	public Double getWxAmount() {
		return wxAmount;
	}

	public void setWxAmount(Double wxAmount) {
		this.wxAmount = wxAmount;
	}

	public Double getWxRefund() {
		return wxRefund;
	}

	public void setWxRefund(Double wxRefund) {
		this.wxRefund = wxRefund;
	}

	public Double getAliAmount() {
		return aliAmount;
	}

	public void setAliAmount(Double aliAmount) {
		this.aliAmount = aliAmount;
	}

	public Double getAliRefund() {
		return aliRefund;
	}

	public void setAliRefund(Double aliRefund) {
		this.aliRefund = aliRefund;
	}

	public Double getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(Double bankAmount) {
		this.bankAmount = bankAmount;
	}

	public Double getBankRefund() {
		return bankRefund;
	}

	public void setBankRefund(Double bankRefund) {
		this.bankRefund = bankRefund;
	}

	public Double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public Double getCashRefund() {
		return cashRefund;
	}

	public void setCashRefund(Double cashRefund) {
		this.cashRefund = cashRefund;
	}

	public Double getYibaoProvinceAmount() {
		return yibaoProvinceAmount;
	}

	public void setYibaoProvinceAmount(Double yibaoProvinceAmount) {
		this.yibaoProvinceAmount = yibaoProvinceAmount;
	}

	public Double getYibaoCityAmount() {
		return yibaoCityAmount;
	}

	public void setYibaoCityAmount(Double yibaoCityAmount) {
		this.yibaoCityAmount = yibaoCityAmount;
	}

	public Double getWxTotal() {
		return wxTotal;
	}

	public void setWxTotal(Double wxTotal) {
		this.wxTotal = wxTotal;
	}

	public Double getAliTotal() {
		return aliTotal;
	}

	public void setAliTotal(Double aliTotal) {
		this.aliTotal = aliTotal;
	}

	public Double getBankTotal() {
		return bankTotal;
	}

	public void setBankTotal(Double bankTotal) {
		this.bankTotal = bankTotal;
	}

	public Double getCashTotal() {
		return cashTotal;
	}

	public void setCashTotal(Double cashTotal) {
		this.cashTotal = cashTotal;
	}

	public Double getYibaoTotal() {
		return yibaoTotal;
	}

	public void setYibaoTotal(Double yibaoTotal) {
		this.yibaoTotal = yibaoTotal;
	}

}
