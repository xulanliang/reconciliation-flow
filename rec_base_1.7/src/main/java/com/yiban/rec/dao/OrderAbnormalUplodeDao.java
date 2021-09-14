package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.OrderAbnormalUplode;

public interface OrderAbnormalUplodeDao extends JpaRepository<OrderAbnormalUplode, Long>, 
JpaSpecificationExecutor<OrderAbnormalUplode> {

}
