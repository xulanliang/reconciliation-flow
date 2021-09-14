package com.yiban.framework.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.ValueTextable;

/**
 * 用户状态.
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserStatus implements ValueTextable<Long> {

    active(0L, "激活"), disabled(1L, "禁用");

    UserStatus(Long value, String text) {
        this.value = value;
        this.text = text;
    }

    private Long value;
    private String text;
    private boolean selected;
    private String description;

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
