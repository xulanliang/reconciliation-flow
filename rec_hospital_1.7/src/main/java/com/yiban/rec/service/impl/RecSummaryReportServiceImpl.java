package com.yiban.rec.service.impl;

import com.yiban.rec.service.RecSummaryReportService;
import com.yiban.rec.service.base.BaseOprService;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RecSummaryReportServiceImpl extends BaseOprService implements RecSummaryReportService {
  public Page<Map<String, Object>> summary(Map<String, String> vo, PageRequest pageable) {
    StringBuffer sbuf = new StringBuffer();
    String startDate = vo.get("startDate");
    String endDate = vo.get("endDate");
    String billSource = vo.get("billSource");
    sbuf.append("SELECT b.*,IFNULL(c.hisAmount,0),IFNULL(d.thridAmount,0) FROM (");
    sbuf.append(" SELECT tradeDate,billSource,payType,SUM(hisPayAmount),SUM(thridPayAmount),SUM(longAmount),-SUM(shortAmount),SUM(longAmount)+SUM(shortAmount) FROM (");
    sbuf.append(" select trade_date tradeDate,bill_source billSource,rec_pay_type payType,");
    sbuf.append(" SUM( IF ( data_source = 'his', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) hisPayAmount,");
    sbuf.append(" SUM( IF ( data_source = 'third', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) thridPayAmount,");
    sbuf.append(" 0 longAmount,0 shortAmount");
    sbuf.append(" from t_follow_summary");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY trade_date,bill_source,rec_pay_type");
    sbuf.append(" UNION ALL");
    sbuf.append(" SELECT trade_date tradeDate,bill_source billSource,Pay_Name payType,0 hisPayAmount,0 thridPayAmount,");
    sbuf.append(" SUM( case when check_state = 2 then 0 ELSE ABS(trade_amount) END) longAmount,");
    sbuf.append(" SUM( case when check_state = 3 then 0 ELSE ABS(trade_amount) END) shortAmount");
    sbuf.append(" from t_trade_check_follow t ");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY trade_date,bill_source,Pay_Name");
    sbuf.append(" ) a");
    sbuf.append(" group BY tradeDate,billSource,payType");
    sbuf.append(" )b");
    sbuf.append(" LEFT JOIN (");
    sbuf.append(" SELECT trade_date,SUM( CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END) hisAmount FROM t_follow_summary ");
    sbuf.append(" WHERE data_source='his' ");
    sbuf.append(" AND trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY trade_date");
    sbuf.append(" ) c ON c.trade_date = b.tradeDate");
    sbuf.append(" LEFT JOIN (");
    sbuf.append(" SELECT trade_date,bill_source,SUM( CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END) thridAmount FROM t_follow_summary ");
    sbuf.append(" WHERE data_source='third'");
    sbuf.append(" AND trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY trade_date,bill_source");
    sbuf.append(" ) d ON d.trade_date = b.tradeDate AND d.bill_source = b.billSource");
    sbuf.append(" order BY tradeDate desc,billSource,payType");
    return handleNativeSql(sbuf.toString(), pageable, new String[] { "tradeDate", "billSource", "payType", "hisPayAmount", "thridPayAmount", "longAmount", "shortAmount", "handleAmount", "hisAmount", "thirdAmount" });
  }
  
  public Page<Map<String, Object>> exceptionSummary(Map<String, String> vo, PageRequest pageable) {
    String startDate = vo.get("startDate");
    String endDate = vo.get("endDate");
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(" SELECT tradeDate,billSource,payType,SUM(longAmount),SUM(shortAmount),SUM(unHandleLongAmount),SUM(handleLongAmount),");
    sbuf.append(" SUM(unHandleShortAmount),SUM(handleShortAmount),SUM(todayHandleLongAmount),SUM(todayHandleShortAmount),SUM(hisPayAmount),SUM(thridPayAmount),");
    sbuf.append(" SUM(afterHandleLongAmount)+SUM(unHandleLongAmount) historyUnHandleLongAmount,SUM(afterHandleShortAmount)+SUM(unHandleShortAmount) historyUnHandleShortAmount, ");
    sbuf.append(" SUM(todayLongAmount) todayLongAmount,SUM(todayShortAmount) todayShortAmount,");
    sbuf.append(" SUM(todayLongAmount) - SUM(todayHandleLongAmount) todayHistoryLongAmount,SUM(todayShortAmount) - SUM(todayHandleShortAmount) todayHistoryShortAmount,");
    sbuf.append(" SUM(longAmount)+SUM(shortAmount) allAmount, SUM(todayHandleLongAmount)+SUM(todayHandleShortAmount) todayHandleAmount,");
    sbuf.append(" SUM(afterHandleLongAmount)+SUM(unHandleLongAmount) + SUM(afterHandleShortAmount)+SUM(unHandleShortAmount) historyUnHandleAmount, ");
    sbuf.append(" SUM(todayLongAmount)+ SUM(todayShortAmount) todayAmount,");
    sbuf.append(" SUM(todayLongAmount) - SUM(todayHandleLongAmount) + SUM(todayShortAmount) - SUM(todayHandleShortAmount) todayHistoryAmount");
    sbuf.append(" FROM ( ");
    sbuf.append(" SELECT trade_date tradeDate,t.bill_source billSource,t.pay_name payType, ");
    sbuf.append(" SUM( case when check_state = 2 then 0 ELSE ABS(t.trade_amount) END) longAmount,");
    sbuf.append(" SUM( case when check_state = 3 then 0 ELSE ABS(t.trade_amount) END) shortAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 2 THEN 0 WHEN td.exception_state IS NULL AND te.state IS NULL THEN ABS(t.trade_amount) ELSE 0 END) unHandleLongAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 2 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 THEN ABS(t.trade_amount) ELSE 0 END) handleLongAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 3 THEN 0 WHEN td.exception_state IS NULL AND te.state IS NULL THEN ABS(t.trade_amount) ELSE 0 END) unHandleShortAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 3 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 THEN ABS(t.trade_amount) ELSE 0 END) handleShortAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 2 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 AND date_format(te.handle_date_time,'%Y-%m-%d') = DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 AND date_format(td.created_date,'%Y-%m-%d') = DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) ELSE 0 END) todayHandleLongAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 3 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 AND date_format(te.handle_date_time,'%Y-%m-%d') = DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 AND date_format(td.created_date,'%Y-%m-%d') = DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) ELSE 0 END) todayHandleShortAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 2 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 AND date_format(te.handle_date_time,'%Y-%m-%d') > DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 AND date_format(td.created_date,'%Y-%m-%d') > DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) ELSE 0 END) afterHandleLongAmount,");
    sbuf.append(" SUM( CASE WHEN check_state = 3 THEN 0 WHEN td.exception_state = 11 AND te.state = 3 AND date_format(te.handle_date_time,'%Y-%m-%d') > DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) WHEN td.exception_state IS NOT NULL AND td.exception_state != 11 AND date_format(td.created_date,'%Y-%m-%d') > DATE_ADD(t.trade_date,INTERVAL 1 DAY) THEN ABS(t.trade_amount) ELSE 0 END) afterHandleShortAmount,");
    sbuf.append(" 0 hisPayAmount,0 thridPayAmount,0 todayLongAmount,0 todayShortAmount");
    sbuf.append(" from t_trade_check_follow t ");
    sbuf.append(" LEFT JOIN t_trade_check_follow_deal td ON td.pay_flow_no = t.business_no");
    sbuf.append(" LEFT JOIN t_exception_handling_record te ON te.Payment_Request_Flow = t.business_no AND (ISNULL(te.father_id) or te.father_id=0) AND te.state = 3 ");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    sbuf.append(" GROUP BY t.trade_date,t.bill_source,t.Pay_Name ");
    sbuf.append(" UNION ALL");
    sbuf.append(" SELECT trade_date tradeDate,bill_source billSource,pay_type payType,0 longAmount,0 shortAmount,0 unHandleLongAmount,0 handleLongAmount,");
    sbuf.append(" 0 unHandleShortAmount,0 handleShortAmount,0 todayHandleLongAmount,0 todayHandleShortAmount, 0 afterHandleLongAmount,0 afterHandleShortAmount,");
    sbuf.append(" SUM( IF ( data_source = 'his', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) hisPayAmount,");
    sbuf.append(" SUM( IF ( data_source = 'third', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) thridPayAmount, ");
    sbuf.append(" 0 todayLongAmount,0 todayShortAmount");
    sbuf.append(" FROM t_follow_summary tf  ");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    sbuf.append(" GROUP BY trade_date ,bill_source,pay_type ");
    sbuf.append(" UNION ALL ");
    sbuf.append(" SELECT CASE WHEN td.exception_state = 11 AND te.state IS NOT NULL THEN date_format(te.handle_date_time,'%Y-%m-%d') ELSE date_format(td.created_date,'%Y-%m-%d') END tradeDate,");
    sbuf.append(" t.bill_source billSource,t.pay_name payType,");
    sbuf.append(" 0 longAmount,0 shortAmount,0 unHandleLongAmount,0 handleLongAmount,0 unHandleShortAmount,0 handleShortAmount,0 todayHandleLongAmount,");
    sbuf.append(" 0 todayHandleShortAmount, 0 afterHandleLongAmount,0 afterHandleShortAmount,0 hisPayAmount,0 thridPayAmount,");
    sbuf.append(" SUM( case when check_state = 2 then 0 ELSE ABS(t.trade_amount) END) todayLongAmount,");
    sbuf.append(" SUM( case when check_state = 3 then 0 ELSE ABS(t.trade_amount) END) todayShortAmount");
    sbuf.append(" FROM t_trade_check_follow_deal td");
    sbuf.append(" LEFT JOIN t_exception_handling_record te ON te.Payment_Request_Flow = td.pay_flow_no AND (ISNULL(te.father_id) or te.father_id=0) AND te.state = 3 ");
    sbuf.append(" LEFT JOIN t_trade_check_follow t ON td.pay_flow_no = t.business_no");
    sbuf.append(" WHERE t.business_no IS NOT NULL");
    sbuf.append(" AND ((td.exception_state = 11 AND te.state = 3) OR td.exception_state != 11) ");
    sbuf.append(" AND ((td.exception_state = 11 AND te.state IS NOT NULL AND date_format(te.handle_date_time,'%Y-%m-%d') >= '").append(startDate).append("' ");
    sbuf.append(" AND date_format(te.handle_date_time,'%Y-%m-%d') <= '").append(endDate).append("') ");
    sbuf.append(" OR (td.exception_state != 11 AND date_format(td.created_date,'%Y-%m-%d') >= '").append(startDate).append("' ");
    sbuf.append(" AND date_format(td.created_date,'%Y-%m-%d') <= '").append(endDate).append("')) ");
    sbuf.append(" GROUP BY tradeDate,billSource,payType");
    sbuf.append(" ) a ");
    sbuf.append(" GROUP BY a.tradeDate,billSource,payType");
    sbuf.append(" order by a.tradeDate desc,billSource,payType");
    return handleNativeSql(sbuf.toString(), pageable, new String[] { 
          "tradeDate", "billSource", "payType", "longAmount", "shortAmount", "unHandleLongAmount", 
          "handleLongAmount", "unHandleShortAmount", "handleShortAmount", "todayHandleLongAmount", 
          "todayHandleShortAmount", "hisPayAmount", 
          "thridPayAmount", "historyUnHandleLongAmount", "historyUnHandleShortAmount", "todayLongAmount", "todayShortAmount", "todayHistoryLongAmount", "todayHistoryShortAmount", 
          "allAmount", 
          "todayHandleAmount", "historyUnHandleAmount", "todayAmount", "todayHistoryAmount" });
  }
  
  public List<Map<String, Object>> summaryAmount(Map<String, String> vo) {
    String startDate = vo.get("startDate");
    String endDate = vo.get("endDate");
    String billSource = vo.get("billSource");
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(" SELECT '合计',SUM(hisAmount),SUM(thridAmount),SUM(hisAmount) hisPayAmount,SUM(thridAmount) thirdPayAmount,SUM(longAmount),SUM(shortAmount),SUM(longAmount)+SUM(shortAmount) FROM(");
    sbuf.append(" SELECT bill_source,");
    sbuf.append(" SUM( IF ( data_source = 'his', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) hisAmount,");
    sbuf.append(" SUM( IF ( data_source = 'third', CASE order_state WHEN '0156' THEN ABS(`pay_amount`) ELSE - ABS(`pay_amount`) END, 0 )) thridAmount,");
    sbuf.append(" 0 longAmount,0 shortAmount");
    sbuf.append(" FROM t_follow_summary ");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY bill_source ");
    sbuf.append(" UNION ALL");
    sbuf.append(" SELECT bill_source,0 hisAmount,0 thridAmount,");
    sbuf.append(" SUM( case when check_state = 2 then 0 ELSE ABS(trade_amount) END) longAmount,");
    sbuf.append(" SUM( case when check_state = 3 then 0 ELSE ABS(trade_amount) END) shortAmount");
    sbuf.append(" from t_trade_check_follow");
    sbuf.append(" WHERE trade_date >= '").append(startDate).append("'");
    sbuf.append(" AND trade_date <= '").append(endDate).append("' ");
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND bill_source = '").append(billSource).append("' "); 
    sbuf.append(" GROUP BY bill_source ");
    sbuf.append(" ) a");
    sbuf.append(" GROUP BY bill_source");
    List<Map<String, Object>> list = handleNativeSql(sbuf.toString(), new String[] { "tradeDate", "hisAmount", "thirdAmount", "hisPayAmount", "thridPayAmount", "longAmount", "shortAmount", "handleAmount" });
    return list;
  }
  
  public Page<Map<String, Object>> shortDetail(Map<String, String> vo, PageRequest pageable) {
    String tradeDate = vo.get("tradeDate");
    String payType = vo.get("payType");
    String billSource = vo.get("billSource");
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(" SELECT t.bill_source billSource,t.Pay_Name payType,t.business_no businessNo,t.trade_date refundDate,DATE_FORMAT(tt.Trade_datatime,'%Y-%m-%d') payDate,");
    sbuf.append(" t.trade_amount refundAmount,tt.Pay_Amount payAmount,CASE WHEN t.rec_his_id IS NOT NULL THEN 'HIS' ELSE '渠道' END ");
    sbuf.append(" FROM t_trade_check_follow t ");
    sbuf.append(" LEFT JOIN t_thrid_bill tt ON (tt.Pay_Flow_No = t.business_no OR tt.shop_flow_no = t.business_no) AND tt.Order_State = '0156' AND t.check_state=2 ");
    sbuf.append(" WHERE t.check_state = 2 ");
    if (StringUtils.isNotBlank(payType))
      sbuf.append(" AND t.Pay_Name = '").append(payType).append("' "); 
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND t.bill_source = '").append(billSource).append("' "); 
    if (StringUtils.isNotBlank(tradeDate))
      sbuf.append(" AND t.trade_date = '").append(tradeDate).append("' "); 
    return handleNativeSql(sbuf.toString(), pageable, new String[] { "billSource", "payType", "businessNo", "refundDate", "payDate", "refundAmount", "payAmount", "type" });
  }
  
  public Page<Map<String, Object>> shortSummary(Map<String, String> vo, PageRequest pageable) {
    String tradeDate = vo.get("tradeDate");
    String payType = vo.get("payType");
    String billSource = vo.get("billSource");
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(" SELECT t.bill_source billSource,t.Pay_Name payType,t.business_no businessNo,t.trade_date refundDate,DATE_FORMAT(tt.Trade_datatime,'%Y-%m-%d') payDate,");
    sbuf.append(" sum(t.trade_amount) refundAmount,sum(tt.Pay_Amount) payAmount,CASE WHEN t.rec_his_id IS NOT NULL THEN 'HIS' ELSE '渠道' END ");
    sbuf.append(" FROM t_trade_check_follow t ");
    sbuf.append(" LEFT JOIN t_thrid_bill tt ON (tt.Pay_Flow_No = t.business_no OR tt.shop_flow_no = t.business_no) AND tt.Order_State = '0156' AND t.check_state=2 ");
    sbuf.append(" WHERE t.check_state = 2 ");
    if (StringUtils.isNotBlank(payType))
      sbuf.append(" AND t.Pay_Name = '").append(payType).append("' "); 
    if (StringUtils.isNotBlank(billSource))
      sbuf.append(" AND t.bill_source = '").append(billSource).append("' "); 
    if (StringUtils.isNotBlank(tradeDate))
      sbuf.append(" AND t.trade_date = '").append(tradeDate).append("' "); 
    sbuf.append(" GROUP BY payDate ");
    return handleNativeSql(sbuf.toString(), pageable, new String[] { "billSource", "payType", "businessNo", "refundDate", "payDate", "refundAmount", "payAmount", "type" });
  }
}
