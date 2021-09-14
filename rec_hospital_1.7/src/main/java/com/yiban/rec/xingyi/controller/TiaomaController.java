package com.yiban.rec.xingyi.controller;

import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.util.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/tiaoma"})
public class TiaomaController {
  @Autowired
  private ThirdBillService thirdBillService;
  
  @GetMapping({"/selectTiaoma"})
  @ResponseBody
  public Map<String, String> selectTiaoma(String payFlowNo) {
    List<ThirdBill> thirdBillList = this.thirdBillService.findByPayFlowNo(payFlowNo);
    String tiaomahao = "";
    String path = "";
    if (thirdBillList.size() > 0) {
      String shopflowno = ((ThirdBill)thirdBillList.get(0)).getShopFlowNo();
      if (!StringUtil.isEmpty(shopflowno)) {
        tiaomahao = shopflowno;
        path = "http://192.168.100.173:7070/image/" + shopflowno + ".jpg";
      } 
    } 
    Map<String, String> map = new HashMap<>();
    map.put("tiaomahao", tiaomahao);
    map.put("path", path);
    return map;
  }
}
