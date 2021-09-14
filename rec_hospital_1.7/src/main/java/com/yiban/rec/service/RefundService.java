package com.yiban.rec.service;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.vo.RefundVo;

public interface RefundService {

	public ResponseResult refundAll(RefundVo vo) throws Exception;
}
