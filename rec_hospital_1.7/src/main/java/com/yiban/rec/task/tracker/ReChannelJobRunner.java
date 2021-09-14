package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.SchedulerJobService;

/**
 * 
*<p>文件名称:ReChannelJobRunner.java
*<p>
*<p>文件描述:重复监测获取银医数据
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年7月4日下午3:45:07</p>
*<p>
*@author fangzuxing
 */
public class ReChannelJobRunner implements Job{

	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		SchedulerJobService schedulerJobService = (SchedulerJobService) SpringBeanUtil.getBean(SchedulerJobService.class);
		schedulerJobService.reChannelJobTask();
	}
}
