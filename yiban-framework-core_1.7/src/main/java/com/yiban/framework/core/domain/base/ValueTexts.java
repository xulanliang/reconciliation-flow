package com.yiban.framework.core.domain.base;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class ValueTexts {

    public static <T> Map<T, String> asMap(Iterable<? extends ValueTextable<T>> vts) {
        Map<T, String> result = Maps.newLinkedHashMap();
        for (ValueTextable<T> vt : vts) {
            result.put(vt.getValue(), vt.getText());
        }
        return result;
    }
}
