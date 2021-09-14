package com.yiban.framework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义日志拦截注解
 * 
 * @author swing
 *
 * @date 2016年8月8日 下午6:14:12
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logable {
	// 操作描述
	String operation();

	String operType() default "用户操作";
}
