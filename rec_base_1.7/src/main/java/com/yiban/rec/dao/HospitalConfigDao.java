package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.HospitalConfiguration;

public interface HospitalConfigDao extends JpaRepository<HospitalConfiguration, Long>, JpaSpecificationExecutor<HospitalConfiguration>{ 


	List<HospitalConfiguration> findByActive(int active);
}
