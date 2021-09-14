package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.account.domain.User;
import com.yiban.rec.domain.TradeCheck;

public interface TradeCheckService {
	
	public Page<TradeCheck> getTradeCheckPage(TradeCheck tradeCheck,User user,Pageable pageable);
	
	void dataSwitch(List<MultipartFile> files,User user,String wechatPayShopNo,String wechatApplyId,
			String alipayPayShopNo,String alipayApplyId)throws Exception;
	
	String handleBill(String accountDate,String str,User user)  throws Exception;
	
	String checkRefund(Long id,User user,String handleRemark) throws Exception;
	
	void handlerZp(Long id);

}
