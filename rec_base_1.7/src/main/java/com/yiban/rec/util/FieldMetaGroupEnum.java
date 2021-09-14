package com.yiban.rec.util;

/**
 * @author swing
 * @date 2018年7月17日 下午3:17:36 类说明
 */
public enum FieldMetaGroupEnum {
//	COMMON("通用", 1),
	RECON_BILL("对账管理", 2),
	BILL_EMAIL("邮件账单解析", 3),;

	private String name;
	private int sort;

	FieldMetaGroupEnum(String name, int sort) {
		this.name = name;
		this.sort = sort;
	}

	public String getName() {
		return name;

	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

}
