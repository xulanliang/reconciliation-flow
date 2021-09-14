package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.SystemMetadata;


public interface SystemMetadataDao extends JpaRepository<SystemMetadata, Long>, JpaSpecificationExecutor<SystemMetadata> {

	
	public List<SystemMetadata> findByMetaDataCode(String metaDataCode);
	
	@Transactional
	@Modifying
	void deleteByMetaDataCode(String metaDataCode);
}
