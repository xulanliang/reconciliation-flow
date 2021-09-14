package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.PayType;


public interface PayTypeDao extends JpaRepository<PayType, Long>, JpaSpecificationExecutor<PayType>{
	
	
	public List<PayType> findByType(String type);
	
	@Query("select t from PayType t where t.type in (?1) ")
	public List<PayType> findByTypes(String[] type);
}
