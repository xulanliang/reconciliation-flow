package com.yiban.framework.core.annotation;
/**
 * indicate the APP action method
 * @author Yang 
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMethod {
	/**
	 * indicate to intercept the operation if match.
	 * @return
	 */
	String value() default "";
	
	String serviceName() default "";
}
