package com.yiban.rec.bill.parse.service.standardbill;

import com.yiban.framework.core.domain.ResponseResult;

/**
* @author swing
* @date 2018年8月3日 上午9:59:47
* 类说明  账单解析
*/
public interface BillParseService {
    
    void parse(String orgCode,String date) throws BillParseException;
    ResponseResult startParse(String orgCode, String beginDate, String endDate) throws BillParseException;
}
