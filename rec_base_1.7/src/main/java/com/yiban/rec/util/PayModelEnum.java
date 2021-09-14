package com.yiban.rec.util;

/**
 * @author swing
 * @date 2018年6月6日 下午2:22:28 支付模式枚举
 */
public enum PayModelEnum {
	//本系统
	SELF("self"), 
	//第三方
	THIRD("third");
	PayModelEnum(String text) {
		this.text = text;

	}

	private String text;

	@Override
	public String toString() {
		return this.text;
	}

	public String getText() {
		return text;
	}
	
}
