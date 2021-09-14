package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.TradeCheckFollow;

public interface ExceptionBillDetailService {
	
	public Page<TradeCheckFollow> queryPage(String orgCode, String billSource, String date,
			String tradeType, List<Organization> orgList, Pageable pageable);
	
	public List<TradeCheckFollow> queryNoPage(String orgCode, String billSource, String date,
			String tradeType, List<Organization> orgList, Sort sort);
}
