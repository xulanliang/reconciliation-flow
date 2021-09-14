package com.yiban.rec.domain.recon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 异常处理记录表
 *
 * @Author WY
 * @Date 2018年11月16日
 */
@Entity
@Table(name = "t_trade_check_follow_deal")
public class TradeCheckFollowDeal implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4488561211975140232L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户名
    private String userName;

    // 支付流水号
    private String payFlowNo;

    // 描述
    private String description;

    // 文件路径
    private String fileLocation;

    private String recHisId;
    private String recThridId;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;

    /** 异常状态：2短款抹平，3长款抹平，10短款追回，11长款退费（要过滤掉）*/
    private String exceptionState;

    /** 处理金额 */
    private BigDecimal dealAmount;

    private String orgCode;

    private String tradeDatetime;

    private String payType;

    private String billSource;
    // 患者类型
    private String patType;

    public TradeCheckFollowDeal() {
    };

    public TradeCheckFollowDeal(String userName, String payFlowNo, String description, String saveFileLocation,
                                Date createdDate) {
        this.userName = userName;
        this.payFlowNo = payFlowNo;
        this.description = description;
        this.fileLocation = saveFileLocation;
        this.createdDate = createdDate;
    };

    public TradeCheckFollowDeal(String userName, String payFlowNo, String description, String fileLocation,
                                Date createdDate, String exceptionState, BigDecimal dealAmount, String orgCode,
                                String tradeDatetime,String payName,String billSource, String patType, String recHisId, String recThridId) {
        super();
        this.userName = userName;
        this.payFlowNo = payFlowNo;
        this.description = description;
        this.fileLocation = fileLocation;
        this.createdDate = createdDate;
        this.exceptionState = exceptionState;
        this.dealAmount = dealAmount;
        this.orgCode = orgCode;
        this.tradeDatetime = tradeDatetime;
        this.payType = payName;
        this.billSource = billSource;
        this.patType = patType;
        this.recHisId = recHisId;
        this.recThridId = recThridId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPayFlowNo() {
        return payFlowNo;
    }

    public void setPayFlowNo(String payFlowNo) {
        this.payFlowNo = payFlowNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getExceptionState() {
        return exceptionState;
    }

    public void setExceptionState(String exceptionState) {
        this.exceptionState = exceptionState;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getTradeDatetime() {
        return tradeDatetime;
    }

    public void setTradeDatetime(String tradeDatetime) {
        this.tradeDatetime = tradeDatetime;
    }

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPatType() {
        return patType;
    }

    public void setPatType(String patType) {
        this.patType = patType;
    }

    public String getRecHisId() {
        return recHisId;
    }

    public void setRecHisId(String recHisId) {
        this.recHisId = recHisId;
    }

    public String getRecThridId() {
        return recThridId;
    }

    public void setRecThridId(String recThridId) {
        this.recThridId = recThridId;
    }
}
