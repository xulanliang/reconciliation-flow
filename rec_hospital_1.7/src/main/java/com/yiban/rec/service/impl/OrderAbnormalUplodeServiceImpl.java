package com.yiban.rec.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.rec.dao.OrderAbnormalUplodeDao;
import com.yiban.rec.domain.OrderAbnormalUplode;
import com.yiban.rec.service.OrderAbnormalUplodeService;

@Service
public class OrderAbnormalUplodeServiceImpl implements OrderAbnormalUplodeService {

	@Autowired
	private OrderAbnormalUplodeDao orderAbnormalUplodeDao;
	
	
	public void save(OrderAbnormalUplode vo) throws Exception{
		orderAbnormalUplodeDao.save(vo);
	}
}
