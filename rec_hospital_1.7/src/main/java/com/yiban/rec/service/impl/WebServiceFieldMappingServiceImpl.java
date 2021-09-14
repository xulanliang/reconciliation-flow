package com.yiban.rec.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yiban.rec.dao.WebServiceFieldMappingDao;
import com.yiban.rec.domain.WebServiceFieldMappingEntity;
import com.yiban.rec.service.WebServiceFieldMappingService;

@Service
public class WebServiceFieldMappingServiceImpl implements WebServiceFieldMappingService {

	@Autowired
	private WebServiceFieldMappingDao webServiceFieldMappingDao;

	@Override
	public Page<WebServiceFieldMappingEntity> getPage(Pageable pageable) {
		return webServiceFieldMappingDao.findAll(pageable);
	}

	@Override
	public WebServiceFieldMappingEntity saveOrUpdate(WebServiceFieldMappingEntity entity) {
		return webServiceFieldMappingDao.saveAndFlush(entity);
	}

	@Override
	public void del(Long id) {
		webServiceFieldMappingDao.delete(id);
	}

}
