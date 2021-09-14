package com.yiban.framework.core.domain.base;

import javax.persistence.Column;
import javax.validation.constraints.Size;

/**
 * @author swing
 * @date 2018年4月2日 下午4:06:47 类说明
 */
public class BaseTree<U> extends BaseEntity<U> {
	private static final long serialVersionUID = 1L;
	private String name;
	@Column(name = "parent_id")
	private String parent;
	@Column(length = 500)
	@Size(max = 500)
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
