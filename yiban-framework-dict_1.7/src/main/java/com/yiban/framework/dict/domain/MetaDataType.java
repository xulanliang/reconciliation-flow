package com.yiban.framework.dict.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.base.BaseEntity;

@Entity
@Table(name = MetaDataType.TABLE_NAME)
public class MetaDataType extends BaseEntity<User> {
	private static final long serialVersionUID = -2942398384183133097L;
	public static final String TABLE_NAME = "t_meta_data_type";

	@Column(length = 50, unique = true)
	@NotBlank(message="键名不能为空")
	private String name;

	@Column(length = 50, unique = true)
	@NotBlank(message="键值不能为空")
	private String value;

	@Column(length = 500)
	@Size(max = 500,message="最大500字符")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MetaDataType() {
		super();
	}

	public String getText() {
		return this.name;
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
}
