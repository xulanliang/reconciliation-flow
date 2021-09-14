package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.vo.PatTypeSummaryVo;

public interface PatTypeSummaryService {

	public List<PatTypeSummaryVo> count(String orgCode, String date,String endTime);
}
