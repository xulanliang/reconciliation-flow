package com.yiban.rec.xingyi.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;

public interface BaoBiaoService {
  List<Map<String, Object>> getList(PageRequest paramPageRequest, String paramString1, String paramString2);
}
