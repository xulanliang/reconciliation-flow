package com.yiban.rec.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.log.TnalysisResult;

import net.sf.json.JSONArray;

public interface TnalysisResultService {
	
	 Page<TnalysisResult> getTnalysisResultList(PageRequest pagerequest,String orgCode);
	 
	 //void tnalysisThread(JSONArray jsonArray,String beforeDate,Integer sourceType);
	 
	 ResponseResult repeatTnalysis(Integer id,String orgCode,String payChannel,String fileId,String systemCode,String orderDate);
	 
	 void tnalysisPlatThread(JSONArray jsonArray);
	 public String[] getTypeAndCodeMap(String type);
}
