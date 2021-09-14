package com.yiban.rec.dao;

import com.yiban.rec.domain.WindowCash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * 窗口现金核对
 */
public interface WindowCashDao
        extends JpaRepository<WindowCash, Long>, JpaSpecificationExecutor<WindowCash> {

    @Query("select t from WindowCash t where t.orgCode in (?1) and  t.cashDate>=?2 and  t.cashDate<=?3 and t.cashierName like ?4 and t.bankType like ?5 and t.businessType like ?6")
    Page<WindowCash> findByOrgNoAndCashDateAndBankType(String[] orgNo, Date startDate, Date endDate, String cashierName, String bankType, String businessType, Pageable pageable);

    @Modifying
    @Query("update  WindowCash t set t.cashStatus = ?1, t.checkDateTime = ?2,t.checkCashierAcount=?3,t.checkCashierName=?4 where id = ?5")
    void updateById(String cashStatus, Date checkDateTime,String checkCashierAcount, String checkCashierName,Long id);

}
