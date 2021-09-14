package com.yiban.rec.xingyi.controller;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.RecLogDetailsService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;
import com.yiban.rec.xingyi.service.YiBaoDuiZhangService;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

@Controller
@RequestMapping({"/yibaoduizhang"})
public class YiBaoDuiZhangController extends CurrentUserContoller {
  @Autowired
  private ReconciliationsService reconciliationsService;
  
  @Autowired
  private RecLogDetailsService recLogDetailsService;
  
  @Autowired
  private PropertiesConfigService propertiesConfigService;
  
  @Autowired
  private BillParseService billParseService;
  
  @Autowired
  private ThirdBillService thirdBillService;
  
  @Autowired
  private YiBaoDuiZhangService yiBaoDuiZhangService;
  
  @Autowired
  private MetaDataService metaDataService;
  
  @Autowired
  private GatherService gatherService;
  
  @RequestMapping({"/toyibaoduizhang"})
  public String toRefundTableUI(ModelMap model) {
    model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(this.metaDataService.NameAsList())));
    model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(this.gatherService.getOrgMap()));
    model.put("nowDate", DateUtil.getSpecifiedDayBefore(new Date()));
    return autoView("admin/yibaolog");
  }
  
  @GetMapping({"/getlog"})
  @ResponseBody
  public WebUiPage<Map<String, Object>> getlog(String orderDate) {
    String startDate = "";
    String endDate = "";
    if (!StringUtil.isEmpty(orderDate)) {
      startDate = orderDate.split("~")[0].trim();
      endDate = orderDate.split("~")[1].trim();
    } 
    PageRequest pagerequest = getRequestPageable();
    List<Map<String, Object>> data = this.yiBaoDuiZhangService.getYibaoLog(startDate, endDate);
    return new WebUiPage(data.size(), data);
  }
  
  @GetMapping({"/duizhang"})
  @ResponseBody
  public ResponseResult duizhang(String orgCode, String orderDate) {
    if (StringUtil.isEmpty(orderDate)) {
      orderDate = DateUtil.getSpecifiedDayBefore(new Date()) + "~" + DateUtil.getSpecifiedDayBefore(new Date());
    } else {
      orderDate = orderDate.split("~")[0].trim() + "~" + orderDate.split("~")[1].trim();
    } 
    String sd = orderDate.split("~")[0].trim();
    String ed = orderDate.split("~")[1].trim();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      for (int i = 0; i <= DateUtil.daysBetween2Date(sdf.parse(sd), sdf.parse(ed)); i++) {
        String time = DateUtil.getSpecifiedDayAfter(sd, Integer.valueOf(i));
        try {
          this.recLogDetailsService.deleteByOrderDateAndOrgCode(time, orgCode);
          try {
            this.yiBaoDuiZhangService.parse(orgCode, time);
          } catch (BillParseException e) {
            this.logger.error("拉取账单异常", (Throwable)e);
            return ResponseResult.failure("拉取账单异常"+ e.getMessage());
          } 
          this.reconciliationsService.compareHealthBill(orgCode, time);
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println(time + "对账失败原因--"+ e.getMessage());
        } 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return ResponseResult.success("操作成功");
  }
  
  private void getHisBill(String orgCode, String orderDate) {
    try {
      CharSequence yinyiBillFlag = this.propertiesConfigService.findValueByPkey("do-net.bill.url");
      if (StringUtils.isNotBlank(yinyiBillFlag))
        insertBillByYinYi(orgCode, orderDate); 
    } catch (Exception e) {
      this.logger.error("异常：{}", e);
    } 
  }
  
  private void insertBillByYinYi(String orgCode, String payDate) {
    HisChannelParaSendInfo hcpsi = new HisChannelParaSendInfo();
    hcpsi.setOrg_no(orgCode);
    hcpsi.setPay_type("");
    hcpsi.setTrade_code(EnumType.TRADE_CODE.getValue());
    hcpsi.setPay_date(payDate);
    String message = JsonUtil.bean2json(hcpsi);
    this.logger.info(""+ message);
    IPaymentService iPaymentService = null;
    try {
      iPaymentService = (new PaymentServiceLocator()).getBasicHttpBinding_IPaymentService();
    } catch (ServiceException e) {
      this.logger.error("", (Throwable)e);
    } 
    String result = null;
    try {
      result = iPaymentService.entrance(message);
    } catch (RemoteException e) {
      this.logger.error("", e);
    } 
    JSONObject jsonObject = JSONObject.fromObject(result);
    this.logger.info(""+ jsonObject.toString());
  }
}
