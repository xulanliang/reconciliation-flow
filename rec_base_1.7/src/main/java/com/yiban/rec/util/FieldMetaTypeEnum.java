package com.yiban.rec.util;

/**
 * @author swing
 * @date 2018年7月16日 下午3:06:59 类说明
 */
public enum FieldMetaTypeEnum {
	TEXT("text", "单行输入框"), 
	TEXTAREA("textarea", "文件输入框"),
    NUMBER("number", "数字"), 
    RADIO("radio","单选"), 
    CHECKBOX("checkbox", "多选"), 
    SELECT("select", "下拉框");
	private String name;
	private String label;

	FieldMetaTypeEnum(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

}
