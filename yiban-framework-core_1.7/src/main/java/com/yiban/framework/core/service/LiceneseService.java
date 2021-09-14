package com.yiban.framework.core.service;

import com.yiban.framework.core.domain.BusinessException;

/**
 * @author swing
 * @date 2018年5月24日 下午3:14:38 类说明 软件版权授权验证
 */
public interface LiceneseService {
	void doAuthorize(String userName, String password) throws BusinessException;
}
