package com.yiban.rec.bill.parse.util;

public enum RefundEnumType {

	BILL_SOURCE_JD(0,"self_jd","巨鼎"),
	BILL_SOURCE(0,"self","银医"),
	BILL_SOURCE_JIND(0,"self_td_jd","金蝶"),
//	BILL_SOURCE_JIND(0,"9948","金蝶"),
	REFUND_SUCCESS(1,"","退费成功"),
	REFUND_FAILURE(2,"","退费失败(正常)"),
	REFUND_NO(0,"","为退费"),
	REFUND_NO_EXCEPTION(3,"","退费失败(异常)"),
	;
	private Integer id;
	private String value;
	private String code;
	private RefundEnumType(Integer id,String value,String code) {
		this.id=id;
		this.value = value;
		this.code=code;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
