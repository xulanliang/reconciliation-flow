package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * 支付类型表
 */
@Entity
@Table(name = "t_pay_type")
public class PayType extends IdEntity {

	private static final long serialVersionUID = 1L;

	//id
	private Long id;
		
	// 名称
	private String name;

	// code
	private String code;
	
	// 类型：微信/支付宝/银行
	private String type;
	
	// 订单code
	private String orderType;

	// 位置：线上/线下
	private String location;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
