package com.yiban.rec.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.dao.payproxy.PayAuthConfigDao;
import com.yiban.rec.domain.payproxy.PayAuthConfig;
import com.yiban.rec.service.PayAuthConfigService;

/**
 * @author swing
 * @date 2018年7月5日 上午9:59:53 类说明
 */
@Service
public class PayAuthConfigServiceImpl implements PayAuthConfigService {
	@Autowired
	private PayAuthConfigDao payAuthConfigDao;

	@Transactional
	@Override
	public void updateState(long id,int state) {
		payAuthConfigDao.updateState(id, state);
	}

	

	@Transactional
	@Override
	public void save(PayAuthConfig config) {
		payAuthConfigDao.save(config);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		payAuthConfigDao.delete(id);
	}

	

	@Override
	public PayAuthConfig findById(Long id) {
		return payAuthConfigDao.findOne(id);
	}

	@Override
	public PayAuthConfig findByApiKey(String apiKey) {
		return payAuthConfigDao.findByApiKey(apiKey);
	}

	@Override
	public Page<PayAuthConfig> findAll(Pageable pageable) {
		return payAuthConfigDao.findAll(pageable);
	}



	@Override
	public Page<PayAuthConfig> findByClientName(String clientName, Pageable pageable) {
		return payAuthConfigDao.findByClientNameContaining(clientName, pageable);
	}

	
}
