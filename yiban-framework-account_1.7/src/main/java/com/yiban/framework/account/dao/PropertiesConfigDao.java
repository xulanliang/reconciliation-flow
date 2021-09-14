package com.yiban.framework.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.framework.account.domain.PropertiesConfig;

public interface PropertiesConfigDao
		extends JpaRepository<PropertiesConfig, Long>, JpaSpecificationExecutor<PropertiesConfig> {

	public PropertiesConfig findOneByPkeyAndIsActived(String pkey, Integer isActived);
	
	public PropertiesConfig findOneByPkey(String pkey);
}
