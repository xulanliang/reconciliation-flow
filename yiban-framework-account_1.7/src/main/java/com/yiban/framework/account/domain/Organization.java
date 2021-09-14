package com.yiban.framework.account.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.base.NullChildTreeEntity;

/**
 * 组织机构.
 * 
 */
@Entity
@Table(name = Organization.TABLE_NAME)
public class Organization extends NullChildTreeEntity<Organization, User> {

    public static final String TABLE_NAME = "t_organization";

    private static final long serialVersionUID = 4775518193679945631L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserOrganization> userOrganizations = Lists.newArrayList();

    // 机构编码.
    @Column(length = 1000, unique = true)
    @Size(max = 1000)
    private String code;

    // 联系人.
    @Column(length = 50)
    @Size(max = 50)
    private String contactUser;

    // 联系人电话.
    @Column(length = 50)
    @Size(max = 50)
    private String contactPhone;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIgnore
    public List<UserOrganization> getUserOrganizations() {
        return userOrganizations;
    }

    public void setUserOrganizations(List<UserOrganization> userOrganizations) {
        this.userOrganizations = userOrganizations;
    }

    public String getContactUser() {
        return contactUser;
    }

    public void setContactUser(String contactUser) {
        this.contactUser = contactUser;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof Organization)){
    		return false;
    	}
    	Organization org =(Organization)obj;
    	return  org.getId().longValue() ==this.getId().longValue();
    }
    @Override
    public int hashCode() {
    	return this.getId().hashCode();
    }
}
