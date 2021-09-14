package com.yiban.rec.dao.baseinfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.baseinfo.DeviceShopR;

public interface DeviceShopRDao extends JpaRepository<DeviceShopR, Long>, JpaSpecificationExecutor<DeviceShopR> {

	void deleteByPayShopId(Long id);

	void deleteByDeviceId(Long id);


}
