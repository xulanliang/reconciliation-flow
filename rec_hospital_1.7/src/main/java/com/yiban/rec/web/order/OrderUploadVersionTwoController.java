package com.yiban.rec.web.order;

import com.yiban.rec.domain.OrderUploadResponseVo;
import com.yiban.rec.domain.PayorderUploadVo;
import com.yiban.rec.domain.vo.RefundorderUploadVo;
import com.yiban.rec.service.PayOrderUploadVersionTwoService;
import com.yiban.rec.service.RefundOrderUploadVersionTwoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 支付服务V2.0 支付结果上送
 *
 * @Author xll
 * @Date 2020年7月15日
 */
@RestController
@RequestMapping("rec")
public class OrderUploadVersionTwoController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PayOrderUploadVersionTwoService payOrderUploadVersionTwoService;
    @Autowired
    private RefundOrderUploadVersionTwoService refundOrderUploadVersionTwoService;

    /**
     * 支付订单数据结果上送
     *
     * @param payorderUploadVo
     * @return Map<String, String>
     */
    @PostMapping(value = "payorder/upload", produces = "application/json; charset=UTF-8")
    public OrderUploadResponseVo upload(@Valid @RequestBody PayorderUploadVo payorderUploadVo,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return OrderUploadResponseVo.failure("请求入参非法:"
                    + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        // 验证参数
        OrderUploadResponseVo vo = validateParameters(payorderUploadVo);
        if (!vo.isSuccess()) {
            return vo;
        }
        logger.info("支付账单上送入参：{}", payorderUploadVo.toString());
        OrderUploadResponseVo responseVo = payOrderUploadVersionTwoService.savePayOrderUpload(payorderUploadVo);
        return responseVo;
    }

    /**
     * 退费订单数据结果上送
     *
     * @param refundorderUploadVo
     * @return Map<String, String>
     */
    //不需要退费的订单，屏蔽
    //@PostMapping(value = "refundorder/upload", produces = "application/json; charset=UTF-8")
    public OrderUploadResponseVo upload(@Valid @RequestBody RefundorderUploadVo refundorderUploadVo,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return OrderUploadResponseVo.failure("请求入参非法:"
                    + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        // 验证参数

        logger.info("退费账单上送入参：{}", refundorderUploadVo.toString());
        OrderUploadResponseVo responseVo = refundOrderUploadVersionTwoService.saveRefundOrderUpload(refundorderUploadVo);
        return responseVo;
    }

    /**
     * 验证入参
     *
     * @param payorderUploadVo
     * @return ResponseVo
     */
    private OrderUploadResponseVo validateParameters(PayorderUploadVo payorderUploadVo) {
        // 验证交易时间格式为：yyyy-MM-dd HH:mm:ss
        /*String tradeDateTime = payorderUpload.getTradeDateTime();
        try {
            DateUtil.stringLineToDateTime(tradeDateTime);
            payorderUpload.setTradeDate(tradeDateTime.substring(0,10));
        } catch (Exception e) {
            return OrderUploadResponseVo.failure("交易时间格式异常：tradeDateTime|" + tradeDateTime);
        }*/
        return OrderUploadResponseVo.success();
    }
}
