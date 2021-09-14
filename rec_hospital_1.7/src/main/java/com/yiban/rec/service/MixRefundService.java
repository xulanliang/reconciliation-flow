package com.yiban.rec.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.vo.BlendRefundVo;

public interface MixRefundService {

	
	public Page<MixRefund> fundData(BlendRefundVo vo,List<Organization> orgListTemp,Pageable pageable);
}
