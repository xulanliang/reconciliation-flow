package com.yiban.rec.service;

import com.yiban.framework.account.domain.MessageVerifyCodeReqVo;
import com.yiban.framework.core.domain.ResponseResult;


/**
 * describe:
 *
 * @author xll
 * @date 2020/09/11
 */
public interface MessageVerifyCodeService {

    ResponseResult getMessageVerifyCode(MessageVerifyCodeReqVo reqVo);

    ResponseResult hasMessageVerifyCode();

}
