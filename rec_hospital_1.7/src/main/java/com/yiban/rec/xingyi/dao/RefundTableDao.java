package com.yiban.rec.xingyi.dao;

import com.yiban.rec.xingyi.bean.RefundTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefundTableDao extends JpaRepository<RefundTable, Long>, JpaSpecificationExecutor<RefundTable> {}
