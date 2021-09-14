package com.yiban.rec.web.order;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.RefundUpload;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.service.RefundUploadService;
import com.yiban.rec.util.DateUtil;

/**
 * 支付结果上送
 * @Author WY
 * @Date 2018年7月26日
 */
@RestController
public class RefundUploadController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private RefundUploadService refundUploadService;
    
    @Autowired
    private OrderUploadService orderUploadService;    
    /**
     * 第三方数据上送
     * @param list
     * @return
     * Map<String,String>
     */
    @PostMapping(value="rec/refundUpload", produces="application/json; charset=UTF-8")
    public ResponseVo upload(@Valid @RequestBody RefundUpload refundUpload, 
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return ResponseVo.failure("请求入参非法:" 
            + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        // 验证参数
        ResponseVo vo = validateParameters(refundUpload);
        if(!vo.resultSuccess()) {
            return vo;
        }
        
        // 判断订单是否已经存在
        String refundOrderNo = refundUpload.getRefundOrderNo();
        RefundUpload old = refundUploadService.findByRefundOrderNo(refundOrderNo);
        if(null != old) {
            refundUpload.setId(old.getId());
        }
        try {
            refundUploadService.save(refundUpload); 
        } catch (Exception e) {
            logger.error("保存第三方支付结果发生异常：", e);
            return ResponseVo.failure(e.getMessage());
        }
        return ResponseVo.success();
    }
    
    /**
     * 验证入参
     * @param o
     * @return
     * ResponseVo
     */
    private ResponseVo validateParameters(RefundUpload o) {
        // 支付订单不存在，则不写入
        String oriTsnOrderNo = o.getOriTsnOrderNo();
        OrderUpload order = orderUploadService.findByTsnOrderNo(oriTsnOrderNo);
        if(null == order) {
            return ResponseVo.failure("支付订单不存在");
        }
        // 验证交易时间格式为：yyyy-MM-dd HH:mm:ss
        String refundDateTime = o.getRefundDateTime();
        try {
            DateUtil.stringLineToDateTime(refundDateTime);
            o.setRefundDate(refundDateTime.substring(0,10));
        } catch (Exception e) {
            return ResponseVo.failure("退费时间格式异常：refundDateTime|" + refundDateTime);
        }
        return ResponseVo.success();
    }
}
