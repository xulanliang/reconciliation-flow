package com.yiban.framework.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.yiban.framework.core.domain.base.TreeEntity;

/**
 * 权限.
 * 
 */
@Entity
@Table(name = Permissions.TABLE_NAME)
public class Permissions extends TreeEntity<Permissions, User> {

	private static final long serialVersionUID = -6575040329079123868L;

	public static final String TABLE_NAME = "t_permissions";

    // 类型.
    @Column(length = 100)
    @Size(max = 100)
    private String type;

    // 资源.
    @Column(length = 100)
    @Size(max = 100)
    private String target;

    // 操作.
    @Column(length = 100)
    @Size(max = 100)
    private String method;

    @Column(length = 200)
    @Size(max = 200)
    private String scope;

    private boolean readOnly = false;

    @Transient
    private boolean checked;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
