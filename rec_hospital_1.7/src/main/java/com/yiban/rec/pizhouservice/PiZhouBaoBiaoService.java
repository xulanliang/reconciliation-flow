package com.yiban.rec.pizhouservice;

import java.util.List;
import java.util.Map;

import com.yiban.rec.service.ReportSummaryService;

public interface PiZhouBaoBiaoService {
	List<Map<String,Object>> findPiZhouBaoBiao(ReportSummaryService.SummaryQuery query,String selectType);
	List<Map<String,Object>> findPiZhouBaoBiaoSummary(ReportSummaryService.SummaryQuery query,String selectType);
}
