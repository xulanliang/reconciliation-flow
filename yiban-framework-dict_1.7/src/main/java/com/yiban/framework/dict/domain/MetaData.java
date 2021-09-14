package com.yiban.framework.dict.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.base.BaseEntity;
import com.yiban.framework.core.domain.base.IdSerializer;

@Entity
@Table(name = MetaData.TABLE_NAME)
public class MetaData extends BaseEntity<User> {
	private static final long serialVersionUID = 421592031528847297L;
	public static final String TABLE_NAME = "t_meta_data";

	// 基础数据代码.
	@Column(length = 200)
	@NotBlank
	private String name;

	// 基础数据值.
	@Column(length = 200)
	@NotBlank
	private String value;

	// 数据类型.
	@ManyToOne(optional = false)
	@JoinColumn(name = "type_id", referencedColumnName = "id")
	@JsonSerialize(using = IdSerializer.class)
	private MetaDataType dictType;

	// 基础数据描述.
	@Column(length = 255)
	private String description;

	private Long sort;
	
	@Transient
    private boolean checked;

	public MetaData() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MetaDataType getDictType() {
		return dictType;
	}

	public void setDictType(MetaDataType dictType) {
		this.dictType = dictType;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
