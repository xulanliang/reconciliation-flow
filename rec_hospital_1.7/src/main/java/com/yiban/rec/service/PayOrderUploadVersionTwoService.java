package com.yiban.rec.service;

import com.yiban.rec.domain.OrderUploadResponseVo;
import com.yiban.rec.domain.PayorderUploadVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
public interface PayOrderUploadVersionTwoService {

	OrderUploadResponseVo savePayOrderUpload(PayorderUploadVo payorderUploadVo);
	Page<Map<String,Object>> getAllPayorderUpload(PageRequest pagerequest,String payId, String mchOrderId,
												  String payOption,String status,String patientName,String startDate,String endDate);

}
