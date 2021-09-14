package com.yiban.framework.core.domain.base;

import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
//自动审计
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<U> extends AbstractEntity {

    private static final long serialVersionUID = 1678372157852941273L;

    @ManyToOne
    @CreatedBy
    private U createdBy;

    @ManyToOne
    @LastModifiedBy
    private U lastModifiedBy;
    

    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final U createdBy) {
        this.createdBy = createdBy;
    }

    public U getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
}
