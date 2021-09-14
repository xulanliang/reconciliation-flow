package com.yiban.rec.domain.vo;

import com.yiban.framework.account.domain.User;

public class RefundVo {

	//商户流水号
	private String orderNo;
	//支付渠道
	private String payCode;
	//支付类型
	private String payType;
	//退款金额
	private String tradeAmount;
	//部分退款金额
	private String batchRefundNo;
	//调用地址
	private String url;
	//支付宝微信订单号
	private String tsn;
	//支付订单来源
	private String billSource;
	//图片地址
	private String imgUrl;
	//操作人id
	private User user;
	//记录原因
	private String reason;
	//医院编码
	private String orgCode;
	//响应流水号(退款流水号)
	private String paymentFlow;
	//患者姓名
	private String patientName;
	//患者类型
	private String patType;
	//病人号
	private String patientNo;
	//状态1：待审核,3已退费
	private String state;
	// 支付业务类型
	private String businessType;
	// 支付 日期
	private String time;
	//交易时间hhmmss
	private String tradetime;
	
	//业务系统流水号
	private String outTradeNo;
	// 商户订单号
	private String shopNo;
	
	//中行退费的授权码
	private String sqm;
	
	//中行退费的票据号
	private String pjh;
	// 订单金额
	private String payAmount;
	//收费员/操作员
	private String cashier;
	//柜台编号
	private String counterNo;
	
	//中行流水号
	private String bocNo;
	
	private String extendArea;
	
	// 商户号
	private String merId;
	// 终端号
	private String termId;

	// HIS流水号
	private String hisFlowNo;
	// 卡号
	private String cardNo;
	// 发票号
	private String invoiceNo;

	//1原路径退回，2现金退费
	private String refundType;
	
	//自定义来源标识:1退款管理调用,0:其他
	private int source;
	
	//兼容之前版本问题
	private String batchRefundNoNew;
	
	// 线上支付- 支付类型
	private String unionPayType;
	// 线上支付-聚合支付标识
	private String unionPayCode;
	// 线上支付-系统编码
	private String unionSystemCode;
	// 退费类型标识  1：全部退费  2：部分退费
	private String refundRemark;
	
	//第三方渠道账单的outTradeNo
	private String thirdOutTradeNo;
	
	//消息类型
	private String msgType;

	// 卡的有效期yymm
	private String cardValidity;

	// 备注
	private String memo;
	

	public String getThirdOutTradeNo() {
		return thirdOutTradeNo;
	}
	public void setThirdOutTradeNo(String thirdOutTradeNo) {
		this.thirdOutTradeNo = thirdOutTradeNo;
	}
	public String getBocNo() {
		return bocNo;
	}
	public void setBocNo(String bocNo) {
		this.bocNo = bocNo;
	}
	public String getCashier() {
		return cashier;
	}
	public void setCashier(String cashier) {
		this.cashier = cashier;
	}
	public String getCounterNo() {
		return counterNo;
	}
	public void setCounterNo(String counterNo) {
		this.counterNo = counterNo;
	}
	public String getExtendArea() {
		return extendArea;
	}
	public void setExtendArea(String extendArea) {
		this.extendArea = extendArea;
	}
	public String getTradetime() {
		return tradetime;
	}
	public void setTradetime(String tradetime) {
		this.tradetime = tradetime;
	}
	public String getSqm() {
		return sqm;
	}
	public void setSqm(String sqm) {
		this.sqm = sqm;
	}
	public String getPjh() {
		return pjh;
	}
	public void setPjh(String pjh) {
		this.pjh = pjh;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getBatchRefundNo() {
		return batchRefundNo;
	}
	public void setBatchRefundNo(String batchRefundNo) {
		this.batchRefundNo = batchRefundNo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTsn() {
		return tsn;
	}
	public void setTsn(String tsn) {
		this.tsn = tsn;
	}
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getPaymentFlow() {
		return paymentFlow;
	}
	public void setPaymentFlow(String paymentFlow) {
		this.paymentFlow = paymentFlow;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientNo() {
		return patientNo;
	}
	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getShopNo() {
		return shopNo;
	}
	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getHisFlowNo() {
		return hisFlowNo;
	}
	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public String getBatchRefundNoNew() {
		return batchRefundNoNew;
	}
	public void setBatchRefundNoNew(String batchRefundNoNew) {
		this.batchRefundNoNew = batchRefundNoNew;
	}
	public String getUnionPayType() {
		return unionPayType;
	}
	public void setUnionPayType(String unionPayType) {
		this.unionPayType = unionPayType;
	}
	public String getUnionPayCode() {
		return unionPayCode;
	}
	public void setUnionPayCode(String unionPayCode) {
		this.unionPayCode = unionPayCode;
	}
	public String getUnionSystemCode() {
		return unionSystemCode;
	}
	public void setUnionSystemCode(String unionSystemCode) {
		this.unionSystemCode = unionSystemCode;
	}

	public String getRefundRemark() {
		return refundRemark;
	}

	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getCardValidity() {
		return cardValidity;
	}

	public void setCardValidity(String cardValidity) {
		this.cardValidity = cardValidity;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public String toString() {
		return "RefundVo{" +
				"orderNo='" + orderNo + '\'' +
				", payCode='" + payCode + '\'' +
				", payType='" + payType + '\'' +
				", tradeAmount='" + tradeAmount + '\'' +
				", batchRefundNo='" + batchRefundNo + '\'' +
				", url='" + url + '\'' +
				", tsn='" + tsn + '\'' +
				", billSource='" + billSource + '\'' +
				", imgUrl='" + imgUrl + '\'' +
				", user=" + user +
				", reason='" + reason + '\'' +
				", orgCode='" + orgCode + '\'' +
				", paymentFlow='" + paymentFlow + '\'' +
				", patientName='" + patientName + '\'' +
				", patType='" + patType + '\'' +
				", patientNo='" + patientNo + '\'' +
				", state='" + state + '\'' +
				", businessType='" + businessType + '\'' +
				", time='" + time + '\'' +
				", tradetime='" + tradetime + '\'' +
				", outTradeNo='" + outTradeNo + '\'' +
				", shopNo='" + shopNo + '\'' +
				", sqm='" + sqm + '\'' +
				", pjh='" + pjh + '\'' +
				", payAmount='" + payAmount + '\'' +
				", cashier='" + cashier + '\'' +
				", counterNo='" + counterNo + '\'' +
				", bocNo='" + bocNo + '\'' +
				", extendArea='" + extendArea + '\'' +
				", merId='" + merId + '\'' +
				", termId='" + termId + '\'' +
				", hisFlowNo='" + hisFlowNo + '\'' +
				", cardNo='" + cardNo + '\'' +
				", invoiceNo='" + invoiceNo + '\'' +
				", refundType='" + refundType + '\'' +
				", source=" + source +
				", batchRefundNoNew='" + batchRefundNoNew + '\'' +
				", unionPayType='" + unionPayType + '\'' +
				", unionPayCode='" + unionPayCode + '\'' +
				", unionSystemCode='" + unionSystemCode + '\'' +
				", refundRemark='" + refundRemark + '\'' +
				", thirdOutTradeNo='" + thirdOutTradeNo + '\'' +
				", msgType='" + msgType + '\'' +
				", cardValidity='" + cardValidity + '\'' +
				", memo='" + memo + '\'' +
				'}';
	}
}
