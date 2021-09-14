package com.yiban.framework.core.api.sms.inface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.framework.core.api.sms.vo.SmsResult;
import com.yiban.framework.core.api.sms.vo.SmsVo;

public class DefaultSmsService implements SmsService{
	protected final Logger log =LoggerFactory.getLogger(this.getClass());
	@Override
	public SmsResult sendSms(SmsVo smsVo) {
		log.info("======默认实现====");
		SmsResult re =new SmsResult();
		re.setResultMsg("没有提供服务");
		return re;
	}
}
