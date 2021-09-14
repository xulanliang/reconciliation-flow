package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.BlendRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;

public interface MixRefundDetailsService {

	
	public List<List<MixRefundDetails>> mixRefundDetailsData(BlendRefundVo vo);
	
	public ResponseVo retryApply(String refundOrderNo,Long id);
	
}
