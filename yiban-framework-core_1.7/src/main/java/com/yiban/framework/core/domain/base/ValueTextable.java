package com.yiban.framework.core.domain.base;

public interface ValueTextable<T> {

    T getValue();

    String getText();

    String getDescription();

    boolean isSelected();
}
