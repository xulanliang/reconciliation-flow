package com.yiban.framework.core.eum;

/**
 * @author swing
 * @date 2018年2月1日 下午2:39:16 类说明 删除枚举类
 */
public enum DeleteEnum {
	NO(0, "否"), YES(1, "是");
	DeleteEnum(Integer value, String text) {
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
