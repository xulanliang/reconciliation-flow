package com.yiban.framework.core.service;
/**
 * 如果服务调用过程中有什么需要手动引发的例外，请使用这种异常类型。
 * @author 
 *
 */
public class ExecuteException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2685416193903426247L;
	/**
	 * 给客户端标识引发异常的资源状态
	 */
	private int erorCode = 0;
	
	public ExecuteException() {
		super();
	}
	
	public ExecuteException(int code,String message) {
		this(message);
		this.erorCode = code;
	}

	public ExecuteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecuteException(String message) {
		super(message);
	}

	public ExecuteException(Throwable cause) {
		super(cause);
	}

	public int getErorCode() {
		return erorCode;
	}

	public void setErorCode(int erorCode) {
		this.erorCode = erorCode;
	}

}
