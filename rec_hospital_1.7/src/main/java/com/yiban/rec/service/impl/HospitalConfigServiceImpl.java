package com.yiban.rec.service.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.dao.HospitalConfigDao;
import com.yiban.rec.domain.HospitalConfiguration;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.base.BaseOprService;

@Service
public class HospitalConfigServiceImpl extends BaseOprService implements HospitalConfigService {
	@Autowired
	private HospitalConfigDao hospitalConfigDao;

	@Transactional
	@Override
	public void save(HospitalConfiguration config) {
		delteAll();
		hospitalConfigDao.save(config);

	}

	@Transactional
	@Override
	public void batchSave(List<HospitalConfiguration> list) {
		delteAll();
		hospitalConfigDao.save(list);
	}

	@Transactional
	@Override
	public void delteAll() {
		hospitalConfigDao.deleteAll();

	}

	@Transactional
	@Override
	public void delteById(Long id) {
		hospitalConfigDao.delete(id);

	}

	@Override
	public AppRuntimeConfig loadConfig() {
	    final List<String> igns = Arrays.asList(new String[] {"orgCode","isBillsSources",
	            "isOutpatient","isHealthAccount","healthCheckWays","checkWays"});
		AppRuntimeConfig dict = new AppRuntimeConfig();
		Class<AppRuntimeConfig> c = AppRuntimeConfig.class;
		List<HospitalConfiguration> configList = hospitalConfigDao.findByActive(1);
		for (HospitalConfiguration config : configList) {
			String fileName = config.getKeyWord();
			String fileValue = config.getKeyValue();
			if(igns.contains(fileName)) {
			    continue;
			}
			if(StringUtils.isNotBlank(fileValue)){
				Field f;
				try {
					f = c.getDeclaredField(fileName);
					f.setAccessible(true);
					String className = f.getType().getName();
					if (className.equals(String.class.getName())) {
						f.set(dict, fileValue);
					} else if (className.equals(Integer.class.getName())) {
						f.set(dict, Integer.parseInt(fileValue));
					} else if (className.equals(Double.class.getName())) {
						f.set(dict, Double.parseDouble(fileValue));
					} else if (className.equals(Float.class.getName())) {
						f.set(dict, Float.parseFloat(fileValue));
					} else if (className.equals(Boolean.class.getName())) {
						f.set(dict, Boolean.parseBoolean(fileValue));
					}
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return dict;

	}

}
