package com.yiban.rec.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.BillSourceTermNo;

public interface BillSourceTermNoDao extends JpaRepository<BillSourceTermNo, Long>, 
JpaSpecificationExecutor<BillSourceTermNo> {

}
