package com.yiban.rec.domain.log;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 对账日志明细（包含对账和拉取账单的日志）
 * @Author WY
 * @Date 2018年9月28日
 */
@Entity
@Table(name = "t_rec_log_details")
public class RecLogDetails implements Serializable {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -258121457724463647L;

    /** 主键 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 账单日期 */
	@NotBlank(message="账单日期不能为空")
	@Column(length=10, nullable=false)
	private String orderDate;

	/** 机构编码 */
	@NotBlank(message="机构编码不能为空")
	@Column(length=20, nullable=false)
	private String orgCode;
	
	/** 日志类型：01：拉取账单，02：对账 */
	@NotBlank(message="机构编码不能为空")
	@Column(length=8, nullable=false)
	private String logType;
	
	/** 对账类型 */
	@NotBlank(message="对账类型不能为空")
	@Column(length=20, nullable=false)
	private String payType;
	
	/** 日志状态：0-异常，1-正常 */
	@NotNull(message="日志状态不能为空")
	@Column(length=4, nullable=false)
	private Integer recState;
	
	/** 日志描述 */
	@Column(length=2048)
	private String exceptionRemark;
	
	/** 创建时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createdDate;
	
	/** 推荐解决方案 */
	@Column(length=8)
	private String dealWay ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getRecState() {
        return recState;
    }

    public void setRecState(Integer recState) {
        this.recState = recState;
    }

    public String getExceptionRemark() {
        return exceptionRemark;
    }

    public void setExceptionRemark(String exceptionRemark) {
        this.exceptionRemark = exceptionRemark;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDealWay() {
        return dealWay;
    }

    public void setDealWay(String dealWay) {
        this.dealWay = dealWay;
    }
}
