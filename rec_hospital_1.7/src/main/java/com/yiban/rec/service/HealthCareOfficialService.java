package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.vo.HealthCareDetailQueryVo; 

public interface HealthCareOfficialService {
	
	// 获取医保中心明细
	public Page<HealthCareOfficial> getHealthCareOfficialPage(HealthCareDetailQueryVo cqvo,Pageable pageable);
	public List<HealthCareOfficial> getHealthCareOfficialList(HealthCareDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable page);

	public HealthCareOfficial findByPayFlowNo(String payFlowNo);
	
	public void saveOrUpdate(HealthCareOfficial hco);
}
