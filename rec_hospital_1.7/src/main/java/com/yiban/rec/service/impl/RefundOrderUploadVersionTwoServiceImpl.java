package com.yiban.rec.service.impl;

import com.yiban.rec.dao.RefundOrderUploadDao;
import com.yiban.rec.domain.OrderUploadResponseVo;
import com.yiban.rec.domain.RefundorderUpload;
import com.yiban.rec.domain.vo.RefundorderUploadVo;
import com.yiban.rec.service.RefundOrderUploadVersionTwoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * describe:
 *
 * @author xll
 * @date 2020/07/16
 */
@Service
public class RefundOrderUploadVersionTwoServiceImpl extends BaseOprService implements RefundOrderUploadVersionTwoService {

    private Logger logger = LoggerFactory.getLogger(RefundOrderUploadVersionTwoServiceImpl.class);

    @Resource
    RefundOrderUploadDao refundOrderUploadDao;

    @Override
    public OrderUploadResponseVo saveRefundOrderUpload(RefundorderUploadVo refundorderUploadVo) {

        List<RefundorderUpload> payorderUploadList = refundOrderUploadDao.findByOrgCodeAndMchOrderIdAndRefundId(refundorderUploadVo.getOrgCode(),
                refundorderUploadVo.getMchOrderId(), refundorderUploadVo.getRefundId());
        if (payorderUploadList.size() > 0) {
            logger.info("退费订单上送接口：退费订单号mchOrderId：{},refundId：{}重复，请核对", refundorderUploadVo.getMchOrderId(), refundorderUploadVo.getRefundId());
            return OrderUploadResponseVo.failure("退费订单号重复，请核对", OrderUploadResponseVo.REPEAT_CODE);
        }
        RefundorderUpload refundorderUpload = new RefundorderUpload();
        BeanUtils.copyProperties(refundorderUploadVo, refundorderUpload);
        if (StringUtils.isNotBlank(refundorderUploadVo.getRefundAmt())) {
            refundorderUpload.setRefundAmt(new BigDecimal(refundorderUploadVo.getRefundAmt()));
        }
        if (StringUtils.isNotBlank(refundorderUploadVo.getPayTotalFee())) {
            refundorderUpload.setPayTotalFee(new BigDecimal(refundorderUploadVo.getPayTotalFee()));
        }
        // 退费完成时间
        if (StringUtils.isBlank(refundorderUploadVo.getRefundSuccessTime())) {
            refundorderUpload.setRefundSuccessTime(refundorderUploadVo.getUpdatedTime());
        }
        refundorderUpload.setCreateDate(new Date());
        refundOrderUploadDao.save(refundorderUpload);
        return OrderUploadResponseVo.success();
    }

    @Override
    public Page<Map<String, Object>> getAllRefundorderUpload(PageRequest pagerequest, String refundId, String payId,
                                                             String mchOrderId,String outOrderNo,String channelCode,String startDate,String endDate) {

        StringBuilder sql=new StringBuilder();
        sql.append("select id,DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s'),refund_id,pay_id,org_code,mch_order_id,refund_amt,status,refund_success_time, ");
        sql.append(" channel_order_id,out_trade_no,out_order_no,channel_code,pay_total_fee,mch_appid,remark,refund_reason,extend_params");
        sql.append(" from t_refundorder_upload ");
        sql.append("where 1=1 ");
        if(!StringUtil.isEmpty(refundId)){
            sql.append("and refund_id= '").append(refundId).append("'");
        }
        if(!StringUtil.isEmpty(payId)){
            sql.append("and pay_id= '").append(payId).append("'");
        }
        if(!StringUtil.isEmpty(mchOrderId)){
            sql.append("and mch_order_id= '").append(mchOrderId).append("'");
        }
        if(!StringUtil.isEmpty(outOrderNo)){
            sql.append("and out_order_no= '").append(outOrderNo).append("'");
        }
        if(!StringUtil.isEmpty(channelCode)){
            sql.append("and channel_code= '").append(channelCode).append("'");
        }
        if(!StringUtil.isEmpty(startDate)){
            sql.append("and DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') >= '").append(startDate.trim()).append("'");
        }
        if(!StringUtil.isEmpty(endDate)){
            sql.append("and DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') <= '").append(endDate.trim()).append("'");
        }
        logger.info("查询智慧服务平台的退款订单构造查询sql{}",sql);
        return super.handleNativeSql(sql.toString(), pagerequest,new String[] {"id","orderCreate","refundId","payId","orgCode","mchOrderId","refundAmt","status","refundSuccessTime","channelOrderId","outTradeNo","outOrderNo","channelCode","payTotalFee","mchAppid","remark","refundReason","extendParams"});
    }
}
