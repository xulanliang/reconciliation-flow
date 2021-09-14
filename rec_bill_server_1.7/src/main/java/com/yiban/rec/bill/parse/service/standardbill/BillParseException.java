package com.yiban.rec.bill.parse.service.standardbill;

import com.yiban.framework.core.domain.BusinessException;

/**
 * @author swing
 * @date 2018年7月25日 上午10:04:56 类说明 自定义账单解析异常
 */
public class BillParseException extends BusinessException {
	
	private static final long serialVersionUID = 1L;

	public BillParseException() {
		super();
	}

	public BillParseException(String msg) {
		super(msg);
	}
}
