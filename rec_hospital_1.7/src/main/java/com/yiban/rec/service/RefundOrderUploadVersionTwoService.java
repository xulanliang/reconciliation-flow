package com.yiban.rec.service;

import com.yiban.rec.domain.OrderUploadResponseVo;
import com.yiban.rec.domain.vo.RefundorderUploadVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
public interface RefundOrderUploadVersionTwoService {

	OrderUploadResponseVo saveRefundOrderUpload(RefundorderUploadVo refundorderUploadVo);

	Page<Map<String,Object>> getAllRefundorderUpload(PageRequest pagerequest, String refundId, String payId,
													 String mchOrderId,String outOrderNo,String channelCode,String startDate,String endDate);
}
