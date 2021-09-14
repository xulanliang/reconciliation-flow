package com.yiban.rec.domain.settlement;

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
 * HIS 结算账单获取日志
 * @Author WY
 * @Date 2019年1月10日
 */
@Entity
@Table(name = "t_rec_log_settlement")
public class RecLogSettlement implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5381378341750852148L;

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
    private String orgCode;
    
    /** 日志状态：70-异常，71-正常 */
    @NotNull(message="日志状态不能为空")
    @Column(length=4, nullable=false)
    private Integer recResult;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;

    /** 日志明细 */
    private String resultInfo;

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

    public Integer getRecResult() {
        return recResult;
    }

    public void setRecResult(Integer recResult) {
        this.recResult = recResult;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }
}
