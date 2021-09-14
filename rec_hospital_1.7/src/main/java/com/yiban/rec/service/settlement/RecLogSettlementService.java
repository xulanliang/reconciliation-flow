package com.yiban.rec.service.settlement;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.rec.domain.settlement.RecLogSettlement;

/**
 * 结算日志Service
 * @Author WY
 * @Date 2019年1月11日
 */
public interface RecLogSettlementService {

	public Page<RecLogSettlement> findPageByQueryParameters(List<SearchFilter> filters, Pageable pageable);
}
