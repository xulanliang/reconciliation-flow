package com.yiban.framework.core.api.email.inface;


import javax.mail.MessagingException;
import com.yiban.framework.core.api.email.vo.EmailVo;

/**
 * 邮件发送服务
 * 
 * @author swing
 *
 * @date 2016年8月19日 下午6:23:50
 */
public interface MailService {
	/**
	 * 发送邮件
	 * @param emailVo 邮件载体
	 * @throws MessagingException
	 */
	
	public void sendEmail(EmailVo emailVo) throws MessagingException ;
}
