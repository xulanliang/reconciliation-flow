package com.yiban.framework.account.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.yiban.framework.core.domain.base.BaseEntity;

/**
 * 用户机构关联实体.
 * 
 */
@Entity
@Table(name = "t_user_organization", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id",
        "organization_id" }) })
public class UserOrganization extends BaseEntity<User> {

    private static final long serialVersionUID = -4734608626561169478L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    public UserOrganization() {

    }

    public UserOrganization(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
