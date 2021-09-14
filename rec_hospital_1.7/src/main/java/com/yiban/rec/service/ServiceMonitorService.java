package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.ServiceMonitor;

public interface ServiceMonitorService {
	 ServiceMonitor getServiceMonitorById(Long id);
	 
	 ServiceMonitor getServiceMonitorByOrgNo(String orgNo);
	 
	 ResponseResult save(ServiceMonitor ServiceMonitor);
	 
	 ResponseResult delete(Long id);
	 
	 ResponseResult update(ServiceMonitor ServiceMonitor);
	 
	 Page<ServiceMonitor> getServiceMonitorList(PageRequest pagerequest,Long orgNo);
	 
	 List<ServiceMonitor> getServiceMonitorByIsDeletedAndIsActived(Integer isDeleted,Integer isActived);
	 
}
