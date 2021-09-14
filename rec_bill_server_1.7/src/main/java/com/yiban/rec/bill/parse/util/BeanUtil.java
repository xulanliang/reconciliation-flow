package com.yiban.rec.bill.parse.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

public class BeanUtil {

	/**
	 * 通过field方式赋值
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 * @throws Exception
	 */
	public static void setFieldValue(Object object, String fieldName, String value) throws Exception {
		// 获取字段对象
		Field field = object.getClass().getDeclaredField(fieldName);
		// 设置可访问权限
		field.setAccessible(true);
		// 获取字段类型
		String fieldType = field.getGenericType().getTypeName();

		if (String.class.getName().equals(fieldType)) {
			field.set(object, value);

		} else if (BigDecimal.class.getName().equals(fieldType)) {
			field.set(object, new BigDecimal(value.trim()));

		} else if (Date.class.getName().equals(fieldType)) {
			field.set(object, DateUtil.getCurrentDate());

		} else if (Integer.class.getName().equals(fieldType)) {
			field.set(object, Integer.valueOf(value.trim()));

		} else if (Long.class.getName().equals(fieldType)) {
			field.set(object, Long.valueOf(value.trim()));
		}
	}
}
