package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

public interface BillDataReportService {

    List<Map<String,Object>> findAllBillAndHisDataByDate(ReportSummaryService.SummaryQuery query);

}
