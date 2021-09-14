package com.yiban.rec.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.baseinfo.DeviceInfo;

public interface DeviceInfoService {
	 DeviceInfo getDeviceInfoById(Long id);
	 
	 ResponseResult save(DeviceInfo deviceInfo);
	 
	 ResponseResult delete(Long id);
	 
	 ResponseResult update(DeviceInfo deviceInfo);
	 
	 Page<Map<String,Object>> getDeviceInfoList(PageRequest pagerequest,String deviceNo,String orgNo);

	DeviceInfo findDeviceInfoByDeviceNo(String deviceNo);

	List<DeviceInfo> findDeviceInfo();
	List<Map<String, Object>> getDeviceInfoByDeviceNos(String[] deviceNos);
	List<Map<String, Object>> getDeviceInfoByDeviceId(String[] deviceNos);
	
	public List<DeviceInfo> findByOrgNo(String orgNo);
}
