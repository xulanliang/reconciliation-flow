package com.yiban.rec.bill.parse.service.standardbill.impl.order;

import java.util.HashMap;

import com.yiban.rec.bill.parse.vo.ExtraParamVo;

public interface QueryOrder {
	// 方法入口
	public HashMap<String, ExtraParamVo> query();

}
