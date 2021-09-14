package com.yiban.rec.domain.vo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yiban.rec.util.FieldMetaGroupEnum;
import com.yiban.rec.util.FieldMetaTypeEnum;

/**
 * @author swing
 * @date 2018年7月13日 下午3:40:20 类说明
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldMeta {
	// 组别
	FieldMetaGroupEnum group() default FieldMetaGroupEnum.RECON_BILL;
	// 排序
	int sort() default 0;
	// 类型
	FieldMetaTypeEnum type() default FieldMetaTypeEnum.TEXT;
	// 名称
	String name();
	String defaultValue() default "";
	String[] options() default {};
}
