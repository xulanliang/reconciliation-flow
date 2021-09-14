package com.yiban.rec.web.facepay;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.emailbill.service.ThirdBillService;

@RestController
@RequestMapping("/save/platform")
public class SavePlatformController  extends FrameworkController {
	@Autowired
	private ThirdBillService thirdBillService;
	/**
	 * 渠道表保存(t_thrid_bill)
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "billSave", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseResult BillInit(@RequestBody List<ThirdBill> vo) {
		ResponseResult result = ResponseResult.success();
		try {
			thirdBillService.batchInsertBill(vo);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "数据保存失败";
			result = ResponseResult.failure(msg + e.getMessage());
		}
		
		return result;
	}
	/**
	 * 平台表保存(t_rec_pay_result)
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "paySave", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseResult payInit(@RequestBody List<HisPayResult> vo) {
		ResponseResult result = ResponseResult.success();
		try {
			thirdBillService.batchInsertPay(vo);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "数据保存失败";
			result = ResponseResult.failure(msg + e.getMessage());
		}
		
		return result;
	}
	/**
	 * his表保存(t_rec_histransactionflow)
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "hisSave", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseResult init(@RequestBody List<HisTransactionFlow> vo) {
		ResponseResult result = ResponseResult.success();
		try {
			thirdBillService.batchInsertHis(vo);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "数据保存失败";
			result = ResponseResult.failure(msg + e.getMessage());
		}
		
		return result;
	}
}
