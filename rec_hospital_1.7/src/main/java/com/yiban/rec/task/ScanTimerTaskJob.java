package com.yiban.rec.task;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.yiban.rec.task.tracker.ScanJobRunner;

/**
 * 注册一个定时器定时查看配置文件的定时器是否到点
 */
@Component
public class ScanTimerTaskJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerTaskJob.class);

	@Value("${scan.job.time:0 0/1 * * * ?}")
	private String scanJobTime;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	public void scheduleJobs() {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			JobDetail job = JobBuilder.newJob(ScanJobRunner.class).withIdentity("job1", "jgroup1").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger1", "triggerGroup1")
					.withSchedule(CronScheduleBuilder.cronSchedule(scanJobTime)).startNow().build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			LOGGER.info("初始化定时任务成功================》");
		} catch (Exception e) {
			LOGGER.error("初始化定时任务失败================》" + e.getMessage());
		}
	}
}
