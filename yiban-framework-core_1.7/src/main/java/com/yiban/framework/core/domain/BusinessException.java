package com.yiban.framework.core.domain;

/**
 * @date 2018年1月5日 下午2:55:11 类说明
 */
public class BusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	int code;

	/**
	 * 构造函数
	 */
	public BusinessException() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param msgKey
	 *            错误代号
	 */
	public BusinessException(String msg) {
		super(msg);
	}

	/**
	 * 构造函数
	 * 
	 * @param msgKey
	 *            错误代号
	 */
	public BusinessException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
