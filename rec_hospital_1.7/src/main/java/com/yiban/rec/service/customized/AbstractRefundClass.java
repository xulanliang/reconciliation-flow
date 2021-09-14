package com.yiban.rec.service.customized;

import java.math.BigDecimal;

import com.yiban.rec.domain.MixRefundDetails;

/**
 * 退费公共方法抽象类
 */
public abstract class AbstractRefundClass {
    
	//目标金额
	private BigDecimal targetAmount;
	
	//源金额
	private BigDecimal sourceAmount;
	
    public AbstractRefundClass(MixRefundDetails vo,BigDecimal sourceAmount) {
    	this.sourceAmount=sourceAmount;
    	this.targetAmount=vo.getPayAmount();
    }
    /**
     * 部分退款或者全额退款true为全额退款false为部分退款
     */
    protected boolean isAllAmount() {
    	//源金额小于目标金额,这笔退费为部分退费
    	if(this.sourceAmount.compareTo(this.targetAmount)==-1) return false;
    	//源金额大于或者等于目标金额,这笔退费为全额退费
    	if(this.sourceAmount.compareTo(this.targetAmount)==1||this.sourceAmount.compareTo(this.targetAmount)==0) return true;
    	return true;
    }
}
