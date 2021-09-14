package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.SchedulerJobService;

/**
 * 
*<p>
*<p>文件描述:定时调用账单解析服务，获取所有账单
*<p>版权所有:深圳市巨鼎科技有限公司版权所有(C)2017</p>
</p>
</p>
*<p>完成日期:2018年9月13</p>
*<p>
*@author tanjian
 */
@Component
public class BillParseJobRunner implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		SchedulerJobService schedulerJobService = (SchedulerJobService) SpringBeanUtil.getBean(SchedulerJobService.class);
		schedulerJobService.billParseJobTask();
	}
}
