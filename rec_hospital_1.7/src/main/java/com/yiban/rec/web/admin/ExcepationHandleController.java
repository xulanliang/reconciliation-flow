package com.yiban.rec.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.service.SessionUserService;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

/**
 * 
 * <p>
 * 文件名称:ReconciliationController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->现金异常订单退费
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年3月21日下午2:40:16
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
@Controller
@RequestMapping("/admin/exceporder")
public class ExcepationHandleController extends FrameworkController {

	@Autowired
	private ReconciliationService reconciliationService;
	
	@Autowired
	private SessionUserService sessionUserService;

	
	@RequestMapping("")
	public String index(ModelMap model) {
		
		return autoView("reconciliation/excephandle");
	}
	@RestController
	@RequestMapping({"/admin/exceporder/data"})
	class UserDataController extends BaseController {
		
		@GetMapping
		public ResponseResult recDetailQuery(String payFlowNo,String password) {
			ResponseResult rs = ResponseResult.success();
			try {
				if(!sessionUserService.isCurrentUserPassword(password)){
					rs=ResponseResult.failure("密码输入错误！");
					return rs;
				}
				RecCash hisPayResult = reconciliationService.findByPayFlowNo(payFlowNo);
				if(StringUtil.isNullOrEmpty(hisPayResult)||(!StringUtil.isNullOrEmpty(hisPayResult)&&hisPayResult.getOrderState()==EnumTypeOfInt.ORDER_STATE_SUCCUSS.getValue())){
					rs=ResponseResult.failure("该流水号无异常订单，请与his核实！");
					return rs;
				}
				rs.data(hisPayResult);
			} catch (Exception e) {
				e.printStackTrace();
				rs=ResponseResult.failure("该流水号查询订单异常！");
			}
			
			return rs;
		}
		
		@Logable( operation = "现金退费处理")
		@PostMapping
		public ResponseResult recrefundQuery(String payFlowNo,String password) {
			ResponseResult rs = ResponseResult.success();
			try {
				
				if(!sessionUserService.isCurrentUserPassword(password)){
					rs=ResponseResult.failure("密码输入错误！");
					return rs;
				}
				RecCash hisPayResult = reconciliationService.findByPayFlowNo(payFlowNo);
				if(StringUtil.isNullOrEmpty(hisPayResult)||(!StringUtil.isNullOrEmpty(hisPayResult)&&hisPayResult.getOrderState()==EnumTypeOfInt.ORDER_STATE_SUCCUSS.getValue())){
					rs=ResponseResult.failure("该流水号无异常订单，请与his核实！");
					return rs;
				}
				//TODO
				//调用银医提供的退费接口
				
				rs.message("退费成功！");
			} catch (Exception e) {
				rs=ResponseResult.failure("退费失败！");
			}
			return rs;
		}
	}
}
