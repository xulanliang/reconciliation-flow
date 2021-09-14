package com.yiban.rec.service.settlement.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.rec.dao.settlement.RecLogSettlementDao;
import com.yiban.rec.domain.settlement.RecLogSettlement;
import com.yiban.rec.service.settlement.RecLogSettlementService;

@Service
public class RecLogSettlementServiceImpl extends DynamicSpecifications implements RecLogSettlementService {
	
	@Autowired
	private RecLogSettlementDao recLogSettlementDao;

	@Override
    public Page<RecLogSettlement> findPageByQueryParameters(List<SearchFilter> filters, Pageable pageable) {
        return recLogSettlementDao.findAll(Specifications.where(bySearchFilter(filters, 
        		RecLogSettlement.class)), pageable);
    }
}
