package com.yiban.rec.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.rec.domain.vo.HealthExceptionVo;

public interface HealthBillService {

	public Map<String, Map<String, Object>> getCount(String orgNo,String orgCode,String startDate,String endDate,String payNo,String orderState, String dataSource);

	public Page<Map<String, Object>> healthException(HealthExceptionVo vo,PageRequest pageable);
}
