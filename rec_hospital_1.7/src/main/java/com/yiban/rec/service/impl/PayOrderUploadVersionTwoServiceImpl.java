package com.yiban.rec.service.impl;

import com.yiban.rec.dao.PayOrderUploadDao;
import com.yiban.rec.domain.OrderUploadResponseVo;
import com.yiban.rec.domain.PayorderUpload;
import com.yiban.rec.domain.PayorderUploadVo;
import com.yiban.rec.service.PayOrderUploadVersionTwoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PayOrderUploadVersionTwoServiceImpl extends BaseOprService implements PayOrderUploadVersionTwoService {

    private Logger logger = LoggerFactory.getLogger(PayOrderUploadVersionTwoServiceImpl.class);

    @Resource
    PayOrderUploadDao payOrderUploadDao;

    @Override
    @Transactional
    public OrderUploadResponseVo savePayOrderUpload(PayorderUploadVo payorderUploadVo) {

        List<PayorderUpload> payorderUploadList = payOrderUploadDao.findByOrgCodeAndPayId(payorderUploadVo.getOrgCode(), payorderUploadVo.getPayId());
        if (payorderUploadList.size() > 0) {
            logger.info("支付订单上送接口：支付订单号{}重复，请核对", payorderUploadVo.getPayId());
            return OrderUploadResponseVo.failure("支付订单号重复，请核对", OrderUploadResponseVo.REPEAT_CODE);
        }
        PayorderUpload payorderUpload = new PayorderUpload();

        BeanUtils.copyProperties(payorderUploadVo, payorderUpload);
        if (StringUtils.isNotBlank(payorderUploadVo.getOrderAmt())) {
            payorderUpload.setOrderAmt(new BigDecimal(payorderUploadVo.getOrderAmt()));
        }
        if (StringUtils.isNotBlank(payorderUploadVo.getTotalRefundedAmt())) {
            payorderUpload.setTotalRefundedAmt(new BigDecimal(payorderUploadVo.getTotalRefundedAmt()));
        }
        if (StringUtils.isBlank(payorderUploadVo.getPayTimeEnd())) {
            payorderUpload.setPayTimeEnd(payorderUploadVo.getUpdatedTime());
        }
        payorderUpload.setCreateDate(new Date());
        payOrderUploadDao.save(payorderUpload);
        return OrderUploadResponseVo.success();
    }

    @Override
    public Page<Map<String, Object>> getAllPayorderUpload(PageRequest pagerequest, String payId, String mchOrderId,
                                                          String payOption,String status,String patientName,String startDate,String endDate) {
        StringBuilder sql=new StringBuilder();
        sql.append("select id,DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s'),pay_id,org_code,mch_order_id,order_amt,pay_type,pay_option,pay_time_end,status,out_trade_no,close_id,cancel_id,total_refunded_amt,terminal_id,business_type,patient_name,patient_id,patient_type,mch_appid,mch_notify_url,order_subject,order_detail,pay_scene,user_client_ip,auth_code,open_id,remark,time_expire,channel_order_id,extend_params ");
        sql.append("from t_payorder_upload ");
        sql.append("where 1=1 ");
        if(!StringUtil.isEmpty(payId)){
            sql.append("and pay_id= '").append(payId).append("'");
        }
        if(!StringUtil.isEmpty(mchOrderId)){
            sql.append("and mch_order_id= '").append(mchOrderId).append("'");
        }
        if(!StringUtil.isEmpty(payOption) && !StringUtil.equals(payOption,"0")){
            sql.append("and pay_option= '").append(payOption).append("'");
        }
        if(!StringUtil.isEmpty(status)){
            sql.append("and status= '").append(status).append("'");
        }
        if(!StringUtil.isEmpty(patientName)){
            sql.append("and patient_name= '").append(patientName).append("'");
        }
        if(!StringUtil.isEmpty(startDate)){
            sql.append("and DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') >= '").append(startDate.trim()).append("'");
        }
        if(!StringUtil.isEmpty(endDate)){
            sql.append("and DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') <= '").append(endDate.trim()).append("'");
        }
        logger.info("查询智慧服务平台的支付订单构造查询sql{}",sql);
        return super.handleNativeSql(sql.toString(), pagerequest,new String[] {"id","orderCreate","payId","orgCode","mchOrderId","orderAmt","payType","payOption","payTimeEnd","status","outTradeNo","closeId","cancelId","totalRefundedAmt","terminalId","businessType","patientName","patientId","patientType","mchAppid","mchNotifyUrl","orderSubject","orderDetail","payScene","userClientIp","authCode","openId","remark","timeExpire","channelOrderId","extendParams"});
    }
}
