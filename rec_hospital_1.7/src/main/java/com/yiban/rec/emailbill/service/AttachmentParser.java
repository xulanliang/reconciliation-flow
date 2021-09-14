package com.yiban.rec.emailbill.service;

import java.util.List;

import com.yiban.rec.domain.ThirdBill;

/**
* @author swing
* @date 2018年6月25日 下午3:56:10
* 类说明
*/
public interface AttachmentParser {
	/**
	 * 附件解析成账单列表
	 * @return
	 */
	List<ThirdBill> convertToBean();
}
