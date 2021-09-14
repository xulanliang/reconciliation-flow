package com.yiban.framework.core.controller;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.web.Servlets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.event.EventData;
import com.yiban.framework.core.util.ReactorEventUtil;


public abstract class BaseController implements ErrorController{
    //private final String PAGE_SIZE_NAME="rows";
   // private final String PAGE_NUM_NAME="page";
    
    private final String PAGE_SIZE_NAME="limit";
    private final String PAGE_NUM_NAME="offset";
    
	private  final String ERROR_PATH = "/error";  
	public void publishEvent(String topic, EventData eventData) {
		ReactorEventUtil.publishEvent(topic, eventData);
	}

	@RequestMapping(value=ERROR_PATH)  
    public String handleError(){  
        return "_common/error/404";  
    }  
	
	@Override
	public String getErrorPath() {
		
		return null;
	}


	/**
	 * 是Ajax请求?
	 */
	protected boolean isAjaxRequest() {
		return "x-requested-with".equals(this.getRequest().getHeader("XMLHttpRequest"));
	}

	/**
	 * 获取ServletRequestAttributes对象.
	 */
	protected final ServletRequestAttributes getServletRequestAttributes() {
		return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	}

	/**
	 * 获取HttpServletRequest对象.
	 */
	protected HttpServletRequest getRequest() {
		return getServletRequestAttributes().getRequest();
	}

	protected final String getStringParameter(String name, String defaultValue) {
		return ServletRequestUtils.getStringParameter(getRequest(), name, defaultValue);
	}

	protected final boolean getBooleanParameter(String name, boolean defaultValue) {
		return ServletRequestUtils.getBooleanParameter(getRequest(), name, defaultValue);
	}

	protected final int getIntParameter(String name, int defaultValue) {
		return ServletRequestUtils.getIntParameter(getRequest(), name, defaultValue);
	}

	protected final double getDoubleParameter(String name, double defaultValue) {
		return ServletRequestUtils.getDoubleParameter(getRequest(), name, defaultValue);
	}

	protected <T> WebUiPage<T> toWebUIPage(Page<T> page) {
		return new WebUiPage<T>(page.getTotalElements(), page.getContent());
	}

	protected Collection<SearchFilter> getSearchFilters() {
		return SearchFilter.parse(Servlets.getParametersStartingWith(this.getRequest(), "f_")).values();
	}

	protected Sort getSortFromDatagrid() {
		return getSortFromDatagridOrElse(null);
	}

	protected Sort getSortFromDatagridOrElse(final Sort defaultSort) {
		String sortStr = getStringParameter("sort", "");
		if (Strings.isNullOrEmpty(sortStr)) {
			return defaultSort;
		}

		String orderStr = getStringParameter("order", "");
		List<String> sortFields = Lists.newArrayList(Splitter.on(",").split(sortStr));
		List<String> orderFields = Lists.newArrayList(Splitter.on(",").split(orderStr));
		List<Order> orders = Lists.newArrayList();
		for (int i = 0; i < sortFields.size(); i++) {
			String sort = sortFields.get(i);
			String order = (i < orderFields.size()) ? orderFields.get(i) : null;
			orders.add(new Order(Direction.fromStringOrNull(order), sort));
		}
		return new Sort(orders);
	}

	protected ResponseResult exceptionAsResult(Exception e) {
		return ResponseResult.failure().message("操作未完成: " + e.getMessage())
				.debugMessage(ExceptionUtils.getRootCauseMessage(e));
	}

	protected PageRequest getRequestPageable() {
		return getPageRequest(getSortFromDatagrid());
	}

	protected PageRequest getRequestPageabledWithInitSort(final Sort sort) {
		return getPageRequest(getSortFromDatagridOrElse(sort));
	}
	
	protected PageRequest getPageRequest(final Sort sort) {
		return getPageRequest(this.getIntParameter(PAGE_SIZE_NAME, getDefaultPageSize()), sort);
	}

	protected PageRequest getPageRequest(final int pageSize, final Sort sort) {
		//bootstrap datatable 分页提交的是从第几条数据，所以这里需要转换成页数
		int offset = this.getIntParameter(PAGE_NUM_NAME, 0);
        int page = offset/pageSize;
        return getPageRequest(page, pageSize, sort);
	}

	protected PageRequest getPageRequest(final int page, final int pageSize, final Sort sort) {
		return new PageRequest(page, pageSize, sort);
	}

	protected int getDefaultPageSize() {
		return 10;
	}

	private final Sort _idAscSort = getFieldAscSort("id");
	private final Sort _idDescSort = getFieldDescSort("id");

	protected final Sort getIdAscSort() {
		return _idAscSort;
	}

	protected final Sort getIdDescSort() {
		return _idDescSort;
	}

	protected final Sort getFieldAscSort(String field) {
		return new Sort(Direction.ASC, field);
	}

	protected final Sort getFieldDescSort(String field) {
		return new Sort(Direction.DESC, field);
	}

	protected final Sort getSort(String field, String order) {
		return new Sort(Direction.fromStringOrNull(order), field);
	}
	
}
