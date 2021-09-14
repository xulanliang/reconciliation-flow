package com.yiban.rec.dao.recon;

import com.yiban.rec.domain.CitizenCardBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CitizenCardBillDao extends JpaRepository<CitizenCardBill, Long>, JpaSpecificationExecutor<CitizenCardBill> {

}
