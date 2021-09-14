package com.yiban.rec.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.dao.OrderUploadDao;
import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.util.RefundStateEnum;

/**
 * 第三方支付结果上送接口实现类
 * @Author WY
 * @Date 2018年7月25日
 */
@Service
public class OrderUploadServiceImpl implements OrderUploadService {
	
    @Autowired
	private OrderUploadDao orderUploadDao;

    @Override
    @Transactional
    public OrderUpload save(OrderUpload orderUpload) {
        return orderUploadDao.save(orderUpload);
    }

    @Override
    public OrderUpload findByOutTradeNo(String outTradeNo) {
        return orderUploadDao.findByOutTradeNo(outTradeNo);
    }

    @Override
    public OrderUpload findByTsnOrderNo(String tsnOrderNo) {
        return orderUploadDao.findByTsnOrderNo(tsnOrderNo);
    }

	@Override
	public void updateOrder(String tsnOrderNo,String state) throws Exception {
		//通过第三方（微信支付宝）订单号查询交易明细表中记录
		OrderUpload orderVo = orderUploadDao.findByTsnOrderNo(tsnOrderNo);
		if(orderVo!=null) {
			String type=state;
			//翻译状态
			if(StringUtils.isNotBlank(state)) {
				//审核中
				if(state.equals(RefundStateEnum.unExamine.getValue())) type="1809303";
				//已驳回
				if(state.equals(RefundStateEnum.reject.getValue())) type="1809305";
				//已退费
				if(state.equals(RefundStateEnum.refund.getValue())) type="1809304";
			}
			//注入状态
			orderVo.setRefundOrderState(type);
			//更新交易明细表（t_order_upload）的退费状态
			orderUploadDao.save(orderVo);
		}
	}
}
