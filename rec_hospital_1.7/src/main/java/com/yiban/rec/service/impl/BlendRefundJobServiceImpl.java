package com.yiban.rec.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.dao.MixRefundDao;
import com.yiban.rec.dao.MixRefundDetailsDao;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.BlendRefundJobService;
import com.yiban.rec.service.BlendRefundService;
import com.yiban.rec.service.customized.refundorder.ClearRefundOrder;
import com.yiban.rec.service.customized.refundorder.TangDuJinDieRefundOrder;

@Service
public class BlendRefundJobServiceImpl implements BlendRefundJobService {

	@Autowired
	private MixRefundDao mixRefundDao;

	@Autowired
	private MixRefundDetailsDao mixRefundDetailsDao;

	@Autowired
	private BlendRefundService blendRefundService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("reconciliationsable-pool-%d")
			.build();

	private final ExecutorService pool = new ThreadPoolExecutor(5, 8, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(216), threadFactory);

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	/**
	 * 异常退费失败逻辑
	 */
	public void BlendExceptionRefundJob() {
		// 查询异常失败数据
		List<MixRefundDetails> list = mixRefundDetailsDao.findByrefundState(RefundEnumType.REFUND_NO_EXCEPTION.getId());

		for (MixRefundDetails v : list) {
			// 多线程处理退费任务
			pool.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						// 处理退款账单
						exceptionRefund(v);
					} catch (Exception e) {
						log.error("退费异常:{}", e);

					}
					return 1;
				}
			});

		}
	}

	private void exceptionRefund(MixRefundDetails vo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer retryCount = Integer.valueOf(propertiesConfigService.findValueByPkey(ProConstants.retryCount,
				ProConstants.DEFAULT.get(ProConstants.retryCount)));
		try {
			// 判断是否执行当前时间大于重试时间并且执行次数小于或者等于指定重试次数
			if (StringUtils.isBlank(vo.getNextTime()) || (vo.getRetryTimes() + 1) <= retryCount) {
				if (StringUtils.isNotBlank(vo.getNextTime())
						&& dateFormat.parse(vo.getNextTime()).getTime() >= new Date().getTime())
					return;
				// 以后有新的类型往后加
				if (vo.getBillSource().equals(RefundEnumType.BILL_SOURCE_JD.getValue())
						|| vo.getBillSource().equals(RefundEnumType.BILL_SOURCE.getValue())) {// 巨鼎
					new ClearRefundOrder(vo);
					// 没有任何异常表明退费成功
					vo.setRefundState(RefundEnumType.REFUND_SUCCESS.getId());
					vo.setRefundStateInfo("退费成功");
				}

				// 唐都金蝶的账单
				if (StringUtils.equals(RefundEnumType.BILL_SOURCE_JIND.getValue(), vo.getBillSource())) {
					new TangDuJinDieRefundOrder(vo);
					// 没有任何异常表明退费成功
					vo.setRefundState(RefundEnumType.REFUND_SUCCESS.getId());
					vo.setRefundStateInfo("退费成功");
				}

				if ((vo.getRetryTimes() + 1) == retryCount) {// 重试还是失败更改为正常退费失败流程
					vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
					vo.setRefundStateInfo("退款失败");
				}
			}
		} catch (Exception e) {
			log.error("退费异常:{}", e);
			if ((vo.getRetryTimes() + 1) == retryCount) {// 重试还是失败更改为正常退费失败流程
				vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
				vo.setRefundStateInfo("退款失败");
			}
			Integer retryTime = Integer.valueOf(propertiesConfigService.findValueByPkey(ProConstants.retryTime,
					ProConstants.DEFAULT.get(ProConstants.retryTime)));
			vo.setNextTime(dateFormat.format(new Date().getTime() + retryTime * 1000 * 60));
			vo.setRetryTimes(vo.getRetryTimes() + 1);
		} finally {
			// 更新数据
			mixRefundDetailsDao.save(vo);
		}

	}

	/**
	 * 正常退费失败逻辑
	 */
	public void BlendNormalRefundJob() {
		// 查询正常失败数据
		List<MixRefundDetails> list = mixRefundDetailsDao.findByrefundState(RefundEnumType.REFUND_FAILURE.getId());
		int refundCount = Integer.valueOf(propertiesConfigService.findValueByPkey(ProConstants.refundCount,
				ProConstants.DEFAULT.get(ProConstants.refundCount)));

		for (MixRefundDetails v : list) {
			// 多线程处理退费任务
			pool.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					try {
						if (v.getRefundCount() < refundCount) {// 小于指定退费次数则执行
							// 处理退款账单
							normalRefund(v);
						}
					} catch (Exception e) {
						log.error("退费异常:{}", e);
					}
					return 1;
				}
			});
		}
	}

	private void normalRefund(MixRefundDetails vo) {
		MixRefund mixRefund = mixRefundDao.findByRefundOrderNo(vo.getRefundOrderNo());
		List<MixRefundDetails> list = new ArrayList<>();
		list.add(vo);
		ResponseVo refundRec = blendRefundService.refund(mixRefund, list, mixRefund.getSettlementType());
		if (!refundRec.resultSuccess())
			return;
		// 更新数据
		mixRefundDetailsDao.save(list);
	}
}
