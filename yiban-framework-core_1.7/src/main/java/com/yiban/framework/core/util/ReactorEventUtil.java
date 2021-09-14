package com.yiban.framework.core.util;

import com.yiban.framework.core.event.EventData;

import reactor.core.Reactor;
import reactor.event.Event;

/**
 * @author swing
 * @date 2018年1月11日 下午3:03:12 类说明
 */
public class ReactorEventUtil {
	private static Reactor reactor = SpringBeanUtil.getBean(Reactor.class);
	public static void publishEvent(String topic, EventData eventData) {
		reactor.notify(topic, Event.wrap(eventData));
	}
}
