package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import com.yiban.rec.domain.reports.AdvancePaySummaryDetailListVo;
import com.yiban.rec.domain.reports.AdvancePaySummaryListVo;

/**
 * 预收款汇总
 */
public interface AdvancePaySummaryService {

    /**
     * 预收款汇总报表列表展示
     * @param queryListVo
     * @return
     */
    List<Map<String, Object>> findAdvancePaySummaryByParams(AdvancePaySummaryListVo queryListVo);

    List<Map<String, Object>> findAdvancePaySummaryDetailListByParams(AdvancePaySummaryDetailListVo paySummaryDetailListVo);

    List<Map<String, Object>> getBillSourceDataList();

}
