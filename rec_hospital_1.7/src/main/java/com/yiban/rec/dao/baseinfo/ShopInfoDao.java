package com.yiban.rec.dao.baseinfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.baseinfo.ShopInfo;

public interface ShopInfoDao extends JpaRepository<ShopInfo, Long>, JpaSpecificationExecutor<ShopInfo> {

	ShopInfo findShopInfoByOrgNoAndApplyIdAndPayShopNo(String orgNo, String applyId, String payShopNo);
	
	ShopInfo findShopInfoByPayShopNo( String payShopNo);
	
	List<ShopInfo> findByOrgNoAndPayName(String orgNo,String payName);
	
	ShopInfo findByOrgNoAndMetaDataPayId(String orgNo,String metaDataPayId);
	
	
	
	
}
