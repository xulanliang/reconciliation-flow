package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.SchedulerJobService;

/**
*<p>文件名称:AutoRecJobRunner.java
*<p>
*<p>文件描述:定时任务实现自动对账
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:定时任务实现自动对账
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年4月28日上午11:17:19</p>
*<p>
*@author fangzuxing
 */

@Component
public class AutoRecJobRunner implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		SchedulerJobService schedulerJobService = (SchedulerJobService) SpringBeanUtil.getBean(SchedulerJobService.class);
		schedulerJobService.autoRecJobTask();
	}
}
