package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.vo.ExcepHandingRecordVo;
import com.yiban.rec.domain.vo.RefundRecordVo;

public interface RefundRecordService {
	
	public Page<ExcepHandingRecord> getExRecordData(RefundRecordVo revo,List<Organization> orgListTemp,Pageable pageable,User user);
	public List<ExcepHandingRecord> getExRecordDataNopage(RefundRecordVo revo,List<Organization> orgListTemp,Sort sort,User user);
	

	public ExcepHandingRecord rejectOrExamine(ExcepHandingRecordVo vo,User user) throws Exception;
	
	public int delete(Long id)throws Exception;
	
	
	public List<ExcepHandingRecord> details(Long id);
}
