package com.yiban.rec.util;

public enum TitleStateEnum {
	UNABUTMENTHIS("0","未对接HIS接口，无数据"),
	NOTHINGNESS("1","此单号HIS系统不存在"),
	NORMAL("2",""),
	NETWORKTIMEOUT("3","HIS连接异常");
	
	private String value;
	private String name;
	
	private TitleStateEnum(String value,String name) {
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
