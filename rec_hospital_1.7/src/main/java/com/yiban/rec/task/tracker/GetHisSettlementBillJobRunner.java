package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.SchedulerJobService;

/**
 * 获取HIS结算明细定时任务
 * @Author WY
 * @Date 2019年1月11日
 */
@Component
public class GetHisSettlementBillJobRunner implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		SchedulerJobService schedulerJobService = (SchedulerJobService) SpringBeanUtil.getBean(SchedulerJobService.class);
		schedulerJobService.getHisSettlementBillJobTask();
	}
}
