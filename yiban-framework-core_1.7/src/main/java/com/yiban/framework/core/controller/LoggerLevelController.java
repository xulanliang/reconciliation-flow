package com.yiban.framework.core.controller;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.StringUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * @author swing
 * @date 2018年5月25日 上午10:57:50 类说明
 */
@Controller
@RequestMapping("/api/log")
public class LoggerLevelController extends FrameworkController {

	@GetMapping("change")
	@ResponseBody
	public ResponseResult change(@RequestParam(value = "name", required = false) String name, String level) {
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		if (!StringUtil.isEmpty(name)) {
			loggerContext.getLogger(name).setLevel(Level.valueOf(level));
		} else {
			List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
			for (ch.qos.logback.classic.Logger logger : loggerList) {
				logger.setLevel(Level.toLevel(level));
			}
		}
		return ResponseResult.success().message("当前日志等级:" + level);
	}
}
