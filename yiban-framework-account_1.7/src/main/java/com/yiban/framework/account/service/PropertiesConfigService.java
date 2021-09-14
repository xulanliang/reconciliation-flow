package com.yiban.framework.account.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.PropertiesConfig;

public interface PropertiesConfigService {

	public Page<PropertiesConfig> findPage(String key, String type, String model, String description,
			Pageable pageable);

	public void saveOrUpdate(PropertiesConfig propertiesConfig);

	public void delete(Long id);

	public String findValueByPkey(String pkey, String defaultValue);

	public String findValueByPkey(String pkey);
	
	public PropertiesConfig findOneByPkey(String pkey);
}
