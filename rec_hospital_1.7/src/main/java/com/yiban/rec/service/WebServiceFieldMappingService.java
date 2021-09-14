package com.yiban.rec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.rec.domain.WebServiceFieldMappingEntity;

public interface WebServiceFieldMappingService {

	public Page<WebServiceFieldMappingEntity> getPage(Pageable pageable);

	public WebServiceFieldMappingEntity saveOrUpdate(WebServiceFieldMappingEntity entity);

	public void del(Long id);
}
