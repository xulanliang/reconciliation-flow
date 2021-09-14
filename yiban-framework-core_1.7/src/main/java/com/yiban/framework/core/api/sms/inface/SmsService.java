package com.yiban.framework.core.api.sms.inface;


import com.yiban.framework.core.api.sms.vo.SmsResult;
import com.yiban.framework.core.api.sms.vo.SmsVo;

/**
 * 短信发送接口
 * @author swing
 *
 * @date 2016年9月18日 上午10:17:28
 */
public interface SmsService {

	/**
	 * 
	 * @param to 短信接收人，多个用逗号隔开
	 * @param contents 短信内容
	 * @return
	 */
	public SmsResult sendSms(SmsVo smsVo);
	
	
	
}
