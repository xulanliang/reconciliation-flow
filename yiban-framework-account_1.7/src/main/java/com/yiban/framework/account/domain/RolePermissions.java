package com.yiban.framework.account.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(RolePermissions.class)
@Table(name = RolePermissions.TABLE_NAME)
public class RolePermissions implements Serializable {

	private static final long serialVersionUID = 3698365086291796590L;

	public static final String TABLE_NAME = "t_role_permissions";
	@Id
	@Column(name="role_id")
	private Long roleId;
	@Id
	@Column(name="permissions_id")
	private Long permissionsId;
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getPermissionsId() {
		return permissionsId;
	}
	public void setPermissionsId(Long permissionsId) {
		this.permissionsId = permissionsId;
	}
	
}
