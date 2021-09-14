package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.bill.parse.service.standardbill.UnifiedGatewayRefund;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.ExcepHandingRecordVo;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.service.ClearRefundService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.RefundRecordService;
import com.yiban.rec.service.RefundService;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.RefundStateEnum;


@Service
public class RefundServiceImpl implements RefundService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExcepHandingRecordDao excepHandingRecordDao;
    @Autowired
    private ClearRefundService clearRefundService;
    @Autowired
    private HospitalConfigService hospitalConfigService;
    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private RefundRecordService refundRecordService;

    private final String type = "1";


    @Transactional(rollbackFor = Exception.class)
    public ResponseResult refundAll(RefundVo vo) {
        try {
            //判断昆明滇医通退费
            if(vo.getBillSource().equals("dyt")){
                logger.info("昆明滇医通账单处理");
                vo.setTsn(vo.getOrderNo());
            }
            logger.info("########## 退费入参：{}", new Gson().toJson(vo));
            ResponseResult re = checkData(vo);
            if (!checkData(vo).isSuccess()) return re;
            AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
            //退款管理来源不需要做这些逻辑
            if (vo.getSource() != 1) {
                //查找单号在记录中存不存在
                if (vo != null && (StringUtils.isNotBlank(vo.getTsn()) || StringUtils.isNotBlank(vo.getOrderNo()))) {
                    List<String> sList = new ArrayList<>();
                    if (StringUtils.isNotBlank(vo.getTsn())) sList.add(vo.getTsn());
                    if (StringUtils.isNotBlank(vo.getOrderNo())) sList.add(vo.getOrderNo());
                    List<ExcepHandingRecord> list = null;
                    //锁表操作验证操作
                    if (StringUtils.isNotBlank(vo.getBillSource())) {
                        list = excepHandingRecordDao.findByPaymentRequestFlowAndbillSource(sList, vo.getBillSource());
                    } else {
                        list = excepHandingRecordDao.findByPaymentRequestFlow(sList);
                    }
                    if (list != null && list.size() > 0) {//存在记录
                        ExcepHandingRecord one = list.get(0);
                        ExcepHandingRecordVo exVo = new ExcepHandingRecordVo();
                        exVo.setId(one.getId());
                        exVo.setHandleRemark(vo.getReason());
                        //注入重新提交后的退回路径
                        exVo.setRefundType(vo.getRefundType());
                        exVo.setTradeAmount(vo.getTradeAmount());
                        if (StringUtils.isBlank(vo.getState()) && !RefundStateEnum.unExamine.getValue().equals(one.getState())) {//如果状态为空的话是来源于不属于退款管理页面的其他入口，直接更新为审核中
                            vo.setState(RefundStateEnum.unExamine.getValue());
                            refundRecordService.rejectOrExamine(exVo, vo.getUser());
                            return ResponseResult.success().data(vo.getState()).message("已经入审核流程");
                        } else {
                            return ResponseResult.failure().message("已经申请过退款");
                        }
                    } else {//不存在记录
                        //退款流程(医院属性配置中配置)
                        if (hConfig.getIsRefundExamine().equals(RefundStateEnum.unExamine.getValue())) {//需要审核
                            vo.setState(RefundStateEnum.unExamine.getValue());
                            refundSeting(vo);
                            return ResponseResult.success().data(vo.getState()).message("已经入审核流程");
                        } else {
                            vo.setState(RefundStateEnum.refund.getValue());
                        }
                        refundSeting(vo);//记录退费记录表
                    }
                } else {
                    return ResponseResult.failure().message("流水号不能为空");
                }
            }
            //判断是否需要原路径返回,1原路径返回，2其他退费
            if (StringUtils.isNotBlank(vo.getRefundType()) && !vo.getRefundType().equals(type)) {
                return ResponseResult.success().data(vo.getState()).message("退款成功");
            }

			
			/**
			 * 以下是退费渠道选择逻辑
			 */
			if(vo.getPayType().equals(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())) {//聚合支付江南银行
				String payUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl,
	                        ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
	            payUrl = payUrl + "/order/union/refund";
	            vo.setUnionPayCode("jiangnan");
	            vo.setUnionPayType("151");
	            vo.setUnionSystemCode("50");
	            new UnifiedGatewayRefund(vo, payUrl).refund();
			}else {
				clearRefundService.ClearRefund(vo);
			}
        } catch (Exception e) {
            logger.error("未知错误：", e);
            return ResponseResult.failure().data(vo.getState()).message(e.getMessage());
        }
        return ResponseResult.success().data(vo.getState()).message("退款成功");
    }

    //数据参数验证
    public ResponseResult checkData(RefundVo vo) {
        if (!(vo != null && (StringUtils.isNotBlank(vo.getTsn()) || StringUtils.isNotBlank(vo.getOrderNo())))) {
            return ResponseResult.failure().message("流水号不能为空");
        }
        if (StringUtils.isBlank(vo.getBillSource())) {
            return ResponseResult.failure().message("单号来源不确定");
        }
        if (StringUtils.isBlank(vo.getOrgCode())) {
            return ResponseResult.failure().message("医院编码(机构编码)不能为空");
        }
        if (StringUtils.isBlank(vo.getTradeAmount())) {
            return ResponseResult.failure().message("退款金额不能为空");
        }
        if (StringUtils.isBlank(vo.getPayType())) {
            return ResponseResult.failure().message("支付类型不能为空");
        }
        if (StringUtils.isBlank(vo.getPayCode())) {
            return ResponseResult.failure().message("支付渠道不能为空");
        }
        if (StringUtils.isBlank(vo.getReason())) {
            return ResponseResult.failure().message("退款原因不能为空");
        }
        if (StringUtils.isBlank(vo.getUser().getLoginName()) || vo.getUser().getId() == null) {
            return ResponseResult.failure().message("操作人不能为空");
        }
        return ResponseResult.success();
    }

    public void refundSeting(RefundVo vo) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //新入库一条
        ExcepHandingRecord ehr = new ExcepHandingRecord();
        ehr.setHandleDateTime(new Date());
        ehr.setHandleRemark(vo.getReason());
        ehr.setOperationUserId(vo.getUser().getId());
        ehr.setUserName(vo.getUser().getName());
        ehr.setOrgNo(vo.getOrgCode());
        ehr.setPaymentRequestFlow(vo.getTsn());
        ehr.setPaymentFlow(vo.getPaymentFlow());
        ehr.setExtendArea(vo.getExtendArea());
        if (StringUtils.isBlank(vo.getState())) {
            ehr.setState(RefundStateEnum.unExamine.getValue());
        } else {
            ehr.setState(vo.getState());
        }
        ehr.setPayName(vo.getPayCode());
        ehr.setBusinessType(vo.getPayType());
        ehr.setPatientName(vo.getPatientName());
        //兼容版本添加
        if (StringUtils.isNotBlank(vo.getBatchRefundNoNew())) {
            ehr.setTradeAmount(new BigDecimal(vo.getTradeAmount()));
        } else {
            if (StringUtils.isNotBlank(vo.getBatchRefundNo())) {
                ehr.setTradeAmount(new BigDecimal(vo.getBatchRefundNo()));
            } else {
                ehr.setTradeAmount(new BigDecimal(vo.getTradeAmount()));
            }
        }
        Date time = null;
        if (StringUtils.isNotBlank(vo.getTradetime())) {
            time = sdf.parse(vo.getTradetime());
        }
        ehr.setTradeTime(time);
        ehr.setBillSource(vo.getBillSource());
        ehr.setImgUrl(vo.getImgUrl());
        ehr.setRefundType(vo.getRefundType());
        excepHandingRecordDao.save(ehr);
    }
}
