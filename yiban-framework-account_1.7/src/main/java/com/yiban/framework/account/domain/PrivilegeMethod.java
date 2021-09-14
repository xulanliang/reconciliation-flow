package com.yiban.framework.account.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.domain.base.ValueTexts;

/**
 * 权限方法.
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PrivilegeMethod implements ValueTextable<String> {

    create("create", "创建"), 
    read("read", "读"), 
    update("update", "更新"), 
    delete("delete", "删除"),
    manage("manage", "管理"),
    all("*", "全部");

    @Override
    public String toString() {
        if (this == all) {
            return "*";
        }
        return this.name();
    }

    PrivilegeMethod(String value, String text) {
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

    private static Map<String, String> map = ValueTexts.asMap(newArrayList(PrivilegeMethod.values()));

    public static Map<String, String> asMap() {
        return map;
    }
}
