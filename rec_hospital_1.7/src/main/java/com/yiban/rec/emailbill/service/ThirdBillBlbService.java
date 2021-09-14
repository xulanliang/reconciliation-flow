package com.yiban.rec.emailbill.service;

import java.util.List;
import java.util.Map;

import com.yiban.rec.service.ReportSummaryService;

/**
 * @Description 仙桃医院有两种渠道账单数据，一份普通业务账单，一份病历本业务账单(his无该数据)，当前定制需求需要对病历本账单进行微信、支付宝汇总，
 *              这里采用复制表 third_bill_blb 存放病历本渠道账单，再从复制表汇总数据的方案，特此说明
 * @Author liugenlai
 * @Date 2020/5/12 17:55
 * @Version 1.0
 */
public interface ThirdBillBlbService {
    /**
     * 复制表汇总病历本数据
     * @param query
     * @return
     */
    List<Map<String, Object>> findAllOfSummaryByPayType(ReportSummaryService.SummaryQuery query);
}
