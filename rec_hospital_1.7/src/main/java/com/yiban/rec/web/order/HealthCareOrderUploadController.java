package com.yiban.rec.web.order;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.vo.HealthCareOrderVo;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.HealthCareHisService;
import com.yiban.rec.service.HealthCareOfficialService;

/**
 * 医保账单上送接口，参考<综合支付医保对外标准接口文档.docx>
 */
@RestController
public class HealthCareOrderUploadController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HealthCareHisService healthCareHisService;

	@Autowired
	private HealthCareOfficialService healthCareOfficialService;

	@PostMapping(value = "order/upload/healthcare", produces = "application/json; charset=UTF-8")
	public ResponseVo upload(@Valid @RequestBody HealthCareOrderVo vo, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseVo.failure("请求入参非法:" + bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		String billType = vo.getBillType();
		// 判断医保账单是否已经存在
		try {
			if (HealthCareOrderVo.BILL_TYPE_HIS.equals(billType.trim())) {
				HealthCareHis hch = healthCareHisService.findByPayFlowNo(vo.getPayFlowNo());
				if (hch == null) {
					hch = new HealthCareHis();
				}
				BeanUtils.copyProperties(vo, hch);
				healthCareHisService.saveOrUpdate(hch);

			} else {
				HealthCareOfficial hco = healthCareOfficialService.findByPayFlowNo(vo.getPayFlowNo());
				if (hco == null) {
					hco = new HealthCareOfficial();
				}
				BeanUtils.copyProperties(vo, hco);
				healthCareOfficialService.saveOrUpdate(hco);
			}
		} catch (BeansException e) {
			logger.error("保存医保账单异常：" + e);
			return ResponseVo.failure("保存医保账单异常|" + e.getMessage());
		}
		return ResponseVo.success();
	}
}
