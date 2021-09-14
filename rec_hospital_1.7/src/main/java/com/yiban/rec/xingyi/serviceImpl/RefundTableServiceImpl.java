package com.yiban.rec.xingyi.serviceImpl;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.xingyi.bean.RefundTable;
import com.yiban.rec.xingyi.dao.RefundTableDao;
import com.yiban.rec.xingyi.service.RefundTableService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundTableServiceImpl extends BaseOprService implements RefundTableService {
  @Autowired
  private RefundTableDao refundTableDao;
  
  @Autowired
  private PropertiesConfigService propertiesConfigService;
  
  @Transactional
  public ResponseResult insertRefundTable(RefundTable vo) {
    try {
      this.refundTableDao.save(vo);
      return ResponseResult.success();
    } catch (Exception e) {
      return ResponseResult.failure(e.getMessage());
    } 
  }
  
  @Transactional
  public ResponseResult updateRefundTable(RefundTable vo) {
    try {
      this.refundTableDao.save(vo);
      return ResponseResult.success();
    } catch (Exception e) {
      return ResponseResult.failure();
    } 
  }
  
  @Transactional
  public ResponseResult deleteRefundTable(Long id) {
    try {
      this.refundTableDao.delete(id);
      return ResponseResult.success();
    } catch (Exception e) {
      return ResponseResult.failure();
    } 
  }
  
  @Transactional(rollbackFor = {Exception.class})
  public void shenghetuifei(RefundTable vo) throws Exception {
    String payCenterUrl = this.propertiesConfigService.findValueByPkey("pay.center.url", (String)ProConstants.DEFAULT
        .get("pay.center.url"));
    String url = payCenterUrl + "/order/refund";
    this.logger.info("请求退费地址："+ url);
    Map<String, String> map = new HashMap<>();
    map.put("orgCode", "12328");
    map.put("tsn", vo.getPayFlowNo());
    map.put("batchRefundNo", UUID.randomUUID().toString().replaceAll("-", ""));
    map.put("refundAmount", vo.getRefundAmount());
    map.put("reason", vo.getReason());
    JSONObject jsonObject = JSONObject.fromObject(map);
    this.logger.info("请款请求入参:"+ jsonObject.toString());
    String result = (new RestUtil()).doPostJson(url, jsonObject.toString(), "utf-8");
    this.logger.info("退费结果："+ result);
    JSONObject json = JSONObject.fromObject(result);
    if (!json.getBoolean("success")) {
      Exception exception = new Exception(json.getString("message"));
      throw exception;
    } 
    this.refundTableDao.save(vo);
  }
  
  public Page<Map<String, Object>> getAllRefundTable(RefundTable refundTable, String rangeTime, PageRequest pageRequest, String sort, String order) {
    String startTime = rangeTime.split("~")[0].trim();
    String endTime = rangeTime.split("~")[1].trim();
    StringBuilder sql = new StringBuilder();
    sql.append("select id,pay_flow_no,pay_type,refund_amount,reason,refund_state,shenghe_reason,shenghe_people,request_time,shenghe_time,request_people ");
    sql.append("from refund_table ");
    sql.append("where 1=1 ");
    if (!StringUtil.isEmpty(refundTable.getPayFlowNo()))
      sql.append("and pay_flow_no ='").append(refundTable.getPayFlowNo()).append("' "); 
    sql.append("and request_time>='").append(startTime).append("' ");
    sql.append("and request_time<='").append(endTime).append("' ");
    sql.append("order by request_time desc ");
    this.logger.info(sql.toString());
    return handleNativeSql(sql.toString(), pageRequest, new String[] { 
          "id", "payFlowNo", "payType", "refundAmount", "reason", "refundState", "shengheReason", "shenghePeople", "requestTime", "shengheTime", 
          "requestPeople" });
  }
}
