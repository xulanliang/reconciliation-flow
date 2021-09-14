package com.yiban.rec.emailbill.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.emailbill.service.ThirdBillService;

/**
 * @author swing
 * @date 2018年6月25日 上午11:02:20 类说明
 */
@Service
public class ThirdBillServiceImpl implements ThirdBillService {

	@PersistenceContext
	protected EntityManager em;
    @Autowired
    private ThirdBillDao thirdBillDao;
    
    @Autowired
    private OrganizationService organizationService;
    
    
	@Async
	@Transactional
	@Override
	public void batchInsertThirdBill(List<ThirdBill> list){
		for (int i = 0; i < list.size(); i++) {
			em.persist(list.get(i));
			if (i % 30 == 0) {
				em.flush();
				em.clear();
			}
		}
	}
	
	@Transactional
	@Override
	public void batchInsertBill(List<ThirdBill> list) throws Exception{
		try {
			for (int i = 0; i < list.size(); i++) {
				em.persist(list.get(i));
				if (i % 30 == 0) {
					em.flush();
					em.clear();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void batchInsertHis(List<HisTransactionFlow> list)throws Exception{
		try {
			for (int i = 0; i < list.size(); i++) {
				em.persist(list.get(i));
				if (i % 30 == 0) {
					em.flush();
					em.clear();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void batchInsertPay(List<HisPayResult> list)throws Exception{
		try {
			for (int i = 0; i < list.size(); i++) {
				em.persist(list.get(i));
				if (i % 30 == 0) {
					em.flush();
					em.clear();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	


	@Override
	public int delete(String startTime, String endTime, List<String> orgCodes, String payType) {
		return thirdBillDao.delete(startTime, endTime, orgCodes, payType);
	}
	
	@Override
	public List<ThirdBill>  queryThrdBill(String orderNo,String orderType){
		return thirdBillDao.findByOrderNoAndOrderState(orderNo,orderType);
	}
	
	@Override
	public List<ThirdBill>  queryThrdBillByOrderNo(String orderNo){
		return thirdBillDao.findByOrderNoAndOrderState(orderNo);
	}

    @Override
    public int delete(String startTime, String endTime, List<String> orgCodes, String payType, String billSource) {
        return thirdBillDao.delete(startTime, endTime, orgCodes, payType, billSource);
    }
    
    @Override
    @Transactional
	public int delete(String startTime, String endTime, String orgCode) throws Exception {
    	//获取子机构
    	List<Organization> orgList = organizationService.findByParentCode(orgCode);
    	List<String> orgCodes=  new ArrayList<String>();
    	for(Organization v:orgList) {
    		orgCodes.add(v.getCode());
    	}
		return thirdBillDao.delete(startTime, endTime, orgCodes);
	}

	@Override
	public List<ThirdBill> findByPayFlowNo(String payFolwNo) {
		return this.thirdBillDao.findByPayFlowNo(payFolwNo);
	}
}
