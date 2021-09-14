package com.yiban.framework.core.exception;


import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.AjaxUtils;
import com.yiban.framework.core.validate.ControllerValidate;

/**
 * @author swing
 * @date 2018年1月5日 下午2:47:41 类说明 全局控制器处理类，所有的Controller中被@RequestMapping
 *       注解的方法都会执行被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final  String template ="<html><body><h2>%s</h2></body></html>";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	/**
	 * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
	 * 
	 * @param model
	 */
	@ModelAttribute
	public void addAttributes(Model model) {
        //  StringBuilder sb =new StringBuilder();
          
	}

	@ExceptionHandler({ Exception.class})
	public void requestException(WebRequest request, HttpServletResponse response, Exception ex) {
		writeLog(logger, ex);
		String exceptionMsg = ex.getMessage();
		if (AjaxUtils.isAjaxRequest(request)) {
			ResponseResult errorResult = ResponseResult.failure(exceptionMsg).debugMessage(ex.getMessage()).code(0);
			AjaxUtils.writeJson(errorResult, response);
		} else{
			writeHtml(exceptionMsg, response);
		}
	}

	//参数绑定验证错误
	@ExceptionHandler(BindException.class)
	public void validExceptionHandler(BindException e, WebRequest request, HttpServletResponse response) {
		writeLog(logger, e);
		logger.info("==请求参数绑定错误==");
		String message = ControllerValidate.convertErrorMessage(e);
		if (AjaxUtils.isAjaxRequest(request)) {
			ResponseResult errorResult = ResponseResult.failure(message).debugMessage(e.toString()).code(0);
			AjaxUtils.writeJson(errorResult, response);
		}else{
			writeHtml(message,response);
		}
	}
	private final void writeHtml(String errorResult, HttpServletResponse response){
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-type","text/html;charset=UTF-8");
			Writer out =response.getWriter();
			out.write(String.format(template, errorResult));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final void writeLog(Logger log, Throwable ex) {
		if (logger.isDebugEnabled()) {
			logger.debug(ex.getMessage());
		}
		if (logger.isInfoEnabled()) {
			logger.info(ex.getMessage());
		}
		if (logger.isErrorEnabled()) {
			logger.error(ex.getMessage());
		}
		ex.printStackTrace();
	}
}
