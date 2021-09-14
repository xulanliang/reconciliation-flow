package com.yiban.rec.web.blendrefund;


import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.vo.AllRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.BlendRefundService;


/**
 * 混合退费接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/rec")
public class BlendRefundController {
	
	@Autowired
	private BlendRefundService blendRefundService;

	@RequestMapping(value = "refund", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseVo BlendRefund(@Valid @RequestBody AllRefundVo vo,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
            return ResponseVo.failure("请求入参非法:" 
            + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
		//检查入参完整
		ResponseVo rec = checkData(vo);
		if(!rec.resultSuccess())return rec;
		//请求退款入口
		try {
			return blendRefundService.BlendRefund(vo);
		} catch (Exception e) {
			return ResponseVo.failure(e.getMessage());
		}
	}
	
	
	
	private ResponseVo checkData(AllRefundVo vo) {
		if(StringUtils.isBlank(vo.getSettlementType())) {
			return ResponseVo.failure("结算方式不能为空");
		}
		if(vo.getSettlementType().equals(EnumTypeOfInt.SETTLEMENT_TYPE.getValue())) {
			//自费类型  支付订单明细数据集不能为空
			if(vo.getOrderItems()==null||vo.getOrderItems().size()==0) {
				return ResponseVo.failure("自费类型:支付订单明细数据集不能为空");
			}
		}else {
			if(StringUtils.isBlank(vo.getYbBillNo())) {
				return ResponseVo.failure("非自费类型:医保结算单据号");
			}
			if(null==vo.getYbPayAmount()) {
				return ResponseVo.failure("非自费类型:记账合计金额不能为空");
			}
		}
		return ResponseVo.success();
	}
}
