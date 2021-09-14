package com.yiban.rec.xingyi.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "refund_table")
@DynamicUpdate
public class RefundTable {
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue
  private Long id;
  
  private String payFlowNo;
  
  private String payType;
  
  private String refundAmount;
  
  private String reason;
  
  private String refundState;
  
  private String shengheReason;
  
  private String shenghePeople;
  
  private String requestTime;
  
  private String shengheTime;
  
  private String requestPeople;
  
  public Long getId() {
    return this.id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getPayFlowNo() {
    return this.payFlowNo;
  }
  
  public void setPayFlowNo(String payFlowNo) {
    this.payFlowNo = payFlowNo;
  }
  
  public String getPayType() {
    return this.payType;
  }
  
  public void setPayType(String payType) {
    this.payType = payType;
  }
  
  public String getRefundAmount() {
    return this.refundAmount;
  }
  
  public void setRefundAmount(String refundAmount) {
    this.refundAmount = refundAmount;
  }
  
  public String getReason() {
    return this.reason;
  }
  
  public void setReason(String reason) {
    this.reason = reason;
  }
  
  public String getRefundState() {
    return this.refundState;
  }
  
  public void setRefundState(String refundState) {
    this.refundState = refundState;
  }
  
  public String getShengheReason() {
    return this.shengheReason;
  }
  
  public void setShengheReason(String shengheReason) {
    this.shengheReason = shengheReason;
  }
  
  public String getShenghePeople() {
    return this.shenghePeople;
  }
  
  public void setShenghePeople(String shenghePeople) {
    this.shenghePeople = shenghePeople;
  }
  
  public String getRequestTime() {
    return this.requestTime;
  }
  
  public void setRequestTime(String requestTime) {
    this.requestTime = requestTime;
  }
  
  public String getShengheTime() {
    return this.shengheTime;
  }
  
  public void setShengheTime(String shengheTime) {
    this.shengheTime = shengheTime;
  }
  
  public String getRequestPeople() {
    return this.requestPeople;
  }
  
  public void setRequestPeople(String requestPeople) {
    this.requestPeople = requestPeople;
  }
}
