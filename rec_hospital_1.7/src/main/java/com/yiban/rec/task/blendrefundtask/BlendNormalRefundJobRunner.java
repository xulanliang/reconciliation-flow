package com.yiban.rec.task.blendrefundtask;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.BlendRefundJobService;

public class BlendNormalRefundJobRunner implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		BlendRefundJobService blendRefundJobService = SpringBeanUtil.getBean(BlendRefundJobService.class);
		blendRefundJobService.BlendNormalRefundJob();
	}
}
