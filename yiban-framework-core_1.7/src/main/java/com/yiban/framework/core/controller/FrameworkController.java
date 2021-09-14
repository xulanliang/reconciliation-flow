package com.yiban.framework.core.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.util.OprPageRequest;


/**
 * @author swing
 * @date 2018年1月8日 上午10:06:19 类说明
 */
public abstract class FrameworkController extends BaseController {
	
	protected Logger logger=LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApplicationContext ctx;

	//模板方法，可以通过目录命名子类进行页面重写
	protected String autoView(String name) {
		final Resource resource = ctx.getResource("classpath:/templates/" + name + ".html");
		if (resource != null && resource.exists()) {
			return name;
		}
		return "_" + name;
	}
	
	/**
	 * 从request中获取参数集合 封装成BaseOprPageRequest对象
	 * 
	 * 注意:url中传递的参数名字不重复 如果重复 取第一个值
	 * 
	 * @param request
	 * @return
	 * @throws BusinessException
	 */
	protected OprPageRequest URL2PageRequest() throws BusinessException {
		
		HttpServletRequest request = getRequest();
		// rows pagesize sort
		Map<String, String[]> map = request.getParameterMap();
		
		OprPageRequest oprPageRequest = new OprPageRequest(
				super.getIntParameter("page", 1) - 1,
				super.getIntParameter("rows", 10)
			);
		Map<String, String> bm = new HashMap<String, String>();
		String key, value;
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			key = entry.getKey();
			if ("page".equals(key) || "rows".equals(key)) {
				continue;
			} 
			
			value = entry.getValue().length >= 1 ? map.get(key)[0].trim() : "";
			bm.put(key, value);
		}
		oprPageRequest.setWhere(bm);
		
		return oprPageRequest;
	}
}
