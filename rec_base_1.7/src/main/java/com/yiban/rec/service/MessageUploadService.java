package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.MessageUpload;

public interface MessageUploadService {
	
	 ResponseResult delete(Long id);
	 
	 Page<MessageUpload> getMessageUploadList(PageRequest pagerequest);
	 
	 void save();
	 
	 List<MessageUpload> getMessageUploadList(String orgNo);

}
