package com.yiban.rec.dao.baseinfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.baseinfo.DeviceInfo;

public interface DeviceInfoDao extends JpaRepository<DeviceInfo, Long>, JpaSpecificationExecutor<DeviceInfo> {

	DeviceInfo findDeviceInfoByDeviceNo(String deviceNo);
	
	List<DeviceInfo> findByOrgNo(String orgNo);

}
