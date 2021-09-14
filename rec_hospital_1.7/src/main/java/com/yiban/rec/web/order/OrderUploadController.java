package com.yiban.rec.web.order;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.util.DateUtil;

/**
 * 支付结果上送
 * @Author WY
 * @Date 2018年7月26日
 */
@RestController
public class OrderUploadController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private OrderUploadService orderUploadService;
    
    /** 订单状态 */
    private final static String ORDERSTATE_PAY_SUCCESS = "1809302";
    private final static String ORDERSTATE_PAY_FAIL = "1809300";
    
    /**
     * 第三方数据上送
     * @param list
     * @return
     * Map<String,String>
     */
    @PostMapping(value="rec/upload", produces="application/json; charset=UTF-8")
    public ResponseVo upload(@Valid @RequestBody OrderUpload orderUpload, 
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return ResponseVo.failure("请求入参非法:" 
            + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        // 验证参数
        ResponseVo vo = validateParameters(orderUpload);
        if(!vo.resultSuccess()) {
            return vo;
        }
        // 判断订单是否已经存在
        String outTradeNo = orderUpload.getOutTradeNo();
        OrderUpload old = orderUploadService.findByOutTradeNo(outTradeNo);
        if(null != old) {
            orderUpload.setId(old.getId());
        }
        try {
            orderUploadService.save(orderUpload); 
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
    private ResponseVo validateParameters(OrderUpload o) {
     // 设置订单状态：默认交易成功，医院his订单号为空，即为交易异常。
        o.setOrderState(ORDERSTATE_PAY_SUCCESS);
        String hisOrderNo = o.getHisOrderNo();
        if(StringUtils.isBlank(hisOrderNo)) {
            String orderStateRemark = o.getOrderStateRemark();
            if(StringUtils.isBlank(orderStateRemark)) {
                return ResponseVo.failure("医院His系统订单号为空时必须上传异常描述，即orderStateRemark不能为空");
            }
            o.setOrderState(ORDERSTATE_PAY_FAIL);
        }
        
        // 验证交易时间格式为：yyyy-MM-dd HH:mm:ss
        String tradeDateTime = o.getTradeDateTime();
        try {
            DateUtil.stringLineToDateTime(tradeDateTime);
            o.setTradeDate(tradeDateTime.substring(0,10));
        } catch (Exception e) {
            return ResponseVo.failure("交易时间格式异常：tradeDateTime|" + tradeDateTime);
        }
        
        // 结算方式 0031自费
        String settlementType = o.getSettlementType();
        if(!StringUtils.equalsIgnoreCase("0031", settlementType)) {
            String ybSerialNo = o.getYbSerialNo();
            if(StringUtils.isBlank(ybSerialNo)) {
                return ResponseVo.failure("结算方式非自费时医保流水号必传");
            }
            String ybBillNo = o.getYbBillNo();
            if(StringUtils.isBlank(ybBillNo)) {
                return ResponseVo.failure("结算方式非自费时医保结算单据号必传");
            }
            BigDecimal ybPayAmount = o.getYbPayAmount();
            if(null == ybPayAmount || ybPayAmount.intValue() < 0) {
                return ResponseVo.failure("结算方式非自费时医保记账合计金额必传");
            }
        }
        return ResponseVo.success();
    }
}
