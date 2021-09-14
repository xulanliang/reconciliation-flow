package com.yiban.rec.task;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.yiban.rec.task.blendrefundtask.BlendExceptionRefundJobRunner;
import com.yiban.rec.task.blendrefundtask.BlendNormalRefundJobRunner;
import com.yiban.rec.task.tracker.AutoRecJobRunner;
import com.yiban.rec.task.tracker.BillParseJobRunner;
import com.yiban.rec.task.tracker.EmailBillParseJob;
import com.yiban.rec.task.tracker.GetHisBillJobRunner;
import com.yiban.rec.task.tracker.GetHisSettlementBillJobRunner;
import com.yiban.rec.task.tracker.ReChannelJobRunner;

/**
 * 
 * <p>
 * 文件名称:TimerTaskJob.java
 * <p>
 * <p>
 * 文件描述:定时任务启动类 ，启动时初始化
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年7月4日下午5:36:26
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
//@Component
public class TimerTaskJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerTaskJob.class);

	@Value("${rechannel.job.time}")
	private String rechannelJobTime;

	@Value("${autorec.job.time}")
	private String autoRecJobTime;

	@Value("${emailbill.job.time}")
	private String emailbillTime;
	
	@Value("${billParse.job.time}")
	private String billParseTime;
	
	@Value("${normalRefund.job.time}")
	private String normalRefundTime;
	
	@Value("${exceptionRefund.job.time}")
	private String exceptionRefundTime;
	
	@Value("${his.job.time:0 30 09 * * ?}")
	private String hisJobTime;
	
	@Value("${his.settlement.job.time:0 30 10 * * ?}")
	private String hisSettlementJobTime;
	// :0 0/60 * * * ?
	@Value("${his.tradedetail.job.time}")
	private String hisTradeDetailJobTime;
	
	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	public void scheduleJobs() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		
		initBillParse(scheduler);
		initAutoRecJob(scheduler);
		if(StringUtils.isNotBlank(rechannelJobTime)) {
		    initReChannelJob(scheduler);
		}
		if(StringUtils.isNotBlank(emailbillTime)) {
		    initEmailBillParseJob(scheduler);
		}
		if(StringUtils.isNotBlank(normalRefundTime)) {
		    initRefundNormalData(scheduler);
		}
		if(StringUtils.isNotBlank(exceptionRefundTime)) {
		    initRefundExceptionData(scheduler);
		}
		if(StringUtils.isNotBlank(hisJobTime)) {
		    getHisBillData(scheduler);
		}
		if(StringUtils.isNotBlank(hisSettlementJobTime)) {
		    getHisSettlementBillData(scheduler);
		}
	}

	/**
	 * 解析邮件账单
	 * @param scheduler
	 * void
	 */
	private void initEmailBillParseJob(Scheduler scheduler){
		if(StringUtils.isNotEmpty(emailbillTime)){
			try {
				JobDetail job = JobBuilder.newJob(EmailBillParseJob.class).withIdentity("emailBill","emailBillGrop").build();
				Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(emailbillTime)).build();
				scheduler.scheduleJob(job, trigger);
				scheduler.start();
				LOGGER.info("========初始化邮件银行账单解析=============");
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * 获取his账单数据(支付宝、微信、银行账单)失败时，在12点再执行一次
	 */
	private void initReChannelJob(Scheduler scheduler) {
		try {
			JobDetail job = JobBuilder.newJob(ReChannelJobRunner.class).withIdentity("job2", "jgroup2").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger2", "triggerGroup2")
					.withSchedule(CronScheduleBuilder.cronSchedule(rechannelJobTime)).startNow().build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化获取his账单数据(支付宝、微信、银行账单)失败时定时任务成功================》");
		} catch (Exception e) {
			LOGGER.error("获取his账单数据(支付宝、微信、银行账单)失败时定时任务失败================》" + e.getMessage());
		}
	}

	/**
	 * 定时对账，每天下午11.10点执行
	 */
	private void initAutoRecJob(Scheduler scheduler) {
		try {
			JobDetail job = JobBuilder.newJob(AutoRecJobRunner.class).withIdentity("job5", "jgroup5").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger5", "triggerGroup5")
					.withSchedule(CronScheduleBuilder.cronSchedule(autoRecJobTime)).startNow().build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化定时对账任务成功================》");
		} catch (Exception e) {
			LOGGER.error("初始化定时对账任务失败================》" + e.getMessage());
		}
	}
	
	/**
	 * 调用账单拉取服务，获取所有的账单
	 */
	private void initBillParse(Scheduler scheduler) {
		try {
			JobDetail job = JobBuilder.newJob(BillParseJobRunner.class).withIdentity("job8", "jgroup8").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger8", "triggerGroup8")
					.withSchedule(CronScheduleBuilder.cronSchedule(billParseTime)).startNow().build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化获取his账单数据和支付宝、微信、银行账单信息定时任务成功================》");
		} catch (Exception e) {
			LOGGER.error("初始化获取his账单数据和支付宝、微信、银行账单信息定时任务失败================》" + e.getMessage());
		}
	}
	
	/**
	 * 定时调用退费（正常退费失败）数据
	 */
	private void initRefundNormalData(Scheduler scheduler) {
		try {
			JobDetail job = JobBuilder.newJob(BlendNormalRefundJobRunner.class).withIdentity("blendNormalRefund", 
			        "blendNormalRefundGrop").build();
			Trigger trigger = TriggerBuilder.newTrigger()
			        .withSchedule(CronScheduleBuilder.cronSchedule(normalRefundTime)).build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化退费（正常失败单）定时任务成功================》");
		} catch (Exception e) {
			LOGGER.error("初始化退费（正常失败单）定时任务失败================》" + e.getMessage());
		}
	}
	
	/**
	 * 定时调用退费（异常退费失败）数据
	 */
	private void initRefundExceptionData(Scheduler scheduler) {
		try {
			JobDetail job = JobBuilder.newJob(BlendExceptionRefundJobRunner.class).withIdentity("blendExceptionRefund", "blendExceptionRefundGrop").build();
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(exceptionRefundTime)).build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化退费（异常失败单）定时任务成功================》");
		} catch (Exception e) {
			LOGGER.error("初始化退费（异常失败单）定时任务失败================》" + e.getMessage());
		}
	}
	
	/**
	 * 通过银医定时获取his账单数据
	 */
	private void getHisBillData(Scheduler scheduler) {
	    try {
	        JobDetail job = JobBuilder.newJob(GetHisBillJobRunner.class).withIdentity("getHisBillJob", 
	                "getHisBillGrop").build();
	        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(hisJobTime)).build();
	        scheduler.scheduleJob(job, trigger);
	        scheduler.start();
	        LOGGER.info("初始化获取his账单（通过银医）定时任务成功================》");
	    } catch (Exception e) {
	        LOGGER.error("初始化获取his账单（通过银医）定时任务失败================》" + e.getMessage());
	    }
	}
	
	/**
	 * 通过银医定时获取his结算单账单数据
	 */
	private void getHisSettlementBillData(Scheduler scheduler) {
	    try {
	        JobDetail job = JobBuilder.newJob(GetHisSettlementBillJobRunner.class).withIdentity("getHisSettBillJob", 
	                "getHisSettBillJobGrop").build();
	        Trigger trigger = TriggerBuilder.newTrigger()
	                .withSchedule(CronScheduleBuilder.cronSchedule(hisSettlementJobTime)).build();
	        scheduler.scheduleJob(job, trigger);
	        scheduler.start();
	        LOGGER.info("初始化获取his结算账单（通过银医）定时任务成功================》");
	    } catch (Exception e) {
	        LOGGER.error("初始化获取his结算账单（通过银医）定时任务失败================》" + e.getMessage());
	    }
	}
}
