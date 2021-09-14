package com.yiban.framework.account.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.base.BaseEntity;
import com.yiban.framework.core.domain.base.IdSerializer;

/**
 * 角色.
 * 
 */
@Entity
@Table(name = Role.TABLE_NAME)
public class Role extends BaseEntity<User> {

    public static final String TABLE_NAME = "t_role";

    private static final long serialVersionUID = -369638231589109651L;

    // 名称.
    @Column(length = 200, nullable = false)
    @Size(max = 200)
    @NotNull
    private String name;

    // 领域.
    @Column(length = 200)
    @Size(max = 200)
    private String realm;

    // 描述.
    @Column(length = 500)
    @Size(max = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonSerialize(using = IdSerializer.class)
    private Role parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Role> children = Lists.newArrayList();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "t_role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permissions_id"), uniqueConstraints = { @UniqueConstraint(columnNames = {
            "role_id", "permissions_id" }) })
    @OrderBy("id ASC")
    @JsonIgnore
    private List<Permissions> permissions = Lists.newArrayList();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("id ASC")
    @JsonIgnore
    private List<UserRole> userRoles = Lists.newArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Role getParent() {
        return parent;
    }

    public void setParent(Role parent) {
        this.parent = parent;
    }

    public List<Role> getChildren() {
        return children;
    }

    public void setChildren(List<Role> children) {
        this.children = children;
    }

    public List<Permissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}

	public void addPermissions(Permissions permissions) {
        this.getPermissions().add(permissions);
    }

    public void removePrivilege(Permissions permissions) {
        this.getPermissions().remove(permissions);
    }
}
