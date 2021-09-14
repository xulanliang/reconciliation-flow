package com.yiban.rec.xingyi.controller;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.xingyi.bean.Result;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/duankaunbutton"})
public class DuanKuanButtonController extends CurrentUserContoller {
  private static final JdbcTemplate jdbcTemplate = getConnect();
  
  public static JdbcTemplate getConnect() {
    String driverName = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://192.168.100.172:3309/xingyi?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&autoReconnect=true&failOverReadOnly=false&useSSL=false";
    String username = "root";
    String password = "Clear123";
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverName);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return new JdbcTemplate((DataSource)dataSource);
  }
  
  @PostMapping({"/getinfo"})
  @ResponseBody
  public Result getInfo(String payFLowNo) {
    List<ExcepHandingRecord> excepHandingRecordList = findByPaymentRequestFlow(payFLowNo);
    if (excepHandingRecordList.size() > 0)
      return Result.success(null); 
    return Result.fail(null);
  }
  
  public List<ExcepHandingRecord> findByPaymentRequestFlow(String payFlowNo) {
    StringBuffer sql = new StringBuffer();
    sql.append("select id,Trade_Amount from t_exception_handling_record t where t.Payment_Request_Flow ='").append(payFlowNo).append("' ");
    sql.append("and (t.father_id=0 or t.father_id is null) ");
    this.logger.info("查询："+ sql.toString());
    List<ExcepHandingRecord> vos = jdbcTemplate.query(sql.toString(), (RowMapper)this);
    return vos;
  }
}
