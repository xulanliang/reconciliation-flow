package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.AllRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;

public interface BlendRefundService {

	
	public ResponseVo BlendRefund(AllRefundVo vo) throws Exception;
	
	public ResponseVo refund(MixRefund mixRefund,List<MixRefundDetails> list,String type);
}
