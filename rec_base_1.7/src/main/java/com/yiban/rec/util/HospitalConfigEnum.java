package com.yiban.rec.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.domain.base.ValueTexts;

public class HospitalConfigEnum {
	/**
     * 医院配置类型
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum HospitalConfigType implements ValueTextable<String> {

    	HOSPITAL_CONFIG("001", "医院配置"), EMAIL_CONFIG("002", "邮箱配置");

    	HospitalConfigType(String value, String text) {
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

        private static Map<String, String> map = ValueTexts.asMap(newArrayList(HospitalConfigType.values()));

        public static Map<String, String> asMap() {
            return map;
        }
    }
}
