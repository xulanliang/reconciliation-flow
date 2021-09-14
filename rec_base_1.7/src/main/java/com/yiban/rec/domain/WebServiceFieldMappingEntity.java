package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 字段映射关系
 */
@Entity
@Table(name = "t_webservice_field_mapping")
public class WebServiceFieldMappingEntity {

	@Id
	@GeneratedValue
	private Long id;

	// his响应账单数据的字段名
	private String dataFieldName;

	// 实体类的字段名
	private String classFieldName;

	// 默认值
	private String defaultValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDataFieldName() {
		return dataFieldName;
	}

	public void setDataFieldName(String dataFieldName) {
		this.dataFieldName = dataFieldName;
	}

	public String getClassFieldName() {
		return classFieldName;
	}

	public void setClassFieldName(String classFieldName) {
		this.classFieldName = classFieldName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
