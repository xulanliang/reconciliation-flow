package com.yiban.rec.util;

public enum RefundStateEnum {

	unExamine("1","未审核"),
	reject("2","已驳回"),
	refund("3","已退费");
	
	private String value;
	private String name;
	
	private RefundStateEnum(String value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
