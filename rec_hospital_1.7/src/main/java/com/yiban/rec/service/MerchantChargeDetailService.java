package com.yiban.rec.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.ThirdBill;

public interface MerchantChargeDetailService {

	public Page<ThirdBill> queryThirdBillPage(String billSource, String date, String tsnOrderNo,
			List<Organization> orgList, String tradeType, Pageable pageable);

	public List<ThirdBill> queryThirdBillNoPage(String billSource, String date, String tsnOrderNo,
			List<Organization> orgList, String tradeType, Sort sort);

	public BigDecimal querySum(String billSource, String date, String tsnOrderNo, List<Organization> orgList, String tradeType);

}
