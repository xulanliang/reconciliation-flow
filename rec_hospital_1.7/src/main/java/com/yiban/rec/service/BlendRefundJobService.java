package com.yiban.rec.service;

public interface BlendRefundJobService {

	/**
	 * 异常退费失败逻辑
	 */
	public void BlendNormalRefundJob();
	
	
	
	/**
	 * 正常退费失败逻辑
	 */
	public void BlendExceptionRefundJob();
}
