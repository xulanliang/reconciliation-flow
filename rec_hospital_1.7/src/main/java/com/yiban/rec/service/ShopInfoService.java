package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.baseinfo.ShopInfo;

public interface ShopInfoService {
	 ShopInfo getShopInfoById(Long id);
	 
	 ResponseResult save(ShopInfo shopInfo);
	 
	 ResponseResult delete(Long id);
	 
	 ResponseResult update(ShopInfo shopInfo);
	 
	 Page<Map<String,Object>> getShopInfoList(PageRequest pagerequest,String deviceNo);

	 ShopInfo findShopInfoByOrgNoAndApplyIdAndPayShopNo(String orgNo, String applyId, String payShopNo);
	 
	 List<ShopInfo> getAllShopInfo();
	 
	 ShopInfo getApplyIdByPayShopNo(String payShopNo);
	 
	 List<ShopInfo> getShopInfoByOrgNo(String orgNo,String payName);
	 
	 Map<String,String> getTerm2PayTerm();
}
