package com.yiban.rec.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RecSummaryReportService {
  Page<Map<String, Object>> summary(Map<String, String> paramMap, PageRequest paramPageRequest);
  
  Page<Map<String, Object>> exceptionSummary(Map<String, String> paramMap, PageRequest paramPageRequest);
  
  List<Map<String, Object>> summaryAmount(Map<String, String> paramMap);
  
  Page<Map<String, Object>> shortDetail(Map<String, String> paramMap, PageRequest paramPageRequest);
  
  Page<Map<String, Object>> shortSummary(Map<String, String> paramMap, PageRequest paramPageRequest);
}
