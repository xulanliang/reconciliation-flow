package com.yiban.rec.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.Reconciliation;


public interface ReconcilitationDao extends JpaRepository<Reconciliation, Long>, JpaSpecificationExecutor<Reconciliation>{
	
	@Transactional
	@Modifying
	@Query("delete from Reconciliation where orgNo in ?1 AND payDateStam >= ?2 AND payDateStam <= ?3")
	void updateIsactivedAndIsDeleted( List<String> orgs,Long tradeDateStart,Long tradeDateEnd );
	
	@Modifying
	@Query("delete from Reconciliation r where r.orgNo = ?1 AND r.payDateStam >= ?2 AND r.payDateStam <= ?3")
	void deleteCash(String orgNo,Long tradeDateStart,Long tradeDateEnd);
	
	@Modifying
	@Query("delete from Reconciliation r where r.orgNo = ?1 AND r.reconciliationDate >= ?2 AND r.reconciliationDate <= ?3")
	void deleteCashByDate(String orgNo,Date tradeDateStart,Date tradeDateEnd);

	
	@Query("select r from Reconciliation r where r.orgNo = ?1 AND r.reconciliationDate >= ?2 and r.reconciliationDate <= ?3")
	public List<Reconciliation> findByOrgNoAndReconciliationDate(String orgNo,Date startTime,Date endTime);
}
