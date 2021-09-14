package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.basicInfo.CycleType;

public interface RecCommonDao extends JpaRepository<CycleType, Long>, JpaSpecificationExecutor<CycleType> {
	
	@Query("select ct from CycleType ct where ct.cType= ?1 order by ct.cKey asc")
    List<CycleType> findCycleTypesByType(Integer type);
}
