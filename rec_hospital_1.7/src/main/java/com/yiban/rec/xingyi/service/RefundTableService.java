package com.yiban.rec.xingyi.service;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.xingyi.bean.RefundTable;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RefundTableService {
  ResponseResult insertRefundTable(RefundTable paramRefundTable);
  
  ResponseResult updateRefundTable(RefundTable paramRefundTable);
  
  ResponseResult deleteRefundTable(Long paramLong);
  
  void shenghetuifei(RefundTable paramRefundTable) throws Exception;
  
  Page<Map<String, Object>> getAllRefundTable(RefundTable paramRefundTable, String paramString1, PageRequest paramPageRequest, String paramString2, String paramString3);
}
