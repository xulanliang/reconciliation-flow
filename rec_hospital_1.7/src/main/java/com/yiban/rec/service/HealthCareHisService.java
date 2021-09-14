package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.vo.HealthCareDetailQueryVo; 

public interface HealthCareHisService {
	
	// 获取医保中心明细
	public Page<HealthCareHis> getHealthCareHisPage(HealthCareDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);
	public List<HealthCareHis> getHealthCareHisList(HealthCareDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable page);

	public HealthCareHis findByPayFlowNo(String payFlowNo);
	
	public void saveOrUpdate(HealthCareHis hch);
}
