package com.yiban.rec.xingyi.controller;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.xingyi.bean.RefundTable;
import com.yiban.rec.xingyi.service.RefundTableService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/refundTable"})
public class RefundTableController extends CurrentUserContoller {
  @Autowired
  private RefundTableService refundTableService;
  
  @RequestMapping({"/toRefundTable"})
  public String toRefundTableUI(ModelMap model) {
    model.put("nowDate", DateUtil.getCurrentDateString());
    return autoView("admin/refundTable");
  }
  
  @GetMapping({"/getAllRefundTable"})
  @ResponseBody
  public WebUiPage<Map<String, Object>> getAllRefundTable(RefundTable refundTable, String rangeTime, String sort, String order) {
    if (StringUtil.isEmpty(rangeTime)) {
      rangeTime = DateUtil.getCurrentDateString() + " 00:00:00~" + DateUtil.getCurrentDateString() + " 23:59:59";
    } else {
      rangeTime = rangeTime.split("~")[0].trim() + " 00:00:00~" + rangeTime.split("~")[1].trim() + " 23:59:59";
    } 
    PageRequest pagerequest = getRequestPageable();
    Page<Map<String, Object>> data = this.refundTableService.getAllRefundTable(refundTable, rangeTime, pagerequest, sort, order);
    return toWebUIPage(data);
  }
  
  @PostMapping({"/insertRefundTable"})
  @ResponseBody
  public ResponseResult insertRefundTable(RefundTable vo) {
    vo.setRequestTime(DateUtil.getCurrentTimeString());
    User user = currentUser();
    vo.setRequestPeople(user.getName());
    vo.setRefundState("1");
    return this.refundTableService.insertRefundTable(vo);
  }
  
  @PostMapping({"/tuifeishenghe"})
  @ResponseBody
  public ResponseResult tuifeishenghe(RefundTable vo) {
    User user = currentUser();
    vo.setShengheTime(DateUtil.getCurrentTimeString());
    vo.setShenghePeople(user.getName());
    vo.setRefundState("2");
    try {
      this.refundTableService.shenghetuifei(vo);
      return ResponseResult.success("退费成功");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseResult.failure("退费失败"+ e.getMessage());
    } 
  }
  
  @PostMapping({"/updateRefundTable"})
  @ResponseBody
  public ResponseResult updateRefundTable(RefundTable vo) {
    return this.refundTableService.updateRefundTable(vo);
  }
  
  @PostMapping({"/deleteRefundTable"})
  @ResponseBody
  public ResponseResult deleteRefundTable(Long id) {
    return this.refundTableService.deleteRefundTable(id);
  }
}
