package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.basicInfo.CycleType;

public interface RecCommonService {
	List<CycleType> findCycleTypesByType(Integer type);
}
