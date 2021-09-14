package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.SchedulerJobService;

/**
 * 获取his账单（通过银医接口）
 * @Author WY
 * @Date 2019年1月3日
 */
@Component
public class GetHisBillJobRunner implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		SchedulerJobService schedulerJobService = (SchedulerJobService) SpringBeanUtil.getBean(SchedulerJobService.class);
		schedulerJobService.getHisBillJobTask();
	}
}
