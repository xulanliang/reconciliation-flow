package com.yiban.rec.web;

import org.apache.commons.lang3.StringUtils;

import com.yiban.framework.core.controller.BaseController;

public abstract class AppController extends BaseController {

	/**
	 * 因IOS、Android传入参数名称不规范，故通过此方法判断接收参数。
	 * 用于相同参数不同参数名获取参数
	 * @param arg1	参数1
	 * @param arg2	参数2
	 * @param required	是否必须
	 * @return
	 */
	protected String getParameter(String arg1, String arg2, boolean required, String message) {
		if(required && StringUtils.isEmpty(arg1) && StringUtils.isEmpty(arg2)) {
			//参数 为必须 且 参数值为空
    		throw new IllegalArgumentException(StringUtils.isEmpty(message)?"请输入正确的参数条件！":message);
		}
		if(StringUtils.isNotEmpty(arg1)) {
			return arg1;
		}else {
			return arg2;
		}
	}

	/**
	 * 因IOS、Android传入参数名称不规范，故通过此方法判断接收参数。
	 * 用于相同参数不同参数名获取参数（arg1/arg2 必须有一个参数有值，否则抛出错误参数异常）
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	protected String getParameter(String arg1, String arg2) {
		return this.getParameter(arg1, arg2, null);
	}

	/**
	 * 因IOS、Android传入参数名称不规范，故通过此方法判断接收参数。
	 * 用于相同参数不同参数名获取参数（arg1/arg2 必须有一个参数有值，否则抛出错误参数异常）
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	protected String getParameter(String arg1, String arg2, String message) {
		return this.getParameter(arg1, arg2, true, message);
	}
}
