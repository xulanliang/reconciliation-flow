package com.yiban.rec.web.facepay;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.util.AlipayServiceEnvConstants;

@RestController
@RequestMapping("/api/pay/face")
public class SmilepayController extends FrameworkController {

	@RequestMapping(value = "init", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseResult init(@Valid @RequestBody String metainfo) {
		ResponseResult result = ResponseResult.success();
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(AlipayServiceEnvConstants.ALIPAY_GATEWAY,
					AlipayServiceEnvConstants.APP_ID, AlipayServiceEnvConstants.PRIVATE_KEY, "json",
					AlipayServiceEnvConstants.CHARSET, AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY,
					AlipayServiceEnvConstants.SIGN_TYPE);
			ZolozAuthenticationCustomerSmilepayInitializeRequest zoloReq = new ZolozAuthenticationCustomerSmilepayInitializeRequest();
			zoloReq.setZimmetainfo(JSON.parse(metainfo));
			ZolozAuthenticationCustomerSmilepayInitializeResponse zoloResp = alipayClient.execute(zoloReq);
			if (zoloResp.isSuccess()) {
				System.out.println("Zoloz调用成功:" + zoloResp.getBody());
			} else {
				System.out.println("Zoloz调用失败:" + zoloResp.getBody());
			}
			result.data(zoloResp.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "人脸初始化服务【face/init】异常！";
			logger.error(msg, e);
			result = ResponseResult.failure(msg + e.getMessage());
		}
		return result;
	}

}
