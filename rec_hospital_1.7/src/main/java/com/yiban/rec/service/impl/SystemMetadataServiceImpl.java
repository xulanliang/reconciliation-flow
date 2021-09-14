package com.yiban.rec.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.rec.dao.SystemMetadataDao;
import com.yiban.rec.domain.SystemMetadata;
import com.yiban.rec.service.SystemMetadataService;

@Service
public class SystemMetadataServiceImpl implements SystemMetadataService {

	@Autowired
	private SystemMetadataDao systemMetadataDao;
	
	
	public List<SystemMetadata> findByMetaDataCode(String code){
		return systemMetadataDao.findByMetaDataCode(code);
	}
	
	public void deleteData(String metaDataCode) throws Exception{
		systemMetadataDao.deleteByMetaDataCode(metaDataCode);
	}
	
	public void save(List<SystemMetadata> list) throws Exception{
		systemMetadataDao.save(list);
	}
	
	public List<SystemMetadata> findAll(){
		return systemMetadataDao.findAll();
	}
}
