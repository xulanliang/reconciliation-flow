package com.yiban.framework.core.event;

import org.springframework.context.ApplicationContext;

/**
 * @author swing
 * @date 2018年1月22日 下午3:24:15 类说明
 */
public class WebInitFinishEvent implements EventData {
	public static final String TOPIC = "web.init.finish.topic";
	private ApplicationContext ac;

	public WebInitFinishEvent(ApplicationContext ac) {
		this.ac = ac;
	}

	public ApplicationContext getAc() {
		return ac;
	}

}
