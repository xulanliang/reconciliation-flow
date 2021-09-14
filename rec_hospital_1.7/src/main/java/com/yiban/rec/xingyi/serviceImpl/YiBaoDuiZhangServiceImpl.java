package com.yiban.rec.xingyi.serviceImpl;

import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.rec.bill.parse.dao.ParseTaskConfigDao;
import com.yiban.rec.bill.parse.domain.ParseTaskConfig;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParserable;
import com.yiban.rec.bill.parse.service.standardbill.HisReportSummaryService;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.xingyi.service.YiBaoDuiZhangService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class YiBaoDuiZhangServiceImpl extends BaseOprService implements YiBaoDuiZhangService {
  private final Logger log = LoggerFactory.getLogger(getClass());
  
  @Autowired
  private ParseTaskConfigDao parseTaskConfigDao;
  
  private Map<String, BillParserable> parserMap = new HashMap<>();
  
  @Autowired
  private EntityManager entityManager;
  
  @Autowired
  private RecLogDao recLogDao;
  
  @Autowired
  private OrganizationService organizationService;
  
  @Autowired
  private HisReportSummaryService hisReportSummaryService;
  
  private ExecutorService executor = Executors.newCachedThreadPool();
  
  private Map<String, BillParserable> initTask() throws Exception {
    this.parserMap.clear();
    List<ParseTaskConfig> taskConfigList = this.parseTaskConfigDao.findByActive(ActiveEnum.YES.getValue());
    List<String> classNameList = new ArrayList<>();
    classNameList.add("com.yiban.rec.bill.parse.service.hospital.xingyi.XingYiYiBaoBillHisParser");
    classNameList.add("com.yiban.rec.bill.parse.service.hospital.xingyi.XingYibaoBillParser");
    int i = 0;
    for (String className : classNameList) {
      Class<?> c = null;
      if (StringUtils.isNotBlank(className))
        try {
          c = Class.forName(className);
          if (c != null) {
            c.newInstance();
            BillParserable service = (BillParserable)c.newInstance();
            this.parserMap.put("yibao," + i++, service);
          } 
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
          throw e;
        }  
    } 
    return this.parserMap;
  }
  
  @Transactional
  public void parse(String orgCode, String date) throws BillParseException {
    RecLog recLog = this.recLogDao.findByOrderDateAndOrgCode(date, orgCode);
    if (null == recLog)
      recLog = new RecLog(); 
    recLog.setRecResult(Integer.valueOf(71));
    recLog.setCreatedDate(DateUtil.getCurrentDateTime());
    recLog.setOrgCode(orgCode);
    recLog.setOrderDate(date);
    try {
      initTask();
    } catch (Exception e1) {
      this.log.error("初始化医保解析异常,{}", e1);
    } 
    try {
      for (Map.Entry<String, BillParserable> entry : this.parserMap.entrySet())
        ((BillParserable)entry.getValue()).parse(orgCode, date, this.entityManager, ((String)entry.getKey()).split(",")[0]); 
    } catch (BillParseException e) {
      recLog.setRecResult(Integer.valueOf(70));
      this.log.error("拉取医保异常，{}", e.getMessage());
      throw e;
    } finally {
      //this.executor.execute((Runnable)new Object(this, orgCode, date));
      //this.executor.execute((Runnable)new Object(this, orgCode, date));
      this.recLogDao.saveAndFlush(recLog);
    } 
  }
  
  public List<Map<String, Object>> getYibaoLog(String startDate, String endDate) {
    StringBuffer sql = new StringBuffer();
    sql.append("select id,order_date,org_code,DATE_FORMAT(created_date,'%Y-%m-%d %H:%i:%s'),rec_result from t_rec_log ");
    sql.append("where 1=1 ");
    if (!StringUtil.isEmpty(startDate))
      sql.append("and order_date>='").append(startDate).append("' "); 
    if (!StringUtil.isEmpty(endDate))
      sql.append("and order_date<='").append(endDate).append("' "); 
    sql.append("order by order_date desc ");
    return handleNativeSqlColumns(sql.toString(), new String[] { "id", "orderDate", "orgCode", "createdDate", "recResult" });
  }
}
