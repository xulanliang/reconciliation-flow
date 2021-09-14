/**
 * Copyright © 2018 深圳市巨鼎医疗设备有限公司
 */
package com.yiban.rec.util;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.Validate;

import groovy.util.logging.Slf4j;



/**
 * Validate Util
 * 
 * @author Zhouych
 * @date 2018年6月13日 下午4:23:15 <br/>
 * @since JDK 1.8
 */
@Slf4j
public class ValidateUtil extends Validate {

	/**
	 * 使用内部类初始化validator
	 */
	private static class InnerValidator {
		static Validator validator;
		static {
			// 获取验证器
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
		}
	}

	/**
	 * 验证bean是否符合规则
	 * 
	 * @param bean        要检验的对象
	 * @param throwDirect 验证不通过时是否直接抛出异常
	 * @return
	 * @throws BusinessException
	 */
	public static <T> boolean valid(T bean, boolean throwDirect, Class<?>... group) throws Exception {
		Set<ConstraintViolation<T>> constraintViolations = InnerValidator.validator.validate(bean, group);

		if (constraintViolations.size() > 0) {
			StringBuilder sbError = new StringBuilder();
			// 遍历所有验证错误取出信息
			Iterator<ConstraintViolation<T>> itErrors = constraintViolations.iterator();
			while (itErrors.hasNext()) {
				sbError.append(itErrors.next().getMessage()).append(";");
			}
			// 直接抛出异常
			if (throwDirect) {
				throw new Exception(sbError.toString());
			}
			return false;
		}
		return true;
	}

	/**
	 * 验证bean是否符合规则，并抛出不符合验证的异常
	 * 
	 * @param bean 要检验的对象
	 * @return 是否校验通过
	 * @throws BusinessException
	 */
	public static <T> boolean valid(T bean, Class<?>... group) throws Exception {
		return valid(bean, true, group);
	}
}
