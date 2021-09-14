package com.yiban.rec.task.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.service.impl.ScanJobServiceImpl;

public class ScanJobRunner implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScanJobServiceImpl scanJobServiceImpl = SpringBeanUtil.getBean(ScanJobServiceImpl.class);
		scanJobServiceImpl.scanJobTask();
	}

}
