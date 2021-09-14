package com.yiban.rec.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.dao.RecCommonDao;
import com.yiban.rec.domain.basicInfo.CycleType;
import com.yiban.rec.service.RecCommonService;
import com.yiban.rec.service.base.BaseOprService;

@Service
@Transactional(readOnly = true)
public class RecCommonServiceImpl extends BaseOprService implements RecCommonService {

	@Autowired
	private RecCommonDao recCommonDao;

	@Override
	public List<CycleType> findCycleTypesByType(Integer type) {
		return recCommonDao.findCycleTypesByType(type);
	}

}
