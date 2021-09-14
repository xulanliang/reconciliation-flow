package com.yiban.rec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.rec.domain.payproxy.PayAuthConfig;

/**
 * @author swing
 * @date 2018年7月5日 上午9:50:18 类说明 认证授权配置接口
 */
public interface PayAuthConfigService {
	void updateState(long id, int state);
	

	void save(PayAuthConfig config);

	void delete(Long id);

	PayAuthConfig findByApiKey(String apiKey);

	PayAuthConfig findById(Long id);

	Page<PayAuthConfig> findAll(Pageable pageable);
	Page<PayAuthConfig> findByClientName(String clientName,Pageable pageable);
}
