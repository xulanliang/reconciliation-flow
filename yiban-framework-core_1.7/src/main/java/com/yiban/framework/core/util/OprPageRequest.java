package com.yiban.framework.core.util;

import java.util.Map;

import org.springframework.data.domain.PageRequest;

public class OprPageRequest extends PageRequest{
	
	public OprPageRequest(int page, int size) {
		super(page, size);
	}

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> where;

	public Map<String, String> getWhere() {
		return where;
	}

	public void setWhere(Map<String, String> where) {
		this.where = where;
	}

}
