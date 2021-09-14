package com.yiban.framework.account.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.ValueTextable;

/**
 * 权限类型.
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PrivilegeType implements ValueTextable<String> {

    Application("Application", "应用"), Business("Business", "业务"), Menu("Menu", "菜单");

    PrivilegeType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    private String value;
    private String text;
    private boolean selected;
    private String description;

    @Override
    public String getValue() {
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
