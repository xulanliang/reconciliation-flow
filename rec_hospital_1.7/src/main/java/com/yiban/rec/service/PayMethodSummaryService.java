package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.vo.PayMethodSummaryVo;



public interface PayMethodSummaryService {

	public List<PayMethodSummaryVo> count(String orgCode, String date,String endTime);
}
