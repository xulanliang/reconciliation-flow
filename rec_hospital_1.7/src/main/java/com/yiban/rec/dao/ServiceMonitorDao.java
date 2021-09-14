package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.ServiceMonitor;

public interface ServiceMonitorDao extends JpaRepository<ServiceMonitor, Long>, JpaSpecificationExecutor<ServiceMonitor> {
	
	ServiceMonitor getServiceMonitorByOrgNo(String orgNo);
	
	
}
