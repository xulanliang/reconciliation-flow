package com.yiban.rec.bill.parse.service.standardbill;

import javax.persistence.EntityManager;

import com.yiban.framework.core.domain.BusinessException;

/**
 * @author swing
 * @date 2018年7月25日 上午9:59:09 类说明 账单解析器顶层接口
 */
public interface BillParserable {
	/**
	 * 账单解析
	 * @throws BusinessException
	 */
	void parse(String orgCode,String date, EntityManager entityManager,String payType) throws BillParseException;
}
