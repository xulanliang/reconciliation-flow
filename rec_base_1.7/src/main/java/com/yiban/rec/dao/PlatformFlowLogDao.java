package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import com.yiban.rec.domain.PlatformrawLog;

public interface PlatformFlowLogDao extends JpaRepository<PlatformrawLog, Integer>, JpaSpecificationExecutor<PlatformrawLog> {

	List<PlatformrawLog> findByFlowNo(@Param("flowNo") String flowNo);
}
