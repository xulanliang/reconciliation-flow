package com.yiban.rec.service;

import com.yiban.rec.domain.CitizenCardBill;
import com.yiban.rec.domain.vo.CitizenCardBillVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface CitizenCardBillService {

	Page<CitizenCardBill> findPage(CitizenCardBillVo citizenCardBillVo, PageRequest pageable);

	List<Map<String, Object>> summary(CitizenCardBillVo citizenCardBillVo);
	
}
