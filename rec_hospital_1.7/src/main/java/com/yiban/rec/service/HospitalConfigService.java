package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.HospitalConfiguration;
import com.yiban.rec.domain.vo.AppRuntimeConfig;

public interface HospitalConfigService {

	void save(HospitalConfiguration config);

	void batchSave(List<HospitalConfiguration> list);

	void delteById(Long id);

	AppRuntimeConfig loadConfig();

	void delteAll();

}
