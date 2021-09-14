package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.SystemMetadata;

public interface SystemMetadataService {

	public List<SystemMetadata> findByMetaDataCode(String code);
	
	public void deleteData(String metaDataCode) throws Exception;
	
	public void save(List<SystemMetadata> list) throws Exception;
	
	public List<SystemMetadata> findAll();
}
