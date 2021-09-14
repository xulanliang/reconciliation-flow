package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.dao.MixRefundDao;
import com.yiban.rec.dao.MixRefundDetailsDao;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.BlendRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.BlendRefundService;
import com.yiban.rec.service.MixRefundDetailsService;

@Service
public class MixRefundDetailsServiceImpl implements MixRefundDetailsService {
	
	@Autowired
	private MixRefundDetailsDao mixRefundDetailsDao;
	
	@Autowired
	private MixRefundDao mixRefundDao;
	
	@Autowired
	private BlendRefundService blendRefundService;
	
	
	
	public List<List<MixRefundDetails>> mixRefundDetailsData(BlendRefundVo vo){
		List<List<MixRefundDetails>> list=new ArrayList<>();
				
		List<MixRefundDetails> unRefund = mixRefundDetailsDao.findByrefundOrderNo(vo.getRefundOrderNo());
		List<MixRefundDetails> refund = mixRefundDetailsDao.findByrefundOrderNoAndRefundState(vo.getRefundOrderNo(),RefundEnumType.REFUND_NO.getId());
		list.add(unRefund);
		list.add(refund);
		return list;
	}


	@Override
	public ResponseVo retryApply(String refundOrderNo, Long id) {
		MixRefund mixRefund = mixRefundDao.findByRefundOrderNo(refundOrderNo);
		List<MixRefundDetails> list=new ArrayList<>();
		list.add(mixRefundDetailsDao.findOne(id));
		ResponseVo refundRec = blendRefundService.refund(mixRefund, list, mixRefund.getSettlementType());
		if(!refundRec.resultSuccess()) return refundRec;
		//更新数据
		mixRefundDetailsDao.save(list);
		return refundRec;
	}
	
}
