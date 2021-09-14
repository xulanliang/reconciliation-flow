package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.EmailConfig;
import com.yiban.rec.emailbill.service.EmailBillService;
import com.yiban.rec.emailbill.service.impl.EmailBillServiceImpl;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.DateUtil;

/**
 * @author swing
 * @date 2018年6月25日 上午11:07:06 类说明
 * 获取当前银行账单邮件(每天10点15后会收到账单邮件)
 * 通过邮件方式接收当前收到的银行账单附件(账单日期是昨天)
 */
public class EmailBillParseJob implements Job {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private String filePath=PropertyUtil.getProperty("application.upload", "c:/clear/bill/");
	private String orgCode=PropertyUtil.getProperty("yiban.projectid", "");
	private HospitalConfigService hospitalConfigService=SpringBeanUtil.getBean(HospitalConfigService.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//今天日期当做邮件日期
		String emailDate = PropertyUtil.getProperty("emailbill.date", DateUtil.getCurrentDateString());
		/*String emailDate = DateUtil.getCurrentDateString();*/
		AppRuntimeConfig webConfig=hospitalConfigService.loadConfig();
		if(webConfig != null){
			EmailConfig emailConfig=new EmailConfig();
			emailConfig.setFrom(webConfig.getFrom());
			emailConfig.setHost(webConfig.getHost());
			emailConfig.setOrgCode(orgCode);
			emailConfig.setPassword(webConfig.getPassword());
			emailConfig.setPort(webConfig.getPort());
			emailConfig.setType(webConfig.getType());
			emailConfig.setUserName(webConfig.getUserName());
			EmailBillService emailParseService = new EmailBillServiceImpl(filePath, emailConfig);
			if (log.isInfoEnabled()) {
				log.info("开始银行账单解析任务,日期:{}", emailDate);
			}
			//解析昨天的账单
			emailParseService.parseBill(emailDate, emailDate);
		}else{
			if (log.isInfoEnabled()) {
				log.info("解析失败，请检查邮件配置是否正确");
			}
		}
	}
}
