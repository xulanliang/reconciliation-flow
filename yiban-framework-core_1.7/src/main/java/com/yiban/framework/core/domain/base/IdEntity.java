package com.yiban.framework.core.domain.base;

import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.AbstractPersistable;

@MappedSuperclass
public abstract class IdEntity extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -1573784109664782022L;

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
}
