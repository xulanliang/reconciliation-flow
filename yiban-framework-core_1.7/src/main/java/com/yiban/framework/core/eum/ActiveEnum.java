package com.yiban.framework.core.eum;

/**
 * @author swing
 * @date 2018年2月1日 下午2:37:21 类说明 激活枚举类
 */
public enum ActiveEnum {
	NO(0, "否"), YES(1, "是");
	ActiveEnum(Integer value, String text) {
		this.value = value;
		this.text = text;
	}

	private int value;
	private String text;

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.text;
	}
	

	public String getText() {
		return text;
	}
}
