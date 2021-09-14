package com.yiban.rec.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.ThirdBill;

@Service
public class BatchService {

    @Autowired
    private EntityManager entityManager;
    
    @Transactional
    public void batchInsertOrderUpload(List<OrderUpload> list) {
        for (int i = 0; i < list.size(); i++) {
            entityManager.persist(list.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    @Transactional
    public void batchInsertHisTransactionFlow(List<HisTransactionFlow> list) {
        for (int i = 0; i < list.size(); i++) {
            entityManager.persist(list.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    @Transactional
    public void batchInsertThirdBill(List<ThirdBill> list) {
        for (int i = 0; i < list.size(); i++) {
            entityManager.persist(list.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    } 
}
