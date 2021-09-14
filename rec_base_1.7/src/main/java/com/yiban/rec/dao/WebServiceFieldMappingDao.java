package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.WebServiceFieldMappingEntity;

public interface WebServiceFieldMappingDao extends JpaRepository<WebServiceFieldMappingEntity, Long>,
		JpaSpecificationExecutor<WebServiceFieldMappingEntity> {

}
