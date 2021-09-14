package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DrmService {
	
	Page<Map<String,Object>> getDrmPageList(PageRequest pagerequest,String bankTypeId,String dataTime);
	
	List<Map<String,Object>> getDrmList(String bankTypeId,String dataTime);

}
