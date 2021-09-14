package com.yiban.rec.xingyi.service;

import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import java.util.List;
import java.util.Map;

public interface YiBaoDuiZhangService {
  void parse(String paramString1, String paramString2) throws BillParseException;
  
  List<Map<String, Object>> getYibaoLog(String paramString1, String paramString2);
}
